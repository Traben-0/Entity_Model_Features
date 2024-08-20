package traben.entity_model_features.mixin.rendering.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.utils.EMFUtils;

#if MC >= MC_20_6
import net.minecraft.client.renderer.entity.layers.SkeletonClothingLayer;

@Mixin(SkeletonClothingLayer.class)
#else
import net.minecraft.client.renderer.entity.layers.StrayClothingLayer;
@Mixin(StrayClothingLayer.class)
#endif
public class MixinStrayOverlayFeatureRenderer<T extends Mob & RangedAttackMob> {


    @Mutable
    @Shadow
    @Final
    private SkeletonModel<T> layerModel;
    @Unique
    private SkeletonModel<T> emf$heldModelToForce = null;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void emf$saveEMFModel(final CallbackInfo ci) {
    if (this.layerModel != null && ((IEMFModel) layerModel).emf$isEMFModel()) {
            emf$heldModelToForce = layerModel;
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Mob;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void emf$resetModel(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T mobEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (emf$heldModelToForce != null) {
            if (!emf$heldModelToForce.equals(layerModel)) {
                boolean replace = EMF.config().getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getKey(mobEntity.getType()).getNamespace());
                EMFUtils.overrideMessage(emf$heldModelToForce.getClass().getName(), layerModel == null ? "null" : layerModel.getClass().getName(), replace);
                if (replace) {
                    layerModel = emf$heldModelToForce;
                }
            }
            emf$heldModelToForce = null;
        }
    }
}
