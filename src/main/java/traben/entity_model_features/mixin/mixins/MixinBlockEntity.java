package traben.entity_model_features.mixin.mixins;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFEntity;

import java.util.HashMap;
import java.util.Map;

@Mixin(BlockEntity.class)
public abstract class MixinBlockEntity implements EMFEntity {


    @Unique
    private final Map<String, Float> emf$variableMap = new HashMap<>();



    @Shadow
    @Nullable
    public abstract Level getLevel();

    @Shadow
    public abstract BlockEntityType<?> getType();

    @Shadow public abstract BlockPos getBlockPos();

    @Shadow public abstract BlockState getBlockState();

    @Override
    public double emf$prevX() {
        return getBlockPos().getX();
    }

    @Override
    public double emf$getX() {
        return getBlockPos().getX();
    }

    @Override
    public double emf$prevY() {
        return getBlockPos().getY();
    }

    @Override
    public double emf$getY() {
        return getBlockPos().getY();
    }

    @Override
    public double emf$prevZ() {
        return getBlockPos().getZ();
    }

    @Override
    public double emf$getZ() {
        return getBlockPos().getZ();
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
        return !getBlockState().getFluidState().isEmpty();
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
        return getLevel() != null && !getLevel().getBlockState(getBlockPos()).isAir();
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
        return !getBlockState().getFluidState().isEmpty();
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
    public Vec3 emf$getVelocity() {
        return new Vec3(0, 0, 0);
    }

    @Override
    public String emf$getTypeString() {
        return getType().toString();
    }

    @Unique private int varHash = 0;

    @Override
    public Map<String, Float> emf$getVariableMap() {
        int managerHash = EMFManager.getManagerInstanceHash();
        if (varHash != managerHash) {
            varHash = managerHash;
            emf$variableMap.clear();
        }

        return emf$variableMap;
    }
}
