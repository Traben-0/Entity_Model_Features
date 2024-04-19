package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(LlamaDecorFeatureRenderer.class)
public class MixinLlamaDecorFeatureRenderer {

    @Mutable
    @Shadow
    @Final
    private LlamaEntityModel<LlamaEntity> model;
    @Unique
    private LlamaEntityModel<LlamaEntity> emf$heldModelToForce = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(FeatureRendererContext<?, ?> context, EntityModelLoader loader, CallbackInfo ci) {
        if (this.model != null && ((IEMFModel) model).emf$isEMFModel()) {
            emf$heldModelToForce = model;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/LlamaEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/LlamaEntityModel;setAngles(Lnet/minecraft/entity/passive/AbstractDonkeyEntity;FFFFF)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$setAngles(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LlamaEntity llamaEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, DyeColor dyeColor, Identifier identifier) {
        if (emf$heldModelToForce != null) {
            //((LivingEntityRendererAccessor)this).setModel(heldModelToForce);
            if (!emf$heldModelToForce.equals(model)) {
                boolean replace = EMF.config().getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getId(llamaEntity.getType()).getNamespace());
                EMFUtils.overrideMessage(emf$heldModelToForce.getClass().getName(), model == null ? "null" : model.getClass().getName(), replace);
                if (replace) {
                    model = emf$heldModelToForce;
                }
            }
            emf$heldModelToForce = null;
        }
        //EMFManager.getInstance().preRenderEMFActions(llamaEntity.isTrader() ? "trader_llama_decor" : "llama_decor", llamaEntity, vertexConsumerProvider, f, g, j, k, l);
    }
}
