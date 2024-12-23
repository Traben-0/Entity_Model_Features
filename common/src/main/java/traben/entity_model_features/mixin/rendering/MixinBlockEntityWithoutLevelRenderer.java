package traben.entity_model_features.mixin.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.EMFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFEntity;

#if MC > MC_21_2
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SkullSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public class MixinBlockEntityWithoutLevelRenderer {


    @Shadow
    @Nullable
    private SpecialModelRenderer<?> specialRenderer;

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/special/SpecialModelRenderer;render(Ljava/lang/Object;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIZ)V",
                    shift = At.Shift.BEFORE))
    private void emf$setRenderFactory(final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final int j, final CallbackInfo ci) {
        if (specialRenderer instanceof SkullSpecialRenderer) {
            EMFAnimationEntityContext.setLayerFactory(RenderType::entityCutoutNoCullZOffset);
        }

        EMFManager.getInstance().entityRenderCount++;
        ETFRenderContext.setCurrentEntity((ETFEntity) Minecraft.getInstance().player);
    }



    @Inject(method = "render", at = @At(value = "RETURN"))
    private void emf$reset(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
    }

}
#else
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class MixinBlockEntityWithoutLevelRenderer {


    @Inject(method = "renderByItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;renderSkull(Lnet/minecraft/core/Direction;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/SkullModelBase;Lnet/minecraft/client/renderer/RenderType;)V",
                    shift = At.Shift.BEFORE))
    private void emf$setRenderFactory(final ItemStack itemStack, final ItemDisplayContext itemDisplayContext, final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final int j, final CallbackInfo ci) {
        EMFAnimationEntityContext.setLayerFactory(RenderType::entityCutoutNoCullZOffset);
        EMFManager.getInstance().entityRenderCount++;
        ETFRenderContext.setCurrentEntity((ETFEntity) Minecraft.getInstance().player);
    }

    @Inject(method = "renderByItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/TridentModel;renderType(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    private void emf$setTrident(final CallbackInfo ci) {
        EMFManager.getInstance().entityRenderCount++;
        ETFRenderContext.setCurrentEntity((ETFEntity) Minecraft.getInstance().player);
    }

    @Inject(method = "renderByItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ShieldModel;renderType(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;",
                    shift = At.Shift.BEFORE))
    private void emf$setTrident(final ItemStack itemStack, final ItemDisplayContext itemDisplayContext, final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final int j, final CallbackInfo ci) {
        EMFManager.getInstance().entityRenderCount++;
        ETFRenderContext.setCurrentEntity((ETFEntity) Minecraft.getInstance().player);
    }

    @Inject(method = "renderByItem", at = @At(value = "RETURN"))
    private void emf$reset(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
    }

}
#endif

