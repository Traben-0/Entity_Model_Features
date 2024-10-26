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
#if MC > MC_21
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
#endif

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {


    @Shadow
    public abstract double distanceToSqr(double x, double y, double z);

    @Inject(method =
        #if MC > MC_21
            "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"),locals = LocalCapture.CAPTURE_FAILHARD)
    private <E extends Entity> void emf$grabEntity(final E entity, final double d, final double e, final double f, final float g, final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final EntityRenderer<? super E, ?> entityRenderer, final CallbackInfo ci, final EntityRenderState entityRenderState, final Vec3 vec3, final double h, final double j, final double k) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntity) entity, entityRenderState);
    }
        #else
            "render",
            at = @At(value = "HEAD"))
    private <E extends Entity> void emf$grabEntity(E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntity) entity);
    }
        #endif


    @Inject(method =
            #if MC > MC_21
            "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            #else
            "render",
            #endif
            at = @At(value = "RETURN"))
    private <E extends Entity> void emf$endOfRender(
            #if MC > MC_21
            final E entity, final double d, final double e, final double f, final float g, final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final EntityRenderer<? super E, ?> entityRenderer, final CallbackInfo ci
            #else
            E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci
            #endif
            ) {
        if (EMFAnimationEntityContext.doAnnounceModels()) {
            EMFAnimationEntityContext.anounceModels((EMFEntity) entity);
        }
    }

    @Inject(method =
            #if MC > MC_21
            "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            #else
            "render",
            #endif
            at = @At(value = "INVOKE",
                    target =
                            #if MC > MC_21
                            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FFLnet/minecraft/world/level/LevelReader;F)V"
                            #else
                            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"
                            #endif
                    , shift = At.Shift.BEFORE))
    private <E extends Entity> void emf$modifyShadowTranslate(
            #if MC > MC_21
            final E entity, final double d, final double e, final double f, final float g, final PoseStack matrices, final MultiBufferSource multiBufferSource, final int i, final EntityRenderer<? super E, ?> entityRenderer, final CallbackInfo ci
            #else
            E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci
            #endif
    ) {
        if (EMFAnimationEntityContext.getShadowX() != 0 || EMFAnimationEntityContext.getShadowZ() != 0) {
            matrices.translate(EMFAnimationEntityContext.getShadowX(), 0, EMFAnimationEntityContext.getShadowZ());
        }
    }

    @Inject(method =
            #if MC > MC_21
            "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            #else
            "render",
            #endif
            at = @At(value = "INVOKE",
                    target =
                            #if MC > MC_21
                            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FFLnet/minecraft/world/level/LevelReader;F)V"
                            #else
                            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"
                            #endif
                    , shift = At.Shift.AFTER))
    private <E extends Entity> void emf$undoModifyShadowTranslate(
            #if MC > MC_21
            final E entity, final double d, final double e, final double f, final float g, final PoseStack matrices, final MultiBufferSource multiBufferSource, final int i, final EntityRenderer<? super E, ?> entityRenderer, final CallbackInfo ci
            #else
            E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci
            #endif
    ) {
        if (EMFAnimationEntityContext.getShadowX() != 0 || EMFAnimationEntityContext.getShadowZ() != 0) {
            matrices.translate(-EMFAnimationEntityContext.getShadowX(), 0, -EMFAnimationEntityContext.getShadowZ());
        }
    }

    @ModifyArg(
            method =
                    #if MC > MC_21
                    "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
                    #else
                    "render",
                    #endif
            at = @At(value = "INVOKE",
                    target =
                            #if MC > MC_21
                            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FFLnet/minecraft/world/level/LevelReader;F)V"
                            #else
                            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"
                            #endif
            ), index = 3
    )
    private float emf$modifyShadowOpacity(float opacity) {
        if (!Float.isNaN(EMFAnimationEntityContext.getShadowOpacity())) {
            double g = this.distanceToSqr(EMFAnimationEntityContext.getEntityX(), EMFAnimationEntityContext.getEntityY(), EMFAnimationEntityContext.getEntityZ());
            return (float) ((1.0 - g / 256.0) * EMFAnimationEntityContext.getShadowOpacity());
        }
        return opacity;
    }

    @ModifyArg(
            method =
                    #if MC > MC_21
                    "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
                    #else
                    "render",
                    #endif
            at = @At(value = "INVOKE",
                    target =
                            #if MC > MC_21
                            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FFLnet/minecraft/world/level/LevelReader;F)V"
                            #else
                            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"
                            #endif
            ), index = 6
    )
    private float emf$modifyShadowSize(float size) {
        if (!Float.isNaN(EMFAnimationEntityContext.getShadowSize())) {
            return Math.min(size * EMFAnimationEntityContext.getShadowSize(), 32.0F);
        }
        return size;
    }
}
