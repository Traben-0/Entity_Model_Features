package traben.entity_model_features.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.IEMFModelNameContainer;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.IEMFTextureSizeSupplier;
import traben.entity_model_features.models.EMFModel_ID;

import java.util.Map;

@Mixin(ModelPart.class)
public class MixinModelPart implements IEMFModelNameContainer, IEMFTextureSizeSupplier {
    @Shadow
    public Map<String, ModelPart> children;
    @Unique
    EMFModel_ID emf$modelInfo = null;
    @Unique
    private int[] emf$textureSize = null;

    @Inject(method =
            //#if MC >= 12100
                "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
            //#else
            //$$     "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
            //#endif
            at = @At(value = "HEAD"))
    private void emf$injectAnnouncerPart(final PoseStack poseStack, final VertexConsumer vertexConsumer, final int i, final int j,
                                         //#if MC >= 12100
                                         final int k
                                         //#else
                                         //$$ float red, float green, float blue, float alpha
                                         //#endif
            , final CallbackInfo ci) {
        if (EMFAnimationEntityContext.doAnnounceModels() && emf$modelInfo != null) {
            EMFManager.getInstance().modelsAnnounced.add(emf$modelInfo);
        }
    }


    @Override
    public void emf$insertKnownMappings(EMFModel_ID newName) {
        emf$modelInfo = newName;
        children.values().forEach(
                (part) -> ((IEMFModelNameContainer) part).emf$insertKnownMappings(newName));
    }

    @Override
    public int[] emf$getTextureSize() {
        return emf$textureSize;
    }

    @Override
    public void emf$setTextureSize(final int[] size) {
        emf$textureSize = size;
    }
}