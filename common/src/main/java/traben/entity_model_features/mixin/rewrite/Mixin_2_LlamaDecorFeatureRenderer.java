package traben.entity_model_features.mixin.rewrite;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.utils.EMFManager;

@Mixin(LlamaDecorFeatureRenderer.class)
public class Mixin_2_LlamaDecorFeatureRenderer<T extends Entity> {

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/LlamaEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/LlamaEntityModel;setAngles(Lnet/minecraft/entity/passive/AbstractDonkeyEntity;FFFFF)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$setAngles(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LlamaEntity llamaEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, DyeColor dyeColor, Identifier identifier) {
        EMFManager.getInstance().setAnglesOnParts(llamaEntity.isTrader() ? "trader_llama_decor" : "llama_decor", llamaEntity, f, g, j, k, l);
    }
}
