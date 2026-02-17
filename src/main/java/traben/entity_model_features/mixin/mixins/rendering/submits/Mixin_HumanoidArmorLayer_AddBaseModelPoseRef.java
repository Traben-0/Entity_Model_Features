package traben.entity_model_features.mixin.mixins.rendering.submits;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFSubmitData;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(HumanoidArmorLayer.class)
public class Mixin_HumanoidArmorLayer_AddBaseModelPoseRef<S extends HumanoidRenderState> {

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
            at = @At(value = "HEAD"))
    private void emf$initRender(final CallbackInfo ci, @Local(argsOnly = true) S humanoidRenderState) {
        var state = EMFEntityRenderState.from(humanoidRenderState);
        if (state != null) {
            EMFSubmitData.AWAITING_bipedPose = state.getBipedPose();
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