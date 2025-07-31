package traben.entity_model_features.models.animation.state;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.world.phys.Vec3;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public interface EMFEntityRenderState extends ETFEntityRenderState {

    /**
     * Deprecated - replace usages with 1.21+ impl that doesn't smuggle entity
     */
    @Deprecated
    EMFEntity emfEntity();

    double prevX();
    double x();
    double prevY();
    double y();
    double prevZ();
    double z();

    float prevPitch();
    float pitch();

    boolean isTouchingWater();
    boolean isOnFire();
    boolean hasVehicle();
    boolean isOnGround();
    boolean isAlive();
    boolean isGlowing();
    boolean isInLava();
    boolean isInvisible();
    boolean hasPassengers();
    boolean isSneaking();
    boolean isSprinting();
    boolean isWet();

    float age();
    float yaw();

    Vec3 emfVelocity(); // nullable

    String typeString(); // nullable

    Object2FloatOpenHashMap<String> variableMap(); // nullable
}