package traben.entity_model_features.mixin;


import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

/**
 * This mixin is used to get the current FOV value from the game renderer.
 * less impactful than collecting all the values required and calling the method itself
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "getFov",
            at = @At(value = "RETURN"))
    private void emf$captureFov(final CallbackInfoReturnable< #if MC > MC_21 Float #else Double #endif > cir) {
        if (EMF.config().getConfig().animationLODDistance != 0) {
            EMFAnimationEntityContext.lastFOV = (double) cir.getReturnValue();
        }
    }

    @Inject(method = "render",
            at = @At(value = "HEAD"))
    private void emf$injectCounter(final CallbackInfo ci) {
        EMFAnimationEntityContext.incFrameCount();
    }
}