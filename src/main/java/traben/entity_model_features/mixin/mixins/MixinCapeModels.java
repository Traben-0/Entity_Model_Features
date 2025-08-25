package traben.entity_model_features.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;

//#if MC < 12102
//$$ import net.minecraft.client.model.PlayerModel;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import com.mojang.blaze3d.vertex.VertexConsumer;
//$$ import com.mojang.math.Axis;
//$$
//$$ import net.minecraft.client.model.geom.ModelLayerLocation;
//$$ import net.minecraft.client.model.geom.ModelPart;
//$$ import net.minecraft.client.model.geom.builders.CubeDeformation;
//$$ import net.minecraft.world.entity.EquipmentSlot;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import traben.entity_model_features.EMF;
//$$ import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
//$$ import traben.entity_model_features.models.parts.EMFModelPartRoot;
//$$ import traben.entity_model_features.EMFManager;
//$$ import traben.entity_model_features.utils.EMFEntity;
//$$ import traben.entity_model_features.utils.EMFUtils;
//$$
//#if MC >= 12002
//$$ import net.minecraft.tags.ItemTags;
//#endif
//$$ @Mixin(value = net.minecraft.client.model.PlayerModel.class, priority = 2000)//higher priority to allow other mods to cancel
//#else
@Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//#endif
public abstract class MixinCapeModels {
    // unneeded now
//#if MC < 12102
//$$   @Unique
//$$     private ModelPart emf$capeModelPart = null;
//$$
//$$     @Inject(method = "<init>", at = @At("TAIL"))
//$$     private void setEmf$Model(CallbackInfo ci) {
//$$         if (EMF.testForForgeLoadingError()) return;
//$$
//$$         var layer = new ModelLayerLocation(EMFUtils.res("minecraft", "player"), "cape");
//$$         ModelPart capeModel = EMFManager.getInstance().injectIntoModelRootGetter(layer, PlayerModel.createMesh(CubeDeformation.NONE,false).getRoot().bake(64,64));
//$$
//$$         //separate cape model, if it has a custom jem model
//$$         if (capeModel instanceof EMFModelPartRoot && capeModel.hasChild("cloak")) {
//$$             emf$capeModelPart = capeModel.getChild("cloak");
//$$         }
//$$     }
//$$
//$$     @Inject(method = "renderCloak",
//$$             at = @At("HEAD"),
//$$             cancellable = true)
//$$     private void emf$RenderCustomModelOnly(final PoseStack poseStack, final VertexConsumer vertexConsumer, final int i, final int j, final CallbackInfo ci) {
//$$         if (emf$capeModelPart != null) {
//$$
//$$             //reset to last pose
//$$             poseStack.popPose();
//$$             poseStack.pushPose();
//$$
//$$             EMFEntity emfEntity = EMFAnimationEntityContext.getEMFEntity();
//$$             if (!(emfEntity instanceof Player)) return;
//$$             Player player = (Player) emfEntity;
//$$
//$$             //if chestplate move cape back
//$$             if (
                 //#if MC >= 12006
                 //$$ player.getItemBySlot(EquipmentSlot.CHEST).is(ItemTags.CHEST_ARMOR)
                 //#else
                 //$$ !player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
                 //#endif
//$$             ) {
//$$                 poseStack.translate(0.0f, -0.0625f, 0.1875f);
//$$             }else{
//$$                 poseStack.translate(0.0f, 0.0f, 0.125f);
//$$             }
//$$             //flip cape
//$$             poseStack.mulPose(Axis.YP.rotationDegrees(180));
//$$
//$$             emf$capeModelPart.render(poseStack, vertexConsumer, i, j);
//$$
//$$             ci.cancel();
//$$         }
//$$     }
//#endif

}
