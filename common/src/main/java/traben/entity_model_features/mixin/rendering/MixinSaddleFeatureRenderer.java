package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Saddleable;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SaddleFeatureRenderer.class)
public class MixinSaddleFeatureRenderer<T extends Entity & Saddleable, M extends EntityModel<T>> {

//    @Mutable
//    @Shadow
//    @Final
//    private M model;
//    @Unique
//    private M emf$heldModelToForce = null;
//
//    @Inject(method = "<init>",
//            at = @At(value = "TAIL"))
//    private void emf$saveEMFModel(FeatureRendererContext<?, ?> context, EntityModel<?> model, Identifier texture, CallbackInfo ci) {
//        if (this.model != null && ((IEMFModel) model).emf$isEMFModel()) {
//            emf$heldModelToForce = this.model;
//        }
//    }

//    @Inject(method = "render", at = @At(value = "HEAD"))
//    private void emf$resetModel(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
//        if (emf$heldModelToForce != null) {
//            if (!emf$heldModelToForce.equals(model)) {
//                boolean replace = EMFConfig.getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getId(entity.getType()).getNamespace());
//                EMFUtils.EMFOverrideMessage(emf$heldModelToForce.getClass().getName(), model == null ? "null" : model.getClass().getName(), replace);
//                if (replace) {
//                    model = emf$heldModelToForce;
//                }
//            }
//            emf$heldModelToForce = null;
//        }
//    }
}
