package traben.entity_model_features.mixin.mixins.optional;


import org.spongepowered.asm.mixin.Mixin;
import traben.entity_texture_features.mixin.CancelTarget;

//#if MC >= 12102
@Mixin(value = CancelTarget.class)
public abstract class MixinLivingEntityRenderer_ValueCapturing{}
//#else
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.ModifyArg;
//$$ import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
//$$ import net.minecraft.client.model.EntityModel;
//$$ import net.minecraft.client.renderer.entity.EntityRenderer;
//$$ import net.minecraft.client.renderer.entity.EntityRendererProvider;
//$$ import net.minecraft.client.renderer.entity.LivingEntityRenderer;
//$$ import net.minecraft.client.renderer.entity.RenderLayerParent;
//$$ import net.minecraft.util.Mth;
//$$ import net.minecraft.world.entity.LivingEntity;
//$$
//$$ @Mixin(value = LivingEntityRenderer.class, priority = 2000)
//$$ public abstract class MixinLivingEntityRenderer_ValueCapturing<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
//$$     @SuppressWarnings("unused")
//$$     protected MixinLivingEntityRenderer_ValueCapturing(EntityRendererProvider.Context ctx) {
//$$         super(ctx);
//$$     }
//$$     @ModifyArg(
//$$             method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//$$             at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"),
//$$             index = 1, require = 0
//$$     )
//$$     private float emf$getLimbAngle(float limbAngle) {
//$$         EMFAnimationEntityContext.setLimbAngle(limbAngle);
//$$         return limbAngle;
//$$     }
//$$     @ModifyArg(
//$$             method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//$$             at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"),
//$$             index = 2, require = 0
//$$     )
//$$     private float emf$getLimbDistance(float limbDistance) {
//$$         EMFAnimationEntityContext.setLimbDistance(limbDistance);
//$$         return limbDistance;
//$$     }
//$$     @ModifyArg(
//$$             method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//$$             at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"),
//$$             index = 4, require = 0
//$$     )
//$$     private float emf$getHeadYaw(float headYaw) {
//$$         if (headYaw >= 180 || headYaw < -180) {
//$$             EMFAnimationEntityContext.setHeadYaw(Mth.wrapDegrees(headYaw));
//$$         } else {
//$$             EMFAnimationEntityContext.setHeadYaw(headYaw);
//$$         }
//$$         return headYaw;
//$$     }
//$$     @ModifyArg(
//$$             method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//$$             at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"),
//$$             index = 5, require = 0
//$$     )
//$$     private float emf$getHeadPitch(float headPitch) {
//$$         EMFAnimationEntityContext.setHeadPitch(headPitch);
//$$         return headPitch;
//$$     }
//$$ }
//#endif
