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
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.math.EMFMath;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_model_features.models.parts.EMFModelPartVanilla;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.ETFState;
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
@Mixin(value = AvatarRenderer.class, priority = 1100) // priority ensures the first person hand state wraps ETF's submits properly
public abstract class MixinPlayerEntityRenderer<AvatarlikeEntity extends Avatar & ClientAvatarEntity>
        extends LivingEntityRenderer<AvatarlikeEntity, AvatarRenderState, PlayerModel> {

    public MixinPlayerEntityRenderer() { super(null, null, 0); }
//#else
//$$ @Mixin(PlayerRenderer.class)
//$$ public abstract class MixinPlayerEntityRenderer {
//#endif


    @Inject(method = "renderHand", at = @At(value = "HEAD"))
    private void emf$setHandAnimState(CallbackInfo ci) {
        // Before visibility checks
        var state = EMFEntityRenderState.manualPlayerState();
        state.setIsFirstPersonHand(true);
        ETFState.mount(state);
    }
    //#if MC >= 12109
    @Inject(method = "renderHand", at = @At(value = "INVOKE", target =
            //#if MC >= 26.3
            //$$ "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/UvMapping;)V"
            //#else
            "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"
            //#endif
    ))
    private void emf$setHandAnims(CallbackInfo ci, @Local(argsOnly = true) ModelPart modelPart) {
        // flag this for later submit render
        if (modelPart instanceof EMFModelPartVanilla vanilla) {
            vanilla.isPlayerArm = true;
            // Position now for mods that need it
            vanilla.getRoot().animate();
        }

    }
    //#endif

    @Inject(method = "renderHand", at = @At(value = "RETURN"))
    private void emf$unsetHand(final CallbackInfo ci) {
        var state = EMFState.state();
        if (state == null || !state.isManualPlayerState()) return;
        ETFState.unMount();
    }


}