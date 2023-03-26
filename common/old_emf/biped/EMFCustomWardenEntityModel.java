package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.biped;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WardenEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.EMFCustomEntityModel;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.EMFGenericCustomEntityModel;

import java.util.HashMap;

public class EMFCustomWardenEntityModel<T extends LivingEntity> extends WardenEntityModel<WardenEntity> implements EMFCustomEntityModel<T>{

    public EMFGenericCustomEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMFGenericCustomEntityModel<T> thisEMFModel;

    private static final HashMap<String,String> optifineMap = new HashMap<>(){{
        put("bone","body");
        put("body","torso");
    }};
    public EMFCustomWardenEntityModel(EMFGenericCustomEntityModel<T> model) {
        super( EMFCustomEntityModel.getFinalModelRootData(
                WardenEntityModel.getTexturedModelData().createModel(),
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
    public void setAngles(WardenEntity livingEntity, float f, float g, float h, float i, float j) {

            thisEMFModel.child = child;
            //thisEMFModel.sneaking = sneaking;
            thisEMFModel.riding = riding;
            thisEMFModel.handSwingProgress = handSwingProgress;

            try{
                thisEMFModel.setAngles((T) livingEntity, f, g, h, i, j);
            }catch (ClassCastException ignored){}
    }

    @Override
    public void animateModel(WardenEntity livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);
            try {
                thisEMFModel.animateModel((T) livingEntity, f, g, h);
            }catch (ClassCastException ignored){}

    }


}
