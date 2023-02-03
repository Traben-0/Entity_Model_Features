package traben.entity_model_features.models.vanilla_model_children;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import traben.entity_model_features.mixin.accessor.entity.model.BipedEntityModelAccessor;
import traben.entity_model_features.mixin.accessor.ModelAccessor;
import traben.entity_model_features.mixin.accessor.entity.model.PlayerEntityModelAccessor;
import traben.entity_model_features.models.EMFArmorableModel;
import traben.entity_model_features.models.EMFCustomModel;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.EMF_ModelPart;

import java.util.ArrayList;
import java.util.List;

public class EMFCustomPlayerModel<T extends LivingEntity> extends PlayerEntityModel<T> implements EMFCustomModel<T>, EMFArmorableModel, ModelWithHat {

    public EMF_EntityModel<T> getThisEMFModel() {
        return thisEMFModel;
    }

    public boolean doesThisModelNeedToBeReset() {
        return false;
    }

    private final  EMF_EntityModel<T> thisEMFModel;


    public EMFCustomPlayerModel(EMF_EntityModel<T> model) {
        super(PlayerEntityModel.getTexturedModelData(Dilation.NONE,((PlayerEntityModelAccessor)model.vanillaModel).isThinArms()).getRoot().createPart(0,0),((PlayerEntityModelAccessor)model.vanillaModel).isThinArms());
        thisEMFModel=model;
        ((ModelAccessor)this).setLayerFactory(getThisEMFModel()::getLayer2);

        List<EMF_ModelPart> headWearCandidates = new ArrayList<>();
        List<EMF_ModelPart> headCandidates = new ArrayList<>();
        List<EMF_ModelPart> bodyCandidates = new ArrayList<>();
        List<EMF_ModelPart> rArmCandidates = new ArrayList<>();
        List<EMF_ModelPart> lArmCandidates = new ArrayList<>();
        List<EMF_ModelPart> lLegCandidates = new ArrayList<>();
        List<EMF_ModelPart> rLegCandidates = new ArrayList<>();
        List<EMF_ModelPart> bodyXCandidates = new ArrayList<>();
        List<EMF_ModelPart> rArmXCandidates = new ArrayList<>();
        List<EMF_ModelPart> lArmXCandidates = new ArrayList<>();
        List<EMF_ModelPart> lLegXCandidates = new ArrayList<>();
        List<EMF_ModelPart> rLegXCandidates = new ArrayList<>();
        List<EMF_ModelPart> capeCandidates = new ArrayList<>();
        List<EMF_ModelPart> earsCandidates = new ArrayList<>();


//        List<EMF_ModelPart> rArmCandidates_slim = new ArrayList<>();
//        List<EMF_ModelPart> lArmCandidates_slim = new ArrayList<>();
//        List<EMF_ModelPart> rArmXCandidates_slim = new ArrayList<>();
//        List<EMF_ModelPart> lArmXCandidates_slim = new ArrayList<>();



        for (EMF_ModelPart part:
             thisEMFModel.childrenMap.values()) {
            switch (part.selfModelData.part){
                case "headwear"->{
                    headWearCandidates.add(part);
                }
                case "head"->{
                    headCandidates.add(part);
                }
                case "body"->{
                    bodyCandidates.add(part);
                }
                case "left_arm"->{
                    lArmCandidates.add(part);
                }
                case "left_leg"->{
                    lLegCandidates.add(part);
                }
//                case "left_arm_slim"->{
//                    lArmCandidates_slim.add(part);
//                }
//                case "right_arm_slim"->{
//                    rArmCandidates_slim.add(part);
//                }
                case "right_arm"->{
                    rArmCandidates.add(part);
                }
                case "right_leg"->{
                    rLegCandidates.add(part);
                }
                case "jacket"->{
                    bodyXCandidates.add(part);
                }
                case "left_sleeve"->{
                    lArmXCandidates.add(part);
                }
                case "left_pants"->{
                    lLegXCandidates.add(part);
                }
                case "right_sleeve"->{
                    rArmXCandidates.add(part);
                }
                case "right_pants"->{
                    rLegXCandidates.add(part);
                }
                case "ears"->{
                    earsCandidates.add(part);
                }
                case "cape"->{
                    capeCandidates.add(part);
                }
//                case "left_sleeve_slim"->{
//                    lArmXCandidates_slim.add(part);
//                }
//                case "right_sleeve_slim"->{
//                    rArmXCandidates_slim.add(part);
//                }
                default->{

                }
            }
        }

//        boolean thinArms = ((PlayerEntityModelAccessor)model.vanillaModel).isThinArms();
//        if(thinArms){
//            if(!rArmCandidates_slim.isEmpty()){
//                rArmCandidates.forEach(emf_modelPart -> emf_modelPart.visible = false);
//                rArmCandidates = rArmCandidates_slim;
//            }
//            if(!lArmCandidates_slim.isEmpty()){
//                lArmCandidates.forEach(emf_modelPart -> emf_modelPart.visible = false);
//                lArmCandidates = lArmCandidates_slim;
//            }
//            if(!rArmXCandidates_slim.isEmpty()){
//                rArmXCandidates.forEach(emf_modelPart -> emf_modelPart.visible = false);
//                rArmXCandidates = rArmXCandidates_slim;
//            }
//            if(!lArmXCandidates_slim.isEmpty()){
//                lArmXCandidates.forEach(emf_modelPart -> emf_modelPart.visible = false);
//                lArmXCandidates = lArmXCandidates_slim;
//            }
//        }else{
//            if(!rArmCandidates_slim.isEmpty()){
//                rArmCandidates_slim.forEach(emf_modelPart -> emf_modelPart.visible = false);
//            }
//            if(!lArmCandidates_slim.isEmpty()){
//                lArmCandidates_slim.forEach(emf_modelPart -> emf_modelPart.visible = false);
//            }
//            if(!rArmXCandidates_slim.isEmpty()){
//                rArmXCandidates_slim.forEach(emf_modelPart -> emf_modelPart.visible = false);
//            }
//            if(!lArmXCandidates_slim.isEmpty()){
//                lArmXCandidates_slim.forEach(emf_modelPart -> emf_modelPart.visible = false);
//            }
//        }

        //this part makes sure head rotation data is available for armor feature renderer
        // mostly as fresh animations tends to use headwear for rotation instead of head
        boolean wasNotEmpty = false;
        for (EMF_ModelPart part:
             headCandidates) {
            if(!part.isEmptyPart){
                wasNotEmpty = true;
                break;
            }
        }
        if(!wasNotEmpty){
            headCandidates = headWearCandidates;
        }

        setNonEmptyPart(headWearCandidates,((BipedEntityModelAccessor)this)::setHat);
        setNonEmptyPart(headCandidates,((BipedEntityModelAccessor)this)::setHead);
        setNonEmptyPart(bodyCandidates,((BipedEntityModelAccessor)this)::setBody);
        setNonEmptyPart(lArmCandidates,((BipedEntityModelAccessor)this)::setLeftArm);
        setNonEmptyPart(lLegCandidates,((BipedEntityModelAccessor)this)::setLeftLeg);
        setNonEmptyPart(rArmCandidates,((BipedEntityModelAccessor)this)::setRightArm);
        setNonEmptyPart(rLegCandidates,((BipedEntityModelAccessor)this)::setRightLeg);

        setNonEmptyPart(bodyXCandidates,((PlayerEntityModelAccessor)this)::setJacket);
        setNonEmptyPart(lArmXCandidates,((PlayerEntityModelAccessor)this)::setLeftSleeve);
        setNonEmptyPart(lLegXCandidates,((PlayerEntityModelAccessor)this)::setLeftPants);
        setNonEmptyPart(rArmXCandidates,((PlayerEntityModelAccessor)this)::setRightSleeve);
        setNonEmptyPart(rLegXCandidates,((PlayerEntityModelAccessor)this)::setRightPants);

        setNonEmptyPart(capeCandidates,((PlayerEntityModelAccessor)this)::setCloak);
        setNonEmptyPart(earsCandidates,((PlayerEntityModelAccessor)this)::setEar);

    }

//    private void setPart(List<EMF_ModelPart> parts, PartSetter setter){
//        for (EMF_ModelPart part:
//                parts) {
//            if(!part.isEmptyPart){
//                setter.setPart(part);
//                //((BipedEntityModelAccessor)this).setHat(part);
//                break;
//            }
//        }
//    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);

    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {

            thisEMFModel.child = child;
            thisEMFModel.sneaking = sneaking;
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
    public EMF_EntityModel<?> getArmourModel(boolean getInner) {
        return thisEMFModel.getArmourModel(getInner);
    }

    @Override
    public void setHatVisible(boolean visible) {
        thisEMFModel.setHatVisible(visible);
    }
}
