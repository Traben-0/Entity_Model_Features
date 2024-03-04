package traben.entity_model_features.mixin;


import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin(ParrotEntityModel.class)
public class MixinParrotEntityModel {

    @Inject(method = "poseOnShoulder", at = @At("HEAD"))
    private void injected(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float limbAngle, float limbDistance, float headYaw, float headPitch, int danceAngle, CallbackInfo ci) {
        EMFAnimationEntityContext.setCurrentEntityOnShoulder();
    }
}
