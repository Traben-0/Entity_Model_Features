package traben.entity_model_features.mixin.accessor.entity.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LlamaEntityModel.class)
public interface LlamaEntityModelAccessor {
    @Accessor
    ModelPart getHead();

    @Accessor
    ModelPart getBody();

    @Accessor
    ModelPart getRightHindLeg();

    @Accessor
    ModelPart getLeftHindLeg();

    @Accessor
    ModelPart getRightFrontLeg();

    @Accessor
    ModelPart getLeftFrontLeg();

    @Accessor
    ModelPart getRightChest();

    @Accessor
    ModelPart getLeftChest();

    @Mutable
    @Accessor
    void setHead(ModelPart head);

    @Mutable
    @Accessor
    void setBody(ModelPart body);
}
