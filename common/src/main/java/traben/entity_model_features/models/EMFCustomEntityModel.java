package traben.entity_model_features.models;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EMFCustomEntityModel<T extends LivingEntity> {

    static <T extends LivingEntity> ModelPart getFinalModelRootData(ModelPart root,
                                                                    EMFGenericCustomEntityModel<T> EMFModelToIntegrateIntoRoot){
        return getFinalModelRootData(root,EMFModelToIntegrateIntoRoot,new HashMap<>());
    }

    static <T extends LivingEntity> ModelPart getFinalModelRootData(ModelPart root,
                                                                    EMFGenericCustomEntityModel<T> EMFModelToIntegrateIntoRoot,
                                                                    HashMap<String,String> optifineNameMap){
        Map<String, ModelPart> rootChildren = ((ModelPartAccessor)root).getChildren();
        Map<String, ModelPart> replacementChildren = forEachChildCheckAndReplace(rootChildren,EMFModelToIntegrateIntoRoot, optifineNameMap);
        //System.out.println(replacementChildren.toString());
        ((ModelPartAccessor)root).setChildren(replacementChildren);
        return root;
    }

    static <T extends LivingEntity> Map<String, ModelPart> forEachChildCheckAndReplace(
            Map<String, ModelPart> rootChildren,
            EMFGenericCustomEntityModel<T> EMFModelToIntegrateIntoRoot,
            HashMap<String,String> optifineNameMap
    ){
        Map<String, ModelPart> replacements = new HashMap<>();
        for (String key:
                rootChildren.keySet()) {
            String sendKey;

            //if("hat".equals(key)) sendKey = "headwear";
            //if("cloak".equals(key)) sendKey = "cape";
           // if("right_arm".equals(key)) sendKey = "placehold";
            sendKey = optifineNameMap.getOrDefault(key, key);

            EMFModelPart emf = getFirstOfPartInModelAndPrepare(sendKey,
                    EMFModelToIntegrateIntoRoot,
                    ((ModelPartAccessor)rootChildren.get(key)).getChildren(), optifineNameMap);
            if(emf != null){
                replacements.put(key,emf);
            }
        }
        rootChildren.putAll(replacements);
        return rootChildren;
    }

    static <T extends LivingEntity> EMFModelPart getFirstOfPartInModelAndPrepare(String partname,
                                                                                 EMFGenericCustomEntityModel<T> EMFModel,
                                                                                 Map<String, ModelPart> vanillaPartChildren,
                                                                                 HashMap<String,String> optifineNameMap){
        EMFModelPart findPart = getFirstOfPartInModel(partname,EMFModel);
        if(findPart != null){
            findPart.setVanillaFakeChildren(forEachChildCheckAndReplace(vanillaPartChildren, EMFModel, optifineNameMap));
        }
        return findPart;
    }

    static <T extends LivingEntity> EMFModelPart getFirstOfPartInModel(String partname, EMFGenericCustomEntityModel<T> EMFModel){
        for (EMFModelPart part:
             EMFModel.childrenMap.values()) {
            if(partname.equals(part.selfModelData.part)){
               // System.out.println("found = "+partname);
                return part;
            }
        }
        return null;
    }



    EMFGenericCustomEntityModel<T> getThisEMFModel() ;

    boolean doesThisModelNeedToBeReset();

    boolean forceRecheckModel_currentlyOnlyTrueForPufferFish = false;

   // EMF_EntityModel<? extends LivingEntity> thisEMFModel = null;

    void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha);

    //void setAngles(T livingEntity, float f, float g, float h, float i, float j);

    //void animateModel(T livingEntity, float f, float g, float h);

    default void setNonEmptyPart(List<EMFModelPart> parts, PartSetter setter){
        for (EMFModelPart part:
                parts) {
            if(!part.isEmptyPart){
                setter.setPart(part);
                //((BipedEntityModelAccessor)this).setHat(part);
                break;
            }
        }
    }

    default EMFModelPart getNonEmptyPart(List<EMFModelPart> parts){
        for (EMFModelPart part:
                parts) {
            if(!part.isEmptyPart){
                return part;
                //setter.setPart(part);
                //((BipedEntityModelAccessor)this).setHat(part);
                //break;
            }
        }
        return null;
    }

    interface PartSetter{
         void setPart(EMFModelPart part);
    }

}
