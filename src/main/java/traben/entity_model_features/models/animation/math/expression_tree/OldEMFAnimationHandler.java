package traben.entity_model_features.models.animation.math.expression_tree;

import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.EMFAnimationHandler;
import traben.entity_model_features.models.animation.math.variables.factories.GlobalVariableFactory;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.utils.ETFLruCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static traben.entity_model_features.models.animation.math.expression_tree.MathValue.FALSE;

public class OldEMFAnimationHandler extends EMFAnimationHandler {

    public final LinkedHashMap<String, @Nullable MathComponent> oldAnimLines = new LinkedHashMap<>();
    public final HashMap<String, Float> defaults = new HashMap<>();
    public final HashMap<String, Consumer<Float>> resultConsumers = new HashMap<>();

    public final ETFLruCache<UUID, ConcurrentHashMap<String, Float>> lastResultsPerEntity = new ETFLruCache<>();

    public OldEMFAnimationHandler(String modelName) {
        super(modelName, new ArrayList<>());
    }

    public MathValue.ResultSupplier getLastResultGetter(String variableKey) {
        if (!oldAnimLines.containsKey(variableKey)) return null;
        return ()-> lastResult(variableKey);
    }

    private Map<String, Float> prevResultsOfEntity() {
        //noinspection deprecation
        var state = EMFAnimationEntityContext.getEmfState();
        if (state == null) return null;

        return lastResultsPerEntity.computeIfAbsent(state.uuid(), u -> new ConcurrentHashMap<>());
    }

    private Float lastResult(String variableKey) {
        var map = prevResultsOfEntity();
        if (map == null) return defaults.get(variableKey);
        var last = map.get(variableKey);
        return last != null ? last : defaults.get(variableKey);
    }

    @Override
    public boolean finishAndValidate() {
        if (oldAnimLines.isEmpty()) {
            EMFUtils.logError("OldEMFAnimationHandler was empty for " + modelName);
            return false;
        }
        if (oldAnimLines.size() != lines().size()) {
            EMFUtils.logError("OldEMFAnimationHandler was not correctly sized " + modelName);
            return false;
        }

        for (var line : lines()) {
            Consumer<Float> consumer = (f)->{};
            final String key = line.animKey;
            if (line.isVar) {
                if (line.isVarGlobal) {
                    if (line.isBoolean) {
                        consumer = value -> GlobalVariableFactory.setGlobalVariable(key, MathValue.isBoolean(value) ? value : FALSE);
                    } else {
                        consumer = value -> GlobalVariableFactory.setGlobalVariable(key, MathValue.isBoolean(value) ? 0 : value);
                    }
                } else {
                    if (line.isBoolean) {
                        //noinspection deprecation
                        consumer = value -> EMFAnimationEntityContext.setEntityVariable(key, MathValue.isBoolean(value) ? value : FALSE);
                    } else {
                        //noinspection deprecation
                        consumer = value -> EMFAnimationEntityContext.setEntityVariable(key, MathValue.isBoolean(value) ? 0 : value);
                    }
                }
            } else if (line.applier != null) {
                var finApply = line.applier;
                @Nullable var finPart = line.partToApplyTo;
                consumer = (f)-> finApply.setValue(finPart, f);
            }
            resultConsumers.put(key, consumer);
        }
        return true;
    }

    @Override
    protected void animateInner(ModelPart[] pausedParts){
        Map<String, Float> prevVals = prevResultsOfEntity();
        //noinspection deprecation
        boolean skip = prevVals != null && EMFAnimationEntityContext.isLODSkippingThisFrame(modelName);

        for (var line : lines()) {
            if (pausedParts != null) {
                boolean needsPause = false;
                for (ModelPart part : pausedParts) {
                    if (line.partToApplyTo == part) {
                        needsPause = true;
                        break;
                    }
                }
                if (needsPause) continue;
            }

            if (skip) {
                if (!line.isVar) {
                    var prev = prevVals.get(line.animKey);
                    if (prev != null) {
                        resultConsumers.get(line.animKey).accept(prev);
                    }
                }
            } else {
                //noinspection DataFlowIssue
                float result = oldAnimLines.get(line.animKey).getResult();
                if (!Float.isNaN(result)) {
                    if (prevVals != null) {
                        prevVals.put(line.animKey, result);
                    }
                    resultConsumers.get(line.animKey).accept(result);
                }
            }
        }
    }
}
