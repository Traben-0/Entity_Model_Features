package traben.entity_model_features.models;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import traben.entity_model_features.mixin.accessor.BipedEntityModelAccessor;

import java.util.List;

public interface EMFCustomModel<T extends LivingEntity> {

    EMF_EntityModel<T> getThisEMFModel() ;

    boolean doesThisModelNeedToBeReset();

   // EMF_EntityModel<? extends LivingEntity> thisEMFModel = null;

    void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha);

    void setAngles(T livingEntity, float f, float g, float h, float i, float j);

    void animateModel(T livingEntity, float f, float g, float h);

    default void setPart(List<EMF_ModelPart> parts, PartSetter setter){
        for (EMF_ModelPart part:
                parts) {
            if(!part.isEmptyPart){
                setter.setPart(part);
                //((BipedEntityModelAccessor)this).setHat(part);
                break;
            }
        }
    }

    interface PartSetter{
         void setPart(EMF_ModelPart part);
    }
}
