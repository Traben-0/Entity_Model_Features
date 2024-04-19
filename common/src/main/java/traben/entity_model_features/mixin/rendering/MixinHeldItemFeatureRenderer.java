package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.EMFAttachments;

@Mixin(HeldItemFeatureRenderer.class)
public class MixinHeldItemFeatureRenderer {

    @Unique
    private EMFAttachments emf$attachment = null;
    @Unique
    private boolean emf$needsPop = false;

    @Inject(method = "renderItem",
            at = @At(value = "HEAD"))
    private void emf$setHand(final LivingEntity entity, final ItemStack stack, final ModelTransformationMode transformationMode, final Arm arm, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light, final CallbackInfo ci) {
        EMFAnimationEntityContext.setInHand = true;
        emf$attachment = arm == Arm.RIGHT ? EMFAttachments.right_handheld_item : EMFAttachments.left_handheld_item;

    }

    @Inject(
            method = "renderItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ModelWithArms;setArmAngle(Lnet/minecraft/util/Arm;Lnet/minecraft/client/util/math/MatrixStack;)V",
                    shift = At.Shift.AFTER)
    )
    private void mixin(final LivingEntity entity, final ItemStack stack, final ModelTransformationMode transformationMode, final Arm arm, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light, final CallbackInfo ci) {
        if (emf$attachment != null) {
            var entry = emf$attachment.getAndNullify();
            if (entry != null) {
                emf$needsPop = true;
                matrices.stack.addLast(entry);
            }
        }
    }

    @Inject(method = "renderItem",
            at = @At(value = "TAIL"))
    private void emf$unsetHand(final LivingEntity entity, final ItemStack stack, final ModelTransformationMode transformationMode, final Arm arm, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light, final CallbackInfo ci) {
        EMFAnimationEntityContext.setInHand = false;
        emf$attachment = null;
        if (emf$needsPop) {
            matrices.pop();
            emf$needsPop = false;
        }
    }

}