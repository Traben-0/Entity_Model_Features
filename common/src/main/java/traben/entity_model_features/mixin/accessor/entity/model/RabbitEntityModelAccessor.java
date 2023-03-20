package traben.entity_model_features.mixin.accessor.entity.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RabbitEntityModel.class)
public interface RabbitEntityModelAccessor {
    @Accessor
    ModelPart getLeftHindLeg();

    @Accessor
    ModelPart getRightHindLeg();

    @Accessor
    ModelPart getLeftHaunch();

    @Accessor
    ModelPart getRightHaunch();

    @Accessor
    ModelPart getBody();

    @Accessor
    ModelPart getLeftFrontLeg();

    @Accessor
    ModelPart getRightFrontLeg();

    @Accessor
    ModelPart getHead();

    @Accessor
    ModelPart getRightEar();

    @Accessor
    ModelPart getLeftEar();

    @Accessor
    ModelPart getTail();

    @Accessor
    ModelPart getNose();
}
