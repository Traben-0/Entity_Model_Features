package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.biped;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.EMFCustomEntityModel;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.EMFGenericCustomEntityModel;

public class EMFCustomIronGolemEntityModel<T extends LivingEntity> extends IronGolemEntityModel<IronGolemEntity> implements EMFCustomEntityModel<T> {

    public EMFGenericCustomEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMFGenericCustomEntityModel<T> thisEMFModel;


    public EMFCustomIronGolemEntityModel(EMFGenericCustomEntityModel<T> model) {
        super(EMFCustomEntityModel.getFinalModelRootData( IronGolemEntityModel.getTexturedModelData().createModel(),model));
        thisEMFModel=model;
        thisEMFModel.clearAllFakePartChildrenData();
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);
        //supports flower holding feature

//        List<EMFModelPart> rArmCandidates = new ArrayList<>();
//        for (EMFModelPart part:
//                thisEMFModel.childrenMap.values()) {
//            if ("right_arm".equals(part.selfModelData.part)) {
//                rArmCandidates.add(part);
//            }
//        }
//        setNonEmptyPart(rArmCandidates,((IronGolemEntityModelAccessor)this)::setRightArm);
    }



    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }


//    @Override
//    public void setAngles(IronGolemEntity ironGolemEntity, float f, float g, float h, float i, float j) {
//        setAngles((T)ironGolemEntity, f, g, h, i, j);
//    }

    @Override
    public void setAngles(IronGolemEntity livingEntity, float f, float g, float h, float i, float j) {
        //System.out.println("ran");
            thisEMFModel.child = child;
            //thisEMFModel.sneaking = sneaking;
            thisEMFModel.riding = riding;
            thisEMFModel.handSwingProgress = handSwingProgress;
            thisEMFModel.setAngles((T) livingEntity, f, g, h, i, j);

    }


//    @Override
//    public void animateModel(IronGolemEntity ironGolemEntity, float f, float g, float h) {
//        animateModel((T)ironGolemEntity, f, g, h);
//    }
    @Override
    public void animateModel(IronGolemEntity livingEntity, float f, float g, float h) {
        thisEMFModel.animateModel((T) livingEntity, f, g, h);

    }


}
