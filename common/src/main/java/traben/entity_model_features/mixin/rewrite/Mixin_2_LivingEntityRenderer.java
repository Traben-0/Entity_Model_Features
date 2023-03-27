package traben.entity_model_features.mixin.rewrite;


import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.accessor.PlayerEntityModelAccessor;
import traben.entity_model_features.utils.EMFManager;


@Mixin(LivingEntityRenderer.class)
public abstract class Mixin_2_LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {


    protected Mixin_2_LivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Shadow
    public abstract M getModel();

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$SetAngles(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci, float h, float j, float k, float m, float l, float n, float o) {
        if (getModel() instanceof PlayerEntityModel<?> plyr && !(plyr instanceof PiglinEntityModel<?>)) {
            EMFManager.getInstance().setAnglesOnParts(((PlayerEntityModelAccessor) plyr).isThinArms() ? "player_slim" : "player", livingEntity, o, n, l, k, m);
        } else {
            EMFManager.getInstance().setAnglesOnParts(livingEntity, o, n, l, k, m);
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$SetModelVariant(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        EMFManager.getInstance().doVariantCheckFor(livingEntity);
    }

    @Inject(
            method = "getRenderLayer",
            at = @At(value = "RETURN"), cancellable = true)
    private void etf$alterTexture(T entity, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<RenderLayer> cir) {
        //todo reimpliment in model variating code
        Identifier overrideTextureFromEMF = EMFManager.getInstance().getEMFOverrideTexture(entity);
        if (overrideTextureFromEMF != null) {
            if (translucent) {
                cir.setReturnValue(RenderLayer.getItemEntityTranslucentCull(overrideTextureFromEMF));
            } else if (showBody) {
                if (EMFConfig.getConfig().forceTranslucentMobRendering) {
                    cir.setReturnValue(RenderLayer.getItemEntityTranslucentCull(overrideTextureFromEMF));
                } else {
                    cir.setReturnValue(this.getModel().getLayer(overrideTextureFromEMF));
                }
            } else {
                cir.setReturnValue(showOutline ? RenderLayer.getOutline(overrideTextureFromEMF) : null);
            }
        }
    }


    @Inject(
            method = "getRenderLayer",
            at = @At(value = "RETURN"), cancellable = true)
    private void etf$alterTexture(T entity, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<RenderLayer> cir) {
        //todo reimpliment in model variating code
        Identifier overrideTextureFromEMF = EMFManager.getInstance().getEMFOverrideTexture(entity);
        if (overrideTextureFromEMF != null) {
            if (translucent) {
                cir.setReturnValue(RenderLayer.getItemEntityTranslucentCull(overrideTextureFromEMF));
            } else if (showBody) {
                if (EMFConfig.getConfig().forceTranslucentMobRendering) {
                    cir.setReturnValue(RenderLayer.getItemEntityTranslucentCull(overrideTextureFromEMF));
                } else {
                    cir.setReturnValue(this.getModel().getLayer(overrideTextureFromEMF));
                }
            } else {
                cir.setReturnValue(showOutline ? RenderLayer.getOutline(overrideTextureFromEMF) : null);
            }
        }
    }

}
