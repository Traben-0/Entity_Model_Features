package traben.entity_model_features.models.animation.math.variables.factories;

import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.models.animation.math.expression_tree.MathConstant;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

import java.util.function.BooleanSupplier;

public class ModelPartVariableFactory extends UniqueVariableFactory {
    @Override
    public MathValue.ResultSupplier getSupplierOrNull(final String variableKey, AnimSetupContext context) {
        String[] split = variableKey.split("\\.");//todo only works with one split point
        String partName = split[0];
        EMFModelOrRenderVariable partVariable = EMFModelOrRenderVariable.get(split[1]);
        EMFModelPart part = EMFManager.getModelFromHierarchicalId(partName, context.allPartsBySingleAndFullHeirachicalId);
        if (partVariable != null) {
            if (part != null) {
                return () -> partVariable.getValue(part);
            } else {
                if (printing()) EMFUtils.logWarn("no part found for: [" + variableKey + "] in [" + context.modelName + "]. Available parts were: " + context.allPartsBySingleAndFullHeirachicalId.keySet());
                if (partVariable.isBoolean()) return MathConstant.FALSE_CONST::getResult;
                else return MathConstant.ZERO_CONST::getResult;
            }
        }

        //cheeky little thing for how I get large chests working from 1 jem file
        //todo possibly need the same in bed.jem???
        if (context.modelName.endsWith("chest_large.jem") && (partName.endsWith("_left") || partName.endsWith("_right"))) {
            //just silences the log spam when the left chest cant find the parts of the right side animation lines :/
            return MathConstant.ZERO_CONST::getResult;
        }

        if (printing()) EMFUtils.logWarn("no part found for: [" + variableKey + "] in [" + context.modelName + "]. Available parts were: " + context.allPartsBySingleAndFullHeirachicalId.keySet());
        return null;
    }


    @Override
    public @Nullable BooleanSupplier getASMBoolSupplierOrNull(String variableKey, AnimSetupContext context) {
        String[] split = variableKey.split("\\.");
        String partName = split[0];
        EMFModelOrRenderVariable partVariable = EMFModelOrRenderVariable.get(split[1]);
        EMFModelPart part = EMFManager.getModelFromHierarchicalId(partName, context.allPartsBySingleAndFullHeirachicalId);
        if (partVariable != null && partVariable.isBoolean()) {
            if (part != null) {
                return switch (partVariable) {
                    case VISIBLE -> ()-> part.visible;
                    case VISIBLE_BOXES -> ()-> !part.skipDraw;
                    default -> null;
                };
            } else {
                if (printing() && !(context.modelName.endsWith("chest_large.jem") && (partName.endsWith("_left") || partName.endsWith("_right"))))
                    EMFUtils.logWarn("no part found for: [" + variableKey + "] in [" + context.modelName + "]. Available parts were: " + context.allPartsBySingleAndFullHeirachicalId.keySet());
                return ()-> false;
            }
        }
        return null;
    }

    @Override
    public @Nullable MathValue.ResultSupplier getASMFloatSupplierOrNull(String variableKey, AnimSetupContext context) {
        String[] split = variableKey.split("\\.");
        String partName = split[0];
        EMFModelOrRenderVariable partVariable = EMFModelOrRenderVariable.get(split[1]);
        EMFModelPart part = EMFManager.getModelFromHierarchicalId(partName, context.allPartsBySingleAndFullHeirachicalId);
        if (partVariable != null && !partVariable.isBoolean()) {
            if (part != null) {
                return switch (partVariable) {
                    case TX -> ()-> part.x;
                    case TY -> ()-> part.y;
                    case TZ -> ()-> part.z;
                    case RX -> ()-> part.xRot;
                    case RY -> ()-> part.yRot;
                    case RZ -> ()-> part.zRot;
                    case SX -> ()-> part.xScale;
                    case SY -> ()-> part.yScale;
                    case SZ -> ()-> part.zScale;
                    default -> null;
                };
            } else {
                if (printing() && !(context.modelName.endsWith("chest_large.jem") && (partName.endsWith("_left") || partName.endsWith("_right"))))
                    EMFUtils.logWarn("no part found for: [" + variableKey + "] in [" + context.modelName + "]. Available parts were: " + context.allPartsBySingleAndFullHeirachicalId.keySet());
                return ()-> 0f;
            }
        }
        return null;
    }

    @Override
    public boolean createsThisVariable(final String variableKey) {
        if (variableKey == null) return false;
        String[] split = variableKey.split("\\.");//todo only works with one split point
        String partName = split[0];
        if ("render".equals(partName) && EMF.config().getConfig().enforceOptiFineAnimSyntaxLimits){
            // if it specifies .ty. or .rx. or .rz. or .sx. or .sy. or .sz. or .visible or .visible_boxes then log an error
            if (EMFModelOrRenderVariable.get(split[1]) != null){
                if (printing()) EMFUtils.logError("Model part variable [" + variableKey + "] is not allowed, 'render' is a protected animation key name.");
            }
            return false;
        }
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
