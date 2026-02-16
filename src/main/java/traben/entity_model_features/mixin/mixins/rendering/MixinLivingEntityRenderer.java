package traben.entity_model_features.mixin.mixins.rendering;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
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
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFEntity;

//#if MC >= 12102
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
//#endif

@Mixin(LivingEntityRenderer.class)
//#if MC >= 12102
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> implements RenderLayerParent<S, M> {

    @Shadow
    public abstract ResourceLocation getTextureLocation(final S livingEntityRenderState);


//#else
//$$ public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
//#endif

    @Shadow
    protected M model;

    @SuppressWarnings("unused")
    protected MixinLivingEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    //#if MC >= 12109
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Ljava/lang/Object;)V",
                    shift = At.Shift.AFTER))
    private void falseAnimation(CallbackInfo ci, @Local PoseStack pose) {
        // animate so that dependant layers can read the positions (only applies if they set their matrix prior to submission)
        IEMFModel model = (IEMFModel) getModel();
        if (model.emf$isEMFModel()) model.emf$getEMFRootModel().triggerManualAnimation(pose);
    }
    //#else
    //$$ @ModifyExpressionValue(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;shouldRenderLayers(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)Z"))
    //$$ private boolean armOverrides(boolean original, @Local PoseStack pose) {
    //$$     if (original) {
    //$$         // check arm overrides
    //$$         IEMFModel model = (IEMFModel) getModel();
    //$$         if (model.emf$isEMFModel()) model.emf$getEMFRootModel().checkArmOverrides(pose);
    //$$     }
    //$$     return original;
    //$$ }
    //#endif



    //#if MC >=12112
    //$$ dont forget this
    //#elseif MC < 12109
    //$$ @Inject(method =
            //#if MC >= 12102
            //$$ "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#else
            //$$ "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#endif
    //$$         at = @At(value = "INVOKE",
                    //#if MC >= 12100
                    //$$ target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"
                    //#else
                    //$$ target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
                    //#endif
    //$$                 , shift = At.Shift.BEFORE))
    //$$
        //#if MC >= 12102
        //$$     private void emf$Animate(final S livingEntityRenderState, final PoseStack matrixStack, final MultiBufferSource vertexConsumerProvider, final int i, final CallbackInfo ci) {
        //$$         if(!(EMFAnimationEntityContext.getEMFEntity() instanceof LivingEntity livingEntity)) {
        //$$             return;
        //$$         }
        //$$
        //#else
        //$$     private void emf$Animate(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
        //#endif
    //$$
    //$$     //EMFManager.getInstance().preRenderEMFActions(emf$ModelId,livingEntity, vertexConsumerProvider, o, n, l, k, m);
    //$$     if (((IEMFModel) model).emf$isEMFModel()) {
    //$$
    //$$         EMFModelPartRoot root = ((IEMFModel) model).emf$getEMFRootModel();
    //$$         if (root != null) {
    //$$             if (EMF.config().getConfig().getVanillaHologramModeFor((EMFEntity) livingEntity) != EMFConfig.VanillaModelRenderMode.OFF) {
    //$$                 root.tryRenderVanillaRootNormally(matrixStack, vertexConsumerProvider.getBuffer(
    //$$                         RenderType.entityTranslucent(getTextureLocation(
                                    //#if MC >= 12102
                                    //$$ livingEntityRenderState
                                    //#else
                                    //$$ livingEntity
                                    //#endif
    //$$                         ))), i, OverlayTexture.NO_OVERLAY);
    //$$             }
    //$$         }
    //$$     }
    //$$ }
    //#endif


    @ModifyExpressionValue(method = "getRenderType", at = @At(value = "INVOKE",
            //#if MC >= 12102
            target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)Lnet/minecraft/resources/ResourceLocation;"
            //#else
            //$$ target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;"
            //#endif
    ))
    private ResourceLocation emf$getTextureRedirect(final ResourceLocation original){

        if (((IEMFModel) model).emf$isEMFModel()) {
            EMFModelPartRoot root = ((IEMFModel) model).emf$getEMFRootModel();
            if (root != null) {
                ResourceLocation texture = root.getTopLevelJemTexture();
                if (texture != null)
                    return texture;
            }
        }

        return original;

    }

    //#if MC >=12112
    //$$ dont forget this
    //#elseif MC < 12109
    //$$ @Inject(method =
            //#if MC >= 12102
            //$$ "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#else
            //$$ "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#endif
    //$$         at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    //$$ private void emf$grabEntity(CallbackInfo ci, @Share("iteration") LocalRef<EMFAnimationEntityContext.IterationContext> emf$heldIteration) {
    //$$     emf$heldIteration.set(EMFAnimationEntityContext.getIterationContext());
    //$$ }
    //$$
    //$$ @Inject(method =
            //#if MC >= 12102
            //$$ "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#else
            //$$ "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            //#endif
    //$$         at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    //$$ private void emf$eachFeatureLoop(CallbackInfo ci, @Share("iteration") LocalRef<EMFAnimationEntityContext.IterationContext> emf$heldIteration) {
    //$$     if (emf$heldIteration.get() != null && EMFManager.getInstance().entityRenderCount != emf$heldIteration.get().entityRenderCount()) {
    //$$         EMFAnimationEntityContext.setIterationContext(emf$heldIteration.get());
    //$$     }
    //$$     //todo needed for stray bogged drowned outer layers in 1.21.2+
    //$$     //check its needed for 1.21.1
    //$$     EMFManager.getInstance().entityRenderCount++;
    //$$ }
    //#endif

}
