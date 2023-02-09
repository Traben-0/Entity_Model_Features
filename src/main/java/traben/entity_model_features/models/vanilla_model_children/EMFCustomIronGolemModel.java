package traben.entity_model_features.models.vanilla_model_children;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.mixin.accessor.entity.model.IronGolemEntityModelAccessor;
import traben.entity_model_features.models.EMFCustomModel;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.EMF_ModelPart;

import java.util.ArrayList;
import java.util.List;

public class EMFCustomIronGolemModel<T extends LivingEntity, M extends IronGolemEntity> extends IronGolemEntityModel<M> implements EMFCustomModel<T> {

    public EMF_EntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMF_EntityModel<T> thisEMFModel;


    public EMFCustomIronGolemModel(EMF_EntityModel<T> model) {
        super(BipedEntityModel.getModelData(Dilation.NONE,0).getRoot().createPart(0,0));
        thisEMFModel=model;
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);
        //supports flower holding feature

        List<EMF_ModelPart> rArmCandidates = new ArrayList<>();
        for (EMF_ModelPart part:
                thisEMFModel.childrenMap.values()) {
            if ("right_arm".equals(part.selfModelData.part)) {
                rArmCandidates.add(part);
            }
        }
        setNonEmptyPart(rArmCandidates,((IronGolemEntityModelAccessor)this)::setRightArm);
    }



    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }


    @Override
    public void setAngles(M ironGolemEntity, float f, float g, float h, float i, float j) {
        setAngles((T)ironGolemEntity, f, g, h, i, j);
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        //System.out.println("ran");
            thisEMFModel.child = child;
            //thisEMFModel.sneaking = sneaking;
            thisEMFModel.riding = riding;
            thisEMFModel.handSwingProgress = handSwingProgress;
            thisEMFModel.setAngles(livingEntity, f, g, h, i, j);

    }


    @Override
    public void animateModel(M ironGolemEntity, float f, float g, float h) {
        animateModel((T)ironGolemEntity, f, g, h);
    }
    @Override
    public void animateModel(T livingEntity, float f, float g, float h) {
        thisEMFModel.animateModel(livingEntity, f, g, h);

    }


}
