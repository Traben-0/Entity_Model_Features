package traben.entity_model_features.mixin.rendering;


import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModel;


@Mixin(CreeperChargeFeatureRenderer.class)
public abstract class MixinCreeperChargeFeatureRenderer {


    @Shadow
    @Final
    private CreeperEntityModel<CreeperEntity> model;

    @Inject(
            method = "getEnergySwirlTexture",
            at = @At(value = "RETURN"), cancellable = true)
    private void emf$getTextureRedirect(CallbackInfoReturnable<Identifier> cir) {
        if (model != null && ((IEMFModel) model).emf$isEMFModel()) {
            EMFModelPartRoot root = ((IEMFModel) model).emf$getEMFRootModel();
            if (root != null) {
                Identifier texture = root.getTopLevelJemTexture();
                if (texture != null) {
                    cir.setReturnValue(texture);
                }
            }
        }
    }


}
