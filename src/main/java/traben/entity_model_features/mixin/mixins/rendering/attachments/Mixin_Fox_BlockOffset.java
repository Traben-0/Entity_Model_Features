package traben.entity_model_features.mixin.mixins.rendering.attachments;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAttachment;

@Mixin(FoxHeldItemLayer.class)
public abstract class Mixin_Fox_BlockOffset extends RenderLayer {

    public Mixin_Fox_BlockOffset() {
        super(null);
    }

    @WrapWithCondition(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/FoxRenderState;FF)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 0))
    private boolean offsetBlock(PoseStack instance, float f, float g, float h, @Local(argsOnly = true) PoseStack poseStack, @Share("cancelRestOfHead") LocalBooleanRef cancelRestOfHead) {
        var model = (IEMFModel) getParentModel();
        if (model.emf$isEMFModel()) {
            var root = model.emf$getEMFRootModel();
            var positioner = root.getPositionerForAttachment(EMFAttachment.Type.FOX);
            if (positioner != null) {
                positioner.accept(poseStack);
                cancelRestOfHead.set(true);
                return false;
            }
        }
        return true;
    }

//    @WrapWithCondition(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/FoxRenderState;FF)V",
//            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal = 0))
//    private boolean offsetBlock2(PoseStack instance, float f, float g, float h, @Share("cancelRestOfHead") LocalBooleanRef cancelRestOfHead) {
//        return !cancelRestOfHead.get();
//    }

    //#if MC >= 26.3

    @WrapWithCondition(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/FoxRenderState;FF)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;rotate(Lcom/mojang/math/Axis;F)V", ordinal = 0))
    private boolean offsetBlock3(PoseStack instance, Axis axis, float v, @Share("cancelRestOfHead") LocalBooleanRef cancelRestOfHead) {
        return !cancelRestOfHead.get();
    }
    @WrapWithCondition(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/FoxRenderState;FF)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;rotateDegrees(Lcom/mojang/math/Axis;F)V", ordinal = 0))
    private boolean offsetBlock4(PoseStack instance, Axis axis, float v, @Share("cancelRestOfHead") LocalBooleanRef cancelRestOfHead) {
        return !cancelRestOfHead.get();
    }
    @WrapWithCondition(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/FoxRenderState;FF)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;rotateDegrees(Lcom/mojang/math/Axis;F)V", ordinal = 1))
    private boolean offsetBlock5(PoseStack instance, Axis axis, float v, @Share("cancelRestOfHead") LocalBooleanRef cancelRestOfHead) {
        return !cancelRestOfHead.get();
    }
    //#else

    //#if MC > 1.21.4
    private static final String MULPOSE = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V";
    //#else
    //$$ private static final String MULPOSE = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V";
    //#endif

    @WrapWithCondition(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/FoxRenderState;FF)V",
            at = @At(value = "INVOKE", target = MULPOSE, ordinal = 0))
    private boolean offsetBlock3(PoseStack instance,
                                    //#if MC > 1.21.4
                                    Quaternionfc
                                    //#else
                                    //$$ org.joml.Quaternionf
                                    //#endif
                                    quaternionfc, @Share("cancelRestOfHead") LocalBooleanRef cancelRestOfHead) {
        return !cancelRestOfHead.get();
    }
    @WrapWithCondition(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/FoxRenderState;FF)V",
            at = @At(value = "INVOKE", target = MULPOSE, ordinal = 1))
    private boolean offsetBlock4(PoseStack instance,
                                 //#if MC > 1.21.4
                                 Quaternionfc
                                 //#else
                                 //$$ org.joml.Quaternionf
                                 //#endif
                                 quaternionfc, @Share("cancelRestOfHead") LocalBooleanRef cancelRestOfHead) {
        return !cancelRestOfHead.get();
    }
    @WrapWithCondition(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/FoxRenderState;FF)V",
            at = @At(value = "INVOKE", target = MULPOSE, ordinal = 2))
    private boolean offsetBlock5(PoseStack instance,
                                 //#if MC > 1.21.4
                                 Quaternionfc
                                 //#else
                                 //$$ org.joml.Quaternionf
                                 //#endif
                                 quaternionfc, @Share("cancelRestOfHead") LocalBooleanRef cancelRestOfHead) {
        return !cancelRestOfHead.get();
    }
    //#endif
}