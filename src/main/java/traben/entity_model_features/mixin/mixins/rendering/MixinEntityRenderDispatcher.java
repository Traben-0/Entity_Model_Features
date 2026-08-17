package traben.entity_model_features.mixin.mixins.rendering;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.math.EMFMath;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;
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


    private static final String SHADOW_RENDER_ETF =
            //#if MC>=12109
            "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitShadow(Lcom/mojang/blaze3d/vertex/PoseStack;FLjava/util/List;)V"
            //#elseif MC>=12105
            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FLnet/minecraft/world/level/LevelReader;F)V"
            //#elseif MC >=12102
            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FFLnet/minecraft/world/level/LevelReader;F)V"
            //#else
            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"
            //#endif
            ;

    private static final String RENDER_ETF =
            //#if MC>=12109
            "submit"
            //#elseif MC>=12105
            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V"
            //#elseif MC >=12102
            //$$ "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V"
            //#else
            //$$ "render"
            //#endif
            ;

    //#if MC>=12109
    //#else
    //$$ @Shadow public abstract double distanceToSqr(double x, double y, double z);
    //#endif



    @Inject(method = RENDER_ETF, at = @At(value = "RETURN"))
    //#if MC >=12102
    private <S extends net.minecraft.client.renderer.entity.state.EntityRenderState> void emf$endOfRender(
            final CallbackInfo ci
    //#if MC>= 12105
            , @Local(argsOnly = true) S state
    ) {
        EMFEntityRenderState emfState = (EMFEntityRenderState) ((HoldsETFRenderState) state).etf$getState();
    //#else
    //$$ ) {
    //$$ EMFEntityRenderState emfState = EMFState.state();
    //#endif
        // todo likely extremely broken in 1.21.9
        if (EMFState.announceModels) {
            EMFState.anounceModels(emfState);
        }
    }
    //#else
    //$$ private <E extends Entity> void emf$endOfRender( CallbackInfo ci, @Local(argsOnly = true) E entity) {
    //$$     if (EMFState.announceModels) {
    //$$         EMFState.anounceModels((EMFEntityRenderState) ETFEntityRenderState.forEntity((ETFEntity) entity));
    //$$     }
    //$$ }
    //$$
    //#endif


    @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF))
    private void emf$modifyShadowTranslate(final CallbackInfo ci, @Local(argsOnly = true) PoseStack matrices) {
        var state = EMFState.state();
        if (state == null) return;
        if (state.shadowX() != 0 || state.shadowZ() != 0) {
            matrices.pushPose();
            matrices.translate(state.shadowX(), 0, state.shadowZ());
        }
    }

    @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF, shift = At.Shift.AFTER))
    private void emf$undoModifyShadowTranslate(final CallbackInfo ci, @Local(argsOnly = true) PoseStack matrices) {
        var state = EMFState.state();
        if (state == null) return;
        if (state.shadowX() != 0 || state.shadowZ() != 0) {
            matrices.popPose();
//            matrices.translate(-state.shadowX(), 0, -state.shadowZ());
        }
    }

    //#if MC<12109
    //$$ @ModifyArg(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF), index = 3)
    //$$ private float emf$modifyShadowOpacity(float opacity) {
    //$$     var state = EMFState.state();
    //$$     if (state == null) return opacity;
    //$$     if (!Float.isNaN(state.shadowOpacity())) {
    //$$         double g = this.distanceToSqr(EMFMath.getEntityX(), EMFMath.getEntityY(), EMFMath.getEntityZ());
    //$$         return (float) ((1.0 - g / 256.0) * state.shadowOpacity());
    //$$     }
    //$$     return opacity;
    //$$ }
    //#endif

    @ModifyArg(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF),
            index =
            //#if MC>=12109
            1
            //#elseif MC>=12105
            //$$ 5
            //#else
            //$$ 6
            //#endif
    )
    private float emf$modifyShadowSize(float size) {
        var state = EMFState.state();
        if (state == null) return size;
        if (!Float.isNaN(state.shadowSize())) {
            return Math.min(size * state.shadowSize(), 32.0F);
        }
        return size;
    }
}
