package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import traben.entity_model_features.models.animation.state.EMFState;
import java.util.UUID;

@Deprecated
public final class EMFAnimationEntityContext {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final ObjectSet<UUID> BLANK_SET = new ObjectOpenHashSet<>();

    // Note don't modify this, both Essential and EmoteCraft mixin to this method
    // EmoteCraft is also now intentionally leaving a mixin with a NPE here to effectively just always
    // crash EMF and is refusing all attempts to rectify, so I'll just wrap this in a catch and use a backup check...
    // unfortunately I cant fix this on my end without also breaking Essential's mixin
    // And EmoteCraft has refused my PR to fix

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated // Now exists just to capture Essential's mixin
    public static boolean isEntityAnimPaused() {
        var state = EMFState.state();
        if (state == null || state.isBlockEntity()) return false;

        return BLANK_SET.contains(state.uuid()); // Essential targets this, will only be true due to it
    }
}
