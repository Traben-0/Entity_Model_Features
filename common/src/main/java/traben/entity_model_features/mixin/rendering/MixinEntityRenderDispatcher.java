package traben.entity_model_features.mixin.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFEntity;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {


    @Shadow
    public abstract double distanceToSqr(double x, double y, double z);

    @Inject(method = "render",
            at = @At(value = "HEAD"))
    private <E extends Entity> void emf$grabEntity(E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntity) entity);
    }

    @Inject(method = "render",
            at = @At(value = "RETURN"))
    private <E extends Entity> void emf$endOfRender(E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (EMFAnimationEntityContext.doAnnounceModels()) {
            EMFAnimationEntityContext.anounceModels((EMFEntity) entity);
        }
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"
                    , shift = At.Shift.BEFORE))
    private <E extends Entity> void emf$modifyShadowTranslate(E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (EMFAnimationEntityContext.getShadowX() != 0 || EMFAnimationEntityContext.getShadowZ() != 0) {
            matrices.translate(EMFAnimationEntityContext.getShadowX(), 0, EMFAnimationEntityContext.getShadowZ());
        }
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"
                    , shift = At.Shift.AFTER))
    private <E extends Entity> void emf$undoModifyShadowTranslate(E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (EMFAnimationEntityContext.getShadowX() != 0 || EMFAnimationEntityContext.getShadowZ() != 0) {
            matrices.translate(-EMFAnimationEntityContext.getShadowX(), 0, -EMFAnimationEntityContext.getShadowZ());
        }
    }

    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"),
            index = 3
    )
    private float emf$modifyShadowOpacity(float opacity) {
        if (!Float.isNaN(EMFAnimationEntityContext.getShadowOpacity())) {
            double g = this.distanceToSqr(EMFAnimationEntityContext.getEntityX(), EMFAnimationEntityContext.getEntityY(), EMFAnimationEntityContext.getEntityZ());
            return (float) ((1.0 - g / 256.0) * EMFAnimationEntityContext.getShadowOpacity());
        }
        return opacity;
    }

    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"),
            index = 6
    )
    private float emf$modifyShadowSize(float size) {
        if (!Float.isNaN(EMFAnimationEntityContext.getShadowSize())) {
            return Math.min(size * EMFAnimationEntityContext.getShadowSize(), 32.0F);
        }
        return size;
    }
}
