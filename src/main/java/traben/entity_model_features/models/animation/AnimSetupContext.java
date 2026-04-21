package traben.entity_model_features.models.animation;

import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.parts.EMFModelPart;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class AnimSetupContext implements AutoCloseable {

    public LinkedHashMap<String, EMFAnimation> emfAnimationVariables;
    public HashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId;

    public @Nullable String animKey = null;
    public final String modelName;

    public AnimSetupContext(
            String modelName,
            LinkedHashMap<String, EMFAnimation> emfAnimationVariables,
            HashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId
    ) {
        this.modelName = modelName;
        this.emfAnimationVariables = emfAnimationVariables;
        this.allPartsBySingleAndFullHeirachicalId = allPartsBySingleAndFullHeirachicalId;
    }


    @Override
    public void close() throws Exception {
        emfAnimationVariables = null;
        allPartsBySingleAndFullHeirachicalId = null;
    }
}
