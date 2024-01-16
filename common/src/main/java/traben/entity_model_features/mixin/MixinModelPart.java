package traben.entity_model_features.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.IEMFModelNameContainer;
import traben.entity_model_features.models.animation.EMFAnimationHelper;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.OptifineMobNameForFileAndEMFMapId;

import java.util.Map;

@Mixin(ModelPart.class)
public class MixinModelPart implements IEMFModelNameContainer {
    @Shadow public Map<String, ModelPart> children;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
            at = @At(value = "HEAD"))
    private void emf$injectAnnouncer(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (EMFAnimationHelper.doAnnounceModels() && emf$modelInfo != null) {
            EMFManager.getInstance().modelsAnnounced.add(emf$modelInfo);
        }
    }
    @Unique
    OptifineMobNameForFileAndEMFMapId emf$modelInfo = null;

    @Override
    public OptifineMobNameForFileAndEMFMapId emf$getKnownMappings() {
        return emf$modelInfo;
    }

    @Override
    public void emf$insertKnownMappings(OptifineMobNameForFileAndEMFMapId newName) {
        emf$modelInfo = newName;
        children.values().forEach(
                (part)->((IEMFModelNameContainer)part).emf$insertKnownMappings(newName));
    }
}