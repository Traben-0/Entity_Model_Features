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
import traben.entity_model_features.models.animation.state.EMFSubmitData;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.animation.state.EMFSubmitExtension;

@Mixin(SubmitNodeStorage.ModelSubmit.class)
public abstract class Mixin_ModelSubmit_AddBackupState<S> implements EMFSubmitExtension {

    @Unique private final EMFSubmitData data = new EMFSubmitData();

    @Override
    public EMFSubmitData emf$getData() {
        return data;
    }

    @Shadow
    public abstract Model<? super S> model();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void emf$init(CallbackInfo ci) {
        if (EMFSubmitData.AWAITING_backupState != null) {
            // this is for those dumb block entities that don't pass the state through because they only need 1 primitive of state data
            data.backupState = EMFSubmitData.AWAITING_backupState;
        }

        if (EMFSubmitData.AWAITING_bipedPose != null) {
            data.bipedPose = EMFSubmitData.AWAITING_bipedPose;
        }

        EMFModelPartRoot emfRoot = model().root() instanceof EMFModelPartRoot ? (EMFModelPartRoot) model().root() : null;
        if (emfRoot != null) {
            data.modelVariant = emfRoot.currentModelVariant;
        }
    }

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_ModelSubmit_AddBackupState { }
//#endif
