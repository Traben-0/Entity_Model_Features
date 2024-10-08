package traben.entity_model_features.mixin;


import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.EMFManager;

import java.util.function.Function;

@Mixin(Model.class)
public class MixinModel implements IEMFModel {
    @Unique
    private EMFModelPartRoot emf$thisEMFModelRoot = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$discoverEMFModel(Function<?, ?> layerFactory, CallbackInfo ci) {
        if (EMF.testForForgeLoadingError()) return;
        emf$thisEMFModelRoot = EMFManager.lastCreatedRootModelPart;
        EMFManager.lastCreatedRootModelPart = null;
    }

    @Override
    public boolean emf$isEMFModel() {
        return emf$thisEMFModelRoot != null;
    }

    @Override
    public EMFModelPartRoot emf$getEMFRootModel() {
        return emf$thisEMFModelRoot;
    }


    @Inject(method = "renderType",
            at = @At(value = "HEAD"))
    private void emf$discoverEMFModel(ResourceLocation texture, CallbackInfoReturnable<RenderType> cir) {
        EMFAnimationEntityContext.setLayerFactory(((Model) ((Object) this)).renderType);
    }
}
