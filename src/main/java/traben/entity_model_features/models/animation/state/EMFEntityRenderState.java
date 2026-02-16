package traben.entity_model_features.models.animation.state;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.EMFAttachments;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

import java.util.function.Function;

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

    Function<ResourceLocation, RenderType> layerFactory();
    void setLayerFactory(Function<ResourceLocation, RenderType> layerFactory);

    @Nullable EMFAttachments leftArmOverride();
    void setLeftArmOverride(EMFAttachments override);
    @Nullable EMFAttachments rightArmOverride();
    void setRightArmOverride(EMFAttachments override);

    /** -1 == unset */
    int modelVariant();
    void setModelVariant(int variant);
}