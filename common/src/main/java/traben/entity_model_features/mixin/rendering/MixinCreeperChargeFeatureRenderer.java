package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreeperChargeFeatureRenderer.class)
public class MixinCreeperChargeFeatureRenderer<T extends Entity> {

//todo doesnt work
//
//    @Mutable
//    @Shadow @Final private CreeperEntityModel<CreeperEntity> model;
//    private CreeperEntityModel<CreeperEntity> heldModelToForce = null;
//
//    @Inject(method = "<init>",
//            at = @At(value = "TAIL"))
//    private void emf$saveEMFModel(FeatureRendererContext context, EntityModelLoader loader, CallbackInfo ci) {
//        if(EMFConfig.getConfig().tryForceEmfModels){
//            heldModelToForce = model;
//        }
//    }
//
//    @Inject(method = "getEnergySwirlModel",
//            at = @At(value = "RETURN"), cancellable = true)
//    private void emf$setAngles(CallbackInfoReturnable<EntityModel<CreeperEntity>> cir) {
//        if(heldModelToForce != null && EMFConfig.getConfig().tryForceEmfModels){
//            //((LivingEntityRendererAccessor)this).setModel(heldModelToForce);
////            model = heldModelToForce;
////            heldModelToForce = null;
//            cir.setReturnValue(heldModelToForce);
//        }
//   }
}
