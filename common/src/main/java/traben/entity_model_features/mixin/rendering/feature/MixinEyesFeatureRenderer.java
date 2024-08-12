package traben.entity_model_features.mixin.rendering.feature;

import net.minecraft.client.renderer.entity.layers.EyesLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static traben.entity_model_features.EMF.EYES_FEATURE_LIGHT_VALUE;

@Mixin(EyesLayer.class)
public class MixinEyesFeatureRenderer {
    @SuppressWarnings("SameReturnValue")
    @ModifyArg(
            method = "render",
            #if MC >= MC_21
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"),
            #else
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"),
            #endif
            index = 2
    )
    private int emf$markEyeLight(int i) {
        return EYES_FEATURE_LIGHT_VALUE;
    }

}
