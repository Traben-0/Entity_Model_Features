package traben.entity_model_features.mixin.rendering;


import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.ETFApi;


@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {


    @Shadow
    protected M model;
    @Unique
    private M emf$heldModelToForce = null;

    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(EntityRendererFactory.Context ctx, EntityModel<T> model, float shadowRadius, CallbackInfo ci) {
        if (this.model != null && ((IEMFModel) this.model).emf$isEMFModel()) {
            emf$heldModelToForce = this.model;
        }
    }


    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
                    , shift = At.Shift.BEFORE))
    private void emf$Animate(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {

        if (emf$heldModelToForce != null) {
            if (!emf$heldModelToForce.equals(model)) {
                boolean replace = EMFConfig.getConfig().tryForceEmfModels && "minecraft".equals(EntityType.getId(livingEntity.getType()).getNamespace());
                EMFUtils.EMFOverrideMessage(emf$heldModelToForce.getClass().getName(), model == null ? "null" : model.getClass().getName(), replace);
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
                if (EMFConfig.getConfig().vanillaModelRenderMode != EMFConfig.VanillaModelRenderMode.Off) {
                    root.tryRenderVanillaRootNormally(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(getTexture(livingEntity))), i, OverlayTexture.DEFAULT_UV);
                }
                //simple attempt at a physics mod workaround
                if (livingEntity.isDead() && EMFManager.getInstance().IS_PHYSICS_MOD_INSTALLED && EMFConfig.getConfig().attemptPhysicsModPatch_2 != EMFConfig.PhysicsModCompatChoice.OFF) {
                    root.tryRenderVanillaFormatRoot(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(getTexture(livingEntity))), i, OverlayTexture.DEFAULT_UV);
                    //the regular render will get cancelled anyway nothing further to do
                }
            }
        }
    }


    @Redirect(
            method = "getRenderLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier emf$getTextureRedirect(LivingEntityRenderer<?, ?> instance, Entity entity) {

        if (((IEMFModel) model).emf$isEMFModel()) {
            EMFModelPartRoot root = ((IEMFModel) model).emf$getEMFRootModel();
            if (root != null) {
                //noinspection unchecked
                return root.textureOverride == null ? getTexture((T) entity) : ETFApi.getCurrentETFVariantTextureOfEntity(entity, root.textureOverride);
            }
        }

        //noinspection unchecked
        return getTexture((T) entity);

    }


}
