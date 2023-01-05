package traben.entity_model_features.models;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.SheepEntity;
import traben.entity_model_features.models.jemJsonObjects.EMF_JemData;
import traben.entity_model_features.models.jemJsonObjects.EMF_ModelData;

import java.util.*;

@Environment(value= EnvType.CLIENT)
public class EMF_CustomModel<T extends Entity> extends EntityModel<T>  {

    private final EMF_JemData jemData;
    private final Object2ObjectOpenHashMap<String, EMF_CustomModelPart<T>> childrenMap = new Object2ObjectOpenHashMap<>();

    private final Object2ObjectOpenHashMap<String,AnimationCalculation> animationKeyToCalculatorObject = new Object2ObjectOpenHashMap<>();

   // private final Properties animationPropertiesOfAll;
    final String modelPathIdentifier;

    public EMF_CustomModel(EMF_JemData jem, String modelPath){
        modelPathIdentifier = modelPath;
        jemData = jem;
        for (EMF_ModelData sub:
                jemData.models) {
            childrenMap.put(sub.id,new EMF_CustomModelPart<T>(0,sub,new ArrayList<EMF_ModelData>()));
        }
        System.out.println("start anim creation for "+modelPathIdentifier);
        preprocessAnimationStrings();
        System.out.println("end anim creation for "+modelPathIdentifier);
    }


    private void preprocessAnimationStrings(){
        ///animation processing/////////////
        ObjectOpenHashSet<Properties> allProperties = new ObjectOpenHashSet<>() {
        };
        for (EMF_CustomModelPart<T> part :
                childrenMap.values()) {
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
                }
                EMF_CustomModelPart<T> thisPart = childrenMap.get(modelId);
                if (thisPart == null){
                    //todo must be vanilla model or stupid custom variable figure out
                }


                AnimationCalculation thisCalculator = new AnimationCalculation(
                        (EMF_CustomModel<LivingEntity>) this,
                        (EMF_CustomModelPart<LivingEntity>) thisPart,
                        thisVariable,
                        animKey,
                        animationExpression);
                if(thisCalculator.isValid()){
                    animationKeyToCalculatorObject.put(animKey,thisCalculator);
                }
                //here we have a set of animation objects that only need to be iterated over and run


            }
        }
    }

    public double getAnimationResultOfKey(String key,LivingEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch,float tickDelta){
        if(!animationKeyToCalculatorObject.containsKey(key))
            return 0;
        return animationKeyToCalculatorObject.get(key).getResultOnly( entity,  limbAngle,  limbDistance,  animationProgress,  headYaw,  headPitch,tickDelta);
    }





    @Override
    public void render(MatrixStack herematrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

    }

    public void render( HashMap<String,ModelPart> vanillaParts, MatrixStack herematrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        for (String key:
                childrenMap.keySet()) {
            herematrices.push();
            //herematrices.translate(0,16,0);
            childrenMap.get(key).render(0,vanillaParts,herematrices,vertices,light,overlay,red,green,blue,alpha);
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
        this.tickDelta = tickDelta;
    }
    float tickDelta = Float.NaN;

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        //process all animation states for all parts
        for (AnimationCalculation calculator:
        animationKeyToCalculatorObject.values()) {
            if (entity instanceof ZombieEntity || entity instanceof SheepEntity)
                calculator.calculateAndSet((LivingEntity) entity,limbAngle,limbDistance,animationProgress,headYaw,headPitch,tickDelta);
        }
        //that's it????
    }

}
