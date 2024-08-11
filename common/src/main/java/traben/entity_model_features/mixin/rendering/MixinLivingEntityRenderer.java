package traben.entity_model_features.mixin.rendering;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pufferfish;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;


@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {


    @Shadow
    protected M model;
    @Unique
    private M emf$heldModelToForce = null;
    @Unique
    private EMFAnimationEntityContext.IterationContext emf$heldIteration = null;

    @SuppressWarnings("unused")
    protected MixinLivingEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(EntityRendererProvider.Context ctx, EntityModel<T> model, float shadowRadius, CallbackInfo ci) {
        if (this.model != null && ((IEMFModel) this.model).emf$isEMFModel()) {
            emf$heldModelToForce = this.model;
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE",
                    #if MC >= MC_21
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"
                    #else
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
                    #endif
                    , shift = At.Shift.BEFORE))
    private void emf$Animate(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {

        if (emf$heldModelToForce != null) {
            if (!emf$heldModelToForce.equals(model) && !(livingEntity instanceof Pufferfish)) {
                boolean replace = EMF.config().getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getKey(livingEntity.getType()).getNamespace());
                EMFUtils.overrideMessage(emf$heldModelToForce.getClass().getName(), model == null ? "null" : model.getClass().getName(), replace);
                if (replace) {
                    model = emf$heldModelToForce;
                }
            }
            emf$heldModelToForce = null;
        }


        //EMFManager.getInstance().preRenderEMFActions(emf$ModelId,livingEntity, vertexConsumerProvider, o, n, l, k, m);
        if (((IEMFModel) model).emf$isEMFModel()) {

            EMFModelPartRoot root = ((IEMFModel) model).emf$getEMFRootModel();
            if (root != null) {
                if (EMF.config().getConfig().vanillaModelHologramRenderMode_2 != EMFConfig.VanillaModelRenderMode.OFF) {
                    root.tryRenderVanillaRootNormally(matrixStack, vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(getTextureLocation(livingEntity))), i, OverlayTexture.NO_OVERLAY);
                }
                //simple attempt at a physics mod workaround
                if (livingEntity.isDeadOrDying() && EMFManager.getInstance().IS_PHYSICS_MOD_INSTALLED && EMF.config().getConfig().attemptPhysicsModPatch_2 != EMFConfig.PhysicsModCompatChoice.OFF) {
                    root.tryRenderVanillaFormatRoot(matrixStack, vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(getTextureLocation(livingEntity))), i, OverlayTexture.NO_OVERLAY);
                    //the regular render will get cancelled anyway nothing further to do
                }
            }
        }
    }


    @Redirect(
            method = "getRenderType",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation emf$getTextureRedirect(LivingEntityRenderer<?, ?> instance, Entity entity) {

        if (((IEMFModel) model).emf$isEMFModel()) {
            EMFModelPartRoot root = ((IEMFModel) model).emf$getEMFRootModel();
            if (root != null) {
                ResourceLocation texture = root.getTopLevelJemTexture();
                if (texture != null)
                    return texture;
            }
        }

        //noinspection unchecked
        return getTextureLocation((T) entity);

    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void emf$grabEntity(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
        emf$heldIteration = EMFAnimationEntityContext.getIterationContext();
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private void emf$eachFeatureLoop(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
        if (EMFManager.getInstance().entityRenderCount != emf$heldIteration.entityRenderCount()) {
            EMFAnimationEntityContext.setIterationContext(emf$heldIteration);
        }
    }

}
