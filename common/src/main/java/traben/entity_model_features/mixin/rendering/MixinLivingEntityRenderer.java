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
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_model_features.utils.EMFUtils;

#if MC > MC_21
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
#endif

@Mixin(LivingEntityRenderer.class)
#if MC > MC_21
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> implements RenderLayerParent<S, M> {

    @Shadow
    public abstract ResourceLocation getTextureLocation(final S livingEntityRenderState);


#else
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
#endif

    @Shadow
    protected M model;

    @Unique
    private EMFAnimationEntityContext.IterationContext emf$heldIteration = null;

    @SuppressWarnings("unused")
    protected MixinLivingEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }



    @Inject(method =
            #if MC > MC_21
            "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            #else
            "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            #endif
            at = @At(value = "INVOKE",
                    #if MC >= MC_21
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"
                    #else
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
                    #endif
                    , shift = At.Shift.BEFORE))

#if MC > MC_21
    private void emf$Animate(final S livingEntityRenderState, final PoseStack matrixStack, final MultiBufferSource vertexConsumerProvider, final int i, final CallbackInfo ci) {
        if(!(EMFAnimationEntityContext.getEMFEntity() instanceof LivingEntity livingEntity)) {
            return;
        }

#else
    private void emf$Animate(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
#endif

//        if (emf$heldModelToForce != null) {
//            if (!emf$heldModelToForce.equals(model) && !(livingEntity instanceof Pufferfish)) {
//                boolean replace = EMF.config().getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getKey(livingEntity.getType()).getNamespace());
//                EMFUtils.overrideMessage(emf$heldModelToForce.getClass().getName(), model == null ? "null" : model.getClass().getName(), replace);
//                if (replace) {
//                    model = emf$heldModelToForce;
//                }
//            }
//            emf$heldModelToForce = null;
//        }


        //EMFManager.getInstance().preRenderEMFActions(emf$ModelId,livingEntity, vertexConsumerProvider, o, n, l, k, m);
        if (((IEMFModel) model).emf$isEMFModel()) {

            EMFModelPartRoot root = ((IEMFModel) model).emf$getEMFRootModel();
            if (root != null) {
                if (EMF.config().getConfig().getVanillaHologramModeFor((EMFEntity) livingEntity) != EMFConfig.VanillaModelRenderMode.OFF) {
                    root.tryRenderVanillaRootNormally(matrixStack, vertexConsumerProvider.getBuffer(
                            RenderType.entityTranslucent(getTextureLocation(#if MC > MC_21 livingEntityRenderState #else livingEntity #endif))), i, OverlayTexture.NO_OVERLAY);
                }
                //simple attempt at a physics mod workaround
//                if (livingEntity.isDeadOrDying() && EMFManager.getInstance().IS_PHYSICS_MOD_INSTALLED
//                        && EMF.config().getConfig().getPhysicsModModeFor((EMFEntity) livingEntity) != EMFConfig.PhysicsModCompatChoice.OFF) {
//                    root.tryRenderVanillaFormatRoot(matrixStack, vertexConsumerProvider.getBuffer(
//                            RenderType.entityTranslucent(getTextureLocation(#if MC > MC_21 livingEntityRenderState #else livingEntity #endif))), i, OverlayTexture.NO_OVERLAY);
//                    //the regular render will get cancelled anyway nothing further to do
//                }
            }
        }
    }



    @Redirect(
            method = "getRenderType",
            at = @At(value = "INVOKE", target =
                    #if MC > MC_21
                    "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)Lnet/minecraft/resources/ResourceLocation;"            ))
    private ResourceLocation emf$getTextureRedirect(final LivingEntityRenderer<?,?,?> instance, final S livingEntityRenderState) {
                    #else
                    "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;"            ))
    private ResourceLocation emf$getTextureRedirect(LivingEntityRenderer<?,?> instance, Entity entity) {
                    #endif


        if (((IEMFModel) model).emf$isEMFModel()) {
            EMFModelPartRoot root = ((IEMFModel) model).emf$getEMFRootModel();
            if (root != null) {
                ResourceLocation texture = root.getTopLevelJemTexture();
                if (texture != null)
                    return texture;
            }
        }

        //noinspection unchecked
        return getTextureLocation(#if MC > MC_21 livingEntityRenderState #else (T) entity #endif);

    }

    @Inject(method =
            #if MC > MC_21
            "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            #else
            "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            #endif
            at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void emf$grabEntity(CallbackInfo ci) {
        emf$heldIteration = EMFAnimationEntityContext.getIterationContext();
    }

    @Inject(method =
            #if MC > MC_21
            "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            #else
            "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            #endif
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private void emf$eachFeatureLoop(CallbackInfo ci) {
        if (emf$heldIteration != null && EMFManager.getInstance().entityRenderCount != emf$heldIteration.entityRenderCount()) {
            EMFAnimationEntityContext.setIterationContext(emf$heldIteration);
        }
        //todo needed for stray bogged drowned outer layers in 1.21.2+
        //check its needed for 1.21.1
        EMFManager.getInstance().entityRenderCount++;
    }

}
