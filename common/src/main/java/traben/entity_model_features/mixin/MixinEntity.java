package traben.entity_model_features.mixin;


import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.models.animation.EMFAnimationHelper;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "Lnet/minecraft/entity/Entity;getLeashOffset()Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"))
    private void injected(CallbackInfoReturnable<Vec3d> cir) {
        //return new Vec3d(0.0, (double)this.getStandingEyeHeight(), (double)(this.getWidth() * 0.4F));
        if (EMFAnimationHelper.getLeashX() != 0 || EMFAnimationHelper.getLeashY() != 0 || EMFAnimationHelper.getLeashZ() != 0) {
            Vec3d vec = cir.getReturnValue();
            vec.add(EMFAnimationHelper.getLeashX(), EMFAnimationHelper.getLeashY(), EMFAnimationHelper.getLeashZ());
        }
    }
}
