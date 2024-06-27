package traben.entity_model_features.mixin.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class MixinBlockEntityWithoutLevelRenderer {


    @Inject(method = "renderByItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;renderSkull(Lnet/minecraft/core/Direction;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/SkullModelBase;Lnet/minecraft/client/renderer/RenderType;)V",
                    shift = At.Shift.BEFORE))
    private void emf$setRenderFactory(final ItemStack itemStack, final ItemDisplayContext itemDisplayContext, final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final int j, final CallbackInfo ci) {
        EMFAnimationEntityContext.setLayerFactory(RenderType::entityCutoutNoCullZOffset);
        EMFManager.getInstance().entityRenderCount++;
        //placeholder entity for inventory rendered skull blocks to trigger ETF vertex consumer actions
        //which won't run with a null entity
        ETFRenderContext.setCurrentEntity((ETFEntity) Minecraft.getInstance().player);
    }

    @Inject(method = "renderByItem", at = @At(value = "RETURN"))
    private void emf$reset(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
    }

}
