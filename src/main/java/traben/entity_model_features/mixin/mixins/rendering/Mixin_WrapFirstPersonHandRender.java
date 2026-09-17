package traben.entity_model_features.mixin.mixins.rendering;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_texture_features.features.state.ETFState;

//#if !NEOFORGE || MC >= 1.21.2
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
//#else
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

//#if MC >= 26.3
//$$ @Mixin(net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer.class)
//#else
@Mixin(net.minecraft.client.renderer.ItemInHandRenderer.class)
//#endif
public class Mixin_WrapFirstPersonHandRender {
    //#if MC >= 26.2
    //$$ private static final String RENDER = "submitHandsWithItems";
    //#else
    private static final String RENDER = "renderHandsWithItems";
    //#endif

    //#if !NEOFORGE || MC >= 1.21.2
    @WrapMethod(method = RENDER)
    private void wrapRenderHandsWithItems(
            //#if MC >= 26.3
            //$$ float a, PoseStack b, net.minecraft.client.renderer.SubmitNodeCollector c, net.minecraft.client.renderer.state.level.PlayerRenderState d, net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState e, Operation<Void> original
            //#elseif MC > 1.21.6
            float a, PoseStack b, net.minecraft.client.renderer.SubmitNodeCollector c, LocalPlayer d, int e, Operation<Void> original
            //#else
            //$$ float a, PoseStack b, net.minecraft.client.renderer.MultiBufferSource.BufferSource c, LocalPlayer d, int e, Operation<Void> original
            //#endif
    ) {
        pre();

        try {
            original.call(a, b, c, d, e);
        } finally {
            post();
        }
    }
    //#else
    //$$ // WrapMethod unavailable, lets just inject at start and end and hope it's not cancelled
    //$$ @Inject(method = RENDER, at = @At("HEAD"))
    //$$ private void preRenderHandsWithItems(CallbackInfo ci) {
    //$$     pre();
    //$$ }
    //$$
    //$$ @Inject(method = RENDER, at = @At("RETURN"))
    //$$ private void postRenderHandsWithItems(CallbackInfo ci) {
    //$$     post();
    //$$ }
    //#endif

    @Unique
    private static void pre() {
        EMFManager.getInstance().entityRenderCount++;
        EMFState.isInHand = true;
        var state = EMFEntityRenderState.manualPlayerState();
        if (state != null) {
            ETFState.mount(state);
        }
    }

    @Unique
    private static void post() {
        var state = EMFState.state();
        if (state != null && state.isManualPlayerState()) {
            ETFState.unMount();
        }
        EMFState.isInHand = false;
    }

}

