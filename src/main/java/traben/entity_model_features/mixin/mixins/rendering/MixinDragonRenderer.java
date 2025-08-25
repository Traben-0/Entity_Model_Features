package traben.entity_model_features.mixin.mixins.rendering;

import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;
import traben.entity_texture_features.features.ETFRenderContext;

@Mixin(EnderDragonRenderer.class)
public abstract class MixinDragonRenderer {

    @Inject(method =
            //#if MC >= 12102
            "render(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#else
            //$$ "render(Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#endif
            at = @At(value = "INVOKE",
                    target =
                    //#if MC >= 12102
                    "Lnet/minecraft/client/model/dragon/EnderDragonModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V",
                    //#elseif MC >= 12100
                    //$$ "Lnet/minecraft/client/renderer/entity/EnderDragonRenderer$DragonModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V",
                    //#else
                    //$$ "Lnet/minecraft/client/renderer/entity/EnderDragonRenderer$DragonModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
                    //#endif
            shift = At.Shift.BEFORE, ordinal =
                                    //#if MC >= 12100
                                    2
                                    //#else
                                    //$$ 3
                                    //#endif
                                    ))
    private void emf$allowMultiPartRender(final CallbackInfo ci) {
        ETFRenderContext.startSpecialRenderOverlayPhase();
    }




    @Inject(method =
            //#if MC >= 12102
            "render(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#else
            //$$ "render(Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#endif
            at = @At(value = "INVOKE",
                    target =
                    //#if MC >= 12102
                    "Lnet/minecraft/client/model/dragon/EnderDragonModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V",
                    //#elseif MC == 12100 || MC == 121001
                    //$$ "Lnet/minecraft/client/renderer/entity/EnderDragonRenderer$DragonModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V",
                    //#else
                    //$$ "Lnet/minecraft/client/renderer/entity/EnderDragonRenderer$DragonModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
                    //#endif
            shift = At.Shift.AFTER, ordinal =
                            //#if MC >= 12100
                            2
                            //#else
                            //$$ 3
                            //#endif
                            ))
    private void emf$allowMultiPartRender2(final CallbackInfo ci) {
        ETFRenderContext.endSpecialRenderOverlayPhase();
    }


    //#if MC >= 12102
    @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/dragon/EnderDragonModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V",
                    shift = At.Shift.BEFORE))
    private void emf$allowMultiPartRender3(final CallbackInfo ci) {
        EMFManager.getInstance().entityRenderCount++;
    }

    @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/dragon/EnderDragonModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
                    shift = At.Shift.BEFORE, ordinal = 0))
    private void emf$allowMultiPartRender24(final CallbackInfo ci) {
        EMFManager.getInstance().entityRenderCount++;
    }
    //#endif

}
