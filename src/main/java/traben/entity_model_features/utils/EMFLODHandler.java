package traben.entity_model_features.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.mod_compat.IrisShadowPassDetection;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_texture_features.ETF;

import java.util.HashMap;
import java.util.Map;

public abstract class EMFLODHandler {

    private static final Map<String, Integer> lodEntityTimers = new HashMap<>();

    private static Boolean lodFrameSkipping = null;
    public static void setNullLodFrameSkipping() {
        lodFrameSkipping = null;
    }

    @Deprecated // In 26.1+
    public static double lastFOV = 70;


    private static int getLODFactorOfEntity(@NotNull EMFEntityRenderState state) {
        //if (lodFactor != -1) return lodFactor;

        if (EMF.config().getConfig().animationLODDistance == 0) return 0;

        //no factor when using spyglass or player is null
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().player.isScoping()) {
            return 0;
        }

        int distance = distanceOfEntityFrom(Minecraft.getInstance().player.blockPosition(), state);
        if (distance < 1) return 0;

        int factor = distance / EMF.config().getConfig().animationLODDistance;
        //reduce factor when using zoom mods or lower fov
        int factorByFOV = (int) (factor *

                //#if MC >= 26.2
                //$$ Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.cameraRenderState.hudFov
                //#elseif MC >= 26.1
                //$$ Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.hudFov
                //#else
                lastFOV
                //#endif
                / 70);

        int lodFactor;
        //factor in low fps detail retention
        if (EMF.config().getConfig().retainDetailOnLowFps && Minecraft.getInstance().getFps() < 59) { // count often drops to 59 while capped at 60 :/
            float fpsPercentageOf60 = Minecraft.getInstance().getFps() / 60f;
            //reduce factor by the percentage of fps below 60 to recover some level of detail
            lodFactor = (int) (factorByFOV * fpsPercentageOf60);
        } else {
            lodFactor = factorByFOV;
        }

        if (EMF.config().getConfig().retainDetailOnLargerMobs && state.emfEntity() instanceof Entity entity) {
            var entitySize = Math.max(entity.getBbWidth(), entity.getBbHeight());
            if (entitySize > 2) {
                lodFactor = (int) (lodFactor / (entitySize / 2));
            }
        }

        return lodFactor;
    }

    public static boolean isLODSkippingThisFrame(String modelId) {
        if (EMFState.isInGui || EMFState.isInShoulderMethod) return false;
        if (lodFrameSkipping != null) return lodFrameSkipping;

        //skip for shadow pass
        if(ETF.IRIS_DETECTED
                && IrisShadowPassDetection.getInstance().inShadowPass()
                && EMF.config().getConfig().animationFrameSkipDuringIrisShadowPass
                //not client player in first person
                && !(emfEntity() instanceof Player player
                && player.isLocalPlayer()
                && Minecraft.getInstance().options.getCameraType().isFirstPerson())) {
            return true;
        }

        var state = emfState();
        if (EMF.config().getConfig().animationLODDistance == 0 || state == null) return false;


        var type = state.entityType();
        if (type == UEntityTypes.VILLAGER || type == UEntityTypes.HORSE) return false;

        // Just putting this here so that fresh animations counter rotation keep working, plus these tend to render from a distance anyway
        if (type == UEntityTypes.BLAZE) return false;

        String lodKey = state.uuid() + modelId;

        int lodTimer = lodEntityTimers.getOrDefault(lodKey, 0);
        int lodResult;
        //check lod
        if (lodTimer < 1) {
            lodResult = getLODFactorOfEntity(state);
        } else {
            lodResult = lodTimer - 1;
        }
        lodEntityTimers.put(lodKey, lodResult);
        //intellij requires it for certain versions :/
        //noinspection RedundantCast
        lodFrameSkipping = (Boolean) (lodResult > 0);
        return lodFrameSkipping;
    }

    private static int distanceOfEntityFrom(BlockPos pos, @NotNull EMFEntityRenderState state) {
        var blockPos = state.blockPos();
        float f = (float) (blockPos.getX() - pos.getX());
        float g = (float) (blockPos.getY() - pos.getY());
        float h = (float) (blockPos.getZ() - pos.getZ());
        return (int) Mth.sqrt(f * f + g * g + h * h);
    }

    private static @Nullable EMFEntityRenderState emfState() { return EMFState.state(); }
    private static @Nullable EMFEntity emfEntity() {
        var state = emfState();
        return state == null ? null : state.emfEntity();
    }

}
