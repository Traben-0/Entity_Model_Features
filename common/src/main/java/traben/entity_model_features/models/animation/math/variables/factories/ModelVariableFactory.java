package traben.entity_model_features.models.animation.math.variables.factories;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.MathValue;

public class ModelVariableFactory extends UniqueVariableFactory {
    @Override
    public MathValue.ResultSupplier getSupplierOrNull(final String variableKey, final EMFAnimation calculationInstance) {
        EMFAnimation variableCalculator = calculationInstance.emfAnimationVariables.get(variableKey);
        if (variableCalculator != null) {
            return variableCalculator::getLastResultOnly;
        }
        return null;
    }

    @Override
    public boolean createsThisVariable(final String variableKey) {
        if (variableKey == null) return false;
        return variableKey.matches("(var|varb)\\.\\w+");
    }
}
