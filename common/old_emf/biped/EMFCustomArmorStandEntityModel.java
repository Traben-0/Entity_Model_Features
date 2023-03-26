package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.biped;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Arm;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.EMFArmorableModel;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.EMFCustomEntityModel;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.EMFGenericCustomEntityModel;

import java.util.HashMap;

public class EMFCustomArmorStandEntityModel<T extends LivingEntity> extends ArmorStandEntityModel implements EMFCustomEntityModel<T>, EMFArmorableModel, ModelWithHat {

    public EMFGenericCustomEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMFGenericCustomEntityModel<T> thisEMFModel;

    private static final HashMap<String,String> optifineMap = new HashMap<>(){{
        put("hat","headwear");
    }};
    public EMFCustomArmorStandEntityModel(EMFGenericCustomEntityModel<T> model) {
        super( EMFCustomEntityModel.getFinalModelRootData(
                ArmorStandEntityModel.getTexturedModelData().createModel(),
                model,optifineMap));

        thisEMFModel=model;
        thisEMFModel.clearAllFakePartChildrenData();
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);

    }

    @Override
    public void setAngles(ArmorStandEntity livingEntity, float f, float g, float h, float i, float j) {
        if(thisEMFModel.vanillaModel instanceof BipedEntityModel<?> model){
            model.leaningPitch = leaningPitch;
        }
            thisEMFModel.child = child;
            thisEMFModel.sneaking = sneaking;
            thisEMFModel.riding = riding;
            thisEMFModel.handSwingProgress = handSwingProgress;
            try {
                thisEMFModel.setAngles((T)livingEntity, f, g, h, i, j);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

    }

    @Override
    public void animateModel(ArmorStandEntity livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);

            try {
                thisEMFModel.animateModel((T)livingEntity, f, g, h);
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

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        if(thisEMFModel.vanillaModel instanceof ArmorStandEntityModel model){
            model.setArmAngle(arm, matrices);
        }
    }
}
