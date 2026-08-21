package traben.entity_model_features.mixin.mixins.rendering.submits;

//#if MC < 1.21.9
//$$ import net.minecraft.client.renderer.entity.EntityRenderer;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Mixin(CancelTarget.class)
//$$ public abstract class MixinFlameFeatureRenderer { }
//#else
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;

@Mixin(FlameFeatureRenderer.class)
public abstract class MixinFlameFeatureRenderer {

    //#if MC >= 26.2
    //$$ private static final String METHOD = "prepare";
    //#else
    private static final String METHOD = "renderFlame";
    //#endif


    @ModifyExpressionValue(method = METHOD, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/EntityRenderState;boundingBoxWidth:F", opcode = Opcodes.GETFIELD))
    private float width(float original, @Local EntityRenderState vanilla, @Local PoseStack.Pose pose) {
        var state = EMFEntityRenderState.from(vanilla);
        if (state == null) return original;

        if (state.needsToModifyFire()) {
            pose.translate(
                    Float.isNaN(state.fireX()) ? 0 : state.fireX(),
                    Float.isNaN(state.fireY()) ? 0 : state.fireY(),
                    Float.isNaN(state.fireZ()) ? 0 : state.fireZ()
            );

            if (!Float.isNaN(state.fireScale())) {
                return state.fireScale();
            }
        }


        return original;
    }

    @ModifyExpressionValue(method = METHOD, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/EntityRenderState;boundingBoxHeight:F", opcode = Opcodes.GETFIELD))
    private float height(float original, @Local EntityRenderState vanilla) {
        var state = EMFEntityRenderState.from(vanilla);
        if (state != null && !Float.isNaN(state.fireHeight())) {
            return state.fireHeight();
        }
        return original;
    }



}
//#endif