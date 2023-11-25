package traben.entity_model_features.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_model_features.utils.EMFEntity;

@Mixin(BlockEntity.class)
public abstract class MixinBlockEntity implements EMFEntity {


    @Shadow
    public abstract BlockPos getPos();

    @Shadow
    public abstract BlockState getCachedState();

    @Shadow
    @Nullable
    public abstract World getWorld();

    @Shadow
    public abstract BlockEntityType<?> getType();

    @Override
    public double emf$prevX() {
        return getPos().getX();
    }

    @Override
    public double emf$getX() {
        return getPos().getX();
    }

    @Override
    public double emf$prevY() {
        return getPos().getY();
    }

    @Override
    public double emf$getY() {
        return getPos().getY();
    }

    @Override
    public double emf$prevZ() {
        return getPos().getZ();
    }

    @Override
    public double emf$getZ() {
        return getPos().getZ();
    }

    @Override
    public float emf$prevPitch() {
        return 0;
    }

    @Override
    public float emf$getPitch() {
        return 0;
    }

    @Override
    public boolean emf$isTouchingWater() {
        return !getCachedState().getFluidState().isEmpty();
    }

    @Override
    public boolean emf$isOnFire() {
        return false;
    }

    @Override
    public boolean emf$hasVehicle() {
        return false;
    }

    @Override
    public boolean emf$isOnGround() {
        return getWorld() != null && !getWorld().getBlockState(getPos()).isAir();
    }

    @Override
    public boolean emf$isAlive() {
        return false;
    }


    @Override
    public boolean emf$isGlowing() {
        return false;
    }

    @Override
    public boolean emf$isInLava() {
        return false;
    }

    @Override
    public boolean emf$isInvisible() {
        return false;
    }

    @Override
    public boolean emf$hasPassengers() {
        return false;
    }

    @Override
    public boolean emf$isSneaking() {
        return false;
    }

    @Override
    public boolean emf$isSprinting() {
        return false;
    }

    @Override
    public boolean emf$isWet() {
        return !getCachedState().getFluidState().isEmpty();
    }

    @Override
    public float emf$age() {
        return 0;
    }

    @Override
    public float emf$getYaw() {
        return 0;
    }

    @Override
    public Vec3d emf$getVelocity() {
        return new Vec3d(0, 0, 0);
    }

    @Override
    public String emf$getTypeString() {
        return getType().toString();
    }
}
