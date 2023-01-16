package traben.entity_model_features.models;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public interface EMFCustomModel<T extends LivingEntity> {

    EMF_EntityModel<T> getThisEMFModel() ;

    boolean doesThisModelNeedToBeReset();

   // EMF_EntityModel<? extends LivingEntity> thisEMFModel = null;

    void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha);

    void setAngles(T livingEntity, float f, float g, float h, float i, float j);

    void animateModel(T livingEntity, float f, float g, float h);

    default void setNonEmptyPart(List<EMF_ModelPart> parts, PartSetter setter){
        for (EMF_ModelPart part:
                parts) {
            if(!part.isEmptyPart){
                setter.setPart(part);
                //((BipedEntityModelAccessor)this).setHat(part);
                break;
            }
        }
    }

    default EMF_ModelPart getNonEmptyPart(List<EMF_ModelPart> parts){
        for (EMF_ModelPart part:
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
         void setPart(EMF_ModelPart part);
    }
}
