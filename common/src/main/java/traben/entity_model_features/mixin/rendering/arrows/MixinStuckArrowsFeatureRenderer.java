package traben.entity_model_features.mixin.rendering.arrows;


import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin(StuckInBodyLayer.class)
public abstract class MixinStuckArrowsFeatureRenderer {


    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void emf$start(CallbackInfo ci) {
        EMFAnimationEntityContext.is_in_ground_override = true;
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "RETURN"))
    private void emf$end(CallbackInfo ci) {
        EMFAnimationEntityContext.is_in_ground_override = false;
    }


}
