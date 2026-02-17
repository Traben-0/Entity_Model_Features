package traben.entity_model_features.mixin.mixins.rendering.submits;

import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFSubmitData;
import com.llamalad7.mixinextras.sugar.Local;
import traben.entity_texture_features.features.state.HoldsETFRenderState;

@Mixin(HumanoidArmorLayer.class)
public class Mixin_HumanoidArmorLayer_AddBaseModelPoseRef<S extends HumanoidRenderState> {

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
            at = @At(value = "HEAD"))
    private void emf$initRender(final CallbackInfo ci, @Local(argsOnly = true) S humanoidRenderState) {
        if (humanoidRenderState instanceof HoldsETFRenderState etf && etf.etf$getState() != null) {
            EMFSubmitData.AWAITING_bipedPose = ((EMFEntityRenderState) etf.etf$getState()).getBipedPose();
        }
    }

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
            at = @At(value = "TAIL"))
    private void emf$endRender(final CallbackInfo ci) {
        EMFSubmitData.AWAITING_bipedPose = null;
    }

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_HumanoidArmorLayer_AddBaseModelPoseRef { }
//#endif