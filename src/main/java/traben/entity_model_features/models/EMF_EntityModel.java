package traben.entity_model_features.models;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.mixin.accessor.entity.model.AnimalModelAccessor;
import traben.entity_model_features.models.anim.AnimationCalculation;
import traben.entity_model_features.models.anim.AnimationGetters;
import traben.entity_model_features.models.anim.AnimationModelDefaultVariable;
import traben.entity_model_features.models.jemJsonObjects.EMF_JemData;
import traben.entity_model_features.models.jemJsonObjects.EMF_ModelData;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.vanilla_part_mapping.VanillaMappings;

import java.util.*;

@Environment(value= EnvType.CLIENT)
public class EMF_EntityModel<T extends LivingEntity> extends EntityModel<T> implements ModelWithHat, ModelWithWaterPatch, ModelWithArms, ModelWithHead, EMFCustomModel<T> {

    private final EMF_JemData jemData;
    public final Object2ObjectOpenHashMap<String, EMF_ModelPart> childrenMap = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectLinkedOpenHashMap<String,AnimationCalculation> animationKeyToCalculatorObject = new Object2ObjectLinkedOpenHashMap<>();
    public final Object2ObjectLinkedOpenHashMap<String,AnimationCalculation> alreadyCalculatedThisTickAnimations = new Object2ObjectLinkedOpenHashMap<>();
    public final Object2ObjectLinkedOpenHashMap<String,AnimationCalculation> needToBeAddedToAnimationMap = new Object2ObjectLinkedOpenHashMap<>();

    private final HashMap<String, VanillaMappings.ModelAndParent> vanillaModelPartsById;



    public final String modelPathIdentifier;
    public final EntityModel<T> vanillaModel;

    public VertexConsumerProvider currentVertexProvider = null;

    public boolean isAnimated;

    //private boolean stillInInit = true;

    public RenderLayer getLayer2(Identifier van) {

        if (texture == null){
            return EMFData.getInstance().getConfig().forceTranslucentMobRendering ? RenderLayer.getEntityTranslucent(van) : vanillaModel.getLayer(van);
        }
        return EMFData.getInstance().getConfig().forceTranslucentMobRendering ? RenderLayer.getEntityTranslucent(texture) : vanillaModel.getLayer(texture);
    }

    public final Identifier texture;



    public EMF_EntityModel(EMF_JemData jem, String modelPath, VanillaMappings.VanillaMapper vanillaPartSupplier, EntityModel<T> vanillaModel){

       // super(EMFData.getInstance().getConfig().forceTranslucentMobRendering ? RenderLayer::getEntityTranslucent : RenderLayer::getEntityCutoutNoCull);

        ((ModelAccessor)this).setLayerFactory(this::getLayer2);

        if(jem != null && !jem.texture.isEmpty()){
            Identifier texture =new Identifier(jem.texture);
            if(MinecraftClient.getInstance().getResourceManager().getResource(texture).isPresent()){
                this.texture = texture;
            }else{
                this.texture = null;
            }
        }else{
            this.texture = null;
        }

        HashMap<String, VanillaMappings.ModelAndParent> vanModelParts = vanillaPartSupplier.getVanillaModelPartsMapFromModel(vanillaModel);
        this.vanillaModel = vanillaModel;
        vanillaModelPartsById = vanModelParts;
        modelPathIdentifier = modelPath;
        jemData = jem;


        if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage(modelPathIdentifier + " = " + vanModelParts);

        for (EMF_ModelData sub:
                jemData.models) {


            ModelPart vanillaPart = sub.part == null? null : (vanModelParts.containsKey(sub.part) ? vanModelParts.get(sub.part).part() : null);
            String vanillaParentPartName = sub.part == null? null : (vanModelParts.containsKey(sub.part) ? vanModelParts.get(sub.part).parentName() : null);
            childrenMap.put(sub.id, new EMF_ModelPart(null, 0, sub, new ArrayList<EMF_ModelData>(), new float[]{}, vanillaPart, vanillaParentPartName, this));

        }
        //init parent to copy setup
        getAllParts().forEach((part, value)-> {
            if(value.vanillaParentPartName != null){
                if (childrenMap.containsKey(value.vanillaParentPartName)){
                    value.vanillaParentPart = childrenMap.get(value.vanillaParentPartName);
                }else if (vanModelParts.containsKey(value.vanillaParentPartName)){
                    value.vanillaParentPart = vanModelParts.get(value.vanillaParentPartName).part();
                }
            }
        });


        if(EMFData.getInstance().getConfig().printModelCreationInfoToLog)   EMFUtils.EMF_modMessage("start anim creation for "+modelPathIdentifier);
        preprocessAnimationStrings();
        if(EMFData.getInstance().getConfig().printModelCreationInfoToLog)  EMFUtils.EMF_modMessage("end anim creation for "+modelPathIdentifier);
        isAnimated = !animationKeyToCalculatorObject.isEmpty();

        //stillInInit = false;
    }




    private void preprocessAnimationStrings(){
        ///animation processing/////////////
        Object2ObjectOpenHashMap<String, EMF_ModelPart> parts = getAllParts();

        LinkedList<LinkedHashMap<String,String>>  allProperties = new LinkedList<>();
        for (EMF_ModelPart part :
                parts.values()) {
            if (part.selfModelData.animations != null && part.selfModelData.animations.length != 0) {
                //todo replace 'this' to represent actual model part
                allProperties.addAll(Arrays.asList(part.selfModelData.animations));
            }
        }
        LinkedHashMap<String,String>  combinedProperties = new LinkedHashMap<>();
        for (LinkedHashMap<String,String> properties :
                allProperties) {
            if (!properties.isEmpty()) {
                combinedProperties.putAll(properties);
            }
        }
        if(!combinedProperties.isEmpty()) {

            combinedProperties.forEach((animKey,animationExpression)-> {
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("parsing animation value: ["+animKey+"]");
                String modelId = animKey.split("\\.")[0];
                String modelVariable = animKey.split("\\.")[1];

                AnimationModelDefaultVariable thisVariable = null;
                try {
                    thisVariable = AnimationModelDefaultVariable.valueOf(modelVariable);
                }catch (IllegalArgumentException e){
                    if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("custom variable located: ["+animKey+"].");
                }
                EMF_ModelPart thisPart = parts.get(modelId);
                AnimationCalculation thisCalculator = null;

                if (thisPart != null){

                    thisCalculator =
                            new AnimationCalculation(
                                    this,
                                    (EMF_ModelPart)thisPart,
                                    thisVariable,
                                    animKey,
                                    animationExpression);
                }else if(vanillaModelPartsById.containsKey(modelId)){
                    thisCalculator = new AnimationCalculation(
                                    this,
                                    vanillaModelPartsById.get(modelId).part(),
                                    thisVariable,
                                    animKey,
                                    animationExpression);
                }else{
                    //not a custom model or vanilla must be a custom variable
                    thisCalculator = new AnimationCalculation(
                                    this,
                                    null,
                                    null,
                                    animKey,
                                    animationExpression);
                }

                if(thisCalculator.isValid()){
                  //  System.out.println("found and added valid animation: "+animKey+"="+animationExpression);
                    animationKeyToCalculatorObject.put(animKey,thisCalculator);
                }else{
                    EMFUtils.EMF_modWarn("invalid animation = "+animKey+"="+ animationExpression);
                }
                //here we have a set of animation objects that only need to be iterated over and run


            });
        }
    }



    //called when another animation wants to get "head.tx" or something similar
    //checks if an existing animation has that key and uses it
    // if it's not existing it tries to find the part, be it EMF or vanilla to copy its value
    //there is an alternate method that is more optimized just for animation variables e.g.g "var.asdf"
    public float getAnimationResultOfKey(
            EMF_ModelPart parentForCheck,
            String key,
            Entity entity) {
        //if(stillInInit) return 1;

        //if(key.contains("arm")) System.out.println(vanillaModelPartsById.keySet());;
        if (!alreadyCalculatedThisTickAnimations.containsKey(key)) {
        //if (!animationKeyToCalculatorObject.containsKey(key)) {
            String partName = key.split("\\.")[0];

            if (vanillaModelPartsById.containsKey(partName)) {

                AnimationModelDefaultVariable variableToGet;
                try {
                    variableToGet = AnimationModelDefaultVariable.valueOf(key.split("\\.")[1]);
                    //attempt to cache an interpolating animation variable pointing to the vanilla part so that vanilla values can match EMF interpolation when needed
                    //////////////////
                    if(!variableToGet.isRotation &&
                            !animationKeyToCalculatorObject.containsKey(key) &&
                            !needToBeAddedToAnimationMap.containsKey(key)){
                        //this means we have not added a custom interpolating variable yet, so add one
                        AnimationCalculation interpolatingVanillaGetter =  new AnimationCalculation(
                                this,
                                getAllParts().get(partName),//null if no mapping is correct
                                variableToGet,
                                key,
                                key);
                        //add to start of anims for next loop to be able to use interpolating value
                        needToBeAddedToAnimationMap.putAndMoveToFirst(key,interpolatingVanillaGetter);
                        //now that it has been put this section will not run again for the same vanilla value
                    }
                    //////////////////

                    //if (key.contains("arm")) System.out.println(key + "=" + value);
                    return variableToGet.getFromVanillaModel(vanillaModelPartsById.get(partName).part());
                } catch (IllegalArgumentException e) {
                    EMFUtils.EMF_modWarn("no animation expression part variable value found for: " + key + " in " + modelPathIdentifier);
                    return 0;
                }

            } else if (getAllParts().containsKey(partName)) {
                AnimationModelDefaultVariable variableToGet;
                EMF_ModelPart otherPart = getAllParts().get(partName);
                try {
                    variableToGet = AnimationModelDefaultVariable.valueOf(key.split("\\.")[1]);
                    if (parentForCheck != null && parentForCheck.equals(otherPart.parent)) {
                        return variableToGet.getFromEMFModel(otherPart, true);
                    } else {
                        return variableToGet.getFromEMFModel(otherPart);
                    }

                } catch (IllegalArgumentException e) {
                    EMFUtils.EMF_modWarn("no animation expression part variable value found for: " + key + " in " + modelPathIdentifier);
                    return 0;
                }

            } else {
                EMFUtils.EMF_modWarn("no animation expression value found for: " + key + " in " + modelPathIdentifier);
                //System.out.println(animationKeyToCalculatorObject.keySet());
                //System.out.println(vanillaModelPartsById.keySet());
                return 0;
            }

        }
        //return animationKeyToCalculatorObject.get(key).getLastResultOnly((LivingEntity) entity);
        return alreadyCalculatedThisTickAnimations.get(key).getLastResultOnly((LivingEntity) entity);
    }
    //same as above method but optimized for variable loading as they only exist in animations
    //also as we do not want an unwanted error message unlike the other method
    public float getAnimationResultOfKeyOptimiseForVariable(
            String key,
            Entity entity) {
        if (animationKeyToCalculatorObject.containsKey(key)) {
            return animationKeyToCalculatorObject.get(key).getLastResultOnly((LivingEntity) entity);
        }
        if(EMFData.getInstance().getConfig().printAllMaths) EMFUtils.EMF_modWarn("no animation variable found for: " + key + " in " + modelPathIdentifier);
        return 0;
    }


    @Override
    public EMF_EntityModel<T> getThisEMFModel() {
        return this;
    }

    @Override
    public boolean doesThisModelNeedToBeReset() {
        return true;
    }


    @Override
    public void render( MatrixStack herematrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

       // if(currentEntity == null)return;

        switch (EMFData.getInstance().getConfig().displayVanillaModelHologram){
            case Yes -> {
                vanillaModel.render(herematrices, vertices, LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay, red/2, green, blue/2, alpha/2);
            }
            case Offset -> {
                herematrices.push();
                herematrices.translate(1.5, 0, 0);
                vanillaModel.render(herematrices, vertices, LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay, red/2, green, blue/2, alpha/2);
                herematrices.pop();
            }
            default -> {}
        }

        herematrices.push();

        if(animationProgress < EMFData.getInstance().getConfig().spawnAnimTime && EMFData.getInstance().getConfig().spawnAnim != EMFConfig.SpawnAnimation.None){
            float delta = MathHelper.clamp( animationProgress / EMFData.getInstance().getConfig().spawnAnimTime,0,1f);
            switch (EMFData.getInstance().getConfig().spawnAnim){
                case InflateGround -> {
                    herematrices.translate(0,2 - 2 * delta,0);
                    herematrices.scale(delta,delta,delta);
                }
                case InflateCenter -> {
                    //herematrices.translate(0,2 - 2 * delta,0);
                    herematrices.scale(delta,delta,delta);
                }
                case Rise -> herematrices.translate(0,3 *(1- delta),0);
                case Fall -> {
                    alpha = delta;
                    herematrices.translate(0,-20 *(1- delta),0);
                }
                case Fade -> alpha = delta;
                case Pitch -> {
                    herematrices.multiply((new Quaternionf()).rotationZYX(0, 0, (float) Math.toRadians(90 - 90*delta)));
                }
                case Yaw -> {
                    herematrices.multiply((new Quaternionf()).rotationZYX(0,  (float) Math.toRadians(360 - 360*delta),0));
                }
                case Dark -> {
                    int lightSimple = LightmapTextureManager.getBlockLightCoordinates(light);
                    lightSimple = Math.max(lightSimple, LightmapTextureManager.getSkyLightCoordinates(light));
                    int lightClamp = MathHelper.clamp( (int)(15 * delta),0,lightSimple);
                    light =LightmapTextureManager.pack(lightClamp,lightClamp);
                }
                case Bright ->{
                    int lightSimple = LightmapTextureManager.getBlockLightCoordinates(light);
                    lightSimple = Math.max(lightSimple, LightmapTextureManager.getSkyLightCoordinates(light));
                    int lightClamp = MathHelper.clamp( (int)(15 - 15* delta),lightSimple,15);
                    light =LightmapTextureManager.pack(lightClamp,lightClamp);
                    //light = MathHelper.clamp(LightmapTextureManager.getSkyLightCoordinates((int)(15 - 15* delta)),light,LightmapTextureManager.MAX_LIGHT_COORDINATE);
                }
            }
        }

        if(this.texture != null && this.currentVertexProvider != null && this.vanillaModel != null){
            vertices = this.currentVertexProvider.getBuffer(this.vanillaModel.getLayer(this.texture));
        }

        if (this.child && vanillaModel instanceof AnimalModel animal) {
            float f = 1.0F / ((AnimalModelAccessor)animal).getInvertedChildBodyScale();
            herematrices.scale(f, f, f);
            herematrices.translate(0.0F, ((AnimalModelAccessor)animal).getChildBodyYOffset() / 16.0F, 0.0F);
        }
        for (String key:
                childrenMap.keySet()) {
            herematrices.push();
            //herematrices.translate(0,24/16f,0);
            EMF_ModelPart emfPart = childrenMap.get(key);
            if(emfPart.selfModelData.attach
                    && emfPart.selfModelData.translate[0] == 0
                    && emfPart.selfModelData.translate[1] == 0
                    && emfPart.selfModelData.translate[2] == 0
                    && emfPart.vanillaPart != null){
                ModelTransform vanilla = emfPart.vanillaPart.getTransform();
                float x =vanilla.pivotX/16;
                float y =  (vanilla.pivotY-24 )/16;//todo i feel like this isnt it, check futher
                float z =vanilla.pivotZ/16;
                herematrices.translate(x,y,z);
                emfPart.render(herematrices, vertices, light, overlay, red, green, blue, alpha);
            }else {
                emfPart.render(herematrices, vertices, light, overlay, red, green, blue, alpha);
            }
            herematrices.pop();
        }
        herematrices.pop();
    }


    private Object2ObjectOpenHashMap<String, EMF_ModelPart> getAllPartsCachedResult = null;
    public Object2ObjectOpenHashMap<String, EMF_ModelPart> getAllParts(){
        if(getAllPartsCachedResult == null) {
            Object2ObjectOpenHashMap<String, EMF_ModelPart> list = new Object2ObjectOpenHashMap<String, EMF_ModelPart>();
            for (EMF_ModelPart part :
                    childrenMap.values()) {
                list.put(part.selfModelData.id, part);
                list.putAll(part.getAllParts());
            }
            getAllPartsCachedResult = list;
        }
        return getAllPartsCachedResult;
    }

    @Override
    public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
       // super.animateModel(entity, limbAngle, limbDistance, tickDelta);
        this.tickDelta = tickDelta;
        vanillaModel.animateModel(entity,limbAngle,limbDistance,tickDelta);
        vanillaModel.child = child;
        vanillaModel.handSwingProgress = handSwingProgress;
        vanillaModel.riding=riding;
        if(vanillaModel instanceof BipedEntityModel biped){
            biped.sneaking = sneaking;
        }
//        handSwingProgress =  ((LivingEntity)entity).getHandSwingProgress(tickDelta);
//        riding = entity.hasVehicle();
//        child = ((LivingEntity)entity).isBaby();
//        sneaking = entity.isSneaking();
    }
    float tickDelta = 0;

    float animationProgress = 0;
    public boolean sneaking = false;

    public float currentAnimationDeltaForThisTick = 0;
    public boolean calculateForThisAnimationTick = false;

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){//, VanillaMappings.VanillaMapper vanillaPartSupplier,EntityModel<T> vanillaModel){
        this.animationProgress = animationProgress;
        calculateForThisAnimationTick = false;


        currentEntity = entity;
        if(entity != null) {
            UUID id = entity.getUuid();
            if (EMFData.getInstance().getConfig().animationRate == EMFConfig.AnimationRatePerSecondMode.Every_frame && EMFData.getInstance().getConfig().animationRateDistanceDropOffRate == 0) {
                calculateForThisAnimationTick = true;
                currentAnimationDeltaForThisTick = 0;
            } else {
                float interpolationLength = prevInterp.getFloat(id);

                if (animationProgress >= prevResultsTick.getFloat(id) + interpolationLength) {
                    //vary interpolation length by distance from client
                    if (MinecraftClient.getInstance().player != null) {
                        //val;
                        //if(entity == MinecraftClient.getInstance().player)
                        //    val=0;
                        //else
                        float val= EMFData.getInstance().getConfig().getInterpolationModifiedByDistance((entity.distanceTo(MinecraftClient.getInstance().player) - EMFData.getInstance().getConfig().animationRateMinimumDistanceDropOff));
                        prevInterp.put(id, EMFData.getInstance().getConfig().getAnimationRateFromFPS(val));
                    } else {
                        prevInterp.put(id, EMFData.getInstance().getConfig().getAnimationRateFromFPS(0));
                    }

                    prevResultsTick.put(id, getNextPrevResultTickValue());//entity.age + tickDelta);
                    calculateForThisAnimationTick = true;



                    //currentAnimationDeltaForThisTick = 0f;
                } else if (animationProgress < prevResultsTick.getFloat(id) - 100 - interpolationLength) {
                    //this is required as animation progress resets with the entity entering render distance
                    //todo possibly use world time ticks instead ??
                    prevResultsTick.put(id, -100);

                    //interpolate easier as will calculate next tick
                    calculateForThisAnimationTick = false;
                    // currentAnimationDeltaForThisTick =  ((animationProgress - prevResultsTick.getFloat(id) ) / interpolationLength);
                } else {
                    //interpolate
                    calculateForThisAnimationTick = false;
                    //currentAnimationDeltaForThisTick =  ((animationProgress - prevResultsTick.getFloat(id) ) / interpolationLength);
                }
                currentAnimationDeltaForThisTick = ((animationProgress - prevResultsTick.getFloat(id)) / interpolationLength);
            }
            vanillaModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

            alreadyCalculatedThisTickAnimations.clear();
            //if(entity instanceof BlazeEntity && entity.getRandom().nextInt(50) == 4) System.out.println(animationKeyToCalculatorObject.keySet());
            animationKeyToCalculatorObject.forEach((key,animationCalculation)->{

                animationCalculation.calculateAndSet(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, tickDelta);
                alreadyCalculatedThisTickAnimations.put(key,animationCalculation);
            });
            if (!needToBeAddedToAnimationMap.isEmpty()){
                needToBeAddedToAnimationMap.forEach(animationKeyToCalculatorObject::putAndMoveToFirst);
            }
        }
        //that's it????
    }

    private float getNextPrevResultTickValue(){
        return animationProgress; //currentEntity.age+ tickDelta;
    }

    T currentEntity = null;

    Object2FloatOpenHashMap<UUID> prevResultsTick = new Object2FloatOpenHashMap<>(){{
        defaultReturnValue(-100);
    }};
    Object2FloatOpenHashMap<UUID> prevInterp = new Object2FloatOpenHashMap<>(){{
        defaultReturnValue(EMFData.getInstance().getConfig().getAnimationRateFromFPS(0));
    }};

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        ((ModelWithArms)vanillaModel).setArmAngle(arm,matrices);
    }

    @Override
    public void setHatVisible(boolean visible) {
        ((ModelWithHat)vanillaModel).setHatVisible(visible);
    }

    @Override
    public ModelPart getHead() {
        return((ModelWithHead) vanillaModel).getHead();
    }

    @Override
    public ModelPart getWaterPatch() {
        return ((ModelWithWaterPatch) vanillaModel).getWaterPatch();
    }


    public void copyStateToEMF(EMF_EntityModel<?> copy) {
        try {
            super.copyStateTo((EntityModel<T>) copy);
        }catch (Exception ignored){}
       // if(copy instanceof EMF_CustomModel<?> emf){
        Object2ObjectOpenHashMap<String, ? extends EMF_ModelPart> copyParts = copy.getAllParts();
        Object2ObjectOpenHashMap<String, ? extends EMF_ModelPart> thisParts = this.getAllParts();
        //System.out.println(copyParts);
        //System.out.println(thisParts);
            for (Map.Entry<String, ? extends EMF_ModelPart> entry:
                 copyParts.entrySet()) {

                if(thisParts.containsKey(entry.getKey())){
                    EMF_ModelPart thisPart = thisParts.get(entry.getKey());
                    if(thisPart != null && entry.getValue() != null)
                        entry.getValue().copyTransform(thisPart);
                }

            }
        //}
    }


    public void setVisibleToplvl(boolean setTo){
        this.childrenMap.forEach((key,part)->{
            part.visible = setTo;
            part.visibilityIsOveridden = true;
            //System.out.println("made"+key+setTo);
        });

    }
    public void setVisibleToplvl(Set<String> partNames, boolean setTo){
        //Map<String,EMF_CustomModelPart<T>> parts = getAllParts();

        childrenMap.forEach((key,part)->{
            if(partNames.contains(key) && part != null){
                part.visible = setTo;
                part.visibilityIsOveridden = true;
                //System.out.println("made2"+key+setTo);
            }
        });

    }

    public EMF_EntityModel<T> getArmourModel(boolean getInner){
        if(getInner){
            if(innerArmor== null){
                String path = modelPathIdentifier.replace(".jem", "_armor_inner.jem");
                EMF_JemData jem = EMFUtils.EMF_readJemData(path);
                if(jem != null) {
                    innerArmor = new EMF_EntityModel<>(jem, path, this::supplierCopy, vanillaModel);
                    innerArmor.isAnimated = this.isAnimated;
                }else{
                    String path2 = "optifine/cem/biped_armor_inner.jem";
                    EMF_JemData jem2 = EMFUtils.EMF_readJemData(path2);
                    if(jem2 != null) {
                        innerArmor = new EMF_EntityModel<>(jem2, path2, this::supplierCopy, vanillaModel);
                        innerArmor.isAnimated = this.isAnimated;
                    }
                }
            }
            return innerArmor;
        }else{
            if(outerArmor== null){

                String path = modelPathIdentifier.replace(".jem", "_armor_outer.jem");
                EMF_JemData jem = EMFUtils.EMF_readJemData(path);
                if(jem != null) {
                    outerArmor = new EMF_EntityModel<>(jem, path, this::supplierCopy, vanillaModel);
                    outerArmor.isAnimated = this.isAnimated;
                }else{
                    String path2 = "optifine/cem/biped_armor_outer.jem";
                    EMF_JemData jem2 = EMFUtils.EMF_readJemData(path2);
                    if(jem2 != null) {
                        outerArmor = new EMF_EntityModel<>(jem2, path2, this::supplierCopy, vanillaModel);
                        outerArmor.isAnimated = this.isAnimated;
                    }
                }
            }
            return outerArmor;
        }
    }


    public HashMap<String, VanillaMappings.ModelAndParent> supplierCopy(EntityModel<?> entityModel) {
        return vanillaModelPartsById;
    }

    private EMF_EntityModel<T> innerArmor = null;

    private EMF_EntityModel<T> outerArmor = null;





}
