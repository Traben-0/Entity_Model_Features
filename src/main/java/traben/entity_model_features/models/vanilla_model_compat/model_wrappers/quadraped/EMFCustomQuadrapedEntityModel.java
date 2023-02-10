package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.quadraped;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.mixin.accessor.entity.model.QuadrupedEntityModelAccessor;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericEntityEntityModel;
import traben.entity_model_features.models.EMFModelPart;

import java.util.ArrayList;
import java.util.List;

public class EMFCustomQuadrapedEntityModel<T extends LivingEntity> extends QuadrupedEntityModel<T> implements EMFCustomEntityModel<T> {

    public EMFGenericEntityEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMFGenericEntityEntityModel<T> thisEMFModel;


    public EMFCustomQuadrapedEntityModel(EMFGenericEntityEntityModel<T> model) {
        super(QuadrupedEntityModel.getModelData(1,Dilation.NONE).getRoot().createPart(0,0),
                false,0,0,0,0,0);
        thisEMFModel=model;
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);

        List<EMFModelPart> headCandidates = new ArrayList<>();
        List<EMFModelPart> bodyCandidates = new ArrayList<>();
        List<EMFModelPart> rFLegCandidates = new ArrayList<>();
        List<EMFModelPart> lFLegCandidates = new ArrayList<>();
        List<EMFModelPart> lBLegCandidates = new ArrayList<>();
        List<EMFModelPart> rBLegCandidates = new ArrayList<>();

        for (EMFModelPart part:
                thisEMFModel.childrenMap.values()) {
            switch (part.selfModelData.part){
                case "head"->{
                    headCandidates.add(part);
                }
                case "body"->{
                    bodyCandidates.add(part);
                }
                case "leg1"->{
                    rBLegCandidates.add(part);
                }
                case "leg2"->{
                    lBLegCandidates.add(part);
                }
                case "leg3"->{
                    rFLegCandidates.add(part);
                }
                case "leg4"->{
                    lFLegCandidates.add(part);
                }
                default->{

                }
            }
        }

        setNonEmptyPart(headCandidates,((QuadrupedEntityModelAccessor)this)::setHead);
        setNonEmptyPart(bodyCandidates,((QuadrupedEntityModelAccessor)this)::setBody);
        setNonEmptyPart(lFLegCandidates,((QuadrupedEntityModelAccessor)this)::setLeftFrontLeg);
        setNonEmptyPart(lBLegCandidates,((QuadrupedEntityModelAccessor)this)::setLeftHindLeg);
        setNonEmptyPart(rFLegCandidates,((QuadrupedEntityModelAccessor)this)::setRightFrontLeg);
        setNonEmptyPart(rBLegCandidates,((QuadrupedEntityModelAccessor)this)::setRightHindLeg);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);

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
    public void animateModel(T livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);

            thisEMFModel.animateModel(livingEntity, f, g, h);

    }


}
