package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin(VillagerModel.class)
public abstract class MixinVillagerModel {

    @Inject(method = "setupAnim", at = @At(value = "HEAD"))
    private void emf$assertLayerFactory(final CallbackInfo ci) {
        EMFAnimationEntityContext.setLayerFactory(RenderType::entityCutoutNoCull);
    }



}
