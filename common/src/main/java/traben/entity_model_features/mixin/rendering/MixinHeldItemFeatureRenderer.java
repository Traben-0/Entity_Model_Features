package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin(HeldItemFeatureRenderer.class)
public class MixinHeldItemFeatureRenderer {


    @Inject(method = "renderItem",
            at = @At(value = "HEAD"))
    private void emf$setHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.setInHand = true;
    }

    @Inject(method = "renderItem",
            at = @At(value = "TAIL"))
    private void emf$unsetHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.setInHand = false;
    }

}