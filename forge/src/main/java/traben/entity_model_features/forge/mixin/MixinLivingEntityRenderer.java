package traben.entity_model_features.forge.mixin;


import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.mixin.accessor.PlayerEntityModelAccessor;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_texture_features.ETFApi;


@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {


    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Shadow
    public abstract M getModel();

    protected String emf$ModelId = null;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
            ,shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$Animate(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci, boolean FORGE_REQUIRED_VALUE, float h, float j, float k, float m, float l, float n, float o) {
            EMFManager.getInstance().preRenderEMFActions(emf$ModelId,livingEntity, vertexConsumerProvider, o, n, l, k, m);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "HEAD"))
    private void emf$SetModelVariant(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (getModel() instanceof PlayerEntityModel<?> plyr && livingEntity instanceof PlayerEntity){// !(plyr instanceof PiglinEntityModel<?>)) {
            //done separately because i need model access to test arm size
            emf$ModelId = ((PlayerEntityModelAccessor) plyr).isThinArms() ? "player_slim" : "player";
        } else {
            emf$ModelId = EMFManager.getTypeName(livingEntity);
        }
        EMFManager.getInstance().doVariantCheckFor(emf$ModelId,livingEntity);
    }


    @Redirect(
            method = "getRenderLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier emf$getTextureRedirect(LivingEntityRenderer<?,?> instance, Entity entity){
        Identifier emfIdentifier = EMFManager.getInstance().getRootModelTextureOverride(emf$ModelId);
        //noinspection unchecked
        return emfIdentifier == null ? getTexture((T) entity) : ETFApi.getCurrentETFVariantTextureOfEntity(entity,emfIdentifier) ;

    }


}
