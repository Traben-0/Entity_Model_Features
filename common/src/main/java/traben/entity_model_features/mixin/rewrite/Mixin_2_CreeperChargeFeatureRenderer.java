package traben.entity_model_features.mixin.rewrite;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.utils.EMFManager;

@Mixin(EnergySwirlOverlayFeatureRenderer.class)
public class Mixin_2_CreeperChargeFeatureRenderer<T extends Entity> {

    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$setAngles(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci, float f, EntityModel entityModel, VertexConsumer vertexConsumer) {
        if(entity instanceof CreeperEntity)
            EMFManager.getInstance().setAnglesOnParts("creeper_charge",entity,  limbAngle,  limbDistance,  animationProgress,  headYaw,  headPitch);
    }
}
