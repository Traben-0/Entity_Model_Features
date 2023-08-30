package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.IEMFModel;

@Mixin(TropicalFishColorFeatureRenderer.class)
public class MixinTropicalFishPatternFeatureRenderer {


    @Mutable
    @Shadow @Final private SmallTropicalFishEntityModel<TropicalFishEntity> smallModel;
    @Mutable
    @Shadow @Final private LargeTropicalFishEntityModel<TropicalFishEntity> largeModel;
    @Unique
    private SmallTropicalFishEntityModel<TropicalFishEntity> emf$heldModelToForce = null;
    @Unique
    private LargeTropicalFishEntityModel<TropicalFishEntity> emf$heldModelToForce2 = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(FeatureRendererContext<?,?> context, EntityModelLoader loader, CallbackInfo ci) {
        if(EMFConfig.getConfig().tryForceEmfModels
                && (
                ((IEMFModel)smallModel).emf$isEMFModel()
                || ((IEMFModel)largeModel).emf$isEMFModel()
        )){
            emf$heldModelToForce = smallModel;
            emf$heldModelToForce2 = largeModel;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/TropicalFishEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void emf$resetModel(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, TropicalFishEntity tropicalFishEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if(emf$heldModelToForce != null && EMFConfig.getConfig().tryForceEmfModels){
            smallModel = emf$heldModelToForce;
            largeModel = emf$heldModelToForce2;
            emf$heldModelToForce = null;
            emf$heldModelToForce2 = null;
        }
   }
}
