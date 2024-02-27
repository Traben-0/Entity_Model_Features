package traben.entity_model_features.models.animation.animation_math_parser.variables.factories;

import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFModelOrRenderVariable;
import traben.entity_model_features.models.animation.animation_math_parser.MathValue;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

public class ModelPartVariableFactory extends ContextVariableFactory{
    @Override
    public MathValue.ResultSupplier getSupplierOrNull(final String variableKey, final EMFAnimation calculationInstance) {
        String[] split = variableKey.split("\\.");//todo only works with one split point
        String partName = split[0];
        EMFModelOrRenderVariable partVariable = EMFModelOrRenderVariable.get(split[1]);
        EMFModelPart part = EMFManager.getModelFromHierarchichalId(partName, calculationInstance.allPartsBySingleAndFullHeirachicalId);
        if (partVariable != null && part != null) {
            return () -> partVariable.getValue(part);
        }
        EMFUtils.logWarn("no part found for: [" + variableKey + "] in [" + calculationInstance.modelName + "]. Available parts were: " + calculationInstance.allPartsBySingleAndFullHeirachicalId.keySet());
        return null;
    }

    @Override
    public boolean createsThisVariable(final String variableKey) {
        if (variableKey == null) return false;
        return variableKey.matches("[a-zA-Z0-9_]+\\.([trs][xyz]$|visible$|visible_boxes$)");
    }
}
