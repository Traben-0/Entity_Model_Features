package traben.entity_model_features.models.animation.math.variables.factories;


import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;

import java.util.HashMap;
import java.util.Map;

public class GlobalVariableFactory extends UniqueVariableFactory {

    private static final Map<String, Float> globalVariables = new HashMap<>();

    public static void setGlobalVariable(String key, float value) {
        globalVariables.put(key, value);
    }

    public static float getGlobalVariable(String key) {
        return globalVariables.getOrDefault(key, 0f);
    }

    @Override
    public MathValue.ResultSupplier getSupplierOrNull(final String variableKey, AnimSetupContext context) {
        return () -> globalVariables.getOrDefault(variableKey, 0f);
    }

    @Override
    public boolean createsThisVariable(final String variableKey) {
        if (variableKey == null) return false;
        return variableKey.matches("global_(var|varb)\\.\\w+");
    }

    @Override
    public @Nullable String getExplanationTranslationKey() {
        return "entity_model_features.config.variable_explanation.global_variable";
    }

    @Override
    public @Nullable String getTitleTranslationKey() {
        return "entity_model_features.config.variable_explanation.global_variable.title";
    }
}
