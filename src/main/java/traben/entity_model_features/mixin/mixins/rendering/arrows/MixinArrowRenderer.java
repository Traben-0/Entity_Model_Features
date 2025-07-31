package traben.entity_model_features.mixin.mixins.rendering.arrows;

import org.spongepowered.asm.mixin.Mixin;

//#if MC < 12102
//$$ import traben.entity_model_features.utils.IEMFCustomModelHolder;
//$$ import net.minecraft.client.renderer.entity.TippableArrowRenderer;
//$$ import traben.entity_model_features.utils.EMFUtils;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import traben.entity_model_features.models.parts.EMFModelPartRoot;
//$$ import net.minecraft.client.model.geom.ModelLayerLocation;
//$$ @Mixin(TippableArrowRenderer.class)
//$$ public abstract class MixinArrowRenderer implements IEMFCustomModelHolder {
//#else
@Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
public abstract class MixinArrowRenderer {
//#endif
//#if MC < 12102
//$$     @Unique
//$$     private EMFModelPartRoot emf$model = null;
//$$
//$$     @Inject(method = "<init>", at = @At(value = "TAIL"))
//$$     private void emf$findModel(CallbackInfo ci) {
//$$         ModelLayerLocation layer = new ModelLayerLocation(EMFUtils.res("minecraft", "arrow"), "main");
//$$         emf$setModel(EMFUtils.getArrowOrNull(layer));
//$$     }
//$$
//$$     @Override
//$$     public EMFModelPartRoot emf$getModel() {
//$$         return emf$model;
//$$     }
//$$
//$$     @Override
//$$     public void emf$setModel(final EMFModelPartRoot model) {
//$$         emf$model = model;
//$$     }
//#endif
}
