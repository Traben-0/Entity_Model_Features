package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.other;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.TadpoleEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TadpoleEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericCustomEntityModel;

public class EMFCustomTadpoleEntityModel<T extends LivingEntity,A extends TadpoleEntity> extends TadpoleEntityModel<A> implements EMFCustomEntityModel<T> {

    public EMFGenericCustomEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMFGenericCustomEntityModel<T> thisEMFModel;


    public EMFCustomTadpoleEntityModel(EMFGenericCustomEntityModel<T> model) {
        //super(QuadrupedEntityModel.getModelData(1,Dilation.NONE).getRoot().createPart(0,0));
        super( EMFCustomEntityModel.getFinalModelRootData(
                TadpoleEntityModel.getTexturedModelData().createModel(),
                model));

        thisEMFModel=model;
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);
        thisEMFModel.clearAllFakePartChildrenData();

//        List<EMFModelPart> headCandidates = new ArrayList<>();

//        for (EMFModelPart part:
//                thisEMFModel.childrenMap.values()) {
//            if ("head".equals(part.selfModelData.part)) {
//                headCandidates.add(part);
//            }
//        }
//        //this is for mooshroom feature renderer
//        setNonEmptyPart(headCandidates,((QuadrupedEntityModelAccessor)this)::setHead);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);

    }

    @Override
    public void setAngles(A livingEntity, float f, float g, float h, float i, float j) {

            thisEMFModel.child = child;
            //thisEMFModel.sneaking = sneaking;
            thisEMFModel.riding = riding;
            thisEMFModel.handSwingProgress = handSwingProgress;
            try {
                thisEMFModel.setAngles((T) livingEntity, f, g, h, i, j);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

    }

    @Override
    public void animateModel(A livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);

            try {
                thisEMFModel.animateModel((T) livingEntity, f, g, h);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

    }


}
