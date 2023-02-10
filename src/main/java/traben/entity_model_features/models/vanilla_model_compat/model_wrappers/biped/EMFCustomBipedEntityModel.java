package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.biped;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import traben.entity_model_features.mixin.accessor.entity.model.BipedEntityModelAccessor;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.EMFArmorableModel;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericEntityEntityModel;
import traben.entity_model_features.models.EMFModelPart;

import java.util.ArrayList;
import java.util.List;

public class EMFCustomBipedEntityModel<T extends LivingEntity> extends BipedEntityModel<T> implements EMFCustomEntityModel<T>, EMFArmorableModel, ModelWithHat {

    public EMFGenericEntityEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMFGenericEntityEntityModel<T> thisEMFModel;


    public EMFCustomBipedEntityModel(EMFGenericEntityEntityModel<T> model) {
        super(BipedEntityModel.getModelData(Dilation.NONE,0).getRoot().createPart(0,0));

        thisEMFModel=model;
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);

        List<EMFModelPart> headWearCandidates = new ArrayList<>();
        List<EMFModelPart> headCandidates = new ArrayList<>();
        List<EMFModelPart> bodyCandidates = new ArrayList<>();
        List<EMFModelPart> rArmCandidates = new ArrayList<>();
        List<EMFModelPart> lArmCandidates = new ArrayList<>();
        List<EMFModelPart> lLegCandidates = new ArrayList<>();
        List<EMFModelPart> rLegCandidates = new ArrayList<>();

        for (EMFModelPart part:
                thisEMFModel.childrenMap.values()) {
            switch (part.selfModelData.part){
                case "headwear"->{
                    headWearCandidates.add(part);
                }
                case "head"->{
                    headCandidates.add(part);
                }
                case "body"->{
                    bodyCandidates.add(part);
                }
                case "left_arm"->{
                    lArmCandidates.add(part);
                }
                case "left_leg"->{
                    lLegCandidates.add(part);
                }
                case "right_arm"->{
                    rArmCandidates.add(part);
                }
                case "right_leg"->{
                    rLegCandidates.add(part);
                }
                default->{

                }
            }
        }
        //this part makes sure head rotation data is available for armor feature renderer
        // mostly as fresh animations tends to use headwear for rotation instead of head
        boolean wasNotEmpty = false;
        for (EMFModelPart part:
                headCandidates) {
            if(!part.isEmptyPart){
                wasNotEmpty = true;
                break;
            }
        }
        if(!wasNotEmpty){
            headCandidates = headWearCandidates;
        }

        setNonEmptyPart(headWearCandidates,((BipedEntityModelAccessor)this)::setHat);
        setNonEmptyPart(headCandidates,((BipedEntityModelAccessor)this)::setHead);
        setNonEmptyPart(bodyCandidates,((BipedEntityModelAccessor)this)::setBody);
        setNonEmptyPart(lArmCandidates,((BipedEntityModelAccessor)this)::setLeftArm);
        setNonEmptyPart(lLegCandidates,((BipedEntityModelAccessor)this)::setLeftLeg);
        setNonEmptyPart(rArmCandidates,((BipedEntityModelAccessor)this)::setRightArm);
        setNonEmptyPart(rLegCandidates,((BipedEntityModelAccessor)this)::setRightLeg);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);

    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {

            thisEMFModel.child = child;
            thisEMFModel.sneaking = sneaking;
            thisEMFModel.riding = riding;
            thisEMFModel.handSwingProgress = handSwingProgress;
            thisEMFModel.setAngles(livingEntity, f, g, h, i, j);

    }

    @Override
    public void animateModel(T livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);

            thisEMFModel.animateModel(livingEntity, f, g, h);

    }


    @Override
    public EMFGenericEntityEntityModel<?> getArmourModel(boolean getInner) {
        return thisEMFModel.getArmourModel(getInner);
    }


    @Override
    public void setHatVisible(boolean visible) {
        thisEMFModel.setHatVisible(visible);
    }
}
