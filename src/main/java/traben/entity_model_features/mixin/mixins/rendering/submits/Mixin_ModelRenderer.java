package traben.entity_model_features.mixin.mixins.rendering.submits;

import net.minecraft.client.model.HumanoidModel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFSubmitData;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.animation.state.EMFSubmitExtension;
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
        EMFSubmitData data = EMFSubmitData.from(modelSubmit);

        // Set up the current entity context for this render
        if (state instanceof HoldsETFRenderState holds && holds.etf$getState() != null) {
            var state2 = (EMFEntityRenderState) holds.etf$getState();
            EMFAnimationEntityContext.setCurrentEntityIteration(state2, setModelVariant(data, modelSubmit));
            ETFRenderContext.setCurrentEntity(state2);
            EMFAnimationEntityContext.setLayerFactory(modelSubmit.model().renderType);
            state2.setBipedPose(null);
        } else if (data != null && data.backupState != null) { // block entity backup
            var state2 = data.backupState;
            EMFAnimationEntityContext.setCurrentEntityIteration(state2, setModelVariant(data, modelSubmit));
            EMFAnimationEntityContext.setLayerFactory(modelSubmit.model().renderType);
            ETFRenderContext.setCurrentEntity(state2);
            state2.setBipedPose(null);
        } else {
            EMFAnimationEntityContext.reset();
        }

        // Handle emissive/eyes lighting setup
        var light = modelSubmit.lightCoords();
        if (light == ETF.EMISSIVE_FEATURE_LIGHT_VALUE || light == EMF.EYES_FEATURE_LIGHT_VALUE) {
            ETFRenderContext.startSpecialRenderOverlayPhase();
        } else {
            ETFRenderContext.endSpecialRenderOverlayPhase();
        }

        if (data != null) {
            EMFAnimationEntityContext.setCurrentEntityOnShoulder(data.onShoulder);
        }
    }

    @Inject(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;setupAnim(Ljava/lang/Object;)V",
            shift = At.Shift.AFTER))
    private <S> void emf$animate(final CallbackInfo ci, @Local(argsOnly = true) SubmitNodeStorage.ModelSubmit<S> modelSubmit) {

        // Apply a simple pose copy to armor if required
        applyArmorBipedPose(modelSubmit);
    }

    @Unique
    private <S> void applyArmorBipedPose(SubmitNodeStorage.ModelSubmit<S> modelSubmit) {

        EMFSubmitData data = EMFSubmitData.from(modelSubmit);
        if (data != null
                && data.bipedPose != null
                && modelSubmit.model() instanceof HumanoidModel<?> humanoidModel
                && !(modelSubmit.model().root() instanceof EMFModelPartRoot root && root.hasAnimation())
        ) {
            data.bipedPose.applyTo(humanoidModel);
        }
    }

    @Unique
    private <S> boolean setModelVariant(@Nullable EMFSubmitData data, SubmitNodeStorage.ModelSubmit<S> modelSubmit) {
        if (data != null && data.modelVariant != -1 && modelSubmit.model().root() instanceof EMFModelPartRoot root) {
            root.setVariantStateTo(data.modelVariant);
            return true;
        }
        return false;
    }

    @Inject(method = "renderTranslucents", at = @At(value = "TAIL"))
    private void emf$endRender(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
        ETFRenderContext.endSpecialRenderOverlayPhase();
        ETFRenderContext.reset();
        EMFAnimationEntityContext.setCurrentEntityOnShoulder(false);
    }

    @Inject(method = "renderBatch", at = @At(value = "TAIL"))
    private void emf$endRender2(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
        ETFRenderContext.endSpecialRenderOverlayPhase();
        ETFRenderContext.reset();
        EMFAnimationEntityContext.setCurrentEntityOnShoulder(false);
    }

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_ModelRenderer { }
//#endif