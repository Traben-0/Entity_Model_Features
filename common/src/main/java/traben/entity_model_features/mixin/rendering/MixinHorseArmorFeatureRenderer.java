package traben.entity_model_features.mixin.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(HorseArmorLayer.class)
public class MixinHorseArmorFeatureRenderer {


    @Mutable
    @Shadow
    @Final
    private HorseModel<Horse> model;
    @Unique
    private HorseModel<Horse> emf$heldModelToForce = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$saveEMFModel(RenderLayerParent<?, ?> context, EntityModelSet loader, CallbackInfo ci) {
        if (this.model != null && ((IEMFModel) model).emf$isEMFModel()) {
            emf$heldModelToForce = model;
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/horse/Horse;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HorseModel;setupAnim(Lnet/minecraft/world/entity/animal/horse/AbstractHorse;FFFFF)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$setAngles(final PoseStack matrixStack, final MultiBufferSource vertexConsumerProvider, final int i, final Horse horseEntity, final float f, final float g, final float h, final float j, final float k, final float l, final CallbackInfo ci, final ItemStack itemStack, final AnimalArmorItem animalArmorItem) {
        if (emf$heldModelToForce != null) {
            if (!emf$heldModelToForce.equals(model)) {
                boolean replace = EMF.config().getConfig().attemptRevertingEntityModelsAlteredByAnotherMod && "minecraft".equals(EntityType.getKey(horseEntity.getType()).getNamespace());
                EMFUtils.overrideMessage(emf$heldModelToForce.getClass().getName(), model == null ? "null" : model.getClass().getName(), replace);
                if (replace) {
                    model = emf$heldModelToForce;
                }
            }
            emf$heldModelToForce = null;
        }
        //EMFManager.getInstance().preRenderEMFActions("horse_armor", horseEntity, vertexConsumerProvider, f, g, j, k, l);
    }
}
