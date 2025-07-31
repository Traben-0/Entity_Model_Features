package traben.entity_model_features.models;

import traben.entity_model_features.models.parts.EMFModelPartRoot;

public interface IEMFModel {


    boolean emf$isEMFModel();

    EMFModelPartRoot emf$getEMFRootModel();

}
