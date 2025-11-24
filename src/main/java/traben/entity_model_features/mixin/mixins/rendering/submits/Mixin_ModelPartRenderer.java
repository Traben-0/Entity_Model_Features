package traben.entity_model_features.mixin.mixins.rendering.submits;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.parts.EMFModelPartVanilla;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ModelPartFeatureRenderer;

@Mixin(ModelPartFeatureRenderer.class)
public class Mixin_ModelPartRenderer {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;last()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;"))
    private void emf$initRender(final CallbackInfo ci, @Local SubmitNodeStorage.ModelPartSubmit modelSubmit) {
        EMFManager.getInstance().entityRenderCount++;
        var light = modelSubmit.lightCoords();
        if (light == ETF.EMISSIVE_FEATURE_LIGHT_VALUE || light == EMF.EYES_FEATURE_LIGHT_VALUE) {
            ETFRenderContext.startSpecialRenderOverlayPhase();
        } else {
            ETFRenderContext.endSpecialRenderOverlayPhase();
        }

        if (modelSubmit.modelPart() instanceof EMFModelPartVanilla vanilla && vanilla.isPlayerArm) {
            EMFAnimationEntityContext.isFirstPersonHand = true;
        }
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void emf$endRender(final CallbackInfo ci) {
        ETFRenderContext.endSpecialRenderOverlayPhase();
        EMFAnimationEntityContext.isFirstPersonHand = false;
    }

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_ModelPartRenderer { }
//#endif