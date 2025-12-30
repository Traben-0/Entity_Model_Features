package traben.entity_model_features.mod_compat;


//#if FORGE || MC <12100
//#elseif MC>=12109
import com.zigythebird.playeranim.accessors.IAnimatedAvatar;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
//#else
//$$ import com.zigythebird.playeranim.accessors.IAnimatedPlayer;
//$$ import com.zigythebird.playeranim.animation.PlayerAnimManager;
//#endif
import traben.entity_model_features.utils.EMFEntity;

public class PALCompat {
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
        return false;
    }
}