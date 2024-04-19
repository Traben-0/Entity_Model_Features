package traben.entity_model_features.utils;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.math.Vec3d;
import traben.entity_texture_features.utils.ETFEntity;

/**
 * This interface is applied to all entities that are used in the Entity Model Features system.
 * EMF itself applies these to all {@link net.minecraft.entity.Entity} and {@link net.minecraft.block.entity.BlockEntity} instances.
 * <p>
 * This interface is used to provide a common set of methods that are used in the EMF system.
 * It extends from an ETF interface that does the exact same thing, but for the Entity Texture Features system.
 * <p>
 * An instance of this interface should always be an instance of either Entity or BlockEntity.
 */
public interface EMFEntity extends ETFEntity {


    double emf$prevX();

    double emf$getX();

    double emf$prevY();

    double emf$getY();

    double emf$prevZ();

    double emf$getZ();

    float emf$prevPitch();

    float emf$getPitch();

    boolean emf$isTouchingWater();

    boolean emf$isOnFire();

    boolean emf$hasVehicle();

    boolean emf$isOnGround();

    boolean emf$isAlive();

    boolean emf$isGlowing();

    boolean emf$isInLava();

    boolean emf$isInvisible();

    boolean emf$hasPassengers();

    boolean emf$isSneaking();

    boolean emf$isSprinting();

    boolean emf$isWet();

    float emf$age();

    float emf$getYaw();

    Vec3d emf$getVelocity();

    String emf$getTypeString();

    Object2FloatOpenHashMap<String> emf$getVariableMap();


}
