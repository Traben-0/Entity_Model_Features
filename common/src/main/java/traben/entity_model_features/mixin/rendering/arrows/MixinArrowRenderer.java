package traben.entity_model_features.mixin.rendering.arrows;

import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import org.spongepowered.asm.mixin.Mixin;
import traben.entity_model_features.utils.IEMFCustomModelHolder;
#if MC < MC_21_2
import traben.entity_model_features.utils.EMFUtils;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import net.minecraft.client.model.geom.ModelLayerLocation;
#endif

@Mixin(TippableArrowRenderer.class)
public abstract class MixinArrowRenderer implements IEMFCustomModelHolder {
#if MC < MC_21_2
    @Unique
    private EMFModelPartRoot emf$model = null;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void emf$findModel(CallbackInfo ci) {
        ModelLayerLocation layer = new ModelLayerLocation(EMFUtils.res("minecraft", "arrow"), "main");
        emf$setModel(EMFUtils.getArrowOrNull(layer));
    }

    @Override
    public EMFModelPartRoot emf$getModel() {
        return emf$model;
    }

    @Override
    public void emf$setModel(final EMFModelPartRoot model) {
        emf$model = model;
    }
    #endif
}
