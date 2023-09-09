package traben.entity_model_features.utils;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import traben.entity_texture_features.entity_handlers.ETFBlockEntityWrapper;

import java.util.UUID;

public class EMFBlockEntityWrapper extends ETFBlockEntityWrapper implements EMFEntity {

    private final BlockEntity blockEntity;

    public EMFBlockEntityWrapper(BlockEntity entity, UUID id) {
        super(entity, id);
        this.blockEntity = entity;
    }

    @Override
    public Entity entity() {
        return null;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public double prevX() {
        return blockEntity.getPos().getX();
    }

    @Override
    public double getX() {
        return blockEntity.getPos().getX();
    }

    @Override
    public double prevY() {
        return blockEntity.getPos().getY();
    }

    @Override
    public double getY() {
        return blockEntity.getPos().getY();
    }

    @Override
    public double prevZ() {
        return blockEntity.getPos().getZ();
    }

    @Override
    public double getZ() {
        return blockEntity.getPos().getZ();
    }

    @Override
    public float prevPitch() {
        return 0;
    }

    @Override
    public float getPitch() {
        return 0;
    }

    @Override
    public boolean isTouchingWater() {
        return !blockEntity.getCachedState().getFluidState().isEmpty();
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean hasVehicle() {
        return false;
    }

    @Override
    public boolean isOnGround() {
        return blockEntity.getWorld() != null && !blockEntity.getWorld().getBlockState(blockEntity.getPos()).isAir();
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public LivingEntity getLiving() {
        return null;
    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public boolean isInLava() {
        return false;
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public boolean hasPassengers() {
        return false;
    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public boolean isSprinting() {
        return false;
    }

    @Override
    public boolean isWet() {
        return !blockEntity.getCachedState().getFluidState().isEmpty();
    }

    @Override
    public float age() {
        return 0;
    }

    @Override
    public float getYaw() {
        return 0;
    }
}
