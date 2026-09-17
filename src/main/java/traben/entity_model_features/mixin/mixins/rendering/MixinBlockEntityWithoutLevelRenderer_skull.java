package traben.entity_model_features.mixin.mixins.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
//#if MC < 26.2
import net.minecraft.client.renderer.MultiBufferSource;
//#endif
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.utils.ETFEntity;

//#if MC >= 1.21.4

import net.minecraft.client.renderer.item.ItemStackRenderState;
//#if MC>= 12111
//$$ import net.minecraft.client.renderer.rendertype.RenderTypes;
//#endif

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SkullSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public class MixinBlockEntityWithoutLevelRenderer_skull {

    @Shadow
    @Nullable
    private SpecialModelRenderer<?> specialRenderer;

    //#if MC >= 12109
    private static final String RENDER = "submit";
    //#else
    //$$ private static final String RENDER = "render";
    //#endif

    @Inject(method = RENDER, at = @At(value = "HEAD"))
    private void emf$setRenderFactory(CallbackInfo ci) {
        var state = EMFState.state();
        if (specialRenderer instanceof SkullSpecialRenderer && state != null) {
            state.setLayerFactory(
                    //#if MC >= 26.1
                    //$$ RenderTypes::entityCutoutZOffset
                    //#elseif MC>= 12111
                    //$$ RenderTypes::entityCutoutNoCullZOffset
                    //#else
                    RenderType::entityCutoutNoCullZOffset
                    //#endif
            );
        }
    }


//#else
//$$ import net.minecraft.world.item.ItemDisplayContext;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
//$$
//$$ @Mixin(BlockEntityWithoutLevelRenderer.class)
//$$ public class MixinBlockEntityWithoutLevelRenderer_skull {
//$$
//$$
//$$     @Inject(method = "renderByItem",
//$$             at = @At(value = "INVOKE",
//$$                     target =
    //#if MC >= 1.21
    //$$ "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;getRenderType(Lnet/minecraft/world/level/block/SkullBlock$Type;Lnet/minecraft/world/item/component/ResolvableProfile;)Lnet/minecraft/client/renderer/RenderType;",
    //#else
    //$$ "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;getRenderType(Lnet/minecraft/world/level/block/SkullBlock$Type;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/client/renderer/RenderType;",
    //#endif
//$$                     shift = At.Shift.BEFORE))
//$$     private void emf$setRenderFactory(final ItemStack itemStack, final ItemDisplayContext itemDisplayContext, final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final int j, final CallbackInfo ci) {
//$$         var state = EMFState.state();
//$$         if (state != null) state.setLayerFactory(RenderType::entityCutoutNoCullZOffset);
//$$     }
//#endif

}

