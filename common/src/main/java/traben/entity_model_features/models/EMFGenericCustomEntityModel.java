package traben.entity_model_features.models;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.mixin.accessor.entity.model.AnimalModelAccessor;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationMathParser.MathValue;
import traben.entity_model_features.models.animation.EMFAnimationVariableSuppliers;
import traben.entity_model_features.models.animation.EMFDefaultModelVariable;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;
import traben.entity_model_features.models.vanilla_model_compat.VanillaModelPartOptiFineMappings;
import traben.entity_model_features.utils.EMFUtils;

import java.util.*;

@Environment(value= EnvType.CLIENT)
public class EMFGenericCustomEntityModel<T extends LivingEntity> extends EntityModel<T> implements ModelWithHat, ModelWithWaterPatch, ModelWithArms, ModelWithHead, EMFCustomEntityModel<T> {

    private final EMFJemData jemData;
    public final Object2ObjectLinkedOpenHashMap<String, EMFModelPart> childrenMap = new Object2ObjectLinkedOpenHashMap<>();
    public final Object2ObjectLinkedOpenHashMap<String, EMFAnimation> animationKeyToCalculatorObject = new Object2ObjectLinkedOpenHashMap<>();
    public final Object2ObjectLinkedOpenHashMap<String, EMFAnimation> alreadyCalculatedThisInitTickAnimations = new Object2ObjectLinkedOpenHashMap<>();
    //public final Object2ObjectLinkedOpenHashMap<String,AnimationCalculation> needToBeAddedToAnimationMap = new Object2ObjectLinkedOpenHashMap<>();

    private final HashMap<String, VanillaModelPartOptiFineMappings.ModelAndParent> vanillaModelPartsById;



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



    public EMFGenericCustomEntityModel(EMFJemData jem, String modelPath, VanillaModelPartOptiFineMappings.VanillaMapper vanillaPartSupplier, EntityModel<T> vanillaModel){

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

        HashMap<String, VanillaModelPartOptiFineMappings.ModelAndParent> vanModelParts = vanillaPartSupplier.getVanillaModelPartsMapFromModel(vanillaModel);
        this.vanillaModel = vanillaModel;
        vanillaModelPartsById = vanModelParts;
        modelPathIdentifier = modelPath;
        jemData = jem;


        if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage(modelPathIdentifier + " = " + vanModelParts);

        for (EMFPartData sub:
                jemData.models) {


            ModelPart vanillaPart = sub.part == null? null : (vanModelParts.containsKey(sub.part) ? vanModelParts.get(sub.part).part() : null);
            String vanillaParentPartName = sub.part == null? null : (vanModelParts.containsKey(sub.part) ? vanModelParts.get(sub.part).parentName() : null);
            childrenMap.put(sub.id, new EMFModelPart(null, 0, sub, new ArrayList<EMFPartData>(), new float[]{}, vanillaPart, vanillaParentPartName, this));

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
//        if(value.vanillaParentPartName != null){
//            value.vanillaParentParts = new LinkedList<>();
//            if (childrenMap.containsKey(value.vanillaParentPartName)){
//                value.vanillaParentParts.add(childrenMap.get(value.vanillaParentPartName));
//            }else if (vanModelParts.containsKey(value.vanillaParentPartName)){
//                value.vanillaParentParts.addAll(iterateVanillaParentsList(vanModelParts.get(value.vanillaParentPartName),vanModelParts));
//            }
//            if(value.vanillaParentParts.size()>=1) value.vanillaParentPart = value.vanillaParentParts.get(0);
//            if (value.vanillaParentParts.isEmpty()) value.vanillaParentParts = null;
//
//        }
        if(vanModelParts.containsKey("root")){
            ModelPart root = vanModelParts.get("root").part();
            rootTransform = root.getDefaultTransform();
        }

        if(EMFData.getInstance().getConfig().printModelCreationInfoToLog)   EMFUtils.EMF_modMessage("start anim creation for "+modelPathIdentifier);
        preprocessAnimationStrings();
        if(EMFData.getInstance().getConfig().printModelCreationInfoToLog)  EMFUtils.EMF_modMessage("end anim creation for "+modelPathIdentifier);
        isAnimated = !animationKeyToCalculatorObject.isEmpty();

        //stillInInit = false;
    }
    public ModelTransform rootTransform = null;

    private LinkedList<ModelPart> iterateVanillaParentsList(VanillaModelPartOptiFineMappings.ModelAndParent entry, HashMap<String, VanillaModelPartOptiFineMappings.ModelAndParent> vanModelParts){
        LinkedList<ModelPart> list = new LinkedList<>();
        String parentName = entry.parentName();
        if(parentName != null) {
            if (childrenMap.containsKey(parentName)){
                list.add(childrenMap.get(parentName));
            }else if (vanModelParts.containsKey(parentName)){
                list.addAll(iterateVanillaParentsList(vanModelParts.get(parentName),vanModelParts));
            }
        }
        return list;
    }



    private void preprocessAnimationStrings(){
        ///animation processing/////////////
        Object2ObjectLinkedOpenHashMap<String, EMFModelPart> parts = getAllParts();

        //this section loads the parts in the alphabetical order of parts for processing
        //this seems to work but my only relevant data point is the FA 1.8 strider
        //this might actually be irrelevant and just coincidentally a fix
        /////////////////////
        SortedMap<String, EMFModelPart> parts2 = new TreeMap<>(Comparator.naturalOrder());
        parts2.putAll(parts);
        ///////////////////////if(modelPathIdentifier.contains("strider")) System.out.println(parts2);

        LinkedList<LinkedHashMap<String,String>>  allProperties = new LinkedList<>();
        for (EMFModelPart part :
                parts2.values()) {
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

                EMFDefaultModelVariable thisVariable = EMFDefaultModelVariable.get(modelVariable);
//                try {
//                    thisVariable = EMFDefaultModelVariable.valueOf(modelVariable);
//                }catch (IllegalArgumentException e){
//                    if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("custom variable located: ["+animKey+"].");
//                }
                //System.out.println(modelId +", "+animKey);
                EMFModelPart thisPart = parts.get(modelId);
                EMFAnimation thisCalculator = null;

//                if (thisPart != null){
//
//                    thisCalculator =
//                            new EMFAnimation(
//                                    this,
//                                    (EMFModelPart)thisPart,
//                                    thisVariable,
//                                    animKey,
//                                    animationExpression);
//                }else if(vanillaModelPartsById.containsKey(modelId)){
//                    thisCalculator = new EMFAnimation(
//                                    this,
//                                    vanillaModelPartsById.get(modelId).part(),
//                                    thisVariable,
//                                    animKey,
//                                    animationExpression);
//                }else{
//                    //not a custom model or vanilla must be a custom variable
//                    thisCalculator = new EMFAnimation(
//                                    this,
//                                    null,
//                                    null,
//                                    animKey,
//                                    animationExpression);
//                }

               // if(thisCalculator.isValid()){
                  //  System.out.println("found and added valid animation: "+animKey+"="+animationExpression);
                    animationKeyToCalculatorObject.put(animKey,thisCalculator);
                //}else{
                //    EMFUtils.EMF_modWarn("invalid animation = "+animKey+"="+ animationExpression);
                //}
                //here we have a set of animation objects that only need to be iterated over and run


            });

            //init expressions after all available variables have been loaded to the animation map
            // utilise a new map over the iteration to ensure non variable calls can consider value availability for mapping to vanilla parts
//            animationKeyToCalculatorObject.forEach((key,anim)->{
//                anim.initExpression();
//                alreadyCalculatedThisInitTickAnimations.put(key,anim);
//            });
            
        }
    }



//    //called when another animation wants to get "head.tx" or something similar
//    //checks if an existing animation has that key and uses it
//    // if it's not existing it tries to find the part, be it EMF or vanilla to copy its value
//    //there is an alternate method that is more optimized just for animation variables e.g.g "var.asdf"
//    public float getAnimationResultOfKey(
//            EMF_ModelPart parentForCheck,
//            String key,
//            Entity entity) {
//        //if(stillInInit) return 1;
//
//        //if(key.contains("arm")) System.out.println(vanillaModelPartsById.keySet());;
//        if (!alreadyCalculatedThisTickAnimations.containsKey(key)) {
//        //if (!animationKeyToCalculatorObject.containsKey(key)) {
//            String partName = key.split("\\.")[0];
//
//            if (vanillaModelPartsById.containsKey(partName)) {
//
//                AnimationModelDefaultVariable variableToGet;
//                try {
//                    variableToGet = AnimationModelDefaultVariable.valueOf(key.split("\\.")[1]);
//                    //attempt to cache an interpolating animation variable pointing to the vanilla part so that vanilla values can match EMF interpolation when needed
//                    //////////////////
////                    if(!variableToGet.isRotation &&
////                            !animationKeyToCalculatorObject.containsKey(key) &&
////                            !needToBeAddedToAnimationMap.containsKey(key)){
////                        //this means we have not added a custom interpolating variable yet, so add one
////                        AnimationCalculation interpolatingVanillaGetter =  new AnimationCalculation(
////                                this,
////                                getAllParts().get(partName),//null if no mapping is correct
////                                variableToGet,
////                                key,
////                                key);
////                        //add to start of anims for next loop to be able to use interpolating value
////                        needToBeAddedToAnimationMap.putAndMoveToFirst(key,interpolatingVanillaGetter);
////                        //now that it has been put this section will not run again for the same vanilla value
////                    }
//                    //////////////////
//
//                    //if (key.contains("arm")) System.out.println(key + "=" + value);
//                    return variableToGet.getFromVanillaModel(vanillaModelPartsById.get(partName).part());
//                } catch (IllegalArgumentException e) {
//                    EMFUtils.EMF_modWarn("no animation expression part variable value found for: " + key + " in " + modelPathIdentifier);
//                    return 0;
//                }
//
//            } else if (getAllParts().containsKey(partName)) {
//                AnimationModelDefaultVariable variableToGet;
//                EMF_ModelPart otherPart = getAllParts().get(partName);
//                try {
//                    variableToGet = AnimationModelDefaultVariable.valueOf(key.split("\\.")[1]);
//                    if (parentForCheck != null && parentForCheck.equals(otherPart.parent)) {
//                        return variableToGet.getFromEMFModel(otherPart, true);
//                    } else {
//                        return variableToGet.getFromEMFModel(otherPart);
//                    }
//
//                } catch (IllegalArgumentException e) {
//                    EMFUtils.EMF_modWarn("no animation expression part variable value found for: " + key + " in " + modelPathIdentifier);
//                    return 0;
//                }
//
//            } else {
//                EMFUtils.EMF_modWarn("no animation expression value found for: " + key + " in " + modelPathIdentifier);
//                //System.out.println(animationKeyToCalculatorObject.keySet());
//                //System.out.println(vanillaModelPartsById.keySet());
//                return 0;
//            }
//
//        }
//        //return animationKeyToCalculatorObject.get(key).getLastResultOnly((LivingEntity) entity);
//        return alreadyCalculatedThisTickAnimations.get(key).getLastResultOnly((LivingEntity) entity);
//    }
//    //same as above method but optimized for variable loading as they only exist in animations
//    //also as we do not want an unwanted error message unlike the other method
//    public float getAnimationResultOfKeyOptimiseForVariable(
//            String key,
//            Entity entity) {
//        if (animationKeyToCalculatorObject.containsKey(key)) {
//            return animationKeyToCalculatorObject.get(key).getLastResultOnly((LivingEntity) entity);
//        }
//        if(EMFData.getInstance().getConfig().printAllMaths) EMFUtils.EMF_modWarn("no animation variable found for: " + key + " in " + modelPathIdentifier);
//        return 0;
//    }



    //called when another animation wants to get "head.tx" or something similar
    //checks if an existing animation has that key and uses it
    // if it's not existing it tries to find the part, be it EMF or vanilla to copy its value
    //there is an alternate method that is more optimized just for animation variables e.g.g "var.asdf"
    public MathValue.AnimationValueSupplier getAnimationResultOfKeyAsSupplier(
            EMFModelPart parentForCheck,
            String key) {
        return null;
//        //if(stillInInit) return 1;
//
//        //if(key.contains("arm")) System.out.println(vanillaModelPartsById.keySet());;
//        if (!alreadyCalculatedThisInitTickAnimations.containsKey(key)) {
//            //if (!animationKeyToCalculatorObject.containsKey(key)) {
//            String partName = key.split("\\.")[0];
//
//            if (vanillaModelPartsById.containsKey(partName)) {
//
//                EMFDefaultModelVariable variableToGet;
//                try {
//                    variableToGet = EMFDefaultModelVariable.valueOf(key.split("\\.")[1]);
//                    //attempt to cache an interpolating animation variable pointing to the vanilla part so that vanilla values can match EMF interpolation when needed
//                    //////////////////
////                    if(!variableToGet.isRotation &&
////                            !animationKeyToCalculatorObject.containsKey(key) &&
////                            !needToBeAddedToAnimationMap.containsKey(key)){
////                        //this means we have not added a custom interpolating variable yet, so add one
////                        AnimationCalculation interpolatingVanillaGetter =  new AnimationCalculation(
////                                this,
////                                getAllParts().get(partName),//null if no mapping is correct
////                                variableToGet,
////                                key,
////                                key);
////                        //add to start of anims for next loop to be able to use interpolating value
////                        needToBeAddedToAnimationMap.putAndMoveToFirst(key,interpolatingVanillaGetter);
////                        //now that it has been put this section will not run again for the same vanilla value
////                    }
//                    //////////////////
//
//                    //if (key.contains("arm")) System.out.println(key + "=" + value);
//
//                    ModelPart part =vanillaModelPartsById.get(partName).part();
//                    return (entity2) -> variableToGet.getFromVanillaModel(part);
//
//                } catch (IllegalArgumentException e) {
//                    EMFUtils.EMF_modWarn("no animation expression part variable value found for: " + key + " in " + modelPathIdentifier);
//                    return (ent)->0;
//                }
//
//            } else if (getAllParts().containsKey(partName)) {
//                EMFDefaultModelVariable variableToGet;
//                EMFModelPart otherPart = getAllParts().get(partName);
//                try {
//                    variableToGet = EMFDefaultModelVariable.valueOf(key.split("\\.")[1]);
//                    if (parentForCheck != null && parentForCheck.equals(otherPart.parent)) {
//                        return (ent)-> variableToGet.getFromEMFModel(otherPart, true);
//                    } else {
//                        return (ent)-> variableToGet.getFromEMFModel(otherPart);
//                    }
//
//                } catch (IllegalArgumentException e) {
//                    EMFUtils.EMF_modWarn("no animation expression part variable value found for: " + key + " in " + modelPathIdentifier);
//                    return (ent)->0;
//                }
//
//            } else {
//                EMFUtils.EMF_modWarn("no animation expression value found for: " + key + " in " + modelPathIdentifier);
//                //System.out.println(animationKeyToCalculatorObject.keySet());
//                //System.out.println(vanillaModelPartsById.keySet());
//                return (ent)-> 0;
//            }
//
//        }
//        EMFAnimation variable =alreadyCalculatedThisInitTickAnimations.get(key);
//        return (entity2) -> variable.getLastResultOnly((LivingEntity) entity2);
        //return alreadyCalculatedThisTickAnimations.get(key).getLastResultOnly((LivingEntity) entity);
    }
    //same as above method but optimized for variable loading as they only exist in animations map and can be loaded out of execution order
    public MathValue.AnimationValueSupplier getAnimationResultOfKeyOptimiseForVariableAsSupplier(String key){
        if (animationKeyToCalculatorObject.containsKey(key)) {
            EMFAnimation variable =animationKeyToCalculatorObject.get(key);
            return (entity2) -> variable.getLastResultOnly((LivingEntity) entity2);
        }
        if(EMFData.getInstance().getConfig().printAllMaths) EMFUtils.EMF_modWarn("no animation variable found for: " + key + " in " + modelPathIdentifier);
        return (entity3) -> 0;
    }


    @Override
    public EMFGenericCustomEntityModel<T> getThisEMFModel() {
        return this;
    }

    @Override
    public boolean doesThisModelNeedToBeReset() {
        return true;
    }


    @Override
    public void render( MatrixStack herematrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
//todo below code segment might assist with some modded entity renderers but honestly not a concern atm, as i want to know of any such incompatibilities
//        if(currentEntity!= null){
//            if( currentEntity.world == null){
//                //attempt to reroute some modded entity rendering
//                vanillaModel.render(herematrices, vertices, light, overlay, red, green, blue, alpha);
//                return;
//            }else if(currentEntity.getPos().equals(new Vec3d(0,0,0))){
//                vanillaModel.render(herematrices, vertices, light, overlay, red, green, blue, alpha);
//                return;
//            }
//        }


        switch (EMFData.getInstance().getConfig().displayVanillaModelHologram){
            case Position_normal -> {
                vanillaModel.render(herematrices, vertices, LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay, red/2, green, blue/2, alpha/2);
            }
            case Positon_offset -> {
                herematrices.push();
                herematrices.translate(1.5, 0, 0);
                vanillaModel.render(herematrices, vertices, LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay, red/2, green, blue/2, alpha/2);
                herematrices.pop();
            }
            default -> {}
        }

        herematrices.push();

        if(currentEntity != null && alterAnimationProgress() < EMFData.getInstance().getConfig().spawnAnimTime && EMFData.getInstance().getConfig().spawnAnim != EMFConfig.SpawnAnimation.None){
            float delta = MathHelper.clamp( alterAnimationProgress() / EMFData.getInstance().getConfig().spawnAnimTime,0,1f);
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

        //todo possibly no longer needed
//        if(this.texture != null && this.currentVertexProvider != null && this.vanillaModel != null){
//            vertices = this.currentVertexProvider.getBuffer(this.vanillaModel.getLayer(this.texture));
//        }

        if (this.child && vanillaModel instanceof AnimalModel animal) {
            float f = 1.0F / ((AnimalModelAccessor)animal).getInvertedChildBodyScale();
            herematrices.scale(f, f, f);
            herematrices.translate(0.0F, ((AnimalModelAccessor)animal).getChildBodyYOffset() / 16.0F, 0.0F);
        }
        for (String key:
                childrenMap.keySet()) {
            herematrices.push();
            //herematrices.translate(0,24/16f,0);
            EMFModelPart emfPart = childrenMap.get(key);
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


    private Object2ObjectLinkedOpenHashMap<String, EMFModelPart> getAllPartsCachedResult = null;
    public Object2ObjectLinkedOpenHashMap<String, EMFModelPart> getAllParts(){
        if(getAllPartsCachedResult == null) {
            Object2ObjectLinkedOpenHashMap<String, EMFModelPart> list = new Object2ObjectLinkedOpenHashMap<String, EMFModelPart>();
            for (EMFModelPart part :
                    childrenMap.values()) {
                list.put(part.selfModelData.id, part);
                list.putAll(part.getAllParts());
            }
            getAllPartsCachedResult = list;
        }
        //System.out.println(getAllPartsCachedResult.keySet());
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

//    public float currentAnimationDeltaForThisTick = 0;
//    public boolean calculateForThisAnimationTick = false;

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){//, VanillaMappings.VanillaMapper vanillaPartSupplier,EntityModel<T> vanillaModel){

        this.animationProgress = animationProgress;
//        calculateForThisAnimationTick = false;

        if(entity != null) {
            currentEntity = entity;
         //   UUID id = entity.getUuid();
//            if (EMFData.getInstance().getConfig().animationRate == EMFConfig.AnimationRatePerSecondMode.Every_frame && EMFData.getInstance().getConfig().animationRateDistanceDropOffRate == 0) {
//                calculateForThisAnimationTick = true;
//                currentAnimationDeltaForThisTick = 0;
//            } else {
//                float interpolationLength = prevInterp.getFloat(id);
//
//                float thisTickValue = getNextPrevResultTickValue();
//                float prevTickValue = prevResultsTick.getFloat(id);
//
//                if (thisTickValue >= prevTickValue + interpolationLength) {
//                    //vary interpolation length by distance from client
//                    if (MinecraftClient.getInstance().player != null) {
//                        //val;
//                        //if(entity == MinecraftClient.getInstance().player)
//                        //    val=0;
//                        //else
//                        float val= EMFData.getInstance().getConfig().getInterpolationModifiedByDistance((entity.distanceTo(MinecraftClient.getInstance().player) - EMFData.getInstance().getConfig().animationRateMinimumDistanceDropOff));
//                        prevInterp.put(id, EMFData.getInstance().getConfig().getAnimationRateFromFPS(val));
//                    } else {
//                        prevInterp.put(id, EMFData.getInstance().getConfig().getAnimationRateFromFPS(0));
//                    }
//
//                    prevResultsTick.put(id, thisTickValue);//entity.age + tickDelta)
//                    calculateForThisAnimationTick = true;
//
//                    animationGetters.entity = entity;
//                    animationGetters.limbAngle = limbAngle;
//                    animationGetters.limbDistance = limbDistance;
//                    animationGetters.animationProgress = animationProgress;
//                    animationGetters.headYaw = headYaw;
//                    animationGetters.headPitch = headPitch;
//                    animationGetters.tickDelta = tickDelta;
//                   // animationGetters.riding = riding;
//                   // animationGetters.child = child;
//
//                    if(entity == MinecraftClient.getInstance().player && EMFData.getInstance().clientGetter == null)
//                        EMFData.getInstance().clientGetter = animationGetters;
//
//                    //currentAnimationDeltaForThisTick = 0f;
//                } else if (thisTickValue < prevTickValue - 100 - interpolationLength) {
//                    //this is required as animation progress resets with the entity entering render distance
//                    //todo possibly use world time ticks instead ??
//                    prevResultsTick.put(id, -100);
//                    //interpolate easier as will calculate next tick
//                    calculateForThisAnimationTick = false;
//                    // currentAnimationDeltaForThisTick =  ((animationProgress - prevResultsTick.getFloat(id) ) / interpolationLength);
//                } else {
//                    //todo wolves and chickens here for some reason
//
////                    if(new Random().nextInt(100)==1 && currentEntity != null)
////                        System.out.println(interpolationLength+", "+prevInterp.getFloat(id)+", "+animationProgress+", "+prevResultsTick.getFloat(id));
////                    0.047732696, 0.047732696, 0.62831855, 0.62831855
////
////                    0.62831855>=0.676051246
//
//                    //interpolate
//                    calculateForThisAnimationTick = false;
//                    //currentAnimationDeltaForThisTick =  ((animationProgress - prevResultsTick.getFloat(id) ) / interpolationLength);
//                }
//                currentAnimationDeltaForThisTick = (float) ((thisTickValue - prevTickValue) / interpolationLength);
//            }



            vanillaModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
                    EMFAnimationVariableSuppliers.entity = entity;
                    EMFAnimationVariableSuppliers.limbAngle = limbAngle;
                    EMFAnimationVariableSuppliers.limbDistance = limbDistance;
                    EMFAnimationVariableSuppliers.animationProgress = alterAnimationProgress();
                    EMFAnimationVariableSuppliers.headYaw = headYaw;
                    EMFAnimationVariableSuppliers.headPitch = headPitch;
                    EMFAnimationVariableSuppliers.tickDelta = tickDelta;
            //alreadyCalculatedThisTickAnimations.clear();
            //if(entity instanceof BlazeEntity && entity.getRandom().nextInt(50) == 4) System.out.println(animationKeyToCalculatorObject.keySet());
            animationKeyToCalculatorObject.forEach((key,animationCalculation)->{

                animationCalculation.calculateAndSet(entity);


                //alreadyCalculatedThisTickAnimations.put(key,animationCalculation);
            });
//            if (!needToBeAddedToAnimationMap.isEmpty()){
//                needToBeAddedToAnimationMap.forEach(animationKeyToCalculatorObject::putAndMoveToFirst);
//            }
        }
        //that's it????
    }

    public boolean calculateVariables = true;
    public EMFAnimationVariableSuppliers EMFAnimationVariableSuppliers = new EMFAnimationVariableSuppliers();

    private float alterAnimationProgress(){
        if(currentEntity == null)
            return animationProgress;

       // if(new Random().nextInt(100)==1 && currentEntity.world != null) System.out.println((System.currentTimeMillis()/50d+tickDelta));
        return currentEntity.age + tickDelta ;//(System.currentTimeMillis()/50d+ tickDelta);
    }

    T currentEntity = null;

    Object2LongOpenHashMap<UUID> prevCalcTick = new Object2LongOpenHashMap<>(){{
        defaultReturnValue(-100);
    }};
//    Object2FloatOpenHashMap<UUID> prevInterp = new Object2FloatOpenHashMap<>(){{
//        defaultReturnValue(EMFData.getInstance().getConfig().getAnimationRateFromFPS(0));
//    }};

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


    public void copyStateToEMF(EMFGenericCustomEntityModel<?> copy) {
        try {
            super.copyStateTo((EntityModel<T>) copy);
        }catch (Exception ignored){}
       // if(copy instanceof EMF_CustomModel<?> emf){
        Object2ObjectLinkedOpenHashMap<String, ? extends EMFModelPart> copyParts = copy.getAllParts();
        Object2ObjectLinkedOpenHashMap<String, ? extends EMFModelPart> thisParts = this.getAllParts();
        //System.out.println(copyParts);
        //System.out.println(thisParts);
            for (Map.Entry<String, ? extends EMFModelPart> entry:
                 copyParts.entrySet()) {

                if(thisParts.containsKey(entry.getKey())){
                    EMFModelPart thisPart = thisParts.get(entry.getKey());
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

    public EMFGenericCustomEntityModel<T> getArmourModel(boolean getInner){
        if(getInner){
            if(innerArmor== null){
                String path = modelPathIdentifier.replace(".jem", "_armor_inner.jem");
                EMFJemData jem = EMFUtils.EMF_readJemData(path);
                if(jem != null) {
                    innerArmor = new EMFGenericCustomEntityModel<>(jem, path, this::supplierCopy, vanillaModel);
                    innerArmor.isAnimated = this.isAnimated;
                }else{
                    String path2 = "optifine/cem/biped_armor_inner.jem";
                    EMFJemData jem2 = EMFUtils.EMF_readJemData(path2);
                    if(jem2 != null) {
                        innerArmor = new EMFGenericCustomEntityModel<>(jem2, path2, this::supplierCopy, vanillaModel);
                        innerArmor.isAnimated = this.isAnimated;
                    }
                }
            }
            return innerArmor;
        }else{
            if(outerArmor== null){

                String path = modelPathIdentifier.replace(".jem", "_armor_outer.jem");
                EMFJemData jem = EMFUtils.EMF_readJemData(path);
                if(jem != null) {
                    outerArmor = new EMFGenericCustomEntityModel<>(jem, path, this::supplierCopy, vanillaModel);
                    outerArmor.isAnimated = this.isAnimated;
                }else{
                    String path2 = "optifine/cem/biped_armor_outer.jem";
                    EMFJemData jem2 = EMFUtils.EMF_readJemData(path2);
                    if(jem2 != null) {
                        outerArmor = new EMFGenericCustomEntityModel<>(jem2, path2, this::supplierCopy, vanillaModel);
                        outerArmor.isAnimated = this.isAnimated;
                    }
                }
            }
            return outerArmor;
        }
    }


    public HashMap<String, VanillaModelPartOptiFineMappings.ModelAndParent> supplierCopy(EntityModel<?> entityModel) {
        return vanillaModelPartsById;
    }

    private EMFGenericCustomEntityModel<T> innerArmor = null;

    private EMFGenericCustomEntityModel<T> outerArmor = null;



    public void clearAllFakePartChildrenData(){
        for (EMFModelPart part:
             getAllParts().values()) {
            part.clearVanillaFakeChildren();
        }
    }

}
