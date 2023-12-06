package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationHelper;
import traben.entity_model_features.utils.EMFEntity;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {


    @Shadow
    public abstract double getSquaredDistanceToCamera(double x, double y, double z);

    @Inject(method = "render",
            at = @At(value = "HEAD"))
    private <E extends Entity> void emf$grabEntity(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        EMFAnimationHelper.setCurrentEntityIteration((EMFEntity) entity);
    }

    @Inject(method = "render",
            at = @At(value = "RETURN"))
    private <E extends Entity> void emf$endOfRender(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (EMFAnimationHelper.doAnnounceModels()){
            EMFAnimationHelper.anounceModels((EMFEntity) entity);
        }
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/Entity;FFLnet/minecraft/world/WorldView;F)V"
                    , shift = At.Shift.BEFORE))
    private <E extends Entity> void emf$modifyShadowTranslate(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (EMFAnimationHelper.getShadowX() != 0 || EMFAnimationHelper.getShadowZ() != 0) {
            matrices.translate(EMFAnimationHelper.getShadowX(), 0, EMFAnimationHelper.getShadowZ());
        }
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/Entity;FFLnet/minecraft/world/WorldView;F)V"
                    , shift = At.Shift.AFTER))
    private <E extends Entity> void emf$undoModifyShadowTranslate(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (EMFAnimationHelper.getShadowX() != 0 || EMFAnimationHelper.getShadowZ() != 0) {
            matrices.translate(-EMFAnimationHelper.getShadowX(), 0, -EMFAnimationHelper.getShadowZ());
        }
    }

    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/Entity;FFLnet/minecraft/world/WorldView;F)V"),
            index = 3
    )
    private float emf$modifyShadowOpacity(float opacity) {
        if (!Float.isNaN(EMFAnimationHelper.getShadowOpacity())) {
            double g = this.getSquaredDistanceToCamera(EMFAnimationHelper.getEntityX(), EMFAnimationHelper.getEntityY(), EMFAnimationHelper.getEntityZ());
            return (float) ((1.0 - g / 256.0) * EMFAnimationHelper.getShadowOpacity());
        }
        return opacity;
    }

    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/Entity;FFLnet/minecraft/world/WorldView;F)V"),
            index = 6
    )
    private float emf$modifyShadowSize(float size) {
        if (!Float.isNaN(EMFAnimationHelper.getShadowSize())) {
            return Math.min(size * EMFAnimationHelper.getShadowSize(), 32.0F);
        }
        return size;
    }
}
