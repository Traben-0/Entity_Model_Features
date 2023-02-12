package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.quadraped;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import traben.entity_model_features.mixin.accessor.entity.model.FoxEntityModelAccessor;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericCustomEntityModel;
import traben.entity_model_features.models.EMFModelPart;

import java.util.ArrayList;
import java.util.List;

public class EMFCustomFoxEntityModel<T extends LivingEntity, M extends FoxEntity> extends FoxEntityModel<M> implements EMFCustomEntityModel<T> {

    public EMFGenericCustomEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMFGenericCustomEntityModel<T> thisEMFModel;


    public EMFCustomFoxEntityModel(EMFGenericCustomEntityModel<T> model) {
        super(FoxEntityModel.getTexturedModelData().createModel());
        thisEMFModel=model;
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);

        List<EMFModelPart> headCandidates = new ArrayList<>();

        for (EMFModelPart part:
                thisEMFModel.childrenMap.values()) {
            if ("head".equals(part.selfModelData.part)) {
                headCandidates.add(part);
            }
        }
        //head needed for fox item feature renderer
        setNonEmptyPart(headCandidates,((FoxEntityModelAccessor)this)::setHead);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);

    }

    @Override
    public void setAngles(M foxEntity, float f, float g, float h, float i, float j) {
        setAngles((T)foxEntity, f, g, h, i, j);
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
    public void animateModel(M foxEntity, float f, float g, float h) {
        animateModel((T)foxEntity, f, g, h);
    }

    @Override
    public void animateModel(T livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);

            thisEMFModel.animateModel(livingEntity, f, g, h);

    }


}
