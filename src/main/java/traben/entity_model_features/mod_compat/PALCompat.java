package traben.entity_model_features.mod_compat;


//#if FORGE || MC <12100
//#elseif MC>=12109
import com.zigythebird.playeranim.accessors.IAnimatedAvatar;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
//#else
//$$ import com.zigythebird.playeranim.accessors.IAnimatedPlayer;
//$$ import com.zigythebird.playeranim.animation.PlayerAnimManager;
//#endif
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_model_features.utils.EMFUtils;

import java.lang.reflect.Method;

public class PALCompat {

    private static boolean checkedIfIEmotePlayerExists = false;
    private static Class<?> iEmotePlayerEntityType = null;
    private static Method isPlayingEmoteMethod = null;

    public static boolean shouldPauseEntityAnim(EMFEntity entity) {
        //#if FORGE || MC <12100
        //#elseif MC>=12109
        if (entity instanceof IAnimatedAvatar animationState) {
            AvatarAnimManager manager = animationState.playerAnimLib$getAnimManager();
            return manager != null && manager.isActive();
        }
        //#else
        //$$ if (entity instanceof IAnimatedPlayer player) {
        //$$     PlayerAnimManager manager = player.playerAnimLib$getAnimManager();
        //$$     return manager != null && manager.isActive();
        //$$ }
        //#endif

        //EMFUtils.log("Emoting: " + isPlayerEmoting(entity));
        // When emoting with EMOTECRAFT mod, the player will be forced to his vanilla model to emote properly
        return isPlayerEmoting(entity);
    }

    // ---------------------------------------------------------------------------------
    // ------------------------ EMOTECRAFT comptability section ------------------------

    public static boolean isPlayerEmoting(EMFEntity entity) {
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
            // Tries to get the IEmotePlayerEntity interface in order to access the isPlayingEmote() method
            // https://github.com/KosmX/emotes/blob/1.20.1/executor/src/main/java/io/github/kosmx/emotes/executor/emotePlayer/IEmotePlayerEntity.java
            // This type should always be found if EmoteCraft mod doesn't change it too much and the mod is actually loaded obv
            iEmotePlayerEntityType = Class.forName("io.github.kosmx.emotes.executor.emotePlayer.IEmotePlayerEntity");
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

    // -------------------- end of EMOTECRAFT comptability section ---------------------
    // ---------------------------------------------------------------------------------


}