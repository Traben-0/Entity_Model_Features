package traben.entity_model_features.models;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.*;
import org.joml.Quaternionf;
import traben.entity_model_features.models.jemJsonObjects.EMF_JemData;
import traben.entity_model_features.models.jemJsonObjects.EMF_ModelData;

import java.util.*;
import java.util.function.Supplier;

@Environment(value= EnvType.CLIENT)
public class EMF_CustomModel<T extends Entity> extends EntityModel<T>  {

    private final EMF_JemData jemData;
    private final Reference2ObjectOpenHashMap<String, EMF_CustomModelPart<T>> childrenMap = new Reference2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String,AnimationCalculation> animationKeyToCalculatorObject = new Object2ObjectOpenHashMap<>();

    private HashMap<String,ModelPart> vanillaModelPartsById;

   // private final Properties animationPropertiesOfAll;
    final String modelPathIdentifier;

    public EMF_CustomModel(EMF_JemData jem, String modelPath,HashMap<String,ModelPart> vanModelParts){
        vanillaModelPartsById = vanModelParts;
        modelPathIdentifier = modelPath;
        jemData = jem;


        for (EMF_ModelData sub:
                jemData.models) {
            ModelPart vanillaPart = sub.part == null? null : vanModelParts.getOrDefault(sub.part, null);
            childrenMap.put(sub.id,new EMF_CustomModelPart<T>(0,sub,new ArrayList<EMF_ModelData>(),new float[]{},vanillaPart));

        }
      //  System.out.println("start anim creation for "+modelPathIdentifier);
        preprocessAnimationStrings();
       // System.out.println("end anim creation for "+modelPathIdentifier);
    }




    private void preprocessAnimationStrings(){
        ///animation processing/////////////

        Object2ReferenceOpenHashMap<String, EMF_CustomModelPart<T>> parts = getAllParts();
        ObjectOpenHashSet<Properties> allProperties = new ObjectOpenHashSet<>() {
        };
        for (EMF_CustomModelPart<T> part :
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
                EMF_CustomModelPart<T> thisPart = parts.get(modelId);
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

    public double getAnimationResultOfKey(String key,
                                          Supplier<Entity> entitySupplier,
                                          Supplier<Float> limbAngleSupplier,
                                          Supplier<Float> limbDistanceSupplier,
                                          Supplier<Float> animationProgressSupplier,
                                          Supplier<Float> headYawSupplier,
                                          Supplier<Float> headPitchSupplier,
                                          Supplier<Float> tickDeltaSupplier){

        LivingEntity entity = (LivingEntity) entitySupplier.get();
        float limbAngle = limbAngleSupplier.get();
        float limbDistance = limbDistanceSupplier.get();
        float animationProgress = animationProgressSupplier.get();
        float headYaw = headYawSupplier.get();
        float headPitch = headPitchSupplier.get();
        float tickDelta = tickDeltaSupplier.get();



        //if(key.equals("head.ty")) return -0.5;
        if(!animationKeyToCalculatorObject.containsKey(key)) {
            String partName = key.split("\\.")[0];
            if(getAllParts().containsKey(partName)){
                AnimationCalculation.AnimVar variableToGet;
                try {
                    variableToGet = AnimationCalculation.AnimVar.valueOf(key.split("\\.")[1]);
                    return variableToGet.getFromEMFModel(getAllParts().get(partName));
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

        return animationKeyToCalculatorObject.get(key).getResultOnly( entity,  limbAngle,  limbDistance,  animationProgress,  headYaw,  headPitch,tickDelta);
    }





    @Override
    public void render(MatrixStack herematrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

    }

    public void render( HashMap<String,ModelPart> vanillaParts, MatrixStack herematrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        for (String key:
                childrenMap.keySet()) {
            herematrices.push();
            //herematrices.translate(0,24/16f,0);
            EMF_CustomModelPart<T> emfPart = childrenMap.get(key);
//            if (emfPart.selfModelData.part != null
//                   // && emfPart.selfModelData.part.equals("body")
//                    && vanillaParts.containsKey(emfPart.selfModelData.part)){
//                ModelPart vanilla = vanillaParts.get(emfPart.selfModelData.part);
//                if(vanilla != null){
//                    ModelTransform defaults =vanilla.getDefaultTransform();
//                    float roll = defaults.roll;
//                    float yaw = defaults.yaw;
//                    float pitch = defaults.pitch;
//                    float pivotX = defaults.pivotX;
//                    float pivotY = defaults.pivotY;
//                    float pivotZ = defaults.pivotZ;
//
//                    herematrices.translate(pivotX / 16.0F, pivotY / 16.0F, pivotZ / 16.0F);
//                    herematrices.multiply((new Quaternionf()).rotationZYX(roll, yaw, pitch));
//                }else{
//                    System.out.println("vanilla part ["+emfPart.selfModelData.part+"] was null in vanilla parts map");
//                }
//
//
//            }
            emfPart.render(0,herematrices,vertices,light,overlay,red,green,blue,alpha);
           // childrenMap.get(key).render(  herematrices,  vertices,  light,  overlay);
            herematrices.pop();
        }
    }


    public Object2ReferenceOpenHashMap<String,EMF_CustomModelPart<T>> getAllParts(){
        Object2ReferenceOpenHashMap<String,EMF_CustomModelPart<T>> list = new Object2ReferenceOpenHashMap<String,EMF_CustomModelPart<T>>();
        for (EMF_CustomModelPart<T> part :
                childrenMap.values()) {
            list.put(part.selfModelData.id,part);
            list.putAll(part.getAllParts());
        }


        return list;
    }

    @Override
    public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
       // super.animateModel(entity, limbAngle, limbDistance, tickDelta);
       // this.tickDelta = tickDelta;
    }
//    float tickDelta = Float.NaN;


    public void setAnglesEMF(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch,float tickDelta,HashMap<String,ModelPart> vanillaPartList) {
        vanillaModelPartsById = vanillaPartList;
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
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}
