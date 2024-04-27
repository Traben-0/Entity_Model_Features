package traben.entity_model_features.mixin;


import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFEntity;

@Mixin(Entity.class)
public abstract class MixinEntity implements EMFEntity {

    @Unique
    private final Object2FloatOpenHashMap<String> emf$variableMap = new Object2FloatOpenHashMap<>() {{
        defaultReturnValue(0);
    }};
    @Shadow
    public double prevX;
    @Shadow
    public double prevY;
    @Shadow
    public double prevZ;
    @Shadow
    public float prevPitch;
    @Shadow
    public int age;

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract boolean isTouchingWater();

    @Shadow
    public abstract boolean isOnFire();

    @Shadow
    public abstract boolean hasVehicle();

    @Shadow
    public abstract boolean isOnGround();

    @Shadow
    public abstract boolean isAlive();

    @Shadow
    public abstract boolean isGlowing();

    @Shadow
    public abstract boolean isInLava();

    @Shadow
    public abstract boolean isInvisible();

    @Shadow
    public abstract boolean hasPassengers();


    @Shadow
    public abstract boolean isSprinting();

    @Shadow
    public abstract boolean isWet();

    @Shadow
    public abstract Vec3d getVelocity();

    @Shadow
    public abstract float getYaw();

    @Shadow
    public abstract float getPitch();

    @Shadow
    public abstract EntityType<?> getType();


    @Shadow public abstract boolean isInSneakingPose();

    @Inject(method = "Lnet/minecraft/entity/Entity;getLeashOffset()Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"))
    private void injected(CallbackInfoReturnable<Vec3d> cir) {
        //return new Vec3d(0.0, (double)this.getStandingEyeHeight(), (double)(this.getWidth() * 0.4F));
        if (EMFAnimationEntityContext.getLeashX() != 0 || EMFAnimationEntityContext.getLeashY() != 0 || EMFAnimationEntityContext.getLeashZ() != 0) {
            Vec3d vec = cir.getReturnValue();
            vec.add(EMFAnimationEntityContext.getLeashX(), EMFAnimationEntityContext.getLeashY(), EMFAnimationEntityContext.getLeashZ());
        }
    }

    @Override
    public double emf$prevX() {
        return prevX;
    }

    @Override
    public double emf$getX() {
        return getX();
    }

    @Override
    public double emf$prevY() {
        return prevY;
    }

    @Override
    public double emf$getY() {
        return getY();
    }

    @Override
    public double emf$prevZ() {
        return prevZ;
    }

    @Override
    public double emf$getZ() {
        return getZ();
    }

    @Override
    public float emf$prevPitch() {
        return prevPitch;
    }

    @Override
    public float emf$getPitch() {
        return getPitch();
    }

    @Override
    public boolean emf$isTouchingWater() {
        return isTouchingWater();
    }

    @Override
    public boolean emf$isOnFire() {
        return isOnFire();
    }

    @Override
    public boolean emf$hasVehicle() {
        return hasVehicle();
    }

    @Override
    public boolean emf$isOnGround() {
        return isOnGround();
    }

    @Override
    public boolean emf$isAlive() {
        return isAlive();
    }

    @Override
    public boolean emf$isGlowing() {
        return isGlowing();
    }

    @Override
    public boolean emf$isInLava() {
        return isInLava();
    }

    @Override
    public boolean emf$isInvisible() {
        return isInvisible();
    }

    @Override
    public boolean emf$hasPassengers() {
        return hasPassengers();
    }

    @Override
    public boolean emf$isSneaking() {
        return isInSneakingPose();
    }

    @Override
    public boolean emf$isSprinting() {
        return isSprinting();
    }

    @Override
    public boolean emf$isWet() {
        return isWet();
    }

    @Override
    public float emf$age() {
        return age;
    }

    @Override
    public float emf$getYaw() {
        return getYaw();
    }

    @Override
    public Vec3d emf$getVelocity() {
        return getVelocity();
    }

    @Override
    public String emf$getTypeString() {
        return getType().toString();
    }

    @Override
    public Object2FloatOpenHashMap<String> emf$getVariableMap() {
        return emf$variableMap;
    }
}
