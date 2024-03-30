package traben.entity_model_features.mixin.rendering.arrows;


import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.SpectralArrowEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFCustomModelHolder;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(SpectralArrowEntityRenderer.class)
public abstract class MixinSpectralArrowRenderer extends ProjectileEntityRenderer<ArrowEntity> implements EMFCustomModelHolder {


    @Unique
    private EMFModelPartRoot emf$model = null;

    public MixinSpectralArrowRenderer(final EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void emf$findModel(CallbackInfo ci) {
        EntityModelLayer layer = new EntityModelLayer(new Identifier("minecraft", "spectral"), "arrow");
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
