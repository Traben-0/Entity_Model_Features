package traben.entity_model_features.models.animation.state;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.state.ETFEntityRenderStateViaReference;

import java.util.Map;
import java.util.function.Function;

public class EMFEntityRenderStateViaReference extends ETFEntityRenderStateViaReference implements EMFEntityRenderState {

    private final EMFEntity emfEntity;

    public EMFEntityRenderStateViaReference(EMFEntity emfEntity) {
        super(emfEntity);
        this.emfEntity = emfEntity;
    }

    @Override @Deprecated public EMFEntity emfEntity() { return emfEntity; }

    boolean isManualPlayerState = false;
    @Override public boolean isManualPlayerState() { return isManualPlayerState; }
    @Override public void setManualPlayerState(boolean manualPlayerState) { isManualPlayerState = manualPlayerState; }

    //region emfEntity passthrough
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

    @Override public Map<String, Float> variableMap() {
        return emfEntity.emf$getVariableMap();
    }
    //endregion

    private Function<ResourceLocation, RenderType> layerFactory = null;
    @Override
    public Function<ResourceLocation, RenderType> layerFactory() {
        return layerFactory;
    }
    @Override
    public void setLayerFactory(final Function<ResourceLocation, RenderType> layerFactory) {
        if (emfEntity instanceof Arrow) {
            return;
        }
        this.layerFactory = layerFactory;
    }

    EMFBipedPose bipedPose = null;
    @Override public void setBipedPose(EMFBipedPose pose) { bipedPose = pose; }
    @Override public EMFBipedPose getBipedPose() { return bipedPose; }

    boolean isFirstPersonHand = false;
    @Override public boolean isFirstPersonHand() { return isFirstPersonHand; }
    @Override public void setIsFirstPersonHand(boolean isFirst) { this.isFirstPersonHand = isFirst; }

    boolean onShoulder = false;
    @Override public boolean onShoulder() { return onShoulder; }
    @Override public void setOnShoulder(boolean onShoulder) { this.onShoulder = onShoulder; }

    boolean skipModelVariate = false;
    @Override public boolean skipModelVariate() { return skipModelVariate; }
    @Override public void setSkipModelVariate(boolean value) { skipModelVariate = value; }

    float shadowSize = Float.NaN;
    @Override public float shadowSize() { return shadowSize; }
    @Override public void setShadowSize(float shadowSize) { this.shadowSize = shadowSize; }

    float shadowOpacity = Float.NaN;
    @Override public float shadowOpacity() { return shadowOpacity; }
    @Override public void setShadowOpacity(float shadowOpacity) { this.shadowOpacity = shadowOpacity; }

    float shadowX = Float.NaN;
    @Override public float shadowX() { return shadowX; }
    @Override public void setShadowX(float shadowX) { this.shadowX = shadowX; }

    float shadowZ = Float.NaN;
    @Override public float shadowZ() { return shadowZ; }
    @Override public void setShadowZ(float shadowZ) { this.shadowZ = shadowZ; }

    float limbAngle = Float.NaN;
    @Override public float limbAngle() { return limbAngle; }
    @Override public void setLimbAngle(float limbAngle) { this.limbAngle = limbAngle; }

    float limbDistance = Float.NaN;
    @Override public float limbDistance() { return limbDistance; }
    @Override public void setLimbDistance(float limbDistance) { this.limbDistance = limbDistance; }

    float headYaw = Float.NaN;
    @Override public float headYaw() { return headYaw; }
    @Override public void setHeadYaw(float headYaw) { this.headYaw = headYaw; }

    float headPitch = Float.NaN;
    @Override public float headPitch() { return headPitch; }
    @Override public void setHeadPitch(float headPitch) { this.headPitch = headPitch; }

    float fireX = Float.NaN;
    @Override public float fireX() { return fireX; }
    @Override public void setFireX(float fireX) { this.fireX = fireX; }
    float fireY = Float.NaN;
    @Override public float fireY() { return fireY; }
    @Override public void setFireY(float fireY) { this.fireY = fireY; }
    float fireZ = Float.NaN;
    @Override public float fireZ() { return fireZ; }
    @Override public void setFireZ(float fireZ) { this.fireZ = fireZ; }
    float fireWidth = Float.NaN;
    @Override public float fireScale() { return fireWidth; }
    @Override public void setFireScale(float fireWidth) { this.fireWidth = fireWidth; }
    float fireHeight = Float.NaN;
    @Override public float fireHeight() { return fireHeight; }
    @Override public void setFireHeight(float fireHeight) { this.fireHeight = fireHeight; }




    @Override
    public String toString() {
        return "EMFState-" + this.typeString();
    }
}