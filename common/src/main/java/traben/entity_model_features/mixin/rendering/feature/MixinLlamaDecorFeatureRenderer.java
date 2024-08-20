package traben.entity_model_features.mixin.rendering.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.LlamaDecorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(LlamaDecorLayer.class)
public class MixinLlamaDecorFeatureRenderer {

    @Mutable
    @Shadow
    @Final
    private LlamaModel<Llama> model;
    @Unique
    private LlamaModel<Llama> emf$heldModelToForce = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(RenderLayerParent<?, ?> context, EntityModelSet loader, CallbackInfo ci) {
        if (this.model != null && ((IEMFModel) model).emf$isEMFModel()) {
            emf$heldModelToForce = model;
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/horse/Llama;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/LlamaModel;setupAnim(Lnet/minecraft/world/entity/animal/horse/AbstractChestedHorse;FFFFF)V", shift = At.Shift.AFTER))
    private void emf$setAngles(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, Llama llamaEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (emf$heldModelToForce != null) {
            //((LivingEntityRendererAccessor)this).setModel(heldModelToForce);
            if (!emf$heldModelToForce.equals(model)) {
                boolean replace = EMF.config().getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getKey(llamaEntity.getType()).getNamespace());
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
