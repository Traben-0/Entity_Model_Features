package traben.entity_model_features.mixin.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(TropicalFishPatternLayer.class)
public class MixinTropicalFishPatternFeatureRenderer {


    @Mutable
    @Shadow
    @Final
    private TropicalFishModelA<TropicalFish> modelA;
    @Mutable
    @Shadow
    @Final
    private TropicalFishModelB<TropicalFish> modelB;
    @Unique
    private TropicalFishModelA<TropicalFish> emf$heldModelToForce = null;
    @Unique
    private TropicalFishModelB<TropicalFish> emf$heldModelToForce2 = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(RenderLayerParent<?, ?> context, EntityModelSet loader, CallbackInfo ci) {
        if (((this.modelA != null && ((IEMFModel) modelA).emf$isEMFModel())
                || (this.modelB != null && ((IEMFModel) modelB).emf$isEMFModel()))) {
            emf$heldModelToForce = modelA;
            emf$heldModelToForce2 = modelB;
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/TropicalFish;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void emf$resetModel(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, TropicalFish tropicalFishEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (emf$heldModelToForce != null) {
            if (!emf$heldModelToForce.equals(modelA)) {
                boolean replace = EMF.config().getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getKey(tropicalFishEntity.getType()).getNamespace());
                EMFUtils.overrideMessage(emf$heldModelToForce.getClass().getName(), modelA == null ? "null" : modelA.getClass().getName(), replace);
                if (replace) {
                    modelA = emf$heldModelToForce;
                }
            }
            emf$heldModelToForce = null;
        }
        if (emf$heldModelToForce2 != null) {
            if (!emf$heldModelToForce2.equals(modelB)) {
                boolean replace = EMF.config().getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getKey(tropicalFishEntity.getType()).getNamespace());
                EMFUtils.overrideMessage(emf$heldModelToForce2.getClass().getName(), modelB == null ? "null" : modelB.getClass().getName(), replace);
                if (replace) {
                    modelB = emf$heldModelToForce2;
                }
            }
            emf$heldModelToForce2 = null;
        }
    }
}
