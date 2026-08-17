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
import traben.entity_model_features.models.animation.state.EMFBipedPose;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.parts.EMFModelPartVanilla;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.features.state.ETFSubmitData;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import traben.entity_texture_features.utils.ETFEntity;

// Priority set for emf$endRender to go before the same target in ETF
@Mixin(value = ModelFeatureRenderer.class, priority = 900)
public class Mixin_ModelRenderer {


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

        ETFSubmitData data = ETFSubmitData.from(modelSubmit);
        if (data == null) return;
        EMFBipedPose pose = (EMFBipedPose) data.data.get("bipedPose");
        if (pose != null
                && modelSubmit.model() instanceof HumanoidModel<?> humanoidModel
                && !(modelSubmit.model().root() instanceof EMFModelPartRoot root && root.hasAnimation())
        ) {
            pose.applyTo(humanoidModel);
        }
    }


    //#if MC >= 26.2
    //$$ @Inject(method = "prepareModel", at = @At(value = "TAIL"))
    //$$ private void emf$endRender(final CallbackInfo ci) {
    //$$     var state = EMFState.state();
    //$$     if (state != null && state.isFirstPersonHand()) {
    //$$         ETFState.unMount();
    //$$     }
    //$$ }
    //#endif

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_ModelRenderer { }
//#endif