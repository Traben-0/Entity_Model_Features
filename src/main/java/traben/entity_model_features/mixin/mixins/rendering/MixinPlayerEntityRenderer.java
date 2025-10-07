package traben.entity_model_features.mixin.mixins.rendering;

import net.minecraft.client.Minecraft;
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

//#if MC < 12109
//$$ import net.minecraft.client.renderer.entity.player.PlayerRenderer;
//#endif

//#if MC >= 12109
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Avatar;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
//#elseif MC >= 12102
//$$ import net.minecraft.client.renderer.entity.state.PlayerRenderState;
//#endif

//#if MC >= 12102
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
//#endif


//#if MC >= 12109
@Mixin(AvatarRenderer.class)
//#else
//$$ @Mixin(PlayerRenderer.class)
//#endif
public abstract class MixinPlayerEntityRenderer {

    //#if MC >= 12102

        //#if MC >= 12109
        @Shadow public abstract <AvatarlikeEntity extends Avatar & ClientAvatarEntity> void extractRenderState(final AvatarlikeEntity avatar, final AvatarRenderState avatarRenderState, final float f);
        //#else
        //$$ @Shadow public abstract void extractRenderState(final AbstractClientPlayer abstractClientPlayer, final PlayerRenderState playerRenderState, final float f);
        //#endif


    @Shadow
    public abstract
        //#if MC >= 12109
        AvatarRenderState
        //#else
        //$$ PlayerRenderState
        //#endif
    createRenderState();


    @Unique
    private
        //#if MC >= 12109
        AvatarRenderState
        //#else
        //$$ PlayerRenderState
        //#endif
    emf$renderState(){
        var state = createRenderState();
        extractRenderState(Minecraft.getInstance().player, state, EMFAnimationEntityContext.getTickDelta());
        return state;
    }
    //#endif

    @Inject(method = "renderHand", at = @At(value = "HEAD"))
    private void emf$setHand(final CallbackInfo ci) {
        //#if MC >= 12102
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState) ((HoldsETFRenderState)emf$renderState()).etf$getState());
        //#else
        //$$ EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState)
        //$$         ETFEntityRenderState.forEntity((ETFEntity) Minecraft.getInstance().player));
        //#endif
        EMFAnimationEntityContext.isFirstPersonHand = true;
    }

    @Inject(method = "renderHand", at = @At(value = "RETURN"))
    private void emf$unsetHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
    }

}