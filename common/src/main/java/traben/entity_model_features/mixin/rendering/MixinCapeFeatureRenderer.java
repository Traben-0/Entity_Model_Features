package traben.entity_model_features.mixin.rendering;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFVersionDifferenceManager;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(CapeLayer.class)
public abstract class MixinCapeFeatureRenderer  {



    @Unique
    private ModelPart emf$capeModelPart = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setEmf$Model(RenderLayerParent<?, ?> featureRendererContext, CallbackInfo ci) {
        if (EMF.testForForgeLoadingError()) return;

        if(EMFVersionDifferenceManager.isThisModLoaded("essential")){
            EMFUtils.logWarn("The 'Essential' mod is loaded, disabling EMF's 'player_cape.jem' support due to conflict");
            return;
        }

        var layer = new ModelLayerLocation(EMFUtils.res("minecraft", "player"), "cape");
        ModelPart capeModel = EMFManager.getInstance().injectIntoModelRootGetter(layer, PlayerModel.createMesh(CubeDeformation.NONE,false).getRoot().bake(64,64));

        //separate cape model, if it has a custom jem model
        if (capeModel instanceof EMFModelPartRoot && capeModel.hasChild("cloak")) {
            emf$capeModelPart = capeModel.getChild("cloak");
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",shift = At.Shift.BEFORE),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void emf$RenderCustomModelOnly(final PoseStack matrixStack, final MultiBufferSource vertexConsumerProvider, final int i, final AbstractClientPlayer abstractClientPlayerEntity, final float f, final float g, final float h, final float j, final float k, final float l, final CallbackInfo ci,
                                           final PlayerSkin skinTextures, final ItemStack itemStack) {
        if (emf$capeModelPart != null) {
            var cape = skinTextures.capeTexture();
            var layer = RenderType.entityTranslucent(cape);
            var consumer = vertexConsumerProvider.getBuffer(layer);


            if (abstractClientPlayerEntity.getItemBySlot(EquipmentSlot.CHEST).is(ItemTags.CHEST_ARMOR)){
                matrixStack.translate(0.0f, -0.0625f, 0.1875f);
            }else{
                matrixStack.translate(0.0f, 0.0f, 0.125f);
            }
            matrixStack.mulPose(Axis.YP.rotationDegrees(180));

            emf$capeModelPart.render(matrixStack, consumer, i, OverlayTexture.NO_OVERLAY #if MC >= MC_21  #else , 1f, 1f, 1f, 1f #endif);
            matrixStack.popPose();
            ci.cancel();
        }
    }

}
