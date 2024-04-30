package traben.entity_model_features.mixin.rendering;


import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFManager;

@Mixin(CapeFeatureRenderer.class)
public abstract class MixinCapeFeatureRenderer  {



    @Unique
    private ModelPart emf$capeModelPart = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setEmf$Model(FeatureRendererContext<?, ?> featureRendererContext, CallbackInfo ci) {
        if (EMF.testForForgeLoadingError()) return;

        var layer = new EntityModelLayer(new Identifier("minecraft", "player"), "cape");
        ModelPart capeModel = EMFManager.getInstance().injectIntoModelRootGetter(layer, PlayerEntityModel.getTexturedModelData(Dilation.NONE,false).getRoot().createPart(64,64));

        //separate cape model, if it has a custom jem model
        if (capeModel instanceof EMFModelPartRoot && capeModel.hasChild("cloak")) {
            emf$capeModelPart = capeModel.getChild("cloak");
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V",shift = At.Shift.BEFORE),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void emf$RenderCustomModelOnly(final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, final int i, final AbstractClientPlayerEntity abstractClientPlayerEntity, final float f, final float g, final float h, final float j, final float k, final float l, final CallbackInfo ci, final ItemStack itemStack) {
        if (emf$capeModelPart != null) {
            var cape = abstractClientPlayerEntity.getCapeTexture();
            var layer = RenderLayer.getEntityTranslucent(cape);
            var consumer = vertexConsumerProvider.getBuffer(layer);


            if (!abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST).isEmpty()){
                matrixStack.translate(0.0f, -0.0625f, 0.1875f);
            }else{
                matrixStack.translate(0.0f, 0.0f, 0.125f);
            }
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

            emf$capeModelPart.render(matrixStack, consumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.pop();
            ci.cancel();
        }
    }

}
