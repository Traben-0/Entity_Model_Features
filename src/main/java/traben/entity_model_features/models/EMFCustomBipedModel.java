package traben.entity_model_features.models;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import traben.entity_model_features.mixin.accessor.BipedEntityModelAccessor;

import java.util.function.Function;

public class EMFCustomBipedModel<T extends LivingEntity> extends BipedEntityModel<T> {

    public EMF_CustomModel<T> thisEMFModel = null;

    public EMFCustomBipedModel(ModelPart root) {
        super(root);
    }


    public void setEMFModel(EMF_CustomModel<T> model){
        thisEMFModel=model;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if(thisEMFModel == null) {
            super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }else{
            thisEMFModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        if(thisEMFModel == null) {
            super.setAngles(livingEntity, f, g, h, i, j);
        }else{
            thisEMFModel.child = child;
            thisEMFModel.sneaking = sneaking;
            thisEMFModel.riding = riding;
            thisEMFModel.handSwingProgress = handSwingProgress;
            thisEMFModel.setAngles(livingEntity, f, g, h, i, j);
        }
    }

    @Override
    public void animateModel(T livingEntity, float f, float g, float h) {
        super.animateModel(livingEntity, f, g, h);
        if(thisEMFModel == null) {
            super.animateModel(livingEntity, f, g, h);
        }else{
            thisEMFModel.animateModel(livingEntity, f, g, h);
        }
    }

    @Override
    public void copyBipedStateTo(BipedEntityModel<T> model) {
        if(thisEMFModel == null) {
            super.copyBipedStateTo(model);
        }else{
            model.leftArmPose = this.leftArmPose;
            model.rightArmPose = this.rightArmPose;
            model.sneaking = this.sneaking;
            model.child = this.child;
            //todo these need to be checking the part names not just their ids
            extend to parent parts probably at getting emf model
            if(thisEMFModel.childrenMap.containsKey("headwear")) {
                ((BipedEntityModelAccessor)this).setHat(thisEMFModel.childrenMap.get("headwear"));
                model.hat.copyTransform(hat);
            }
            if(thisEMFModel.childrenMap.containsKey("head")) {
                EMF_CustomModelPart<?> emfPart = thisEMFModel.childrenMap.get("head");
                //just if they dont have a head part try this
                // this is specifically for fresh animations having blank heads and putting it in headwear
                if(emfPart.isEmptyPart){
                    if(thisEMFModel.childrenMap.containsKey("headwear")) {
                        emfPart = thisEMFModel.childrenMap.get("headwear");
                    }
                }
                ((BipedEntityModelAccessor)this).setHead(emfPart);
                model.head.copyTransform(head);
            }
            if(thisEMFModel.childrenMap.containsKey("body")) {
                ((BipedEntityModelAccessor)this).setBody(thisEMFModel.childrenMap.get("body"));
                model.body.copyTransform(body);
            }
            if(thisEMFModel.childrenMap.containsKey("left_arm")) {
                ((BipedEntityModelAccessor) this).setLeftArm(thisEMFModel.childrenMap.get("left_arm"));
                model.leftArm.copyTransform(leftArm);
            }
            if(thisEMFModel.childrenMap.containsKey("left_leg")) {
                ((BipedEntityModelAccessor) this).setLeftLeg(thisEMFModel.childrenMap.get("left_leg"));
                model.leftLeg.copyTransform(leftLeg);
            }
            if(thisEMFModel.childrenMap.containsKey("right_arm")) {
                ((BipedEntityModelAccessor) this).setRightArm(thisEMFModel.childrenMap.get("right_arm"));
                model.rightArm.copyTransform(rightArm);
            }
            if(thisEMFModel.childrenMap.containsKey("right_leg")) {
                ((BipedEntityModelAccessor) this).setRightLeg(thisEMFModel.childrenMap.get("right_leg"));
                model.rightLeg.copyTransform(rightLeg);
            }
        }
    }
}
