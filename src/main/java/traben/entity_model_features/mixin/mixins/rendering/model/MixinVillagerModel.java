package traben.entity_model_features.mixin.mixins.rendering.model;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin(VillagerModel.class)
public abstract class MixinVillagerModel {

    @Inject(method =
        //#if MC >=12102
            "setupAnim(Lnet/minecraft/client/renderer/entity/state/VillagerRenderState;)V"
        //#else
        //$$     "setupAnim"
        //#endif
            , at = @At(value = "HEAD"))
    private void emf$assertLayerFactory(final CallbackInfo ci) {
        EMFAnimationEntityContext.setLayerFactory(
                //#if MC>= 12111
                //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                //#else
                RenderType
                //#endif
                        ::entityCutoutNoCull);
    }



}
