package traben.entity_model_features.models.animation.math.variables.factories;

import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.MathConstant;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.utils.EMFManager;

public class ModelPartVariableFactory extends UniqueVariableFactory {
    @Override
    public MathValue.ResultSupplier getSupplierOrNull(final String variableKey, final EMFAnimation calculationInstance) {
        String[] split = variableKey.split("\\.");//todo only works with one split point
        String partName = split[0];
        EMFModelOrRenderVariable partVariable = EMFModelOrRenderVariable.get(split[1]);
        EMFModelPart part = EMFManager.getModelFromHierarchichalId(partName, calculationInstance.allPartsBySingleAndFullHeirachicalId);
        if (partVariable != null && part != null) {
            return () -> partVariable.getValue(part);
        }

        //cheeky little thing for how I get large chests working from 1 jem file
        //todo possibly need the same in bed.jem???
        if (calculationInstance.modelName.endsWith("chest_large.jem") && (partName.endsWith("_left") || partName.endsWith("_right"))) {
            //just silences the log spam when the left chest cant find the parts of the right side animation lines :/
            return MathConstant.ZERO.getResultSupplier();
        }

        //EMFUtils.logWarn("no part found for: [" + variableKey + "] in [" + calculationInstance.modelName + "]. Available parts were: " + calculationInstance.allPartsBySingleAndFullHeirachicalId.keySet());
        return null;
    }

    @Override
    public boolean createsThisVariable(final String variableKey) {
        if (variableKey == null) return false;
        return variableKey.matches("[a-zA-Z0-9_]+\\.([trs][xyz]$|visible$|visible_boxes$)");
    }
}
