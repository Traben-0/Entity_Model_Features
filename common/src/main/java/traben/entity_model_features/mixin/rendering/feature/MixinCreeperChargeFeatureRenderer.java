package traben.entity_model_features.mixin.rendering.feature;


import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModel;


@Mixin(CreeperPowerLayer.class)
public abstract class MixinCreeperChargeFeatureRenderer {


    @Shadow
    @Final
    private CreeperModel<Creeper> model;

    @Inject(
            method = "getTextureLocation",
            at = @At(value = "RETURN"), cancellable = true)
    private void emf$getTextureRedirect(CallbackInfoReturnable<ResourceLocation> cir) {
        if (model != null && ((IEMFModel) model).emf$isEMFModel()) {
            EMFModelPartRoot root = ((IEMFModel) model).emf$getEMFRootModel();
            if (root != null) {
                ResourceLocation texture = root.getTopLevelJemTexture();
                if (texture != null) {
                    cir.setReturnValue(texture);
                }
            }
        }
    }


}
