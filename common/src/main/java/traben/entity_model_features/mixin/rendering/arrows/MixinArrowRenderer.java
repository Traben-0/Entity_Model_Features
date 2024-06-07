package traben.entity_model_features.mixin.rendering.arrows;


import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Arrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFCustomModelHolder;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(TippableArrowRenderer.class)
public abstract class MixinArrowRenderer extends ArrowRenderer<Arrow> implements EMFCustomModelHolder {


    @Unique
    private EMFModelPartRoot emf$model = null;

    public MixinArrowRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

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
}
