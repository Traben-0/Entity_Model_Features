package traben.entity_model_features.mixin.mixins.rendering;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
//#if MC < 26.2
import net.minecraft.client.renderer.MultiBufferSource;
//#endif
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
import traben.entity_model_features.models.animation.state.EMFBipedPose;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFEntity;

//#if MC >= 12102
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
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

    @Shadow
    public abstract M getModel();

    @SuppressWarnings("unused")
    protected MixinLivingEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    //#if MC > 26.2
    //$$ todo check that they still use this.model and not the getter
    //#elseif MC >= 12109
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Ljava/lang/Object;)V",
                    shift = At.Shift.AFTER))
    private void falseAnimation(CallbackInfo ci, @Local(argsOnly = true) PoseStack pose, @Local(argsOnly = true) S renderState) {
        // animate so that dependant layers can read the positions (only applies if they set their matrix prior to submission)
        IEMFModel model = (IEMFModel) this.model;
        if (model.emf$isEMFModel() && model.emf$getEMFRootModel().hasAnimation()) {
            model.emf$getEMFRootModel().triggerManualAnimation(pose);
            // Store the biped pose in the render state for use by layers that need it.
            if (getModel() instanceof HumanoidModel<?> humanoidModel) {
                var state = (EMFEntityRenderState) ((HoldsETFRenderState) renderState).etf$getState();
                if (state != null) state.setBipedPose(new EMFBipedPose(humanoidModel));
            }

        }
    }
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

    //#if MC >= 1.21.9
    private static final String RENDER = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V";
    //#elseif MC >= 1.21.2
    //$$ private static final String RENDER = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";
    //#else
    //$$ private static final String RENDER = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";
    //#endif


    @Inject(method = RENDER, at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void emf$grabEntity(CallbackInfo ci) {
        EMFState.isLayerPhase = true;
        EMFState.isMainPhase = false;
        //#if MC < 1.21.9
        //$$ // Set whatever model we used as the main one, handled by submits in 1.21.9+
        //$$ if (getModel() instanceof IEMFModel emf && emf.emf$isEMFModel()) {
        //$$     emf.emf$getEMFRootModel().isMainModel = true;
        //$$ }
        //#endif
    }

    @Inject(method = RENDER, at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private void emf$eachFeatureLoop(CallbackInfo ci) {
        //todo needed for stray bogged drowned outer layers in 1.21.2+
        //check its needed for 1.21.1
        EMFManager.getInstance().entityRenderCount++;
        EMFState.isLayerPhase = true;
    }

    @Inject(method = RENDER, at = @At("HEAD"))
    private void emf$preRender(CallbackInfo ci) {
        EMFState.isLayerPhase = false;
        EMFState.isMainPhase = true;
    }

    @Inject(method = RENDER, at = @At("TAIL"))
    private void emf$postRender(CallbackInfo ci) {
        EMFState.isLayerPhase = false;
        EMFState.isMainPhase = false;
    }

}
