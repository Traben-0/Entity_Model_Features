package traben.entity_model_features.mixin.rewrite;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.utils.EMFManager;

@Mixin(BipedEntityModel.class)
public class Mixin_2_ZombieEntityModel<T extends LivingEntity> {

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void emf$setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if(entity instanceof ZombieEntity)
            EMFManager.getInstance().setAnglesOnParts(entity,limbAngle,limbDistance,animationProgress,headYaw,headPitch);
    }
}
