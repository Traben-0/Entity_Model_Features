package traben.entity_model_features.models;

import traben.entity_model_features.utils.OptifineMobNameForFileAndEMFMapId;

public interface IEMFModelNameContainer {

    OptifineMobNameForFileAndEMFMapId emf$getKnownMappings();

    void emf$insertKnownMappings(OptifineMobNameForFileAndEMFMapId newName);
}
