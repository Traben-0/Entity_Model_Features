package traben.entity_model_features.mixin.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.EMFAttachments;

@Mixin(ItemInHandLayer.class)
public class MixinHeldItemFeatureRenderer {

    @Unique
    private EMFAttachments emf$attachment = null;
    @Unique
    private boolean emf$needsPop = false;

    @Inject(method = "renderArmWithItem",
            at = @At(value = "HEAD"))
    private void emf$setHand(final LivingEntity entity, final ItemStack stack, final ItemDisplayContext transformationMode, final HumanoidArm arm, final PoseStack matrices, final MultiBufferSource vertexConsumers, final int light, final CallbackInfo ci) {
        EMFAnimationEntityContext.setInHand = true;
        emf$attachment = arm == HumanoidArm.RIGHT ? EMFAttachments.right_handheld_item : EMFAttachments.left_handheld_item;
    }

    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ArmedModel;translateToHand(Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
                    shift = At.Shift.AFTER)
    )
    private void emf$transforms(final LivingEntity entity, final ItemStack stack, final ItemDisplayContext transformationMode, final HumanoidArm arm, final PoseStack matrices, final MultiBufferSource vertexConsumers, final int light, final CallbackInfo ci) {
        if (emf$attachment != null) {
            var entry = emf$attachment.getAndNullify();
            if (entry != null) {
                emf$needsPop = true;
                matrices.poseStack.addLast(entry);
            }
        }
    }

    @Inject(method = "renderArmWithItem",
            at = @At(value = "TAIL"))
    private void emf$unsetHand(final LivingEntity entity, final ItemStack stack, final ItemDisplayContext transformationMode, final HumanoidArm arm, final PoseStack matrices, final MultiBufferSource vertexConsumers, final int light, final CallbackInfo ci) {
        EMFAnimationEntityContext.setInHand = false;
        emf$attachment = null;
        if (emf$needsPop) {
            matrices.popPose();
            emf$needsPop = false;
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "TAIL"))
    private void emf$unsetHand(final CallbackInfo ci) {
        EMFAttachments.closeBoth();
    }

}