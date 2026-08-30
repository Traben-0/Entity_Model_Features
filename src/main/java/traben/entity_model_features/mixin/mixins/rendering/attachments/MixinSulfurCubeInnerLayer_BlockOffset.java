package traben.entity_model_features.mixin.mixins.rendering.attachments;

//#if MC < 26.2
import org.spongepowered.asm.mixin.Mixin;
import traben.entity_texture_features.mixin.CancelTarget;

@Mixin(CancelTarget.class)
public abstract class MixinSulfurCubeInnerLayer_BlockOffset { }
//#else
//$$ import com.llamalad7.mixinextras.sugar.Local;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.client.model.monster.slime.SulfurCubeModel;
//$$ import net.minecraft.client.renderer.entity.layers.SulfurCubeInnerLayer;
//$$ import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import traben.entity_model_features.models.IEMFModel;
//$$ import traben.entity_model_features.models.animation.EMFAttachment;
//$$ import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
//$$
//$$ @Mixin(SulfurCubeInnerLayer.class)
//$$ public abstract class MixinSulfurCubeInnerLayer_BlockOffset {
//$$
//$$     @Shadow
//$$     @Final
//$$     private SulfurCubeModel smallModel;
//$$
//$$     @Shadow
//$$     @Final
//$$     private SulfurCubeModel normalModel;
//$$
//$$     @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/SulfurCubeRenderState;FF)V",
//$$             at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V"))
//$$     private void offsetBlock(CallbackInfo ci, @Local(argsOnly = true) PoseStack poseStack, @Local(argsOnly = true) SulfurCubeRenderState vanilla) {
//$$         var model = (IEMFModel) (vanilla.isBaby ? smallModel : normalModel);
//$$         if (model.emf$isEMFModel()) {
//$$             var root = model.emf$getEMFRootModel();
//$$             var positioner = root.getPositionerForAttachment(EMFAttachment.Type.SULFUR_CUBE);
//$$             if (positioner != null) {
//$$                 // Animate as it won't otherwise
//$$                 root.animate();
//$$                 positioner.accept(poseStack);
//$$             }
//$$         }
//$$     }
//$$ }
//#endif