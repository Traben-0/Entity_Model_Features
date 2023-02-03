package traben.entity_model_features.mixin.renderers.features;


import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.EMFCustomModel;


@Mixin(CreeperChargeFeatureRenderer.class)
public abstract class MixinCreeperChargeFeatureEntityRenderer extends EnergySwirlOverlayFeatureRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>> {


    @Shadow @Final private CreeperEntityModel<CreeperEntity> model;

    public MixinCreeperChargeFeatureEntityRenderer(FeatureRendererContext<CreeperEntity, CreeperEntityModel<CreeperEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Inject(method = "getEnergySwirlModel", at = @At("RETURN"), cancellable = true)
    private void injected(CallbackInfoReturnable<EntityModel<CreeperEntity>> cir) {
        if(emf$originalModel == null) {
            emf$originalModel = this.model;
            if (super.getContextModel() instanceof EMFCustomModel) {
                String entityTypeName = "creeper_charge";
                EMFCustomModel<?> emfModel =  EMFData.getInstance().getModelVariant(null,entityTypeName, this.model);
                if(emfModel != null){
                    emf$emfModel = (EntityModel<CreeperEntity>) emfModel;

                }
            }
        }
        if(emf$emfModel != null){
            cir.setReturnValue(emf$emfModel);
        }

    }



    private  EntityModel<CreeperEntity> emf$originalModel;
    private  EntityModel<CreeperEntity> emf$emfModel;


}
