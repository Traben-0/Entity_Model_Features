package traben.entity_model_features.mixin.mixins.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
//#if MC < 26.2
import net.minecraft.client.renderer.MultiBufferSource;
//#endif
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
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

//#if MC >=12104

//#if MC>= 12111
//$$ import net.minecraft.client.renderer.rendertype.RenderTypes;
//#endif

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

    //#if MC >= 12109
    private static final String RENDER = "submit";
    //#else
    //$$ private static final String RENDER = "render";
    //#endif

    //#if MC >= 26.1
    //$$ private static final String TARGET = "Lnet/minecraft/client/renderer/special/SpecialModelRenderer;submit(Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V";
    //#elseif MC >= 12109
    private static final String TARGET = "Lnet/minecraft/client/renderer/special/SpecialModelRenderer;submit(Ljava/lang/Object;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V";
    //#else
    //$$ private static final String TARGET = "Lnet/minecraft/client/renderer/special/SpecialModelRenderer;render(Ljava/lang/Object;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIZ)V";
    //#endif

    @Inject(method = RENDER, at = @At(value = "INVOKE", target = TARGET))
    private void emf$setRenderFactory(CallbackInfo ci) {
        EMFManager.getInstance().entityRenderCount++;
        setPlayerEntity();

        if (specialRenderer instanceof SkullSpecialRenderer && EMFState.state() != null) {
            EMFState.state().setLayerFactory(
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


    @Inject(method = RENDER, at = @At(value = "INVOKE", target = TARGET, shift = At.Shift.AFTER))
    private void emf$reset(final CallbackInfo ci) {
        unSetPlayerEntity();
    }

//#else
//$$ import net.minecraft.world.item.ItemDisplayContext;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
//$$
//$$ @Mixin(BlockEntityWithoutLevelRenderer.class)
//$$ public class MixinBlockEntityWithoutLevelRenderer {
//$$
//$$
//$$     @Inject(method = "renderByItem",
//$$             at = @At(value = "INVOKE",
//$$                     target =
                    //#if MC > 1.21
                    //$$ "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;getRenderType(Lnet/minecraft/world/level/block/SkullBlock$Type;Lnet/minecraft/world/item/component/ResolvableProfile;)Lnet/minecraft/client/renderer/RenderType;",
                    //#else
                    //$$ "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;getRenderType(Lnet/minecraft/world/level/block/SkullBlock$Type;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/client/renderer/RenderType;",
                    //#endif
//$$                     shift = At.Shift.BEFORE))
//$$     private void emf$setRenderFactory(final ItemStack itemStack, final ItemDisplayContext itemDisplayContext, final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final int j, final CallbackInfo ci) {
//$$         if (EMFState.state() != null) EMFState.state().setLayerFactory(RenderType::entityCutoutNoCullZOffset);
//$$         EMFManager.getInstance().entityRenderCount++;
//$$         setPlayerEntity();
//$$     }
//$$
//$$     @Inject(method = "renderByItem",
//$$             at = @At(value = "INVOKE",
//$$                     target = "Lnet/minecraft/client/model/TridentModel;renderType(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
//$$     private void emf$setTrident(final CallbackInfo ci) {
//$$         EMFManager.getInstance().entityRenderCount++;
//$$         setPlayerEntity();
//$$     }
//$$
//$$     @Inject(method = "renderByItem",
//$$             at = @At(value = "INVOKE",
//$$                     target = "Lnet/minecraft/client/model/ShieldModel;renderType(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;",
//$$                     shift = At.Shift.BEFORE))
//$$     private void emf$setTrident(final ItemStack itemStack, final ItemDisplayContext itemDisplayContext, final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final int j, final CallbackInfo ci) {
//$$         EMFManager.getInstance().entityRenderCount++;
//$$         setPlayerEntity();
//$$     }
//$$
//$$     @Inject(method = "renderByItem", at = @At(value = "RETURN"))
//$$     private void emf$reset(final CallbackInfo ci) {
//$$         unSetPlayerEntity();
//$$     }
//$$
//$$
//#endif

    @Unique
    private void setPlayerEntity() {
        var state = EMFEntityRenderState.manualPlayerState();
        ETFState.mount(state);
    }

    @Unique
    private void unSetPlayerEntity() {
        //#if MC < 1.21.4
        //$$ var state = EMFState.state();
        //$$ if (state != null && state.isManualPlayerState())
        //#endif
            ETFState.unMount();
    }
}

