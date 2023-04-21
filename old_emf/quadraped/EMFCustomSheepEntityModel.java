package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.quadraped;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.EMFCustomEntityModel;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.EMFGenericCustomEntityModel;

import java.util.HashMap;

public class EMFCustomSheepEntityModel<T extends LivingEntity> extends SheepEntityModel<SheepEntity> implements EMFCustomEntityModel<T> {

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
    }};
    public EMFCustomSheepEntityModel(EMFGenericCustomEntityModel<T> model) {
        //super(QuadrupedEntityModel.getModelData(1,Dilation.NONE).getRoot().createPart(0,0));
        super( EMFCustomEntityModel.getFinalModelRootData(
                SheepEntityModel.getTexturedModelData().createModel(),
                model, optifineMap));

        thisEMFModel=model;
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);
        thisEMFModel.clearAllFakePartChildrenData();


    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);

    }

//    @Override
//    public void setAngles(SheepEntity sheepEntity, float f, float g, float h, float i, float j) {
//        setAngles((T)sheepEntity, f, g, h, i, j);
//    }

    @Override
    public void setAngles(SheepEntity livingEntity, float f, float g, float h, float i, float j) {

            thisEMFModel.child = child;
            //thisEMFModel.sneaking = sneaking;
            thisEMFModel.riding = riding;
            thisEMFModel.handSwingProgress = handSwingProgress;
            thisEMFModel.setAngles((T) livingEntity, f, g, h, i, j);

    }

//    @Override
//    public void animateModel(SheepEntity sheepEntity, float f, float g, float h) {
//        animateModel((T)sheepEntity, f, g, h);
//    }

    @Override
    public void animateModel(SheepEntity livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);

            thisEMFModel.animateModel((T) livingEntity, f, g, h);

    }


}
