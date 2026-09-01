package traben.entity_model_features.models.animation.state;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.math.EMFMath;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_model_features.utils.EMFLODHandler;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.ETFSubmitData;
import traben.entity_texture_features.features.state.HoldsETFRenderState;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import traben.entity_texture_features.utils.ETFEntity;

public interface EMFEntityRenderState extends ETFEntityRenderState {

    /**
     * Deprecated - replace usages with 1.21+ impl that doesn't smuggle entity
     */
    @Deprecated
    EMFEntity emfEntity();

    boolean isManualPlayerState();
    void setManualPlayerState(boolean manualPlayerState);
    static @Nullable EMFEntityRenderState manualPlayerState() {
        if (Minecraft.getInstance().player == null) return null;

        //#if MC >= 1.21.2
        // Attempt to capture the full render state
        EMFEntityRenderState state = null;
        try {
            EntityRenderer<LocalPlayer, net.minecraft.client.renderer.entity.state.EntityRenderState> renderer =
                    (EntityRenderer<LocalPlayer, net.minecraft.client.renderer.entity.state.EntityRenderState>)
                            Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(Minecraft.getInstance().player);
            var vanilla = renderer.createRenderState();
            renderer.extractRenderState(Minecraft.getInstance().player, vanilla, EMFMath.getTickDelta());
            state = EMFEntityRenderState.from(vanilla);
        } catch (Exception ignored) {}
        if (state == null) state = (EMFEntityRenderState) ETFEntityRenderState.forEntity((ETFEntity) Minecraft.getInstance().player);
        //#else
        //$$ var state = (EMFEntityRenderState) ETFEntityRenderState.forEntity((ETFEntity) Minecraft.getInstance().player);
        //#endif

        state.setManualPlayerState(true);
        return state;
    }

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

    Map<String, Float> variableMap(); // nullable

    Function<ResourceLocation, RenderType> layerFactory();
    void setLayerFactory(Function<ResourceLocation, RenderType> layerFactory);

    void setBipedPose(EMFBipedPose pose);
    /** Returns the biped pose if it was animated. */
    @Nullable EMFBipedPose getBipedPose();


    boolean isFirstPersonHand();
    void setIsFirstPersonHand(boolean isFirst);

    float shadowSize();
    void setShadowSize(float shadowSize);

    float shadowOpacity();
    void setShadowOpacity(float shadowOpacity);

    float shadowX();
    void setShadowX(float shadowX);

    float shadowZ();
    void setShadowZ(float shadowZ);

    float limbAngle();
    void setLimbAngle(float limbAngle);

    float limbDistance();
    void setLimbDistance(float limbDistance);

    float headYaw();
    void setHeadYaw(float headYaw);

    float headPitch();
    void setHeadPitch(float headPitch);

    boolean onShoulder();
    void setOnShoulder(boolean onShoulder);

    default boolean needsToModifyShadow() {
        return !Float.isNaN(shadowSize())
                || !Float.isNaN(shadowOpacity())
                || !Float.isNaN(shadowX())
                || !Float.isNaN(shadowZ());
    }

    float fireX();
    void setFireX(float fireX);
    float fireY();
    void setFireY(float fireY);
    float fireZ();
    void setFireZ(float fireZ);

    float fireHeight();
    void setFireHeight(float fireHeightScale);
    float fireScale();
    void setFireScale(float fireWidthScale);

    default boolean needsToModifyFire() {
        return !Float.isNaN(fireScale())
                || !Float.isNaN(fireHeight())
                || !Float.isNaN(fireX())
                || !Float.isNaN(fireY())
                || !Float.isNaN(fireZ());
    }


    boolean skipModelVariate();
    void setSkipModelVariate(boolean value);

    //#if MC >= 1.21.9
    @Override
    default void preSubmitActivate(ETFSubmitData submitData,
                                   //#if MC >= 26.2
                                   //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit vanillaSubmit
                                   //#else
                                   net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit vanillaSubmit
                                   //#endif
    ) {
        ETFEntityRenderState.super.preSubmitActivate(submitData, vanillaSubmit);

        // The specific submit might have more precise changes that multiple submits using the same state might need to
        // be different, consider that here

        if (submitData == null) {
            setSkipModelVariate(false);
            setBipedPose(null);
        } else {
            Integer variant = (Integer) submitData.data.get("modelVariant");
            if (variant != null && vanillaSubmit.model().root() instanceof EMFModelPartRoot root) {
                root.setVariantStateTo(variant);
                setSkipModelVariate(true);
            } else {
                setSkipModelVariate(false);
            }

            // Submit data now controls if the state keeps this during the submit consumption phase, setting it null if we didn't want it this submit
            if (!isFirstPersonHand()) setBipedPose((EMFBipedPose) submitData.data.get("bipedPose"));
        }

        setLayerFactory(vanillaSubmit.model().renderType);

        if (vanillaSubmit.state() instanceof net.minecraft.client.renderer.entity.state.ItemFrameRenderState) {
            EMFState.isInItemFrame = true;
        }

        EMFState.isLayerPhase = false;
        if (submitData != null) {
            setOnShoulder(submitData.data.get("onShoulder") == Boolean.TRUE);

            if (vanillaSubmit.model() instanceof IEMFModel emfModel && emfModel.emf$isEMFModel()) {
                if (submitData.data.get("isMainModelPhase") == Boolean.TRUE) {
                    emfModel.emf$getEMFRootModel().isMainModel = true;
                    EMFState.isLayerPhase = false;
                }
                if (submitData.data.get("isLayerModelPhase") == Boolean.TRUE) {
                    EMFState.isLayerPhase = true;
                }
            }
        }


    }
    //#endif

    @Override
    default void activate(boolean inMount) {
        ETFEntityRenderState.super.activate(inMount);

        if (inMount) {
            EMFManager.getInstance().entityRenderCount++;

            if (EMFState.isInShoulderMethod) setOnShoulder(true);

            //#if MC >= 12102
            if (vanillaState() instanceof net.minecraft.client.renderer.entity.state.LivingEntityRenderState livingEntityRenderState) {
                if (EMFState.isInGui) {
                    // entity isn't actually walking in the gui, zero these out so animations don't jitter
                    setLimbAngle(0);
                    setLimbDistance(0);
                } else {
                    setLimbAngle(livingEntityRenderState.walkAnimationPos);
                    setLimbDistance(livingEntityRenderState.walkAnimationSpeed);
                }
                setHeadYaw(livingEntityRenderState.yRot);
                if (headYaw() >= 180 || headYaw() < -180) {
                    setHeadYaw(Mth.wrapDegrees(headYaw()));
                }
                setHeadPitch(livingEntityRenderState.xRot);
            } else { //block entity
                setLimbAngle(Float.NaN);
                setLimbDistance(Float.NaN);
                setHeadYaw(Float.NaN);
                setHeadPitch(Float.NaN);
            }
            //#else
            //$$ setLimbAngle(Float.NaN);
            //$$ setLimbDistance(Float.NaN);
            //$$ setHeadYaw(Float.NaN);
            //$$ setHeadPitch(Float.NaN);
            //#endif

            if (entity() instanceof Arrow) {
                setLayerFactory(
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                                //#endif
                                ::entityCutout);
            } else if (isBlockEntity()) {
                setLayerFactory(
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                                //#endif
                                ::entitySolid);
            }

            setOnShoulder(false);

            //perform variant checking for this entity types models
            //this is the only way to keep it generic and also before the entity is rendered and affect al its models
            boolean playerNeedsReset = emfEntity() instanceof Player
                    && EMF.config().getConfig().resetPlayerModelEachRender_v2
                    && !isFirstPersonHand();

            if (!skipModelVariate() || playerNeedsReset) {
                Set<EMFModelPartRoot> roots = EMFManager.getInstance().rootPartsPerEntityTypeForVariation.get(typeString());
                if (roots != null) {
                    if (!skipModelVariate()) {
                        if (EMFState.isEntityForcedToVanillaModel(this)) {
                            roots.forEach(root -> root.setVariantStateTo(0));
                        } else {
                            roots.forEach(root -> root.doVariantCheck(this));
                        }
                    }

                    if (playerNeedsReset) {
                        roots.forEach(EMFModelPartRoot::resetVanillaPartsToDefaults);
                    }
                }
            }

            //if this entity requires a debug print do it now after models have variated
            if (EMF.config().getConfig().debugOnRightClick
                    && uuid().equals(EMFManager.getInstance().entityForDebugPrint)) {
                EMFState.announceModels = true;
                EMFManager.getInstance().entityForDebugPrint = null;
            }
        }
        EMFLODHandler.setNullLodFrameSkipping();
    }

    @Override
    default void deactivate(boolean inMount) {
        ETFEntityRenderState.super.deactivate(inMount);
        EMFState.modelVariationIgnoresVisibility = false;
        EMFState.isInItemFrame = false;

        if (!inMount) {
            setSkipModelVariate(false);
        }
    }

    //#if MC >= 1.21.2
    @Nullable
    static EMFEntityRenderState from(net.minecraft.client.renderer.entity.state.EntityRenderState state) {
        if (state instanceof HoldsETFRenderState holds && holds.etf$getState() instanceof EMFEntityRenderState emf) {
            return emf;
        }
        return null;
    }
    //#endif
}