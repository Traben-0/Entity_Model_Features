package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import traben.entity_model_features.models.animation.state.EMFState;
import java.util.UUID;

/**
 * Most state usages of this class were moved into the current EMFEntityRenderState mounted to EMFState.state()
 * Most static methods from this class moved to EMFMaths
 */
@Deprecated
public final class EMFAnimationEntityContext {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final ObjectSet<UUID> BLANK_SET = new ObjectOpenHashSet<>();

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated // Now exists just to capture Essential's mixin
    public static boolean isEntityAnimPaused() {
        var state = EMFState.state();
        if (state == null || state.isBlockEntity()) return false;

        return BLANK_SET.contains(state.uuid()); // Essential targets this, will only be true due to it
    }
}
