package traben.entity_model_features.mixin.rewrite;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CatEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.config.EMFConfig;

@Mixin(CatCollarFeatureRenderer.class)
public class MixinCatCollarFeatureRenderer {


    @Mutable
    @Shadow @Final private CatEntityModel<CatEntity> model;
    private CatEntityModel<CatEntity> heldModelToForce = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(FeatureRendererContext context, EntityModelLoader loader, CallbackInfo ci) {
        if(EMFConfig.getConfig().tryForceEmfModels){
            heldModelToForce = model;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/CatEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void emf$resetModel(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CatEntity catEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if(heldModelToForce != null && EMFConfig.getConfig().tryForceEmfModels){
            model = heldModelToForce;
            heldModelToForce = null;
        }
   }
}
