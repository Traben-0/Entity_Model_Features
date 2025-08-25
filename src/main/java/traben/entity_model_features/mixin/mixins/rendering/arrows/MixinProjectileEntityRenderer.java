package traben.entity_model_features.mixin.mixins.rendering.arrows;


//#if MC >=12102
import org.spongepowered.asm.mixin.Mixin;
@Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
public abstract class MixinProjectileEntityRenderer { }
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import com.mojang.blaze3d.vertex.VertexConsumer;
//$$ import net.minecraft.client.renderer.MultiBufferSource;
//$$ import net.minecraft.client.renderer.RenderType;
//$$ import net.minecraft.client.renderer.entity.ArrowRenderer;
//$$ import net.minecraft.client.renderer.entity.EntityRenderer;
//$$ import net.minecraft.client.renderer.entity.EntityRendererProvider;
//$$ import net.minecraft.client.renderer.texture.OverlayTexture;
//$$ import net.minecraft.util.Mth;
//$$ import net.minecraft.world.entity.projectile.AbstractArrow;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
//$$ import traben.entity_model_features.utils.IEMFCustomModelHolder;
//$$
//$$ @Mixin(ArrowRenderer.class)
//$$ public abstract class MixinProjectileEntityRenderer<T extends AbstractArrow> extends EntityRenderer<T> {
//$$
//$$
//$$     public MixinProjectileEntityRenderer(final EntityRendererProvider.Context context) {
//$$         super(context);
//$$     }
//$$
//$$
//$$     @Inject(method = "render(Lnet/minecraft/world/entity/projectile/AbstractArrow;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//$$             at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;last()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;", shift = At.Shift.BEFORE), cancellable = true)
//$$     private void emf$cancelAndCEMRender(final T entity, final float entityYaw, final float partialTicks, final PoseStack poseStack, final MultiBufferSource buffer, final int packedLight, final CallbackInfo ci) {
//$$         if (this instanceof IEMFCustomModelHolder customModelHolder && customModelHolder.emf$hasModel()) {
//$$             //matrixStack.translate(4,0,0);
//$$             poseStack.scale(16, -12.8f, -12.8f);//result 0.9,  0.72   0.72
//$$             EMFAnimationEntityContext.setHeadYaw(entityYaw);
//$$             float s = (float) entity.shakeTime - partialTicks;
//$$             EMFAnimationEntityContext.setHeadPitch(-Mth.sin(s * 3.0F) * s);// copy of t
//$$             VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
//$$             customModelHolder.emf$getModel().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY
                    //#if MC >= 12100
                    //#else
                    //$$ ,1f, 1f, 1f, 1f
                    //#endif
//$$                 );
//$$             poseStack.popPose();
//$$             ci.cancel();
//$$         }
//$$     }
//$$
//$$ }
//#endif
