package traben.entity_model_features.models.animation.math.variables.factories;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.MathValue;

public class GlobalVariableFactory extends UniqueVariableFactory {

    private static final Object2FloatOpenHashMap<String> globalVariables = new Object2FloatOpenHashMap<>();

    public static void setGlobalVariable(String key, float value) {
        globalVariables.put(key, value);
    }

    public static float getGlobalVariable(String key) {
        return globalVariables.getFloat(key);
    }

    @Override
    public MathValue.ResultSupplier getSupplierOrNull(final String variableKey, final EMFAnimation calculationInstance) {
        return () -> globalVariables.getFloat(variableKey);
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
