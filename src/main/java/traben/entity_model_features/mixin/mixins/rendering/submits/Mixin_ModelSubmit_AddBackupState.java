package traben.entity_model_features.mixin.mixins.rendering.submits;

import net.minecraft.client.model.Model;
import org.spongepowered.asm.mixin.Mixin;

//#if MC>=12109
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFSubmitData;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.animation.state.EMFSubmitExtension;

//#if MC >= 26.2
//$$ @Mixin(net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit.class)
//#else
@Mixin(net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit.class)
//#endif
public abstract class Mixin_ModelSubmit_AddBackupState<S> implements EMFSubmitExtension {
    //TODO to go upstream in ETF refactor

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

        data.onShoulder = EMFAnimationEntityContext.isOnShoulder();

        data.isMainModelPhase = EMFSubmitData.AWAITING_isMainModelPhase;
        data.isLayerModelPhase = EMFSubmitData.AWAITING_isLayerModelPhase;

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
