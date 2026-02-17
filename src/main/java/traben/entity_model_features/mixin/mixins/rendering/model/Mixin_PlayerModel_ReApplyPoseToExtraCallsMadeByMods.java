package traben.entity_model_features.mixin.mixins.rendering.model;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;

@Mixin(value = PlayerModel.class)
public class Mixin_PlayerModel_ReApplyPoseToExtraCallsMadeByMods {

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V",
            at = @At(value = "TAIL"))
    private void emf$initRender(final CallbackInfo ci, @Local(argsOnly = true) AvatarRenderState avatarRenderState) {
        EMFEntityRenderState state = EMFEntityRenderState.from(avatarRenderState);
        if (state != null && state.getBipedPose() != null) {
            // this means that some mod called setupAnim on the player model sometime after we already did this for extra layers
            // not sure why but lets go with it and re-apply the animated pose to make sure it is correct for the extra layers

            // Note: Essential uses this for cosmetics
            state.getBipedPose().applyTo((PlayerModel) (Object) this);
        }
    }

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_PlayerModel_ReApplyPoseToExtraCallsMadeByMods { }
//#endif