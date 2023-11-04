package traben.entity_model_features.utils;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class EMFBlockEntityWrapper implements EMFEntity {

    private final BlockEntity blockEntity;

    public EMFBlockEntityWrapper(BlockEntity entity) {
        this.blockEntity = entity;
    }
    private UUID id = null;

    private UUID getUuidFromPos() {
//        int seed = blockEntity.getPos().hashCode();
//        if (blockEntity instanceof Nameable nameable && nameable.hasCustomName()) {
//            seed += Objects.requireNonNull(nameable.getCustomName()).hashCode();
//        }else{
//            seed += blockEntity.getCachedState().getBlock().getTranslationKey().hashCode();
//        }
        // BlockPos is actually enough for EMF's needs
        return UUID.nameUUIDFromBytes(new byte[]{(byte) blockEntity.getPos().hashCode()});
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
    public World getWorld() {
        return blockEntity.getWorld();
    }

    @Override
    public UUID getUuid() {
        if (id == null) {
            id = getUuidFromPos();
        }
        return id;
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

    @Override
    public Vec3d getVelocity() {
        return new Vec3d(0,0,0);
    }

    @Override
    public String getTypeString() {
        return blockEntity.getType().toString();
    }
}
