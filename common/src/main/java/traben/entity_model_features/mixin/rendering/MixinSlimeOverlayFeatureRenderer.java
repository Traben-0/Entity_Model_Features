package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(SlimeOverlayFeatureRenderer.class)
public class MixinSlimeOverlayFeatureRenderer<T extends LivingEntity> {

    @Mutable
    @Shadow
    @Final
    private EntityModel<T> model;
    @Unique
    private EntityModel<T> emf$heldModelToForce = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(FeatureRendererContext<?, ?> context, EntityModelLoader loader, CallbackInfo ci) {
        if (this.model != null && ((IEMFModel) model).emf$isEMFModel()) {
            emf$heldModelToForce = model;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$setAngles(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, MinecraftClient minecraftClient, boolean bl, VertexConsumer vertexConsumer) {
        if (emf$heldModelToForce != null) {
            if (!emf$heldModelToForce.equals(model)) {
                boolean replace = EMF.config().getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getId(livingEntity.getType()).getNamespace());
                EMFUtils.overrideMessage(emf$heldModelToForce.getClass().getName(), model == null ? "null" : model.getClass().getName(), replace);
                if (replace) {
                    model = emf$heldModelToForce;
                }
            }
            emf$heldModelToForce = null;
        }
        //EMFManager.getInstance().preRenderEMFActions("slime_outer", livingEntity, vertexConsumerProvider, f, g, j, k, l);
    }
}
