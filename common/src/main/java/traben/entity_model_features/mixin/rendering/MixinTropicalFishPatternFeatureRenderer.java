package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TropicalFishEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(TropicalFishColorFeatureRenderer.class)
public class MixinTropicalFishPatternFeatureRenderer {


    @Mutable
    @Shadow
    @Final
    private SmallTropicalFishEntityModel<TropicalFishEntity> smallModel;
    @Mutable
    @Shadow
    @Final
    private LargeTropicalFishEntityModel<TropicalFishEntity> largeModel;
    @Unique
    private SmallTropicalFishEntityModel<TropicalFishEntity> emf$heldModelToForce = null;
    @Unique
    private LargeTropicalFishEntityModel<TropicalFishEntity> emf$heldModelToForce2 = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(FeatureRendererContext<?, ?> context, EntityModelLoader loader, CallbackInfo ci) {
        if (((this.smallModel != null && ((IEMFModel) smallModel).emf$isEMFModel())
                || (this.largeModel != null && ((IEMFModel) largeModel).emf$isEMFModel()))) {
            emf$heldModelToForce = smallModel;
            emf$heldModelToForce2 = largeModel;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/TropicalFishEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void emf$resetModel(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, TropicalFishEntity tropicalFishEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (emf$heldModelToForce != null) {
            if (!emf$heldModelToForce.equals(smallModel)) {
                boolean replace = EMF.config().getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getId(tropicalFishEntity.getType()).getNamespace());
                EMFUtils.overrideMessage(emf$heldModelToForce.getClass().getName(), smallModel == null ? "null" : smallModel.getClass().getName(), replace);
                if (replace) {
                    smallModel = emf$heldModelToForce;
                }
            }
            emf$heldModelToForce = null;
        }
        if (emf$heldModelToForce2 != null) {
            if (!emf$heldModelToForce2.equals(largeModel)) {
                boolean replace = EMF.config().getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getId(tropicalFishEntity.getType()).getNamespace());
                EMFUtils.overrideMessage(emf$heldModelToForce2.getClass().getName(), largeModel == null ? "null" : largeModel.getClass().getName(), replace);
                if (replace) {
                    largeModel = emf$heldModelToForce2;
                }
            }
            emf$heldModelToForce2 = null;
        }
    }
}
