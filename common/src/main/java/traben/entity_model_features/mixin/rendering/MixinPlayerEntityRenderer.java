package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFEntity;

@Mixin(PlayerRenderer.class)
public class MixinPlayerEntityRenderer {


    @Inject(method = "renderHand",
            at = @At(value = "HEAD"))
    private void emf$setHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntity) Minecraft.getInstance().player);
        EMFAnimationEntityContext.isFirstPersonHand = true;
    }

    @Inject(method = "renderHand",
            at = @At(value = "RETURN"))
    private void emf$unsetHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
    }

}