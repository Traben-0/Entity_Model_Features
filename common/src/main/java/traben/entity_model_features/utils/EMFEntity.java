package traben.entity_model_features.utils;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public interface EMFEntity {

    Entity entity();


    BlockEntity getBlockEntity();

    World getWorld();

    UUID getUuid();

    double prevX();

    double getX();

    double prevY();

    double getY();

    double prevZ();

    double getZ();

    float prevPitch();

    float getPitch();

    boolean isTouchingWater();

    boolean isOnFire();

    boolean hasVehicle();

    boolean isOnGround();

    boolean isAlive();

    LivingEntity getLiving();

    boolean isGlowing();

    boolean isInLava();

    boolean isInvisible();

    boolean hasPassengers();

    boolean isSneaking();

    boolean isSprinting();

    boolean isWet();

    float age();

    float getYaw();

    Vec3d getVelocity();

    String getTypeString();

}
