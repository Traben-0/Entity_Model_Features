package traben.entity_model_features.mixin.mixins.rendering;

import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin(ItemFrameRenderer.class)
public class MixinItemFrameEntityRenderer {

    //#if MC >=12112
    //$$ dont forget this
    //#endif

    //#if MC < 12109
    //$$ @Inject(method =
            //#if MC >=12102
            //$$ "render(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#else
            //$$ "render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#endif
    //$$         at = @At(value = "INVOKE",
    //$$                 target =
                            //#if MC >=12102
                            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                            //#else
                            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                            //#endif
    //$$                 shift = At.Shift.AFTER))
    //$$ private void emf$setFrame(final CallbackInfo ci) {
    //$$     //basically "HEAD"
    //$$     EMFAnimationEntityContext.setInItemFrame = true;
    //$$ }
    //$$
    //$$ @Inject(method =
            //#if MC >=12102
            //$$ "render(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#else
            //$$ "render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#endif
    //$$         at = @At(value = "TAIL"))
    //$$ private void emf$unsetFrame(final CallbackInfo ci) {
    //$$     EMFAnimationEntityContext.setInItemFrame = false;
    //$$ }
    //#endif
}