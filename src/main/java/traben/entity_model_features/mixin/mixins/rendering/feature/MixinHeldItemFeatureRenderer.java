package traben.entity_model_features.mixin.mixins.rendering.feature;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.EMFAttachments;

//#if MC>=12109
import traben.entity_model_features.models.IEMFModel;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
//#endif

//#if MC >= 12104
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;

//#elseif MC >= 12102
//$$ import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
//$$ import net.minecraft.client.resources.model.BakedModel;
//#else
//$$ import net.minecraft.world.entity.LivingEntity;
//#endif

@Mixin(ItemInHandLayer.class)
public class
//#if MC >= 12104
MixinHeldItemFeatureRenderer<S extends ArmedEntityRenderState, M extends EntityModel<S> & ArmedModel>
//#elseif MC >= 12102
//$$ MixinHeldItemFeatureRenderer<S extends LivingEntityRenderState>
//#else
//$$ MixinHeldItemFeatureRenderer
//#endif
{

    //#if MC >= 12109
    private static final String RENDER_ARM = "submitArmWithItem";
    //#else
    //$$ private static final String RENDER_ARM = "renderArmWithItem";
    //#endif

    @Inject(method = RENDER_ARM,
            at = @At(value = "HEAD"))
    private void emf$setHand(final CallbackInfo ci, @Local HumanoidArm arm, @Share("armOverride") LocalRef<EMFAttachments> armOverride) {
        EMFAnimationEntityContext.setInHand = true;
        var state = EMFAnimationEntityContext.getEmfState();
        if (state == null) return;

        armOverride.set(arm == HumanoidArm.RIGHT ? state.rightArmOverride() : state.leftArmOverride());
    }

    @Inject(
            method = RENDER_ARM,
            at = @At(value = "INVOKE", target =
                    //#if MC >= 12109
                    "Lnet/minecraft/client/model/ArmedModel;translateToHand(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
                    //#else
                    //$$ "Lnet/minecraft/client/model/ArmedModel;translateToHand(Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
                    //#endif
                    shift = At.Shift.AFTER)
    )
    private void emf$transforms(final CallbackInfo ci, @Local(argsOnly = true) PoseStack matrices,
                                @Share("armOverride") LocalRef<EMFAttachments> armOverride,
                                @Share("needsPop") LocalBooleanRef needsToPop) {
        if (armOverride.get() != null) {
            var entry = armOverride.get().pose;
            if (entry != null) {
                needsToPop.set(true);

                //#if MC>=12105
                matrices.pushPose();
                matrices.last().set(entry);
                //#else
                //$$ matrices.poseStack.addLast(entry);
                //#endif
            }
        }
    }

    @Inject(method = RENDER_ARM,
            at = @At(value = "TAIL"))
    private void emf$unsetHand(final CallbackInfo ci, @Local(argsOnly = true) PoseStack matrices, @Share("needsPop") LocalBooleanRef needsToPop) {
        EMFAnimationEntityContext.setInHand = false;
        if (needsToPop.get()) matrices.popPose();
    }

}