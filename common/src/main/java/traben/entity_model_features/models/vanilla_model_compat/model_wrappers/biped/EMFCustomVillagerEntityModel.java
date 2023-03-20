package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.biped;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.EMFArmorableModel;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericCustomEntityModel;

import java.util.HashMap;

public class EMFCustomVillagerEntityModel<T extends LivingEntity> extends VillagerResemblingModel<T> implements EMFCustomEntityModel<T>, EMFArmorableModel {

    public EMFGenericCustomEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMFGenericCustomEntityModel<T> thisEMFModel;

    private static final HashMap<String,String> optifineMap = new HashMap<>(){{
        put("hat","headwear");
        put("hat_rim", "headwear2");
        put("bodywear", "jacket");
    }};
    public EMFCustomVillagerEntityModel(EMFGenericCustomEntityModel<T> model) {
        //super(VillagerResemblingModel.getModelData().getRoot().createPart(0,0));
        super( EMFCustomEntityModel.getFinalModelRootData(
                VillagerResemblingModel.getModelData().getRoot().createPart(0,0),
                model, optifineMap));

        thisEMFModel=model;
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);
        thisEMFModel.clearAllFakePartChildrenData();
//        List<EMFModelPart> headWearCandidates = new ArrayList<>();
//
//        for (EMFModelPart part:
//                thisEMFModel.childrenMap.values()) {
//            if ("headwear".equals(part.selfModelData.part)) {
//                headWearCandidates.add(part);
//            }
//        }
//
//        setNonEmptyPart(headWearCandidates,((VillagerResemblingModelAccessor)this)::setHat);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);

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
    public void animateModel(T livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);

            thisEMFModel.animateModel(livingEntity, f, g, h);

    }

    @Override
    public EMFGenericCustomEntityModel<?> getArmourModel(boolean getInner) {
        return thisEMFModel.getArmourModel(getInner);
    }


}
