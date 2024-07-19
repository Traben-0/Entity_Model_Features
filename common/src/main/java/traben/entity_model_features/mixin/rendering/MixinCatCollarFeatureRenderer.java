package traben.entity_model_features.mixin.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(CatCollarLayer.class)
public class MixinCatCollarFeatureRenderer {


    @Mutable
    @Shadow
    @Final
    private CatModel<Cat> catModel;
    @Unique
    private CatModel<Cat> emf$heldModelToForce = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(RenderLayerParent<?, ?> context, EntityModelSet loader, CallbackInfo ci) {
        if (this.catModel != null && ((IEMFModel) catModel).emf$isEMFModel()) {
            emf$heldModelToForce = catModel;
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/Cat;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void emf$resetModel(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, Cat catEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (emf$heldModelToForce != null) {
            if (!emf$heldModelToForce.equals(catModel)) {
                boolean replace = EMF.config().getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getKey(catEntity.getType()).getNamespace());
                EMFUtils.overrideMessage(emf$heldModelToForce.getClass().getName(), catModel == null ? "null" : catModel.getClass().getName(), replace);
                if (replace) {
                    catModel = emf$heldModelToForce;
                }
            }
            emf$heldModelToForce = null;
        }
    }
}
