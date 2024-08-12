package traben.entity_model_features.mixin;


import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;


@Mixin(EntityModelSet.class)
public class MixinEntityModelLoader {
    @Inject(method = "bakeLayer",
            at = @At(value = "RETURN"), cancellable = true)
    private void emf$injectModelLoad(ModelLayerLocation layer, CallbackInfoReturnable<ModelPart> cir) {
        if (EMF.testForForgeLoadingError()) return;

        cir.setReturnValue(EMFManager.getInstance().injectIntoModelRootGetter(layer, cir.getReturnValue()));

    }
}
