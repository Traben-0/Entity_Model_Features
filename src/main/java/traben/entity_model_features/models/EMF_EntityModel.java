package traben.entity_model_features.models;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import traben.entity_model_features.mixin.accessor.AnimalModelAccessor;
import traben.entity_model_features.models.jemJsonObjects.EMF_JemData;
import traben.entity_model_features.models.jemJsonObjects.EMF_ModelData;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.vanilla_part_mapping.VanillaMappings;

import java.util.*;

@Environment(value= EnvType.CLIENT)
public class EMF_EntityModel<T extends LivingEntity> extends EntityModel<T> implements ModelWithHat, ModelWithWaterPatch, ModelWithArms, ModelWithHead, EMFCustomModel<T> {

    private final EMF_JemData jemData;
    public final Object2ObjectOpenHashMap<String, EMF_ModelPart> childrenMap = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String,AnimationCalculation> animationKeyToCalculatorObject = new Object2ObjectOpenHashMap<>();



    private HashMap<String,ModelPart> vanillaModelPartsById;

   // private final Properties animationPropertiesOfAll;
    final String modelPathIdentifier;
    public final EntityModel<T> vanillaModel;

    public VertexConsumerProvider currentVertexProvider = null;

    public boolean isAnimated = false;

    public EMF_EntityModel(EMF_JemData jem, String modelPath, VanillaMappings.VanillaMapper vanillaPartSupplier, EntityModel<T> vanillaModel){
        HashMap<String,ModelPart> vanModelParts = vanillaPartSupplier.getVanillaModelPartsMapFromModel(vanillaModel);
        this.vanillaModel = vanillaModel;
        vanillaModelPartsById = vanModelParts;
        modelPathIdentifier = modelPath;
        jemData = jem;

        System.out.println(modelPathIdentifier + " = " + vanModelParts);

        for (EMF_ModelData sub:
                jemData.models) {
            ModelPart vanillaPart = sub.part == null? null : vanModelParts.getOrDefault(sub.part, null);
            childrenMap.put(sub.id, new EMF_ModelPart(null, 0, sub, new ArrayList<EMF_ModelData>(), new float[]{}, vanillaPart, this));

        }
      //  System.out.println("start anim creation for "+modelPathIdentifier);
        preprocessAnimationStrings();
       // System.out.println("end anim creation for "+modelPathIdentifier);
        isAnimated = !animationKeyToCalculatorObject.isEmpty();
    }




    private void preprocessAnimationStrings(){
        ///animation processing/////////////

        Object2ObjectOpenHashMap<String, EMF_ModelPart> parts = getAllParts();
        ObjectOpenHashSet<Properties> allProperties = new ObjectOpenHashSet<>() {
        };
        for (EMF_ModelPart part :
                parts.values()) {
            if (part.selfModelData.animations.length != 0) {
                //todo replace 'this' to represent actual model part
                allProperties.addAll(Arrays.asList(part.selfModelData.animations));
            }
        }
        Properties combinedProperties = new Properties();
        for (Properties properties :
                allProperties) {
            if (!properties.isEmpty()) {
                combinedProperties.putAll(properties);
            }
        }
        //////////////
        if(!combinedProperties.isEmpty()) {

            for (Object modelVariableObject :
                    combinedProperties.keySet()) {

              //  System.out.println("processing animation:" +modelVariableObject.toString()+" in "+this.modelPathIdentifier);
                String animKey = modelVariableObject.toString();
                String modelId = animKey.split("\\.")[0];
                String modelVariable = animKey.split("\\.")[1];
                String animationExpression = combinedProperties.getProperty((String) modelVariableObject);

                //insert constants
               ///// animationExpression = animationExpression.replaceAll("(?=[^a-zA-Z_])pi(?=[^a-zA-Z_])", String.valueOf(Math.PI));
                //animationExpression = animationExpression.replaceAll("true", "1");
                //animationExpression = animationExpression.replaceAll("false", "0");


                AnimationCalculation.AnimVar thisVariable = null;
                try {
                    thisVariable = AnimationCalculation.AnimVar.valueOf(modelVariable);
                }catch (IllegalArgumentException e){
                    //todo custom variable
                    System.out.println("UNKOWN VARIABLE VALUE"+modelVariable +" in "+animKey+" = "+ e);
                }
                EMF_ModelPart thisPart = parts.get(modelId);
                AnimationCalculation thisCalculator = null;

                if (thisPart != null){
                    thisCalculator = new AnimationCalculation(
                            this,
                            thisPart,
                            thisVariable,
                            animKey,
                            animationExpression);
                }else if(vanillaModelPartsById.containsKey(modelId)){
                    thisCalculator = new AnimationCalculation(
                            this,
                            vanillaModelPartsById.get(modelId),
                            thisVariable,
                            animKey,
                            animationExpression);
                }else{
                    //not a custom model or vanilla must be a custom variable
                    thisCalculator = new AnimationCalculation(
                            this,
                            null,
                            thisVariable,
                            animKey,
                            animationExpression);
                }

                if(thisCalculator.isValid()){
                  //  System.out.println("found and added valid animation: "+animKey+"="+animationExpression);
                    animationKeyToCalculatorObject.put(animKey,thisCalculator);
                }else{
                    System.out.println("invalid animation = "+animKey+"="+ animationExpression);
                }
                //here we have a set of animation objects that only need to be iterated over and run


            }
        }
    }

    public double getAnimationResultOfKey(
            EMF_ModelPart parentForCheck,
            String key,
            Entity entity,
            float limbAngle,
            float limbDistance,
            float animationProgress,
            float headYaw,
            float headPitch,
            float tickDelta){
//    },
//                                          Supplier<Entity> entitySupplier,
//                                          Supplier<Float> limbAngleSupplier,
//                                          Supplier<Float> limbDistanceSupplier,
//                                          Supplier<Float> animationProgressSupplier,
//                                          Supplier<Float> headYawSupplier,
//                                          Supplier<Float> headPitchSupplier,
//                                          Supplier<Float> tickDeltaSupplier){

//        LivingEntity entity = (LivingEntity) entitySupplier.get();
//        float limbAngle = limbAngleSupplier.get();
//        float limbDistance = limbDistanceSupplier.get();
//        float animationProgress = animationProgressSupplier.get();
//        float headYaw = headYawSupplier.get();
//        float headPitch = headPitchSupplier.get();
//        float tickDelta = tickDeltaSupplier.get();



        //if(key.equals("head.ty")) return -0.5;
        if(!animationKeyToCalculatorObject.containsKey(key)) {
            String partName = key.split("\\.")[0];
            if(getAllParts().containsKey(partName)){
                AnimationCalculation.AnimVar variableToGet;
                EMF_ModelPart otherPart = getAllParts().get(partName);
                try {
                    variableToGet = AnimationCalculation.AnimVar.valueOf(key.split("\\.")[1]);
                    if(parentForCheck != null && parentForCheck.equals(otherPart.parent)){
                        return variableToGet.getFromEMFModel(otherPart,true);
                    }else{
                        return variableToGet.getFromEMFModel(otherPart);
                    }

                }catch(IllegalArgumentException e){
                    System.out.println("no animation expression part variable value found for: "+key);
                    return 0;
                }
            }else {
                if(vanillaModelPartsById.containsKey(partName)){
                    AnimationCalculation.AnimVar variableToGet;
                    try {
                        variableToGet = AnimationCalculation.AnimVar.valueOf(key.split("\\.")[1]);
                        return variableToGet.getFromVanillaModel(vanillaModelPartsById.get(partName));
                    }catch(IllegalArgumentException e){
                        System.out.println("no animation expression part variable value found for: "+key);
                        return 0;
                    }
                }else {
                    System.out.println("no animation expression value found for: " + key);
                    System.out.println(animationKeyToCalculatorObject.keySet());
                    System.out.println(vanillaModelPartsById.keySet());
                    return 0;
                }
            }
        }

        return animationKeyToCalculatorObject.get(key).getResultOnly((LivingEntity) entity,  limbAngle,  limbDistance,  animationProgress,  headYaw,  headPitch,tickDelta);
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
        herematrices.push();
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


    public Object2ObjectOpenHashMap<String, EMF_ModelPart> getAllParts(){
        Object2ObjectOpenHashMap<String, EMF_ModelPart> list = new Object2ObjectOpenHashMap<String, EMF_ModelPart>();
        for (EMF_ModelPart part :
                childrenMap.values()) {
            list.put(part.selfModelData.id,part);
            list.putAll(part.getAllParts());
        }


        return list;
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
    float tickDelta = Float.NaN;
    public boolean sneaking = false;

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){//, VanillaMappings.VanillaMapper vanillaPartSupplier,EntityModel<T> vanillaModel){
        vanillaModel.setAngles( entity,limbAngle,limbDistance,animationProgress,headYaw,headPitch);
        //todo check if needs running supplier on each render or only once at init
       // vanillaModelPartsById = vanillaPartSupplier.getVanillaModelPartsMapFromModel(vanillaModel);
        //process all animation states for all parts
//        System.out.println("hpy="+headPitch+", "+headYaw);
//        headPitch = (float) Math.toDegrees(headPitch);
//        headYaw = (float) Math.toDegrees(headYaw);
//        System.out.println("hpy="+headPitch+", "+headYaw);
        for (AnimationCalculation calculator:
        animationKeyToCalculatorObject.values()) {
//            if (entity instanceof ZombieEntity
//                    || entity instanceof SheepEntity
//                    || entity instanceof VillagerEntity
//                    || entity instanceof CreeperEntity
//                    || entity instanceof PigEntity
//                    || entity instanceof IronGolemEntity
//                    || entity instanceof ChickenEntity) {

//                if (vanillaPartList.containsKey("head")) {
//                    ModelPart head = vanillaPartList.get("head");
//                    headPitch = head.pitch;
//                    headYaw = head.yaw;
//                }


                calculator.calculateAndSet((LivingEntity) entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, tickDelta);
           // }
        }
        //that's it????
    }


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
        for (EMF_ModelPart part:
                this.childrenMap.values()) {
            part.visible = setTo;
        }
    }
    public void setVisibleToplvl(Set<String> partNames, boolean setTo){
        //Map<String,EMF_CustomModelPart<T>> parts = getAllParts();

        for (String name:
                partNames) {
            //System.out.println("matcching:"+name+", "+childrenMap.keySet());
            if(childrenMap.containsKey(name)){
                //System.out.println("match");
                EMF_ModelPart part = childrenMap.get(name);

                if(part != null)
                    part.visible = setTo;
            }
        }
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


    public HashMap<String, ModelPart> supplierCopy(EntityModel<?> entityModel) {
        return vanillaModelPartsById;
    }

    private EMF_EntityModel<T> innerArmor = null;

    private EMF_EntityModel<T> outerArmor = null;

    public interface ModelRenderLayerSupplier{
        RenderLayer getLayer(Identifier id);
    }

}
