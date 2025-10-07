package traben.entity_model_features.mixin.mixins.rendering.feature;

import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin(CustomHeadLayer.class)
public class MixinHeadFeatureRenderer {

    //#if MC >= 12109
    private static final String RENDER = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V";
    //#elseif MC >= 12102
    //$$ private static final String RENDER = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V";
    //#else
    //$$ private static final String RENDER = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V";
    //#endif


    @Inject(method = RENDER, at = @At(value = "HEAD"))
    private void emf$setHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.setIsOnHead = true;
    }

    @Inject(method = RENDER, at = @At(value = "TAIL"))
    private void emf$unsetHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.setIsOnHead = false;
    }

}