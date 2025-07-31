package traben.entity_model_features.models.animation.math.variables.factories;

import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.MathConstant;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

public class ModelPartVariableFactory extends UniqueVariableFactory {
    @Override
    public MathValue.ResultSupplier getSupplierOrNull(final String variableKey, final EMFAnimation calculationInstance) {
        String[] split = variableKey.split("\\.");//todo only works with one split point
        String partName = split[0];
        if ("render".equals(partName) && EMF.config().getConfig().enforceOptiFineAnimSyntaxLimits){
            //silently skip so render variable factory can read it
            //unless it specifies .ty. or .rx. or .rz. or .sx. or .sy. or .sz. or .visible or .visible_boxes then log an error
            if (EMFModelOrRenderVariable.get(split[1]) != null){
                EMFUtils.logError("Model part variable [" + variableKey + "] is not allowed, 'render' is a protected animation key name.");
            }
            return null;
        }
        EMFModelOrRenderVariable partVariable = EMFModelOrRenderVariable.get(split[1]);
        EMFModelPart part = EMFManager.getModelFromHierarchichalId(partName, calculationInstance.temp_allPartsBySingleAndFullHeirachicalId);
        if (partVariable != null && part != null) {
            return () -> partVariable.getValue(part);
        }

        //cheeky little thing for how I get large chests working from 1 jem file
        //todo possibly need the same in bed.jem???
        if (calculationInstance.modelName.endsWith("chest_large.jem") && (partName.endsWith("_left") || partName.endsWith("_right"))) {
            //just silences the log spam when the left chest cant find the parts of the right side animation lines :/
            return MathConstant.ZERO_CONST::getResult;
        }

        EMFUtils.logWarn("no part found for: [" + variableKey + "] in [" + calculationInstance.modelName + "]. Available parts were: " + calculationInstance.temp_allPartsBySingleAndFullHeirachicalId.keySet());
        return null;
    }

    @Override
    public boolean createsThisVariable(final String variableKey) {
        if (variableKey == null) return false;
        return variableKey.matches("[a-zA-Z0-9_]+\\.([trs][xyz]$|visible$|visible_boxes$)");
    }

    @Override
    public @Nullable String getExplanationTranslationKey() {
        return "entity_model_features.config.variable_explanation.model_part";
    }

    @Override
    public @Nullable String getTitleTranslationKey() {
        return "entity_model_features.config.variable_explanation.model_part.title";
    }
}
