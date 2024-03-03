package traben.entity_model_features.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFFovSupplier;

/**
 * This mixin is used to get the current FOV value from the game renderer.
 * less impactful than collecting all the values required and calling the method itself
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer implements EMFFovSupplier {

    @Unique
    private double emf$fov = 70;

    @Inject(method = "getFov",
            at = @At(value = "RETURN"))
    private void emf$injectAnnouncer(final Camera camera, final float tickDelta, final boolean changingFov, final CallbackInfoReturnable<Double> cir) {
        if (EMFConfig.getConfig().animationLODDistance != 0) {
            emf$fov = cir.getReturnValue();
        }
    }

    @Override
    public double emf$getFov() {
        return emf$fov;
    }
}