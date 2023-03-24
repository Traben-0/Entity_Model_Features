package traben.entity_model_features.mixin.rewrite;

import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.utils.EMFManager;

@Mixin(SheepWoolEntityModel.class)
public class Mixin_2_SheepWoolEntityModel<T extends SheepEntity> {

    @Inject(method = "setAngles(Lnet/minecraft/entity/passive/SheepEntity;FFFFF)V", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        EMFManager.getInstance().setAnglesOnParts(entity,limbAngle,limbDistance,animationProgress,headYaw,headPitch);
    }
}
