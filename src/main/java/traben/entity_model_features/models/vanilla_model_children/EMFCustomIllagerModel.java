package traben.entity_model_features.models.vanilla_model_children;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import traben.entity_model_features.mixin.accessor.entity.model.IllagerEntityModelAccessor;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.EMFArmorableModel;
import traben.entity_model_features.models.EMFCustomModel;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.EMF_ModelPart;

import java.util.ArrayList;
import java.util.List;

public class EMFCustomIllagerModel<T extends LivingEntity, M extends IllagerEntity> extends IllagerEntityModel<M> implements EMFCustomModel<T>, EMFArmorableModel {

    public EMF_EntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final  EMF_EntityModel<T> thisEMFModel;


    public EMFCustomIllagerModel(EMF_EntityModel<T> model) {
        super(IllagerEntityModel.getTexturedModelData().createModel());

        thisEMFModel=model;
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);

        List<EMF_ModelPart> headCandidates = new ArrayList<>();
        List<EMF_ModelPart> lACandidates = new ArrayList<>();
        List<EMF_ModelPart> rACandidates = new ArrayList<>();

        for (EMF_ModelPart part:
                thisEMFModel.childrenMap.values()) {
            if ("head".equals(part.selfModelData.part)) {
                headCandidates.add(part);
            }else if ("left_arm".equals(part.selfModelData.part)) {
                lACandidates.add(part);
            }else if ("right_arm".equals(part.selfModelData.part)) {
                rACandidates.add(part);
            }
        }

        setNonEmptyPart(headCandidates,((IllagerEntityModelAccessor)this)::setHead);
        setNonEmptyPart(lACandidates,((IllagerEntityModelAccessor)this)::setLeftArm);
        setNonEmptyPart(rACandidates,((IllagerEntityModelAccessor)this)::setRightArm);
    }



    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);

    }

    @Override
    public void setAngles(M illagerEntity, float f, float g, float h, float i, float j) {

        ((IllagerEntityModel<M>)thisEMFModel.getThisEMFModel().vanillaModel).setAngles(illagerEntity, f, g, h, i, j);
        setAngles((T)illagerEntity, f, g, h, i, j);
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
        ((IllagerEntityModel<M>)thisEMFModel.getThisEMFModel().vanillaModel).animateModel(entity, limbAngle, limbDistance, tickDelta);
        animateModel((T)entity, limbAngle, limbDistance, tickDelta);
    }

    @Override
    public void animateModel(T livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);

            thisEMFModel.animateModel(livingEntity, f, g, h);

    }

    @Override
    public EMF_EntityModel<?> getArmourModel(boolean getInner) {
        return thisEMFModel.getArmourModel(getInner);
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        //((IllagerEntityModel<M>)thisEMFModel.getThisEMFModel().vanillaModel).setArmAngle(arm, matrices);
        //ModelPart armP =(arm == Arm.LEFT ? ((IllagerEntityModelAccessor)this).getLeftArm() : ((IllagerEntityModelAccessor)this).getRightArm());
        //if(armP instanceof EMF_ModelPart emf)
        //    emf.rotateV3(matrices);


        super.setArmAngle(arm, matrices);
    }
}
