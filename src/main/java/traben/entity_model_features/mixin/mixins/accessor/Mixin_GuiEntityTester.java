package traben.entity_model_features.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12106
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
@Mixin(GuiEntityRenderer.class)
public class Mixin_GuiEntityTester {
    @Inject(method = "renderToTexture(Lnet/minecraft/client/gui/render/state/pip/GuiEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
        at = @At("HEAD"))
    private static void etf$beforeRenderToTexture(final CallbackInfo ci) {
        EMFAnimationEntityContext.setIsInGui = true;
    }

    @Inject(method = "renderToTexture(Lnet/minecraft/client/gui/render/state/pip/GuiEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At("TAIL"))
    private static void etf$afterRenderToTexture(final CallbackInfo ci) {
        EMFAnimationEntityContext.setIsInGui = false;
    }
}
// todo update this old method?
//#else
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//$$ import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
//$$ @Mixin(EntityRenderDispatcher.class)
//$$ public interface Mixin_GuiEntityTester { @Accessor boolean isShouldRenderShadow(); }
//#endif
