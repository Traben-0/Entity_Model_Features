package traben.entity_model_features.mixin;


import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMFClient;
import traben.entity_model_features.utils.EMFManager;


@Mixin(EntityModelLoader.class)
public class MixinEntityModelLoader {
    @Inject(method = "getModelPart",
            at = @At(value = "RETURN"), cancellable = true)
    private void emf$injectModelLoad(EntityModelLayer layer, CallbackInfoReturnable<ModelPart> cir) {
        if (EMFClient.testForForgeLoadingError()) return;

        cir.setReturnValue(EMFManager.getInstance().injectIntoModelRootGetter(layer, cir.getReturnValue()));

    }
}
