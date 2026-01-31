package traben.entity_model_features.mixin.mixins.rendering.arrows;

//#if MC >= 1.21.9

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;

@Mixin(ThrownTridentRenderer.class)
public abstract class Mixin_TridentRenderer_PassState {

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "HEAD"))
    private void emf$backupState(CallbackInfo ci, @Local(argsOnly = true) ThrownTridentRenderState thrownTridentRenderState) {
        EMFManager.getInstance().awaitingState = (EMFEntityRenderState) ((HoldsETFRenderState)thrownTridentRenderState).etf$getState();
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "TAIL"))
    private void emf$backupStateClear(CallbackInfo ci) {
        EMFManager.getInstance().awaitingState = null;
    }
}
//#else
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Mixin(CancelTarget.class)
//$$ public class Mixin_TridentRenderer_PassState { }
//#endif



