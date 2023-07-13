package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.StrayOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.config.EMFConfig;

@Mixin(StrayOverlayFeatureRenderer.class)
public class MixinStrayOverlayFeatureRenderer<T extends MobEntity & RangedAttackMob> {


    @Mutable
    @Shadow @Final private SkeletonEntityModel<T> model;
    private SkeletonEntityModel<T> heldModelToForce = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(FeatureRendererContext context, EntityModelLoader loader, CallbackInfo ci) {
        if(EMFConfig.getConfig().tryForceEmfModels){
            heldModelToForce = model;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/MobEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void emf$resetModel(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T mobEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if(heldModelToForce != null && EMFConfig.getConfig().tryForceEmfModels){
            model = heldModelToForce;
            heldModelToForce = null;
        }
   }
}
