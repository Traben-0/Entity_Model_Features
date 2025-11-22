package traben.entity_model_features.mixin.mixins.rendering.submits;

import org.spongepowered.asm.mixin.Mixin;

//#if MC>=12109
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.utils.HoldsBackupEMFRenderState;

@Mixin(SubmitNodeStorage.ModelSubmit.class)
public class Mixin_ModelSubmit_AddBackupState implements HoldsBackupEMFRenderState {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void emf$init(CallbackInfo ci) {
        var emf = EMFManager.getInstance();
        if (emf.awaitingState != null) {
            // this is for those dumb block entities that don't pass the state through because they only need 1 primitive of state data
            this.state = emf.awaitingState;
        }
    }

    @Unique
    private EMFEntityRenderState state = null;

    @Override
    public void emf$setState(final EMFEntityRenderState state) {
        this.state = state;
    }

    @Override
    public EMFEntityRenderState emf$getState() {
        return state;
    }
}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_ModelSubmit_AddBackupState { }
//#endif
