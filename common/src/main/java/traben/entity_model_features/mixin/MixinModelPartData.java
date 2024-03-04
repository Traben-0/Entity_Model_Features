package traben.entity_model_features.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFTextureSizeSupplier;

@Mixin(ModelPartData.class)
public class MixinModelPartData {
    @Inject(method = "createPart",
            at = @At(value = "RETURN"))
    private void emf$injectAnnouncer(final int textureWidth, final int textureHeight, final CallbackInfoReturnable<ModelPart> cir) {
        if (EMFConfig.getConfig().modelExportMode.doesLog() && cir.getReturnValue() instanceof EMFTextureSizeSupplier) {
            ((EMFTextureSizeSupplier) cir.getReturnValue()).emf$setTextureSize(new int[]{textureWidth, textureHeight});
        }

    }
}