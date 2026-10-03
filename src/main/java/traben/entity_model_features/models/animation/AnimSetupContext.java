package traben.entity_model_features.models.animation;

import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.math.expression_tree.OldEMFAnimationHandler;
import traben.entity_model_features.models.parts.EMFModelPart;

import java.util.HashMap;

public class AnimSetupContext implements AutoCloseable {

    public OldEMFAnimationHandler oldAnimationHandler;
    public HashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId;
    // Remembers the getModelFromHierarchicalId() results of this setup, misses included, as the part map no longer changes
    private final HashMap<String, EMFModelPart> hierarchicalIdResults = new HashMap<>();

    public @Nullable String animKey = null;
    public final String modelName;

    public AnimSetupContext(
            String modelName,
            OldEMFAnimationHandler oldAnimationHandler,
            HashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId
    ) {
        this.modelName = modelName;
        this.oldAnimationHandler = oldAnimationHandler;
        this.allPartsBySingleAndFullHeirachicalId = allPartsBySingleAndFullHeirachicalId;
    }

    public @Nullable EMFModelPart getModelFromHierarchicalId(String hierarchId) {
        if (hierarchicalIdResults.containsKey(hierarchId)) {
            return hierarchicalIdResults.get(hierarchId);
        }
        EMFModelPart part = EMFManager.getModelFromHierarchicalId(hierarchId, allPartsBySingleAndFullHeirachicalId);
        hierarchicalIdResults.put(hierarchId, part);
        return part;
    }

    @Override
    public void close() {
        oldAnimationHandler = null;
        allPartsBySingleAndFullHeirachicalId = null;
        hierarchicalIdResults.clear();
    }
}
