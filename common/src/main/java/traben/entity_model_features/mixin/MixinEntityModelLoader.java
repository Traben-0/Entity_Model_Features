package traben.entity_model_features.mixin;


import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMFVersionDifferenceManager;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(EntityModelLoader.class)
public class MixinEntityModelLoader {
    @Inject(method = "getModelPart",
            at = @At(value = "RETURN"), cancellable = true)
    private void emf$injectModelLoad(EntityModelLayer layer, CallbackInfoReturnable<ModelPart> cir) {
        if (EMFVersionDifferenceManager.isForge()) {
            try {
                cir.setReturnValue(EMFManager.getInstance().injectIntoModelRootGetter(layer, cir.getReturnValue()));
            } catch (IncompatibleClassChangeError error) {
                EMFUtils.logError("///////////////////");
                EMFUtils.logError("EMF crashed due to a forge dependency error (probably), suppressing the EMF crash so the true culprit will be sent to the crash report tool\nIF THIS HAPPENS MORE THAN ONCE THIS MIGHT ACTUALLY BE AN EMF ISSUE\n");
                error.printStackTrace();
                EMFUtils.logError("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
                //this is required for forge
                // if forge detects a missing dependency in any mod it will crash but first it trys to finish loading this mixin
                // and fails due to the accesswidening of ModelPart being broken due to forge preparing to shut down
                // this ofcourse crashes forge again but now it blames emf.....
            }
        } else {
            cir.setReturnValue(EMFManager.getInstance().injectIntoModelRootGetter(layer, cir.getReturnValue()));
        }
    }
}
