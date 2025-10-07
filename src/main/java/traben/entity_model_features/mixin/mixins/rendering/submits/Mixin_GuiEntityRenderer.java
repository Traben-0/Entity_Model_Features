package traben.entity_model_features.mixin.mixins.rendering.submits;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(GuiEntityRenderer.class)
public class Mixin_GuiEntityRenderer {

    @Inject(method = "renderToTexture(Lnet/minecraft/client/gui/render/state/pip/GuiEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At(value = "HEAD"))
    private void emf$initRender(final CallbackInfo ci, @Local(argsOnly = true) GuiEntityRenderState guiEntityRenderState) {
        // so the entity dispatcher can use this state
        assertEmfState(guiEntityRenderState);
    }
    @Inject(method = "renderToTexture(Lnet/minecraft/client/gui/render/state/pip/GuiEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures()V"))
    private void emf$initRender2(final CallbackInfo ci, @Local(argsOnly = true) GuiEntityRenderState guiEntityRenderState) {
        // things get reset by the render dispatcher, re-assert before the actual render
        assertEmfState(guiEntityRenderState);
    }

    @Unique
    private static void assertEmfState(final GuiEntityRenderState guiEntityRenderState) {
        var state = guiEntityRenderState.renderState();
        if (state instanceof HoldsETFRenderState holds && holds.etf$getState() != null) {
            var emf = (EMFEntityRenderState) holds.etf$getState();
            EMFAnimationEntityContext.setCurrentEntityIteration(emf);
            if (emf != null) EMFAnimationEntityContext.setLayerFactory(emf.layerFactory());
        } else {
            EMFAnimationEntityContext.reset();
        }

        var light = state.lightCoords;
        if (light == ETF.EMISSIVE_FEATURE_LIGHT_VALUE || light == EMF.EYES_FEATURE_LIGHT_VALUE) {
            ETFRenderContext.startSpecialRenderOverlayPhase();
        } else {
            ETFRenderContext.endSpecialRenderOverlayPhase();
        }
    }

    @Inject(method = "renderToTexture(Lnet/minecraft/client/gui/render/state/pip/GuiEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At(value = "TAIL"))
    private void emf$endRender(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
        ETFRenderContext.endSpecialRenderOverlayPhase();
    }

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_GuiEntityRenderer { }
//#endif