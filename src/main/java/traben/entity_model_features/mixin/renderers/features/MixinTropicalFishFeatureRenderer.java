package traben.entity_model_features.mixin.renderers.features;


import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TintableCompositeModel;
import net.minecraft.entity.passive.TropicalFishEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericEntityEntityModel;


@Mixin(TropicalFishColorFeatureRenderer.class)
public abstract class MixinTropicalFishFeatureRenderer extends FeatureRenderer<TropicalFishEntity, TintableCompositeModel<TropicalFishEntity>> {

    public MixinTropicalFishFeatureRenderer(FeatureRendererContext<TropicalFishEntity, TintableCompositeModel<TropicalFishEntity>> context) {
        super(context);
    }

    @ModifyVariable(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/TropicalFishEntity;FFFFFF)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/DyeColor;getColorComponents()[F"),
            index = 12
    )
    private EntityModel<TropicalFishEntity> injectedRaplaceModels(EntityModel<TropicalFishEntity> value) {
        EntityModel<?> context = getContextModel();
        if (context instanceof EMFCustomEntityModel<?> em) {
            EMFGenericEntityEntityModel<?> emf = em.getThisEMFModel();
            String typeName = "tropical_fish_pattern_" + (emf.modelPathIdentifier.equals("tropical_fish_a") ? 'a' : 'b');
            EMFData emfData = EMFData.getInstance();
            int typeHash = typeName.hashCode();
            if (!emfData.JEMPATH_CustomModel.containsKey(typeHash)) {
                emfData.createEMFModelOnly(typeName, value);
            }
            if (emfData.JEMPATH_CustomModel.containsKey(typeHash)) {
                if (emfData.JEMPATH_CustomModel.get(typeHash) != null) {
                    return (EntityModel<TropicalFishEntity>) emfData.JEMPATH_CustomModel.get(typeHash);
                }
            }
        }
        return value;
    }

}
