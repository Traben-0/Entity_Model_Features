package traben.entity_model_features.mixin.rewrite;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.utils.EMFManager;

@Mixin(SaddleFeatureRenderer.class)
public class MixinSaddleFeatureRenderer<T extends Entity> {

    @Inject(method = "Lnet/minecraft/client/render/entity/feature/SaddleFeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/Entity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$setAngles(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        EMFManager.getInstance().preRenderEMFActions(
                Registry.ENTITY_TYPE.getId(entity.getType()).toString().replace("minecraft:", "") + "_saddle",
                entity, vertexConsumers, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
    }
}
