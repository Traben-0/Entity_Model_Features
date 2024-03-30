package traben.entity_model_features.models.animation.math.variables.factories;

import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.MathValue;

import static traben.entity_model_features.models.animation.math.MathValue.FALSE;

public class ModelVariableFactory extends UniqueVariableFactory {
    @Override
    public MathValue.ResultSupplier getSupplierOrNull(final String variableKey, final EMFAnimation calculationInstance) {
//        EMFAnimation variableCalculator = calculationInstance.emfAnimationVariables.get(variableKey);
//        if (variableCalculator != null) {
//            return variableCalculator::getLastResultOnly;
//        }
        if (variableKey.startsWith("varb")) {
            return () -> EMFAnimationEntityContext.getEntityVariable(variableKey, FALSE);
        } else {
            return () -> EMFAnimationEntityContext.getEntityVariable(variableKey, 0);
        }

    }

    @Override
    public boolean createsThisVariable(final String variableKey) {
        if (variableKey == null) return false;
        return variableKey.matches("(var|varb)\\.\\w+");
    }

    @Override
    public @Nullable String getExplanationTranslationKey() {
        return "entity_model_features.config.variable_explanation.entity_variable";
    }

    @Override
    public @Nullable String getTitleTranslationKey() {
        return "entity_model_features.config.variable_explanation.entity_variable.title";
    }
}
