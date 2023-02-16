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

public class EMFCustomLlamaEntityModel<T extends LivingEntity, M extends AbstractDonkeyEntity> extends LlamaEntityModel<M> implements EMFCustomEntityModel<T> {

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

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);

    }

    @Override
    public void setAngles(M abstractDonkeyEntity, float f, float g, float h, float i, float j) {
        setAngles((T)abstractDonkeyEntity, f, g, h, i, j);
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {

            thisEMFModel.child = child;
            //thisEMFModel.sneaking = sneaking;
            thisEMFModel.riding = riding;
            thisEMFModel.handSwingProgress = handSwingProgress;
            thisEMFModel.setAngles(livingEntity, f, g, h, i, j);

    }

    @Override
    public void animateModel(M entity, float limbAngle, float limbDistance, float tickDelta) {
        animateModel((T)entity, limbAngle, limbDistance, tickDelta);
    }

    @Override
    public void animateModel(T livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);

            thisEMFModel.animateModel(livingEntity, f, g, h);

    }


}
