package traben.entity_model_features.mixin.mixins.rendering.attachments;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAttachment;

@Mixin(ParrotOnShoulderLayer.class)
public abstract class Mixin_Parrot_ShoulderOffset extends RenderLayer {

    public Mixin_Parrot_ShoulderOffset() {
        super(null);
    }

    //#if MC >= 12109
    private static final String RENDER_METHOD = "submitOnShoulder";
    //#elseif MC > 1.21.2
    //$$ private static final String RENDER_METHOD = "renderOnShoulder";
    //#else
    //$$ private static final String RENDER_METHOD = "method_17958";
    //#endif


    @WrapWithCondition(method = RENDER_METHOD, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 0))
    private boolean offsetBlock(PoseStack instance, float f, float g, float h, @Local(argsOnly = true) boolean isLeft) {
        var model = (IEMFModel) this.getParentModel();
        if (model.emf$isEMFModel()) {
            var root = model.emf$getEMFRootModel();
            var positioner = root.getPositionerForAttachment(
                isLeft ? EMFAttachment.Type.PARROT_LEFT : EMFAttachment.Type.PARROT_RIGHT
            );
            if (positioner != null) {
                positioner.accept(instance);
                return false;
            }
            if (EMF.config().getConfig().parrotShoulderPositionAnimatesByDefault) {
                // Similar deal except we want to discard rotations and scale, so we instead will pass a point through
                // the matrix and then use that to translate the parrot to the correct position.
                positioner = root.getPositionerForAttachment(
                        isLeft ? EMFAttachment.Type.PARROT_LEFT_AUTO : EMFAttachment.Type.PARROT_RIGHT_AUTO
                );
                if (positioner != null) {
                    var tempMatrix = new PoseStack();
                    positioner.accept(tempMatrix);
                    var pos = new Vector4f(isLeft ? 0.4f : -0.4f, -1.5f, 0, 1.0f);
                    tempMatrix.last().pose().transform(pos);
                    instance.translate(
                            pos.x(),
                            -1.5f + pos.y(),
                            pos.z()
                    );

                    return false;
                }
            }
        }
        return true;
    }
}