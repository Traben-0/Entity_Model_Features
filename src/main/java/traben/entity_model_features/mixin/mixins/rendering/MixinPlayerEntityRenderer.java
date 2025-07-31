package traben.entity_model_features.mixin.mixins.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import traben.entity_texture_features.utils.ETFEntity;
//#if MC >= 12102
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
//#endif

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerEntityRenderer {

    //#if MC >= 12102
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
    //#endif

    @Inject(method = "renderHand",
            at = @At(value = "HEAD"))
    private void emf$setHand(final CallbackInfo ci) {
        //#if MC >= 12102
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState) ((HoldsETFRenderState)emf$renderState()).etf$getState());
        //#else
        //$$ EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState)
        //$$         ETFEntityRenderState.forEntity((ETFEntity) Minecraft.getInstance().player));
        //#endif
        EMFAnimationEntityContext.isFirstPersonHand = true;
    }

    @Inject(method = "renderHand",
            at = @At(value = "RETURN"))
    private void emf$unsetHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
    }

}