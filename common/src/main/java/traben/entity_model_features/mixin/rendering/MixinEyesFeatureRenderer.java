package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EyesFeatureRenderer.class)
public class MixinEyesFeatureRenderer {
    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"),
            index = 2
    )
    private int emf$markEyeLight(int i) {
        return LightmapTextureManager.MAX_LIGHT_COORDINATE+1;
    }
}
