package traben.entity_model_features.models;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public interface EMFCustomEntityModel<T extends LivingEntity> {

    EMFGenericEntityEntityModel<T> getThisEMFModel() ;

    boolean doesThisModelNeedToBeReset();

    boolean forceRecheckModel_currentlyOnlyTrueForPufferFish = false;

   // EMF_EntityModel<? extends LivingEntity> thisEMFModel = null;

    void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha);

    void setAngles(T livingEntity, float f, float g, float h, float i, float j);

    void animateModel(T livingEntity, float f, float g, float h);

    default void setNonEmptyPart(List<EMFModelPart> parts, PartSetter setter){
        for (EMFModelPart part:
                parts) {
            if(!part.isEmptyPart){
                setter.setPart(part);
                //((BipedEntityModelAccessor)this).setHat(part);
                break;
            }
        }
    }

    default EMFModelPart getNonEmptyPart(List<EMFModelPart> parts){
        for (EMFModelPart part:
                parts) {
            if(!part.isEmptyPart){
                return part;
                //setter.setPart(part);
                //((BipedEntityModelAccessor)this).setHat(part);
                //break;
            }
        }
        return null;
    }

    interface PartSetter{
         void setPart(EMFModelPart part);
    }

}
