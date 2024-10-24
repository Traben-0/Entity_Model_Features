package traben.entity_model_features.mixin;


import net.minecraft.client.model.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;

#if MC < MC_21_2
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

#if MC >= MC_20_2
import net.minecraft.tags.ItemTags;
#endif
#endif

@Mixin(value = PlayerModel.class, priority = 2000)//higher priority to allow other mods to cancel
public abstract class MixinCapeModels {

#if MC < MC_21_2 // unneeded now
  @Unique
    private ModelPart emf$capeModelPart = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setEmf$Model(CallbackInfo ci) {
        if (EMF.testForForgeLoadingError()) return;

        var layer = new ModelLayerLocation(EMFUtils.res("minecraft", "player"), "cape");
        ModelPart capeModel = EMFManager.getInstance().injectIntoModelRootGetter(layer, PlayerModel.createMesh(CubeDeformation.NONE,false).getRoot().bake(64,64));

        //separate cape model, if it has a custom jem model
        if (capeModel instanceof EMFModelPartRoot && capeModel.hasChild("cloak")) {
            emf$capeModelPart = capeModel.getChild("cloak");
        }
    }

    @Inject(method = "renderCloak",
            at = @At("HEAD"),
            cancellable = true)
    private void emf$RenderCustomModelOnly(final PoseStack poseStack, final VertexConsumer vertexConsumer, final int i, final int j, final CallbackInfo ci) {
        if (emf$capeModelPart != null) {

            //reset to last pose
            poseStack.popPose();
            poseStack.pushPose();

            Player player = (Player) EMFAnimationEntityContext.getEMFEntity();
            if (player == null) return;

            //if chestplate move cape back
            if (#if MC >= MC_20_6 player.getItemBySlot(EquipmentSlot.CHEST).is(ItemTags.CHEST_ARMOR)#else !player.getItemBySlot(EquipmentSlot.CHEST).isEmpty() #endif ){
                poseStack.translate(0.0f, -0.0625f, 0.1875f);
            }else{
                poseStack.translate(0.0f, 0.0f, 0.125f);
            }
            //flip cape
            poseStack.mulPose(Axis.YP.rotationDegrees(180));

            emf$capeModelPart.render(poseStack, vertexConsumer, i, j);

            ci.cancel();
        }
    }
#endif



}
