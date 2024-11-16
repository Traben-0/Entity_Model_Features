package traben.entity_model_features.mixin.rendering.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.EMFAttachments;
#if MC > MC_21_2
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

#elif MC > MC_21
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.BakedModel;
#else
import net.minecraft.world.entity.LivingEntity;
#endif

@Mixin(ItemInHandLayer.class)
public class #if MC > MC_21_2  MixinHeldItemFeatureRenderer<S extends ArmedEntityRenderState, M extends EntityModel<S> & ArmedModel>
        #elif MC > MC_21 MixinHeldItemFeatureRenderer<S extends LivingEntityRenderState>
        #else MixinHeldItemFeatureRenderer #endif {

    @Unique
    private EMFAttachments emf$attachment = null;
    @Unique
    private boolean emf$needsPop = false;

    @Inject(method = "renderArmWithItem",
            at = @At(value = "HEAD"))
    private void emf$setHand(
            #if MC > MC_21_2
            final S armedEntityRenderState, final ItemStackRenderState itemStackRenderState, final HumanoidArm arm, final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final CallbackInfo ci
            #elif MC > MC_21
            final S livingEntityRenderState, final BakedModel bakedModel, final ItemStack itemStack, final ItemDisplayContext itemDisplayContext, final HumanoidArm arm, final PoseStack matrices, final MultiBufferSource multiBufferSource, final int i, final CallbackInfo ci
            #else
            final LivingEntity entity, final ItemStack stack, final ItemDisplayContext transformationMode, final HumanoidArm arm, final PoseStack matrices, final MultiBufferSource vertexConsumers, final int light, final CallbackInfo ci
            #endif ) {
        EMFAnimationEntityContext.setInHand = true;
        emf$attachment = arm == HumanoidArm.RIGHT ? EMFAttachments.right_handheld_item : EMFAttachments.left_handheld_item;
    }

    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ArmedModel;translateToHand(Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
                    shift = At.Shift.AFTER)
    )
    private void emf$transforms(
            #if MC > MC_21_2
            final S armedEntityRenderState, final ItemStackRenderState itemStackRenderState, final HumanoidArm arm, final PoseStack matrices, final MultiBufferSource multiBufferSource, final int i, final CallbackInfo ci
            #elif MC > MC_21
            final S livingEntityRenderState, final BakedModel bakedModel, final ItemStack itemStack, final ItemDisplayContext itemDisplayContext, final HumanoidArm humanoidArm, final PoseStack matrices, final MultiBufferSource multiBufferSource, final int i, final CallbackInfo ci
            #else
            final LivingEntity entity, final ItemStack stack, final ItemDisplayContext transformationMode, final HumanoidArm arm, final PoseStack matrices, final MultiBufferSource vertexConsumers, final int light, final CallbackInfo ci
            #endif) {
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
    private void emf$unsetHand(
            #if MC > MC_21_2
            final S armedEntityRenderState, final ItemStackRenderState itemStackRenderState, final HumanoidArm arm, final PoseStack matrices, final MultiBufferSource multiBufferSource, final int i, final CallbackInfo ci
            #elif MC > MC_21
            final S livingEntityRenderState, final BakedModel bakedModel, final ItemStack itemStack, final ItemDisplayContext itemDisplayContext, final HumanoidArm humanoidArm, final PoseStack matrices, final MultiBufferSource multiBufferSource, final int i, final CallbackInfo ci
            #else
            final LivingEntity entity, final ItemStack stack, final ItemDisplayContext transformationMode, final HumanoidArm arm, final PoseStack matrices, final MultiBufferSource vertexConsumers, final int light, final CallbackInfo ci
            #endif ) {
        EMFAnimationEntityContext.setInHand = false;
        emf$attachment = null;
        if (emf$needsPop) {
            matrices.popPose();
            emf$needsPop = false;
        }
    }

    @Inject(method =
#if MC > MC_21_2
            "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/ArmedEntityRenderState;FF)V",
#elif MC > MC_21
            "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V",
#else
            "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
#endif
            at = @At(value = "TAIL"))
    private void emf$unsetHand(final CallbackInfo ci) {
        EMFAttachments.closeBoth();
    }

}