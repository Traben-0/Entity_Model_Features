package traben.entity_model_features.mixin.mixins.rendering.submits.adjustments;

//#if MC >= 1.21.9

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFSubmitData;

@Mixin(BoatRenderer.class)
public abstract class Mixin_BoatRenderer_PassState {

    @Inject(method = "submitTypeAdditions",
            at = @At(value = "HEAD"))
    private void emf$backupState(CallbackInfo ci, @Local(argsOnly = true) BoatRenderState state) {
        EMFSubmitData.AWAITING_backupState = EMFEntityRenderState.from(state);
    }

    @Inject(method = "submitTypeAdditions",
            at = @At(value = "TAIL"))
    private void emf$backupStateClear(CallbackInfo ci) {
        EMFSubmitData.AWAITING_backupState = null;
    }
}
//#else
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Mixin(CancelTarget.class)
//$$ public class Mixin_BoatRenderer_PassState { }
//#endif



