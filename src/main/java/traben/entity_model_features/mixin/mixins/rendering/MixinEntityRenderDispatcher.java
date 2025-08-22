package traben.entity_model_features.mixin.mixins.rendering;

import com.llamalad7.mixinextras.sugar.Local;
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
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_model_features.utils.EMFEntity;
//#if MC >=12102
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
//#endif

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {

    @Shadow
    public abstract double distanceToSqr(double x, double y, double z);

    //#if MC >= 12103
    //#if MC>= 12105
    @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            at = @At(value = "HEAD"))
    //#else
    //$$ @Inject(method = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
    //$$         at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
    //#endif
    private <S extends net.minecraft.client.renderer.entity.state.EntityRenderState>
    void emf$grabContext(final CallbackInfo ci, @SuppressWarnings("LocalMayBeArgsOnly") @Local S state) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState) ((HoldsETFRenderState) state).etf$getState());
    }
    //#else
    //$$ @Inject(method = "render",
    //$$     at = @At(value = "HEAD"))
    //$$ private <E extends net.minecraft.world.entity.Entity> void emf$grabContext(CallbackInfo ci, @Local(argsOnly = true) E entity) {
    //$$     EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState) ETFEntityRenderState.forEntity((ETFEntity) entity));
    //$$ }
    //#endif



    @Inject(method =
            //#if MC>= 12105
            "render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            //#elseif MC >=12102
            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            //#else
            //$$ "render",
            //#endif

            // state not in scope
            //#if MC >=12102 && MC<=12104
            //$$ at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V")
            //#else
            at = @At(value = "RETURN")
            //#endif
    )
    //#if MC >=12102
    private <S extends net.minecraft.client.renderer.entity.state.EntityRenderState> void emf$endOfRender(
            final CallbackInfo ci, @SuppressWarnings("LocalMayBeArgsOnly") @Local S state // not an arg on 1.21.2-4, also not in RETURN scope
    ) {
        // todo likely extremely broken in 1.21.9
        if (EMFAnimationEntityContext.doAnnounceModels()) {
            EMFAnimationEntityContext.anounceModels((EMFEntityRenderState) ((HoldsETFRenderState) state).etf$getState());
        }
    }
    //#else
    //$$ private <E extends Entity> void emf$endOfRender( CallbackInfo ci, @Local(argsOnly = true) E entity) {
    //$$     if (EMFAnimationEntityContext.doAnnounceModels()) {
    //$$         EMFAnimationEntityContext.anounceModels((EMFEntityRenderState) ETFEntityRenderState.forEntity((ETFEntity) entity));
    //$$     }
    //$$ }

    //#endif
    private static final String SHADOW_RENDER_ETF =
            //#if MC>=12105
            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FLnet/minecraft/world/level/LevelReader;F)V"
            //#elseif MC >=12102
            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FFLnet/minecraft/world/level/LevelReader;F)V"
            //#else
            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"
            //#endif
        ;

    private static final String RENDER_ETF =
            //#if MC>=12105
            "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V"
            //#elseif MC >=12102
            //$$ "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V"
            //#else
            //$$ "render"
            //#endif
        ;

    @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF, shift = At.Shift.BEFORE))
    private void emf$modifyShadowTranslate(final CallbackInfo ci, @Local(argsOnly = true) PoseStack matrices) {
        if (EMFAnimationEntityContext.getShadowX() != 0 || EMFAnimationEntityContext.getShadowZ() != 0) {
            matrices.translate(EMFAnimationEntityContext.getShadowX(), 0, EMFAnimationEntityContext.getShadowZ());
        }
    }

    @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF, shift = At.Shift.AFTER))
    private void emf$undoModifyShadowTranslate(final CallbackInfo ci, @Local(argsOnly = true) PoseStack matrices) {
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
            index =
            //#if MC>=12105
            5
            //#else
            //$$ 6
            //#endif
    )
    private float emf$modifyShadowSize(float size) {
        if (!Float.isNaN(EMFAnimationEntityContext.getShadowSize())) {
            return Math.min(size * EMFAnimationEntityContext.getShadowSize(), 32.0F);
        }
        return size;
    }
}
