package traben.entity_model_features.models.animation.animation_math_parser.variables.factories;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.animation_math_parser.MathValue;
import traben.entity_model_features.utils.EMFUtils;

import java.util.ArrayList;

public class ModelVariableFactory extends ContextVariableFactory{
    @Override
    public MathValue.ResultSupplier getSupplierOrNull(final String variableKey, final EMFAnimation calculationInstance) {
        EMFAnimation variableCalculator = calculationInstance.emfAnimationVariables.get(variableKey);
        if (variableCalculator != null) {
            return variableCalculator::getLastResultOnly;
        }
        ArrayList<String> vars = new ArrayList<>();
        for (String var : calculationInstance.emfAnimationVariables.keySet()) {
            if (var.startsWith("var.") || var.startsWith("varb.")) vars.add(var);
        }
        EMFUtils.logWarn("no animation variable found for: [" + variableKey + "] in [" + calculationInstance.modelName + "]. Available variables were: " + vars);
        return null;
    }

    @Override
    public boolean createsThisVariable(final String variableKey) {
        if (variableKey == null) return false;
        return variableKey.matches("(var|varb)\\.\\w+");
    }
}
