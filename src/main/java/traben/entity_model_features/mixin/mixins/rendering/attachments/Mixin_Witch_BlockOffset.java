package traben.entity_model_features.mixin.mixins.rendering.attachments;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAttachment;

@Mixin(WitchItemLayer.class)
public abstract class Mixin_Witch_BlockOffset extends CrossedArmsItemLayer {

    public Mixin_Witch_BlockOffset() {
        //#if MC > 1.21.3
        super(null);
        //#else
        //$$ super(null, null);
        //#endif
    }

    //#if MC > 1.21.3
    private static final String RENDER_METHOD = "applyTranslation(Lnet/minecraft/client/renderer/entity/state/WitchRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V";
    //#else
    //$$ private static final String RENDER_METHOD = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/WitchRenderState;FF)V";
    //#endif

    @WrapWithCondition(method = RENDER_METHOD,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;translateAndRotate(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 0))
    private boolean offsetBlock(ModelPart instance, PoseStack arg, @Local(argsOnly = true) PoseStack poseStack, @Share("needsCancel") LocalBooleanRef needsCancel) {
        var model = (IEMFModel) getParentModel();
        if (model.emf$isEMFModel()) {
            var root = model.emf$getEMFRootModel();
            var positioner = root.getPositionerForAttachment(EMFAttachment.Type.WITCH);
            if (positioner != null) {
                positioner.accept(poseStack);
                needsCancel.set(true);
                return false;
            }
        }
        return true;
    }

    @WrapWithCondition(method = RENDER_METHOD,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;translateAndRotate(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 1))
    private boolean cancel(ModelPart instance, PoseStack arg, @Share("needsCancel") LocalBooleanRef needsCancel) {
        return !needsCancel.get();
    }

    //#if MC >= 1.21.9
    @WrapWithCondition(method = RENDER_METHOD,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/WitchModel;translateToHead(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private boolean cancel2(WitchModel instance, PoseStack poseStack, @Share("needsCancel") LocalBooleanRef needsCancel) {
        return !needsCancel.get();
    }
    //#elseif MC > 1.21
    //$$ @WrapWithCondition(method = RENDER_METHOD,
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;translateAndRotate(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 2))
    //$$ private boolean cancel2(ModelPart instance, PoseStack arg, @Share("needsCancel") LocalBooleanRef needsCancel) {
    //$$     return !needsCancel.get();
    //$$ }
    //#endif
}