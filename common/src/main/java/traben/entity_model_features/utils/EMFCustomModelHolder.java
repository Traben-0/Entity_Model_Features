package traben.entity_model_features.utils;

import traben.entity_model_features.models.EMFModelPartRoot;

public interface EMFCustomModelHolder {

    default boolean emf$hasModel() {
        return emf$getModel() != null;
    }

    EMFModelPartRoot emf$getModel();

    void emf$setModel(EMFModelPartRoot model);
}
