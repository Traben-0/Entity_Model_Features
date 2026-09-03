package traben.entity_model_features.utils;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public abstract class EMFAnimationPauseHandler {

    public static HashMap<UUID, ModelPart[]> entitiesPausedParts = new HashMap<>();
    public static Set<UUID> entitiesPaused = new HashSet<>();
    public static List<Function<EMFEntity, Boolean>> pauseListeners = new ArrayList<>();

    public static boolean shouldAnimationsPause(EMFEntityRenderState state) {
        if (state == null) return false;

        // API for other mods to pause animations on specific entities
        var entity = state.emfEntity();
        if (entity != null) {
            if (isPlayerEmoting_KosmX_mod(entity)) return true;

            if (isPlayerEmoting_Essential(entity)) return true;

            for (Function<EMFEntity, Boolean> pauseListener : pauseListeners) {
                try {
                    if (pauseListener.apply(entity)) return true;
                } catch (Exception ignored) {}
            }
        }

        return entitiesPaused.contains(state.uuid());
    }

    //region KosmX emote mod compat

    private static boolean checkedIfIEmotePlayerExists = false;
    private static Class<?> iEmotePlayerEntityType = null;
    private static Method isPlayingEmoteMethod = null;

    private static boolean isPlayerEmoting_KosmX_mod(EMFEntity entity) {
        if (!(entity instanceof Player player)) return false;

        Method emoteMethod = getIsPlayingEmoteMethod();
        if (emoteMethod == null) return false;

        try {
            return (boolean) emoteMethod.invoke(player);
        } catch (Exception ignored) {
            return false;
        }
    }

    private static @Nullable Class<?> getIEmotePlayerEntityType() {
        if (checkedIfIEmotePlayerExists) return iEmotePlayerEntityType;
        checkedIfIEmotePlayerExists = true;

        try {
            try {
                // Tries to get the IEmotePlayerEntity interface in order to access the isPlayingEmote() method
                // https://github.com/KosmX/emotes/blob/1.20.1/executor/src/main/java/io/github/kosmx/emotes/executor/emotePlayer/IEmotePlayerEntity.java
                // This type should always be found if EmoteCraft mod doesn't change it too much and the mod is actually loaded obv
                iEmotePlayerEntityType = Class.forName("io.github.kosmx.emotes.executor.emotePlayer.IEmotePlayerEntity");
            } catch (ClassNotFoundException ignored) {
                // 1.21.4+
                // https://github.com/KosmX/emotes/blob/1.21.11/minecraft/archCommon/src/main/java/io/github/kosmx/emotes/main/mixinFunctions/IPlayerEntity.java
                iEmotePlayerEntityType = Class.forName("io.github.kosmx.emotes.main.mixinFunctions.IPlayerEntity");
            }
        } catch (ClassNotFoundException ignored) {
            iEmotePlayerEntityType = null;
        }

        return iEmotePlayerEntityType;
    }

    private static @Nullable Method getIsPlayingEmoteMethod() {
        if (isPlayingEmoteMethod != null) return isPlayingEmoteMethod;

        Class<?> emotePlayerType = getIEmotePlayerEntityType();
        if (emotePlayerType == null) return null;

        try {
            isPlayingEmoteMethod = emotePlayerType.getMethod("isPlayingEmote");
        } catch (NoSuchMethodException ignored) {
            isPlayingEmoteMethod = null;
        }

        return isPlayingEmoteMethod;
    }
    //endregion

    //region Essential mod compat
    private static boolean skipEssential = false;
    private static Method essentialIsPoseModifiedMethod = null;

    static boolean isPlayerEmoting_Essential(EMFEntity entity) {
        if (skipEssential) return false;
        if (entity == null) return false;
        if (!(entity instanceof AbstractClientPlayer)) return false;

        try {
            if (essentialIsPoseModifiedMethod == null) {
                essentialIsPoseModifiedMethod = Class.forName("gg.essential.mixins.impl.client.entity.AbstractClientPlayerExt").getMethod("isPoseModified");
            }

            return (boolean) essentialIsPoseModifiedMethod.invoke(entity);
        } catch (Throwable ignored) {
            skipEssential = true; // Expected if Essential isn't present, or ever changes
            return false;
        }
    }
    //endregion

    public static @Nullable ModelPart[] getEntityPartsAnimPaused() {
        var state = EMFState.state();
        if (state == null) return null;
        var parts = entitiesPausedParts.get(state.uuid());
        return parts == null || parts.length == 0 ? null : parts;
    }

}
