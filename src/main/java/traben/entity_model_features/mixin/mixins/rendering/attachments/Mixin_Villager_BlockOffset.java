package traben.entity_model_features.mixin.mixins.rendering.attachments;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerLikeModel;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAttachment;

@Mixin(CrossedArmsItemLayer.class)
public abstract class Mixin_Villager_BlockOffset extends RenderLayer {

    public Mixin_Villager_BlockOffset() {
        super(null);
    }

    //#if MC > 1.21.6
    @WrapWithCondition(method = "applyTranslation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/VillagerLikeModel;translateToArms(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 0))
    private boolean offsetBlock(VillagerLikeModel instance, net.minecraft.client.renderer.entity.state.EntityRenderState entityRenderState, PoseStack poseStack) {
    //#elseif MC > 1.21.3
    //$$ @WrapWithCondition(method = "applyTranslation",
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/VillagerLikeModel;translateToArms(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 0))
    //$$ private boolean offsetBlock(VillagerLikeModel instance, PoseStack poseStack) {
    //#elseif MC == 1.21.3
    //$$ @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V",
    //$$         at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 0))
    //$$ private boolean offsetBlock(PoseStack poseStack, org.joml.Quaternionf quaternionf) {
    //#else
    //$$ @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
    //$$     at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 0))
    //$$ private boolean offsetBlock(PoseStack poseStack, org.joml.Quaternionf quaternionf) {
    //#endif

        var model = (IEMFModel) getParentModel();
        if (model.emf$isEMFModel()) {
            var root = model.emf$getEMFRootModel();
            var positioner = root.getPositionerForAttachment(EMFAttachment.Type.VILLAGER);
            if (positioner != null) {
                positioner.accept(poseStack);
                return false;
            }
        }
        return true;
    }

}