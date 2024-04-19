package traben.entity_model_features.mixin.optional;


import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;


@Mixin(value = LivingEntityRenderer.class, priority = 2000)
public abstract class MixinLivingEntityRenderer_ValueCapturing<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {


    @SuppressWarnings("unused")
    protected MixinLivingEntityRenderer_ValueCapturing(EntityRendererFactory.Context ctx) {
        super(ctx);
    }


    @ModifyArg(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"),
            index = 1
    )
    private float emf$getLimbAngle(float limbAngle) {
        EMFAnimationEntityContext.setLimbAngle(limbAngle == Float.MIN_VALUE ? 0 : limbAngle);
        return limbAngle;
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"),
            index = 2
    )
    private float emf$getLimbDistance(float limbDistance) {
        EMFAnimationEntityContext.setLimbDistance(limbDistance == Float.MIN_VALUE ? 0 : limbDistance);
        return limbDistance;
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"),
            index = 4
    )
    private float emf$getHeadYaw(float headYaw) {
        if (headYaw >= 180 || headYaw < -180) {
            EMFAnimationEntityContext.setHeadYaw(MathHelper.wrapDegrees(headYaw));
        } else {
            EMFAnimationEntityContext.setHeadYaw(headYaw);
        }
        return headYaw;
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"),
            index = 5
    )
    private float emf$getHeadPitch(float headPitch) {
        EMFAnimationEntityContext.setHeadPitch(headPitch);
        return headPitch;
    }

}
