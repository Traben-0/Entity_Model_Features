package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.biped;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.StriderEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.EMFArmorableModel;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericCustomEntityModel;

import java.util.HashMap;

public class EMFCustomStriderEntityModel<T extends LivingEntity,A extends StriderEntity> extends StriderEntityModel<A> implements EMFCustomEntityModel<T>, EMFArmorableModel, ModelWithHat {

    public EMFGenericCustomEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMFGenericCustomEntityModel<T> thisEMFModel;

    private static final HashMap<String,String> optifineMap = new HashMap<>(){{
        put("right_bottom_bristle","hair_right_bottom");
        put("right_middle_bristle","hair_right_middle");
        put("right_top_bristle","hair_right_top");
        put("left_top_bristle","hair_left_top");
        put("left_middle_bristle","hair_left_middle");
        put("left_bottom_bristle","hair_left_bottom");
    }};
    public EMFCustomStriderEntityModel(EMFGenericCustomEntityModel<T> model) {
        super( EMFCustomEntityModel.getFinalModelRootData(
                StriderEntityModel.getTexturedModelData().createModel()
                ,model,optifineMap));

        thisEMFModel=model;
        thisEMFModel.clearAllFakePartChildrenData();
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);

    }

    @Override
    public void setAngles(StriderEntity livingEntity, float f, float g, float h, float i, float j) {

            thisEMFModel.child = child;
            //thisEMFModel.sneaking = sneaking;
            thisEMFModel.riding = riding;
            thisEMFModel.handSwingProgress = handSwingProgress;
            try {
                thisEMFModel.setAngles((T)livingEntity, f, g, h, i, j);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

    }

    @Override
    public void animateModel(StriderEntity livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);

            try {
                thisEMFModel.animateModel((T) livingEntity, f, g, h);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

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
