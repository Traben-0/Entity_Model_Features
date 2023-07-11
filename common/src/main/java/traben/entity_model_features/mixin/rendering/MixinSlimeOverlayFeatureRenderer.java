package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFManager;

@Mixin(SlimeOverlayFeatureRenderer.class)
public class MixinSlimeOverlayFeatureRenderer<T extends LivingEntity> {

    @Mutable
    @Shadow
    @Final
    private EntityModel<T> model;
    private EntityModel<T> heldModelToForce = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(FeatureRendererContext context, EntityModelLoader loader, CallbackInfo ci) {
        if(EMFConfig.getConfig().tryForceEmfModels){
            heldModelToForce = model;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$setAngles(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, MinecraftClient minecraftClient, boolean bl, VertexConsumer vertexConsumer) {
        if(heldModelToForce != null && EMFConfig.getConfig().tryForceEmfModels){
            //((LivingEntityRendererAccessor)this).setModel(heldModelToForce);
            model = heldModelToForce;
            heldModelToForce = null;
        }
        EMFManager.getInstance().preRenderEMFActions("slime_outer", livingEntity, vertexConsumerProvider, f, g, j, k, l);
    }
}
