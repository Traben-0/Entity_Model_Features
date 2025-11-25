package traben.entity_model_features.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

//#if MC >= 12106
import net.minecraft.client.gui.render.GuiRenderer;
@Mixin(GuiRenderer.class)
public class Mixin_GuiEntityTester {
    @Inject(method = "render",
        at = @At("HEAD"))
    private static void etf$beforeRenderToTexture(final CallbackInfo ci) {
        EMFAnimationEntityContext.setIsInGui = true;
    }

    @Inject(method = "render",
            at = @At("TAIL"))
    private static void etf$afterRenderToTexture(final CallbackInfo ci) {
        EMFAnimationEntityContext.setIsInGui = false;
    }
}
//#else
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//$$ import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
//$$ import net.minecraft.client.renderer.GameRenderer;
//$$ import org.spongepowered.asm.mixin.injection.ModifyArg;
//$$ @Mixin(GameRenderer.class)
//$$ public class Mixin_GuiEntityTester {
//$$     @ModifyArg(method = "render",
//$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"))
//$$     private static String etf$beforeRenderToTexture(String string) {
//$$         if (string.equals("gui")) EMFAnimationEntityContext.setIsInGui = true;
//$$         return string;
//$$     }
//$$
//$$     @Inject(method = "render",
//$$             at = @At("TAIL"))
//$$     private static void etf$afterRenderToTexture(final CallbackInfo ci) {
//$$         EMFAnimationEntityContext.setIsInGui = false;
//$$     }
//$$ }
//#endif
