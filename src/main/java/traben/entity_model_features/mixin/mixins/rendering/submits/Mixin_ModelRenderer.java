package traben.entity_model_features.mixin.mixins.rendering.submits;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.utils.HoldsBackupEMFRenderState;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;

@Mixin(ModelFeatureRenderer.class)
public class Mixin_ModelRenderer {

    @Inject(method = "renderModel", at = @At(value = "HEAD"))
    private <S> void emf$initRender(final CallbackInfo ci, @Local(argsOnly = true) SubmitNodeStorage.ModelSubmit<S> modelSubmit) {
        var state = modelSubmit.state();
        if (state instanceof HoldsETFRenderState holds && holds.etf$getState() != null) {
            EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState) holds.etf$getState());
            EMFAnimationEntityContext.setLayerFactory(modelSubmit.model().renderType);
        } else if (((Object) modelSubmit) instanceof HoldsBackupEMFRenderState emf) { // block entity backup
            var state2 = emf.emf$getState();
            EMFAnimationEntityContext.setCurrentEntityIteration(state2);
            EMFAnimationEntityContext.setLayerFactory(modelSubmit.model().renderType);
        } else {
            EMFAnimationEntityContext.reset();
        }

        var light = modelSubmit.lightCoords();
        if (light == ETF.EMISSIVE_FEATURE_LIGHT_VALUE || light == EMF.EYES_FEATURE_LIGHT_VALUE) {
            ETFRenderContext.startSpecialRenderOverlayPhase();
        } else {
            ETFRenderContext.endSpecialRenderOverlayPhase();
        }
    }

    @Inject(method = "renderTranslucents", at = @At(value = "TAIL"))
    private void emf$endRender(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
        ETFRenderContext.endSpecialRenderOverlayPhase();
    }

    @Inject(method = "renderBatch", at = @At(value = "TAIL"))
    private void emf$endRender2(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
        ETFRenderContext.endSpecialRenderOverlayPhase();
    }

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_ModelRenderer { }
//#endif