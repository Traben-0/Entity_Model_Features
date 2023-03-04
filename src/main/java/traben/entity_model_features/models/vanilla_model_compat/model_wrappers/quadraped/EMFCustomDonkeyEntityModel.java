package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.quadraped;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericCustomEntityModel;

import java.util.HashMap;

public class EMFCustomDonkeyEntityModel<T extends LivingEntity> extends DonkeyEntityModel<AbstractDonkeyEntity> implements EMFCustomEntityModel<T> {

    public EMFGenericCustomEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }



    private final EMFGenericCustomEntityModel<T> thisEMFModel;

    private static final HashMap<String,String> optifineMap = new HashMap<>(){{
        put("right_hind_leg","back_right_leg");
        put("left_hind_leg", "back_left_leg");
        put("right_front_leg", "front_right_leg");
        put("left_front_leg", "neck");
        put("head_parts", "front_left_leg");
        put("upper_mouth", "mouth");
        put("head_saddle", "headpiece");
        put("mouth_saddle_wrap", "nose_band");
        put("left_saddle_mouth", "left_bit");
        put("right_saddle_mouth", "right_bit");
        put("left_saddle_line", "left_rein");
        put("right_saddle_line", "right_rein");
        put("right_hind_baby_leg", "child_back_right_leg");
        put("left_hind_baby_leg", "child_back_left_leg");
        put("left_front_baby_leg", "child_front_right_leg");
        put("right_front_baby_leg", "child_front_left_leg");
    }};
    public EMFCustomDonkeyEntityModel(EMFGenericCustomEntityModel<T> model) {
        //super(HorseEntityModel.getModelData(Dilation.NONE).getRoot().createPart(0, 0));
        super( EMFCustomEntityModel.getFinalModelRootData(
                DonkeyEntityModel.getTexturedModelData().createModel(),
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
//    public void setAngles(AbstractDonkeyEntity abstractHorseEntity, float f, float g, float h, float i, float j) {
//        //super.setAngles(abstractHorseEntity, f, g, h, i, j);
//        setAngles((T)abstractHorseEntity, f, g, h, i, j);
//    }

    @Override
    public void setAngles(AbstractDonkeyEntity livingEntity, float f, float g, float h, float i, float j) {
        thisEMFModel.child = child;
        thisEMFModel.riding = riding;
        thisEMFModel.handSwingProgress = handSwingProgress;
        thisEMFModel.setAngles((T) livingEntity, f, g, h, i, j);
    }

//    @Override
//    public void animateModel(AbstractDonkeyEntity abstractHorseEntity, float f, float g, float h) {
//        //super.animateModel(abstractHorseEntity, f, g, h);
//        animateModel((T)abstractHorseEntity, f, g, h);
//    }

    @Override
    public void animateModel(AbstractDonkeyEntity livingEntity, float f, float g, float h) {
        thisEMFModel.animateModel((T) livingEntity, f, g, h);
    }
}
