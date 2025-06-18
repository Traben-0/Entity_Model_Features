package traben.entity_model_features.mixin;


import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFEntity;

import java.util.List;

@Mixin(Entity.class)
public abstract class MixinEntity implements EMFEntity {

    @Unique
    private final Object2FloatOpenHashMap<String> emf$variableMap = new Object2FloatOpenHashMap<>() {{
        defaultReturnValue(0);
    }};




    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();



    @Shadow
    public abstract boolean isOnFire();





    @Shadow
    public abstract boolean isAlive();



    @Shadow
    public abstract boolean isInLava();

    @Shadow
    public abstract boolean isInvisible();


    @Shadow
    public abstract boolean isSprinting();


    @Shadow
    public abstract EntityType<?> getType();

    @Shadow public abstract boolean isCrouching();

    @Shadow public double zo;

    @Shadow public double yo;

    @Shadow public double xo;

    @Shadow public float xRotO;

    @Shadow private float xRot;

    @Shadow #if MC > MC_21 public #else private #endif float yRot;

    @Shadow public int tickCount;

    @Shadow public abstract boolean isInWater();

    @Shadow public abstract boolean isPassenger();

    @Shadow public abstract boolean onGround();

    @Shadow public abstract boolean hasGlowingTag();

    @Shadow public abstract List<Entity> getPassengers();

    #if MC>=MC_21_5
    #else
    @Shadow public abstract boolean isInWaterRainOrBubble();
    #endif

    @Shadow public abstract Vec3 getDeltaMovement();

    @Shadow
    public abstract boolean equals(final Object object);

    #if MC > MC_21
    @Shadow
    public abstract Vec3 oldPosition();
    #endif

    @Shadow
    public abstract Vec3 position();


    @Shadow
    public double xOld;

    @Shadow
    public double yOld;

    @Shadow
    public double zOld;

    @Shadow public abstract boolean isInWaterOrRain();

    #if MC >= MC_21_6
    @Inject(method = "getRopeHoldPosition", at = @At("RETURN"))
    #else
    @Inject(method = "getLeashOffset()Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"))
    #endif
    private void emf$leashwither(CallbackInfoReturnable<Vec3> cir) {
        //return new Vec3d(0.0, (double)this.getStandingEyeHeight(), (double)(this.getWidth() * 0.4F));
        if (EMFAnimationEntityContext.getLeashX() != 0 || EMFAnimationEntityContext.getLeashY() != 0 || EMFAnimationEntityContext.getLeashZ() != 0) {
            Vec3 vec = cir.getReturnValue();
            vec.add(EMFAnimationEntityContext.getLeashX(), EMFAnimationEntityContext.getLeashY(), EMFAnimationEntityContext.getLeashZ());
        }
    }

    @Override
    public double emf$prevX() {
        return xo;
    }

    @Override
    public double emf$getX() {
        return getX();
    }

    @Override
    public double emf$prevY() {
        return yo;
    }

    @Override
    public double emf$getY() {
        return getY();
    }

    @Override
    public double emf$prevZ() {
        return zo;
    }

    @Override
    public double emf$getZ() {
        return getZ();
    }

    @Override
    public float emf$prevPitch() {
        return xRotO;
    }

    @Override
    public float emf$getPitch() {
        return xRot;
    }

    @Override
    public boolean emf$isTouchingWater() {
        return isInWater();
    }

    @Override
    public boolean emf$isOnFire() {
        return isOnFire();
    }

    @Override
    public boolean emf$hasVehicle() {
        return isPassenger();
    }

    @Override
    public boolean emf$isOnGround() {
        return onGround();
    }

    @Override
    public boolean emf$isAlive() {
        return isAlive();
    }

    @Override
    public boolean emf$isGlowing() {
        return hasGlowingTag();
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
        return !getPassengers().isEmpty();
    }

    @Override
    public boolean emf$isSneaking() {
        return isCrouching();
    }

    @Override
    public boolean emf$isSprinting() {
        return isSprinting();
    }

    @Override
    public boolean emf$isWet() {
        #if MC>=MC_21_5
        return isInWaterOrRain();
        #else
        return isInWaterRainOrBubble();
        #endif
    }

    @Override
    public float emf$age() {
        return tickCount;
    }

    @Override
    public float emf$getYaw() {
        return yRot;
    }

    @Override
    public Vec3 emf$getVelocity() {
        //noinspection EqualsBetweenInconvertibleTypes
        if (equals(Minecraft.getInstance().player)) {
            return getDeltaMovement();//ez get
        }
        return position().subtract(
                #if MC > MC_21
                oldPosition()
                #else
                    new Vec3(xOld,yOld,zOld)
                #endif
        );
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
