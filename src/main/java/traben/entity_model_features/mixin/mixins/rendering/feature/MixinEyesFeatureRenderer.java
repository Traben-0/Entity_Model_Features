package traben.entity_model_features.mixin.mixins.rendering.feature;

import net.minecraft.client.renderer.entity.layers.EyesLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static traben.entity_model_features.EMF.EYES_FEATURE_LIGHT_VALUE;

@Mixin(EyesLayer.class)
public class MixinEyesFeatureRenderer {
    @SuppressWarnings("SameReturnValue")
    @ModifyArg(
            method =
                //#if MC >= 12109
                "submit", index = 4,
                //#else
                //$$ "render", index = 2,
                //#endif

                //#if MC >= 12109
                at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V")
                //#elseif MC >= 12100
                //$$ at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V")
                //#else
                //$$ at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V")
                //#endif
    )
    private int emf$markEyeLight(int i) {
        return EYES_FEATURE_LIGHT_VALUE;
    }

}
