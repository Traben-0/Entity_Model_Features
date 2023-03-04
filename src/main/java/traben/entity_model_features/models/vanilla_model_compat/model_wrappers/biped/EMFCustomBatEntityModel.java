package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.biped;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BatEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.EMFArmorableModel;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericCustomEntityModel;

import java.util.HashMap;

public class EMFCustomBatEntityModel<T extends LivingEntity> extends BatEntityModel implements EMFCustomEntityModel<T>, EMFArmorableModel, ModelWithHat {

    public EMFGenericCustomEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMFGenericCustomEntityModel<T> thisEMFModel;

    private static final HashMap<String,String> optifineMap = new HashMap<>(){{
        put("right_wing_tip","outer_right_wing");
        put("left_wing_tip","outer_left_wing");
    }};
    public EMFCustomBatEntityModel(EMFGenericCustomEntityModel<T> model) {
        super( EMFCustomEntityModel.getFinalModelRootData(
                BatEntityModel.getTexturedModelData().createModel(),
                model,optifineMap));

        thisEMFModel=model;
        thisEMFModel.clearAllFakePartChildrenData();
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);

    }

    //   @Override
//    public void setAngles(BatEntity allayEntity, float f, float g, float h, float i, float j) {
//        setAngles((T)allayEntity, f, g, h, i, j);
//    }

    @Override
    public void setAngles(BatEntity livingEntity, float f, float g, float h, float i, float j) {

            thisEMFModel.child = child;
            //thisEMFModel.sneaking = sneaking;
            thisEMFModel.riding = riding;
            thisEMFModel.handSwingProgress = handSwingProgress;
            thisEMFModel.setAngles((T) livingEntity, f, g, h, i, j);

    }

//    @Override
//    public void animateModel(BatEntity entity, float limbAngle, float limbDistance, float tickDelta) {
//        animateModel((T)entity, limbAngle, limbDistance, tickDelta);
//    }

    @Override
    public void animateModel(BatEntity livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);

            thisEMFModel.animateModel((T) livingEntity, f, g, h);

    }


    @Override
    public EMFGenericCustomEntityModel<?> getArmourModel(boolean getInner) {
        return thisEMFModel.getArmourModel(getInner);
    }


    @Override
    public void setHatVisible(boolean visible) {
        thisEMFModel.setHatVisible(visible);
    }
}
