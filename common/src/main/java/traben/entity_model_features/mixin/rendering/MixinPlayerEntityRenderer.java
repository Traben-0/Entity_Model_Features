package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFEntity;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer {


    @Inject(method = "renderArm",
            at = @At(value = "HEAD"))
    private void emf$setHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.isFirstPersonHand = true;
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntity) MinecraftClient.getInstance().player);
    }

    @Inject(method = "renderArm",
            at = @At(value = "RETURN"))
    private void emf$unsetHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
    }

}