package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.quadraped;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericCustomEntityModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EMFCustomLlamaEntityModel<T extends LivingEntity> extends LlamaEntityModel<AbstractDonkeyEntity> implements EMFCustomEntityModel<T> {

    public EMFGenericCustomEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMFGenericCustomEntityModel<T> thisEMFModel;

    private static final HashMap<String,String> optifineMap = new HashMap<>(){{
        put("right_hind_leg","leg1");
        put("left_hind_leg", "leg2");
        put("right_front_leg", "leg3");
        put("left_front_leg", "leg4");
        put("right_chest", "chest_right");
        put("left_chest", "chest_left");
    }};
    public EMFCustomLlamaEntityModel(EMFGenericCustomEntityModel<T> model) {
        //super(LlamaEntityModel.getTexturedModelData(Dilation.NONE).createModel());
        super( EMFCustomEntityModel.getFinalModelRootData(
                LlamaEntityModel.getTexturedModelData(Dilation.NONE).createModel(),
                model, optifineMap));
        thisEMFModel=model;
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);
        thisEMFModel.clearAllFakePartChildrenData();

//        List<EMFModelPart> headCandidates = new ArrayList<>();
//        List<EMFModelPart> bodyCandidates = new ArrayList<>();
////        List<EMF_ModelPart> rFLegCandidates = new ArrayList<>();
////        List<EMF_ModelPart> lFLegCandidates = new ArrayList<>();
////        List<EMF_ModelPart> lBLegCandidates = new ArrayList<>();
////        List<EMF_ModelPart> rBLegCandidates = new ArrayList<>();
//
//        for (EMFModelPart part:
//                thisEMFModel.childrenMap.values()) {
//            switch (part.selfModelData.part){
//                case "head"->{
//                    headCandidates.add(part);
//                }
//                case "body"->{
//                    bodyCandidates.add(part);
//                }
////                case "leg1"->{
////                    rBLegCandidates.add(part);
////                }
////                case "leg2"->{
////                    lBLegCandidates.add(part);
////                }
////                case "leg3"->{
////                    rFLegCandidates.add(part);
////                }
////                case "leg4"->{
////                    lFLegCandidates.add(part);
////                }
//                default->{
//
//                }
//            }
//        }
//
//        setNonEmptyPart(headCandidates,((LlamaEntityModelAccessor)this)::setHead);
//        setNonEmptyPart(bodyCandidates,((LlamaEntityModelAccessor)this)::setBody);
////        setNonEmptyPart(lFLegCandidates,((QuadrupedEntityModelAccessor)this)::setLeftFrontLeg);
////        setNonEmptyPart(lBLegCandidates,((QuadrupedEntityModelAccessor)this)::setLeftHindLeg);
////        setNonEmptyPart(rFLegCandidates,((QuadrupedEntityModelAccessor)this)::setRightFrontLeg);
////        setNonEmptyPart(rBLegCandidates,((QuadrupedEntityModelAccessor)this)::setRightHindLeg);
    }


    private final Set<String> headSet = new HashSet<>(){{add("head");}};
    private final Set<String> bodySet = new HashSet<>(){{add("body");}};

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

           //matrices.push();
           if(child){

               //selectively hide parts to mimic vanilla child scaling
               matrices.push();
               matrices.scale(0.71428573F, 0.64935064F, 0.7936508F);
               matrices.translate(0.0F, 1.3125F, 0.22F);
               thisEMFModel.setVisibleToplvl(false);
               thisEMFModel.setVisibleToplvl(headSet,true);
               thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);
               matrices.pop();
               matrices.push();
               matrices.scale(0.625F, 0.45454544F, 0.45454544F);
               matrices.translate(0.0F, 2.0625F, 0.0F);
               thisEMFModel.setVisibleToplvl(false);
               thisEMFModel.setVisibleToplvl(bodySet,true);
               thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);
               matrices.pop();
               matrices.push();
               matrices.scale(0.45454544F, 0.41322312F, 0.45454544F);
               matrices.translate(0.0F, 2.0625F, 0.0F);
               thisEMFModel.setVisibleToplvl(true);
               thisEMFModel.setVisibleToplvl(bodySet,false);
               thisEMFModel.setVisibleToplvl(headSet,false);
               thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);
//               ImmutableList.of(this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightChest, this.leftChest).forEach((part) -> {
//                   part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
//               });
               thisEMFModel.setVisibleToplvl(true);
               matrices.pop();
           }else {
               thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);
           }
           //matrices.pop();
    }

//    @Override
//    public void setAngles(AbstractDonkeyEntity abstractDonkeyEntity, float f, float g, float h, float i, float j) {
//        setAngles((T)abstractDonkeyEntity, f, g, h, i, j);
//    }

    @Override
    public void setAngles(AbstractDonkeyEntity livingEntity, float f, float g, float h, float i, float j) {

            thisEMFModel.child = child;
            //thisEMFModel.sneaking = sneaking;
            thisEMFModel.riding = riding;
            thisEMFModel.handSwingProgress = handSwingProgress;
            thisEMFModel.setAngles((T) livingEntity, f, g, h, i, j);

    }

//    @Override
//    public void animateModel(AbstractDonkeyEntity entity, float limbAngle, float limbDistance, float tickDelta) {
//        animateModel((T)entity, limbAngle, limbDistance, tickDelta);
//    }

    @Override
    public void animateModel(AbstractDonkeyEntity livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);

            thisEMFModel.animateModel((T) livingEntity, f, g, h);

    }


}
