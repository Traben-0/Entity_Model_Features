package traben.entity_model_features.utils;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public record EMFEntityWrapper(Entity entity) implements EMFEntity {

    @Override
    public Entity entity() {
        return entity;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return null;
    }

    @Override
    public World getWorld() {
        return entity.getWorld();
    }

    @Override
    public UUID getUuid() {
        if (entity == null) return null;
        return entity.getUuid();
    }

    @Override
    public double prevX() {
        return entity.prevX;
    }

    @Override
    public double getX() {
        return entity.getX();
    }

    @Override
    public double prevY() {
        return entity.prevY;
    }

    @Override
    public double getY() {
        return entity.getY();
    }

    @Override
    public double prevZ() {
        return entity.prevZ;
    }

    @Override
    public double getZ() {
        return entity.getZ();
    }

    @Override
    public float prevPitch() {
        return entity.prevPitch;
    }

    @Override
    public float getPitch() {
        return entity.getPitch();
    }

    @Override
    public boolean isTouchingWater() {
        return entity.isTouchingWater();
    }

    @Override
    public boolean isOnFire() {
        return entity.isOnFire();
    }

    @Override
    public boolean hasVehicle() {
        return entity.hasVehicle();
    }

    @Override
    public boolean isOnGround() {
        return entity.isOnGround();
    }

    @Override
    public boolean isAlive() {
        return entity.isAlive();
    }

    @Override
    public LivingEntity getLiving() {
        if (entity instanceof LivingEntity alive)
            return alive;
        return null;
    }

    @Override
    public boolean isGlowing() {
        return entity.isGlowing();
    }

    @Override
    public boolean isInLava() {
        return entity.isInLava();
    }

    @Override
    public boolean isInvisible() {
        return entity.isInvisible();
    }

    @Override
    public boolean hasPassengers() {
        return entity.hasPassengers();
    }

    @Override
    public boolean isSneaking() {
        return entity.isSneaking();
    }

    @Override
    public boolean isSprinting() {
        return entity.isSprinting();
    }

    @Override
    public boolean isWet() {
        return entity.isWet();
    }

    @Override
    public float age() {
        return entity.age;
    }

    @Override
    public float getYaw() {
        return entity.getYaw();
    }

    @Override
    public Vec3d getVelocity() {
        return entity.getVelocity();
    }

    @Override
    public String getTypeString() {
        return entity.getType().toString();
    }


}
