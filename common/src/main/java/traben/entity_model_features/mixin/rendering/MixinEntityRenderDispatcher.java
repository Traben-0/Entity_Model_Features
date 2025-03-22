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

    //todo @Local is deperately needed here, also refactor emf context into a form of render state

    #if MC>MC_21
    @Inject(method ="render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            at = @At(value = "INVOKE", target =
                    #if MC>=MC_21_5
                    "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V"
                    #else
                    "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"
                    #endif
            ),locals = LocalCapture.CAPTURE_FAILHARD)
    private
    #if MC>=MC_21_5
    <E extends Entity, S extends EntityRenderState>
    #else
    <E extends Entity>
    #endif
    void emf$grabEntity(
            #if MC>=MC_21_5
            final E entity, final double d, final double e, final double f, final float g, final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final EntityRenderer<? super E, S> entityRenderer, final CallbackInfo ci, final EntityRenderState entityRenderState
            #else
            final E entity, final double d, final double e, final double f, final float g, final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final EntityRenderer<? super E, ?> entityRenderer, final CallbackInfo ci, final EntityRenderState entityRenderState, final Vec3 vec3, final double h, final double j, final double k
            #endif
    ) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntity) entity, entityRenderState);
    }
    #else
    @Inject(method ="render", at = @At(value = "HEAD"))
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

    private static final String SHADOW_RENDER_ETF =
            #if MC>=MC_21_5
            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FLnet/minecraft/world/level/LevelReader;F)V"
            #elif MC > MC_21
            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FFLnet/minecraft/world/level/LevelReader;F)V"
            #else
            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"
            #endif ;

    private static final String RENDER_ETF =
            #if MC>=MC_21_5
            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V"
            #elif MC > MC_21
            "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V"
            #else
            "render"
            #endif ;

    @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF, shift = At.Shift.BEFORE))
    private
    #if MC>=MC_21_5
    <S extends EntityRenderState>
    #else
    <E extends Entity>
    #endif
    void emf$modifyShadowTranslate(
            #if MC>=MC_21_5
            S entityRenderState, double d, double e, double f, PoseStack matrices, MultiBufferSource multiBufferSource, int i, EntityRenderer<?, S> entityRenderer, final CallbackInfo ci
            #elif MC > MC_21
            final E entity, final double d, final double e, final double f, final float g, final PoseStack matrices, final MultiBufferSource multiBufferSource, final int i, final EntityRenderer<? super E, ?> entityRenderer, final CallbackInfo ci
            #else
            E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci
            #endif
    ) {
        if (EMFAnimationEntityContext.getShadowX() != 0 || EMFAnimationEntityContext.getShadowZ() != 0) {
            matrices.translate(EMFAnimationEntityContext.getShadowX(), 0, EMFAnimationEntityContext.getShadowZ());
        }
    }

    @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF, shift = At.Shift.AFTER))
    private
    #if MC>=MC_21_5
    <S extends EntityRenderState>
    #else
    <E extends Entity>
    #endif
    void emf$undoModifyShadowTranslate(
            #if MC>=MC_21_5
            S entityRenderState, double d, double e, double f, PoseStack matrices, MultiBufferSource multiBufferSource, int i, EntityRenderer<?, S> entityRenderer, final CallbackInfo ci
            #elif MC > MC_21
            final E entity, final double d, final double e, final double f, final float g, final PoseStack matrices, final MultiBufferSource multiBufferSource, final int i, final EntityRenderer<? super E, ?> entityRenderer, final CallbackInfo ci
            #else
            E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci
            #endif
    ) {
        if (EMFAnimationEntityContext.getShadowX() != 0 || EMFAnimationEntityContext.getShadowZ() != 0) {
            matrices.translate(-EMFAnimationEntityContext.getShadowX(), 0, -EMFAnimationEntityContext.getShadowZ());
        }
    }

    @ModifyArg(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF), index = 3)
    private float emf$modifyShadowOpacity(float opacity) {
        if (!Float.isNaN(EMFAnimationEntityContext.getShadowOpacity())) {
            double g = this.distanceToSqr(EMFAnimationEntityContext.getEntityX(), EMFAnimationEntityContext.getEntityY(), EMFAnimationEntityContext.getEntityZ());
            return (float) ((1.0 - g / 256.0) * EMFAnimationEntityContext.getShadowOpacity());
        }
        return opacity;
    }

    @ModifyArg(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF),
            index = #if MC>=MC_21_5 5 #else 6 #endif)
    private float emf$modifyShadowSize(float size) {
        if (!Float.isNaN(EMFAnimationEntityContext.getShadowSize())) {
            return Math.min(size * EMFAnimationEntityContext.getShadowSize(), 32.0F);
        }
        return size;
    }
}
