package traben.entity_model_features.mixin.mixins.rendering.submits;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109 && MC < 26.2
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_model_features.models.parts.EMFModelPartVanilla;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ModelPartFeatureRenderer;
import traben.entity_texture_features.features.state.ETFState;

@Mixin(ModelPartFeatureRenderer.class)
public class Mixin_ModelPartRenderer {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;last()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;"))
    private void emf$initRender(final CallbackInfo ci, @Local SubmitNodeStorage.ModelPartSubmit modelSubmit) {
        EMFManager.getInstance().entityRenderCount++;


        if (modelSubmit.modelPart() instanceof EMFModelPartVanilla vanilla && vanilla.isPlayerArm) {
            if (Minecraft.getInstance().player != null) {
                EMFState.modelVariationIgnoresVisibility = true;
                var state = EMFEntityRenderState.manualPlayerState();
                if (state != null) ETFState.mount(state);
            }
            if (EMFState.state() != null) EMFState.state().setIsFirstPersonHand(true);
        }
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void emf$endRender(final CallbackInfo ci) {
            if (EMFState.state().isManualPlayerState()) {
                ETFState.unMount();
            }
    }

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_ModelPartRenderer { }
//#endif