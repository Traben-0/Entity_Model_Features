package traben.entity_model_features.mixin.rendering.arrows;


import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFCustomModelHolder;

@Mixin(ProjectileEntityRenderer.class)
public abstract class MixinProjectileEntityRenderer<T extends PersistentProjectileEntity> extends EntityRenderer<T> {


    public MixinProjectileEntityRenderer(final EntityRendererFactory.Context context) {
        super(context);
    }


    @Inject(method = "render(Lnet/minecraft/entity/projectile/PersistentProjectileEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;peek()Lnet/minecraft/client/util/math/MatrixStack$Entry;",
                    shift = At.Shift.BEFORE), cancellable = true)
    private void emf$cancelAndCEMRender(final T persistentProjectileEntity, final float f, final float g, final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, final int i, final CallbackInfo ci) {
        if (this instanceof EMFCustomModelHolder customModelHolder && customModelHolder.emf$hasModel()) {
            //matrixStack.translate(4,0,0);
            matrixStack.scale(16, -12.8f, -12.8f);//result 0.9,  0.72   0.72
            EMFAnimationEntityContext.setHeadYaw(f);
            float s = (float) persistentProjectileEntity.shake - g;
            EMFAnimationEntityContext.setHeadPitch(-MathHelper.sin(s * 3.0F) * s);// copy of t
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(persistentProjectileEntity)));
            customModelHolder.emf$getModel().render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            matrixStack.pop();
            ci.cancel();
        }
    }

}
