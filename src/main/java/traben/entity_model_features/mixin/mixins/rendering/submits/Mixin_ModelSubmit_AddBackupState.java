package traben.entity_model_features.mixin.mixins.rendering.submits;

import net.minecraft.client.model.Model;
import org.spongepowered.asm.mixin.Mixin;

//#if MC>=12109
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.HoldsBackupEMFRenderState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;

@Mixin(SubmitNodeStorage.ModelSubmit.class)
public abstract class Mixin_ModelSubmit_AddBackupState<S> implements HoldsBackupEMFRenderState {

    @Shadow
    public abstract S state();

    @Shadow
    public abstract Model<? super S> model();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void emf$init(CallbackInfo ci) {
        var emf = EMFManager.getInstance();
        if (emf.awaitingState != null) {
            // this is for those dumb block entities that don't pass the state through because they only need 1 primitive of state data
            this.emfState = emf.awaitingState;
        }

        if (state() instanceof HoldsETFRenderState etf) {
            if (model().root() instanceof EMFModelPartRoot emfRoot) {
                int variant = emfRoot.currentModelVariant;
                if (variant != -1) {
                    if (etf.etf$getState() instanceof EMFEntityRenderState emfEntityRenderState) {
                        emfEntityRenderState.setModelVariant(variant);
                    }
                    if (this.emfState != null) {
                        this.emfState.setModelVariant(variant);
                    }
                }
            }
        }
    }

    @Unique
    private EMFEntityRenderState emfState = null;

    @Override
    public void emf$setState(final EMFEntityRenderState state) {
        this.emfState = state;
    }

    @Override
    public EMFEntityRenderState emf$getState() {
        return emfState;
    }
}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_ModelSubmit_AddBackupState { }
//#endif
