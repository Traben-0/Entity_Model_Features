package traben.entity_model_features.utils;

import traben.entity_model_features.models.animation.state.EMFEntityRenderState;

public interface HoldsBackupEMFRenderState {
    void emf$setState(EMFEntityRenderState state);
    EMFEntityRenderState emf$getState();

}
