package traben.entity_model_features.mixin.mixins;

//#if MC < 1.21.9
//$$ import net.minecraft.client.renderer.entity.EntityRenderer;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Mixin(CancelTarget.class)
//$$ public abstract class MixinEntityRenderer { }
//#else
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import traben.entity_model_features.models.animation.state.EMFState;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @ModifyExpressionValue(method = "extractShadow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;getShadowRadius(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)F"))
    private float radius(float original) {
        var state = EMFState.state();
        if (state != null && !Float.isNaN(state.shadowSize())) {
            return state.shadowSize();
        }
        return original;
    }

    @ModifyExpressionValue(method = "extractShadow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;getShadowStrength(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)F"))
    private float strength(float original) {
        var state = EMFState.state();
        if (state != null && !Float.isNaN(state.shadowOpacity())) {
            return state.shadowOpacity();
        }
        return original;
    }

}
//#endif