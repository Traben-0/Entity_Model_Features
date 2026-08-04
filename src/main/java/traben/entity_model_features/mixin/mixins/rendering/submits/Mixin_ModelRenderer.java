package traben.entity_model_features.mixin.mixins.rendering.submits;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFSubmitData;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.animation.state.EMFSubmitExtension;
import traben.entity_model_features.models.parts.EMFModelPartVanilla;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(ModelFeatureRenderer.class)
public class Mixin_ModelRenderer {

    @Unique
    private <S> void headLogic(
            //#if MC >= 26.2
            //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit<S> modelSubmit
            //#else
            net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<S> modelSubmit
            //#endif
    ) {
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

        if (state instanceof net.minecraft.client.renderer.entity.state.ItemFrameRenderState) {
            EMFAnimationEntityContext.setInItemFrame = true;
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

            if (modelSubmit.model() instanceof IEMFModel emfModel && emfModel.emf$isEMFModel()) {
                if (data.isMainModelPhase) {
                    emfModel.emf$getEMFRootModel().isMainModel = true;
                    EMFAnimationEntityContext.unsetLayerPhase();
                }
                if (data.isLayerModelPhase) {
                    EMFAnimationEntityContext.setLayerPhase();
                }
            }
        }

        //#if MC >= 26.2
        //$$ // Replaces logic for model part submits that now wrap in simple models
        //$$ if (modelSubmit.model().root() instanceof EMFModelPartVanilla partVanilla && partVanilla.getRoot() != partVanilla) {
        //$$     EMFManager.getInstance().entityRenderCount++;
        //$$
        //$$     if (partVanilla.isPlayerArm) {
        //$$         if (Minecraft.getInstance().player != null)
        //$$             EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState)
        //$$                     ETFEntityRenderState.forEntity((ETFEntity) Minecraft.getInstance().player));
        //$$         EMFAnimationEntityContext.isFirstPersonHand = true;
        //$$     }
        //$$ }
        //#endif

    }

    @Inject(method =
            //#if MC >= 26.2
            //$$ "prepareModel",
            //#else
            "renderModel",
            //#endif
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;setupAnim(Ljava/lang/Object;)V",
            shift = At.Shift.AFTER))
    private <S> void emf$animate(final CallbackInfo ci,
                                 @Local(argsOnly = true)
                                 //#if MC >= 26.2
                                 //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit<S> modelSubmit
                                 //#else
                                 net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<S> modelSubmit
                                 //#endif

    ) {

        // Apply a simple pose copy to armor if required
        applyArmorBipedPose(modelSubmit);
    }

    @Unique
    private <S> void applyArmorBipedPose(
            //#if MC >= 26.2
            //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit<S> modelSubmit
            //#else
            net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<S> modelSubmit
            //#endif

    ) {

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
    private <S> boolean setModelVariant(@Nullable EMFSubmitData data,
                                        //#if MC >= 26.2
                                        //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit<S> modelSubmit
                                        //#else
                                        net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<S> modelSubmit
                                        //#endif

    ) {
        if (data != null && data.modelVariant != -1 && modelSubmit.model().root() instanceof EMFModelPartRoot root) {
            root.setVariantStateTo(data.modelVariant);
            return true;
        }
        return false;
    }

    @Unique
    private static void tailLogic() {
        EMFAnimationEntityContext.reset();
        EMFAnimationEntityContext.setCurrentEntityOnShoulder(false);
        EMFAnimationEntityContext.setInItemFrame = true;
        EMFAnimationEntityContext.isFirstPersonHand = false;
    }

    //#if MC >= 26.2
    //$$ @Inject(method = "prepareModel", at = @At(value = "HEAD"))
    //$$ private <S> void emf$initRender(final CallbackInfo ci, @Local(argsOnly = true) net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit<S> modelSubmit) {
    //$$     headLogic(modelSubmit);
    //$$ }
    //$$
    //$$ @Inject(method = "prepareModel", at = @At(value = "TAIL"))
    //$$ private void emf$endRender(final CallbackInfo ci) {
    //$$     tailLogic();
    //$$ }
    //#else
    @Inject(method = "renderModel", at = @At(value = "HEAD"))
    private <S> void emf$initRender(final CallbackInfo ci, @Local(argsOnly = true) net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<S> modelSubmit) {
        headLogic(modelSubmit);
    }

    @Inject(method = "renderTranslucents", at = @At(value = "TAIL"))
    private void emf$endRender(final CallbackInfo ci) {
        tailLogic();
    }

    @Inject(method = "renderBatch", at = @At(value = "TAIL"))
    private void emf$endRender2(final CallbackInfo ci) {
        tailLogic();
    }
    //#endif

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_ModelRenderer { }
//#endif