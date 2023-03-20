package traben.entity_model_features.models.vanilla_model_compat.model_wrappers.quadraped;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericCustomEntityModel;

import java.util.HashMap;

public class EMFCustomHorseEntityModel<T extends LivingEntity> extends HorseEntityModel<AbstractHorseEntity> implements EMFCustomEntityModel<T> {

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
    public EMFCustomHorseEntityModel(EMFGenericCustomEntityModel<T> model) {
        //super(HorseEntityModel.getModelData(Dilation.NONE).getRoot().createPart(0, 0));
        super( EMFCustomEntityModel.getFinalModelRootData(
                HorseEntityModel.getModelData(Dilation.NONE).getRoot().createPart(0, 0),
                model, optifineMap));
        thisEMFModel=model;
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);
        thisEMFModel.clearAllFakePartChildrenData();

//        List<EMF_ModelPart> headCandidates = new ArrayList<>();
//        List<EMF_ModelPart> bodyCandidates = new ArrayList<>();
//        List<EMF_ModelPart> neckCandidates = new ArrayList<>();
//        List<EMF_ModelPart> blLegCandidates = new ArrayList<>();
//        List<EMF_ModelPart> brLegCandidates = new ArrayList<>();
//        List<EMF_ModelPart> flLegCandidates = new ArrayList<>();
//        List<EMF_ModelPart> frLegCandidates = new ArrayList<>();
//        List<EMF_ModelPart> tailCandidates = new ArrayList<>();
//        List<EMF_ModelPart> saddleCandidates = new ArrayList<>();
//        List<EMF_ModelPart> maneCandidates = new ArrayList<>();
//        List<EMF_ModelPart> mouthCandidates = new ArrayList<>();
//        List<EMF_ModelPart> lEarCandidates = new ArrayList<>();
//        List<EMF_ModelPart> rEarCandidates = new ArrayList<>();
//        List<EMF_ModelPart> lBitCandidates = new ArrayList<>();
//        List<EMF_ModelPart> rBitCandidates = new ArrayList<>();
//        List<EMF_ModelPart> lReinCandidates = new ArrayList<>();
//        List<EMF_ModelPart> rReinCandidates = new ArrayList<>();
//        List<EMF_ModelPart> headPieceCandidates = new ArrayList<>();
//        List<EMF_ModelPart> noseBandCandidates = new ArrayList<>();
//        List<EMF_ModelPart> BblLegCandidates = new ArrayList<>();
//        List<EMF_ModelPart> BbrLegCandidates = new ArrayList<>();
//        List<EMF_ModelPart> BflLegCandidates = new ArrayList<>();
//        List<EMF_ModelPart> BfrLegCandidates = new ArrayList<>();
//
//        for (EMF_ModelPart part :
//                thisEMFModel.childrenMap.values()) {
//            switch (part.selfModelData.part) {
//                case "head" -> {
//                    headCandidates.add(part);
//                }
//                case "body" -> {
//                    bodyCandidates.add(part);
//                }
//                case "neck" -> {
//                    neckCandidates.add(part);
//                }
//                case "back_left_leg" -> {
//                    blLegCandidates.add(part);
//                }
//                case "back_right_leg" -> {
//                    brLegCandidates.add(part);
//                }
//                case "front_left_leg" -> {
//                    flLegCandidates.add(part);
//                }
//                case "front_right_leg" -> {
//                    frLegCandidates.add(part);
//                }
//                case "tail" -> {
//                    tailCandidates.add(part);
//                }
//                case "saddle" -> {
//                    saddleCandidates.add(part);
//                }
//                case "mane" -> {
//                    maneCandidates.add(part);
//                }
//                case "mouth" -> {
//                    mouthCandidates.add(part);
//                }
//                case "left_ear" -> {
//                    lEarCandidates.add(part);
//                }
//                case "right_ear" -> {
//                    rEarCandidates.add(part);
//                }
//                case "left_bit" -> {
//                    lBitCandidates.add(part);
//                }
//                case "right_bit" -> {
//                    rBitCandidates.add(part);
//                }
//                case "right_rein" -> {
//                    rReinCandidates.add(part);
//                }
//                case "left_rein" -> {
//                    lReinCandidates.add(part);
//                }
//                case "headpiece" -> {
//                    headPieceCandidates.add(part);
//                }
//                case "noseband" -> {
//                    noseBandCandidates.add(part);
//                }
//                case "child_back_left_leg" -> {
//                    BblLegCandidates.add(part);
//                }
//                case "child_back_right_leg" -> {
//                    BbrLegCandidates.add(part);
//                }
//                case "child_front_left_leg" -> {
//                    BflLegCandidates.add(part);
//                }
//                case "child_front_right_leg" -> {
//                    BfrLegCandidates.add(part);
//                }
//                default -> {
//
//                }
//            }
//        }

//        setNonEmptyPart(neckCandidates, ((HorseEntityModelAccessor) this)::setHead);
//        setNonEmptyPart(bodyCandidates, ((HorseEntityModelAccessor) this)::setBody);
//        setNonEmptyPart(flLegCandidates, ((HorseEntityModelAccessor) this)::setLeftFrontLeg);
//        setNonEmptyPart(frLegCandidates, ((HorseEntityModelAccessor) this)::setRightFrontLeg);
//        setNonEmptyPart(blLegCandidates, ((HorseEntityModelAccessor) this)::setLeftHindLeg);
//        setNonEmptyPart(brLegCandidates, ((HorseEntityModelAccessor) this)::setRightHindLeg);
//
//        setNonEmptyPart(BflLegCandidates, ((HorseEntityModelAccessor) this)::setLeftFrontBabyLeg);
//        setNonEmptyPart(BblLegCandidates, ((HorseEntityModelAccessor) this)::setLeftHindBabyLeg);
//        setNonEmptyPart(BfrLegCandidates, ((HorseEntityModelAccessor) this)::setRightFrontBabyLeg);
//        setNonEmptyPart(BbrLegCandidates, ((HorseEntityModelAccessor) this)::setRightHindBabyLeg);
//        setNonEmptyPart(tailCandidates, ((HorseEntityModelAccessor) this)::setTail);
//
//        ModelPart[] saddle = ((HorseEntityModelAccessor) this).getSaddle();
//        ModelPart sad = getNonEmptyPart(saddleCandidates);
//        if(sad != null)
//            saddle[0] = sad;
//        ModelPart sad1 = getNonEmptyPart(lBitCandidates);
//        if(sad1 != null)
//            saddle[1] = sad1;
//        ModelPart sad2 = getNonEmptyPart(rBitCandidates);
//        if(sad2 != null)
//            saddle[2] = sad2;
//        ModelPart sad3 = getNonEmptyPart(headPieceCandidates);
//        if(sad3 != null)
//            saddle[3] = sad3;
//        ModelPart sad4 = getNonEmptyPart(noseBandCandidates);
//        if(sad4 != null)
//            saddle[4] = sad4;
//        ((HorseEntityModelAccessor) this).setSaddle(saddle);
//
//        ModelPart[] straps = ((HorseEntityModelAccessor) this).getStraps();
//        ModelPart lrein = getNonEmptyPart(lReinCandidates);
//        if(lrein != null)
//            straps[0] = lrein;
//        ModelPart rrein = getNonEmptyPart(rReinCandidates);
//        if(rrein != null)
//            straps[1] = rrein;
//        ((HorseEntityModelAccessor) this).setStraps(straps);

        //setPart(saddleCandidates,((HorseEntityModelAccessor)this)::setSaddle);
        //setPart(st,((HorseEntityModelAccessor)this)::setStraps);
    }




    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

//    @Override
//    public void setAngles(AbstractHorseEntity abstractHorseEntity, float f, float g, float h, float i, float j) {
//        //super.setAngles(abstractHorseEntity, f, g, h, i, j);
//        setAngles((T)abstractHorseEntity, f, g, h, i, j);
//    }

    @Override
    public void setAngles(AbstractHorseEntity livingEntity, float f, float g, float h, float i, float j) {
        thisEMFModel.child = child;
        thisEMFModel.riding = riding;
        thisEMFModel.handSwingProgress = handSwingProgress;
        thisEMFModel.setAngles((T) livingEntity, f, g, h, i, j);
    }

//    @Override
//    public void animateModel(AbstractHorseEntity abstractHorseEntity, float f, float g, float h) {
//        //super.animateModel(abstractHorseEntity, f, g, h);
//        animateModel((T)abstractHorseEntity, f, g, h);
//    }

    @Override
    public void animateModel(AbstractHorseEntity livingEntity, float f, float g, float h) {
        thisEMFModel.animateModel((T) livingEntity, f, g, h);
    }
}
