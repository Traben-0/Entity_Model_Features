package traben.entity_model_features.mixin.mixins.rendering.feature;
//#if MC >= 12102
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMFManager;

@Mixin(HumanoidArmorLayer.class)
public class MixinHumanoidArmorLayer<S extends HumanoidRenderState, A extends HumanoidModel<S>> {

    @Unique A emf$helmetModel = null;
    @Unique A emf$chestplateModel = null;
    @Unique A emf$leggingsModel = null;
    @Unique A emf$bootsModel = null;

    @Unique A emf$helmetModel_baby = null;
    @Unique A emf$chestplateModel_baby = null;
    @Unique A emf$leggingsModel_baby = null;
    @Unique A emf$bootsModel_baby = null;

    @Unique boolean emf$hasCustom = false;

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/RenderLayerParent;Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;)V",
            at = @At(value = "TAIL"))
    private void emf$partialArmorSetup(CallbackInfo ci) {
        try {
            var armorParts = EMFManager.getInstance().getArmorParts();
            if (armorParts != null && armorParts.hasCustom()){
                if (armorParts.helmetModel != null) {
                    emf$helmetModel = (A) new HumanoidArmorModel<S>(armorParts.helmetModel);
                }
                if (armorParts.chestplateModel != null) {
                    emf$chestplateModel = (A) new HumanoidArmorModel<S>(armorParts.chestplateModel);
                }
                if (armorParts.leggingsModel != null) {
                    emf$leggingsModel = (A) new HumanoidArmorModel<S>(armorParts.leggingsModel);
                }
                if (armorParts.bootsModel != null) {
                    emf$bootsModel = (A) new HumanoidArmorModel<S>(armorParts.bootsModel);
                }

                if (armorParts.helmetModel_baby != null) {
                    emf$helmetModel_baby = (A) new HumanoidArmorModel<S>(armorParts.helmetModel_baby);
                }
                if (armorParts.chestplateModel_baby != null) {
                    emf$chestplateModel_baby = (A) new HumanoidArmorModel<S>(armorParts.chestplateModel_baby);
                }
                if (armorParts.leggingsModel_baby != null) {
                    emf$leggingsModel_baby = (A) new HumanoidArmorModel<S>(armorParts.leggingsModel_baby);
                }
                if (armorParts.bootsModel_baby != null) {
                    emf$bootsModel_baby = (A) new HumanoidArmorModel<S>(armorParts.bootsModel_baby);
                }

                emf$hasCustom = true;
            }
        }catch (Exception e) {
            EMFManager.getInstance().receiveException(e);
        }
    }

    @Inject(method = "getArmorModel", at = @At(value = "RETURN"), cancellable = true)
    private void emf$modifyGetArmorModelForPartial(final S humanoidRenderState, final EquipmentSlot equipmentSlot, final CallbackInfoReturnable<A> cir) {
        if(emf$hasCustom){
            A returning = switch (equipmentSlot) {
                case HEAD -> humanoidRenderState.isBaby ? emf$helmetModel_baby : emf$helmetModel;
                case CHEST -> humanoidRenderState.isBaby ? emf$chestplateModel_baby : emf$chestplateModel;
                case LEGS -> humanoidRenderState.isBaby ? emf$leggingsModel_baby : emf$leggingsModel;
                case FEET -> humanoidRenderState.isBaby ? emf$bootsModel_baby : emf$bootsModel;
                default -> null;
            };
            if (returning != null) {
                cir.getReturnValue().copyPropertiesTo(returning);
                cir.setReturnValue(returning);
            }
        }
    }
}

//#else
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public class MixinHumanoidArmorLayer {}
//#endif