package traben.entity_model_features.mixin.rendering.arrows;


import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin(StuckObjectsFeatureRenderer.class)
public abstract class MixinStuckArrowsFeatureRenderer {


    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void emf$start(CallbackInfo ci) {
        EMFAnimationEntityContext.is_in_ground_override = true;
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "RETURN"))
    private void emf$end(CallbackInfo ci) {
        EMFAnimationEntityContext.is_in_ground_override = false;
    }


}
