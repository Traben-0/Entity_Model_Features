package traben.entity_model_features.models.animation.state;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.EMFAttachments;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.state.ETFEntityRenderStateViaReference;

import java.util.function.Function;

public class EMFEntityRenderStateViaReference extends ETFEntityRenderStateViaReference implements EMFEntityRenderState {

    private final EMFEntity emfEntity;

    public EMFEntityRenderStateViaReference(EMFEntity emfEntity) {
        super(emfEntity);
        this.emfEntity = emfEntity;
    }

    @Override @Deprecated public EMFEntity emfEntity() { return emfEntity; }

    @Override public double prevX() { return emfEntity.emf$prevX(); }
    @Override public double x() { return emfEntity.emf$getX(); }
    @Override public double prevY() { return emfEntity.emf$prevY(); }
    @Override public double y() { return emfEntity.emf$getY(); }
    @Override public double prevZ() { return emfEntity.emf$prevZ(); }
    @Override public double z() { return emfEntity.emf$getZ(); }

    @Override public float prevPitch() { return emfEntity.emf$prevPitch(); }
    @Override public float pitch() { return emfEntity.emf$getPitch(); }

    @Override public boolean isTouchingWater() { return emfEntity.emf$isTouchingWater(); }
    @Override public boolean isOnFire() { return emfEntity.emf$isOnFire(); }
    @Override public boolean hasVehicle() { return emfEntity.emf$hasVehicle(); }
    @Override public boolean isOnGround() { return emfEntity.emf$isOnGround(); }
    @Override public boolean isAlive() { return emfEntity.emf$isAlive(); }
    @Override public boolean isGlowing() { return emfEntity.emf$isGlowing(); }
    @Override public boolean isInLava() { return emfEntity.emf$isInLava(); }
    @Override public boolean isInvisible() { return emfEntity.emf$isInvisible(); }
    @Override public boolean hasPassengers() { return emfEntity.emf$hasPassengers(); }
    @Override public boolean isSneaking() { return emfEntity.emf$isSneaking(); }
    @Override public boolean isSprinting() { return emfEntity.emf$isSprinting(); }
    @Override public boolean isWet() { return emfEntity.emf$isWet(); }

    @Override public float age() { return emfEntity.emf$age(); }
    @Override public float yaw() { return emfEntity.emf$getYaw(); }

    @Override public Vec3 emfVelocity() { return emfEntity.emf$getVelocity(); }

    @Override public String typeString() { return emfEntity.emf$getTypeString(); }

    @Override public Object2FloatOpenHashMap<String> variableMap() {
        return emfEntity.emf$getVariableMap();
    }


    private Function<ResourceLocation, RenderType> layerFactory = null;
    @Override
    public Function<ResourceLocation, RenderType> layerFactory() {
        return layerFactory;
    }
    @Override
    public void setLayerFactory(final Function<ResourceLocation, RenderType> layerFactory) {
        this.layerFactory = layerFactory;
    }

    private EMFAttachments leftArmOverride = null;
    @Override public @Nullable EMFAttachments leftArmOverride() { return leftArmOverride; }
    @Override public void setLeftArmOverride(EMFAttachments override) { leftArmOverride = override; }

    private EMFAttachments rightArmOverride = null;
    @Override public @Nullable EMFAttachments rightArmOverride() { return rightArmOverride; }
    @Override public void setRightArmOverride(EMFAttachments override) { rightArmOverride = override; }

    EMFBipedPose bipedPose = null;
    @Override public void setBipedPose(EMFBipedPose pose) { bipedPose = pose; }
    @Override public EMFBipedPose getBipedPose() { return bipedPose; }
}