package traben.entity_model_features.mixin.rewrite;

import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.passive.IronGolemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.utils.EMFManager;

@Mixin(IronGolemEntityModel.class)
public class Mixin_2_IronGolemEntityModel<T extends IronGolemEntity> {

    @Inject(method = "setAngles(Lnet/minecraft/entity/passive/IronGolemEntity;FFFFF)V", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        EMFManager.getInstance().setAnglesOnParts(entity,limbAngle,limbDistance,animationProgress,headYaw,headPitch);
    }
}
