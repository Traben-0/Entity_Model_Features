package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.entity.feature.StrayOverlayFeatureRenderer;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StrayOverlayFeatureRenderer.class)
public class MixinStrayOverlayFeatureRenderer<T extends MobEntity & RangedAttackMob> {


//    @Mutable
//    @Shadow
//    @Final
//    private SkeletonEntityModel<T> model;
//    @Unique
//    private SkeletonEntityModel<T> emf$heldModelToForce = null;
//
//    @Inject(method = "<init>",
//            at = @At(value = "TAIL"))
//    private void emf$saveEMFModel(FeatureRendererContext<?, ?> context, EntityModelLoader loader, CallbackInfo ci) {
//        if (this.model != null && ((IEMFModel) model).emf$isEMFModel()) {
//            emf$heldModelToForce = model;
//        }
//    }

//    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/MobEntity;FFFFFF)V",
//            at = @At(value = "HEAD"))
//    private void emf$resetModel(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T mobEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
//        if (emf$heldModelToForce != null) {
//            if (!emf$heldModelToForce.equals(model)) {
//                boolean replace = EMFConfig.getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getId(mobEntity.getType()).getNamespace());
//                EMFUtils.EMFOverrideMessage(emf$heldModelToForce.getClass().getName(), model == null ? "null" : model.getClass().getName(), replace);
//                if (replace) {
//                    model = emf$heldModelToForce;
//                }
//            }
//            emf$heldModelToForce = null;
//        }
//    }
}
