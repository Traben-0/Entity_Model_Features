package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.quadraped;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.RabbitEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.EMFCustomEntityModel;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.EMFGenericCustomEntityModel;

import java.util.HashMap;

public class EMFCustomRabbitEntityModel<T extends LivingEntity> extends RabbitEntityModel<RabbitEntity> implements EMFCustomEntityModel<T> {

    public EMFGenericCustomEntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final EMFGenericCustomEntityModel<T> thisEMFModel;

    private static final HashMap<String,String> optifineMap = new HashMap<>(){{
        put("left_hind_foot","left_foot");
        put("right_hind_foot","right_foot");
        put("left_haunch","left_thigh");
        put("right_haunch","right_thigh");
        put("left_front_leg","left_arm");
        put("right_front_leg","right_arm");
    }};
    public EMFCustomRabbitEntityModel(EMFGenericCustomEntityModel<T> model) {
        //super(QuadrupedEntityModel.getModelData(1,Dilation.NONE).getRoot().createPart(0,0));
        super( EMFCustomEntityModel.getFinalModelRootData(
                RabbitEntityModel.getTexturedModelData().createModel(),
                model, optifineMap));

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
    public void setAngles(RabbitEntity livingEntity, float f, float g, float h, float i, float j) {

        thisEMFModel.child = child;
            //thisEMFModel.sneaking = sneaking;
        thisEMFModel.riding = riding;
        thisEMFModel.handSwingProgress = handSwingProgress;
        try{
            thisEMFModel.setAngles((T) livingEntity, f, g, h, i, j);
        }catch (ClassCastException ignored){}
    }

    @Override
    public void animateModel(RabbitEntity livingEntity, float f, float g, float h) {
        //super.animateModel(livingEntity, f, g, h);




        try {
            thisEMFModel.animateModel((T) livingEntity, f, g, h);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


}
