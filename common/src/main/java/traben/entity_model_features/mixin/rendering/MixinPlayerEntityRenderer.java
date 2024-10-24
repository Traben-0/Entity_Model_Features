package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFEntity;
#if MC > MC_21
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
#endif

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerEntityRenderer {

    #if MC > MC_21
    @Shadow
    public abstract void extractRenderState(final AbstractClientPlayer abstractClientPlayer, final PlayerRenderState playerRenderState, final float f);

    @Shadow
    public abstract PlayerRenderState createRenderState();

    @Unique
    private PlayerRenderState emf$renderState(){
        var state = createRenderState();
        extractRenderState(Minecraft.getInstance().player, state, EMFAnimationEntityContext.getTickDelta());
        return state;
    }
    #endif

    @Inject(method = "renderHand",
            at = @At(value = "HEAD"))
    private void emf$setHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntity) Minecraft.getInstance().player#if MC > MC_21 , emf$renderState() #endif);
        EMFAnimationEntityContext.isFirstPersonHand = true;
    }

    @Inject(method = "renderHand",
            at = @At(value = "RETURN"))
    private void emf$unsetHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
    }

}