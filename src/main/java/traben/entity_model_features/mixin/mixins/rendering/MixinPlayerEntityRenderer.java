package traben.entity_model_features.mixin.mixins.rendering;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.parts.EMFModelPartVanilla;
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
public abstract class MixinPlayerEntityRenderer<AvatarlikeEntity extends Avatar & ClientAvatarEntity>
        extends LivingEntityRenderer<AvatarlikeEntity, AvatarRenderState, PlayerModel> {

    public MixinPlayerEntityRenderer() { super(null, null, 0); }
//#else
//$$ @Mixin(PlayerRenderer.class)
//$$ public abstract class MixinPlayerEntityRenderer {
//#endif


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




    //#if MC >= 12109
    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    private void emf$setHandAnims(CallbackInfo ci, @Local(argsOnly = true) ModelPart modelPart, @Local(argsOnly = true) PoseStack poseStack
    ) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState) ((HoldsETFRenderState)emf$renderState()).etf$getState());
        EMFAnimationEntityContext.isFirstPersonHand = true;

        // flag this for later submit render
        if (modelPart instanceof EMFModelPartVanilla vanilla) {
            vanilla.isPlayerArm = true;

            // Animate
            if (getModel() instanceof IEMFModel emf && emf.emf$isEMFModel()) {
                emf.emf$getEMFRootModel().triggerManualAnimation(poseStack);
                modelPart.translateAndRotate(poseStack);
            }
        }

    }
    //#else
    //$$ @Inject(method = "renderHand", at = @At(value = "HEAD"))
    //$$ private void emf$setHand(CallbackInfo ci
    //$$ ) {
        //#if MC >= 12102
        //$$ EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState) ((HoldsETFRenderState)emf$renderState()).etf$getState());
        //#else
        //$$ EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState)
        //$$         ETFEntityRenderState.forEntity((ETFEntity) Minecraft.getInstance().player));
        //#endif
    //$$     EMFAnimationEntityContext.isFirstPersonHand = true; // moot in 1.21.9+ as despite the method name this is actually a submit
    //$$ }
    //#endif

    @Inject(method = "renderHand", at = @At(value = "RETURN"))
    private void emf$unsetHand(final CallbackInfo ci) {
        EMFAnimationEntityContext.reset();
    }


}