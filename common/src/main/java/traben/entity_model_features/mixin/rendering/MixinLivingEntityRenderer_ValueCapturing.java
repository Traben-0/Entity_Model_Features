package traben.entity_model_features.mixin.rendering;


import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_model_features.models.animation.EMFAnimationHelper;


@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer_ValueCapturing<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {




    protected MixinLivingEntityRenderer_ValueCapturing(EntityRendererFactory.Context ctx) {
        super(ctx);
    }



    @ModifyArg(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"),
            index = 1
    )
    private float emf$getLimbAngle(float limbAngle) {
        EMFAnimationHelper.setLimbAngle(limbAngle == Float.MIN_VALUE ? 0 : limbAngle);
        return limbAngle;
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"),
            index = 2
    )
    private float emf$getLimbDistance(float limbDistance) {
        EMFAnimationHelper.setLimbDistance(limbDistance == Float.MIN_VALUE ? 0 : limbDistance);
        return limbDistance;
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"),
            index = 4
    )
    private float emf$getHeadYaw(float headYaw) {
        if (headYaw > 180 || headYaw < -180) {
            float normalizedAngle = headYaw % 360;
            if (normalizedAngle > 180) {
                normalizedAngle -= 360;
            } else if (normalizedAngle < -180) {
                normalizedAngle += 360;
            }
            EMFAnimationHelper.setHeadYaw(normalizedAngle);
        } else {
            EMFAnimationHelper.setHeadYaw(headYaw);
        }
        return headYaw;
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"),
            index = 5
    )
    private float emf$getHeadPitch(float headPitch) {
        EMFAnimationHelper.setHeadPitch(headPitch);
        return headPitch;
    }

}
