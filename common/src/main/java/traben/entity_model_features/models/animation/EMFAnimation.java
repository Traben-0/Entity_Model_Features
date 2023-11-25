package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.animation.animation_math_parser.MathComponent;
import traben.entity_model_features.models.animation.animation_math_parser.MathExpressionParser;

import java.util.UUID;

public class EMFAnimation {

    public final EMFModelPart partToApplyTo;
    public final EMFModelOrRenderVariable variableToChange;
    public final String animKey;
    public final String expressionString;
    public final String modelName;
    public final boolean isVariable;
    public final Object2FloatOpenHashMap<UUID> prevResult = new Object2FloatOpenHashMap<>();
    final float defaultValue;
    public Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimationVariables = null;
    public Object2ObjectOpenHashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId = null;
    MathComponent EMFCalculator = MathExpressionParser.NULL_EXPRESSION;

    public EMFAnimation(EMFModelPart partToApplyTo,
                        EMFModelOrRenderVariable variableToChange,
                        String animKey,
                        String initialExpression,
                        String modelName
    ) {
        this.modelName = modelName;
        this.animKey = animKey;
        isVariable = animKey.startsWith("var");
        this.variableToChange = isVariable ? null : variableToChange;
        this.partToApplyTo = partToApplyTo;

        if (this.variableToChange != null) {
            if (partToApplyTo == null) {
                if (this.variableToChange.isRenderVariable()) {
                    defaultValue = this.variableToChange.getValue();
                } else {
                    if (EMFConfig.getConfig().logModelCreationData)
                        System.out.println("null part for " + animKey);
                    defaultValue = 0;
                }
            } else {
                defaultValue = this.variableToChange.getValue(partToApplyTo);
            }
        } else {
            defaultValue = 0;
        }
        prevResult.defaultReturnValue(defaultValue);
        expressionString = initialExpression;
    }

    @Override
    public String toString() {
        return animKey;
    }


    public void initExpression(Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimationVariables,
                               Object2ObjectOpenHashMap<String, EMFModelPart> allPartByName) {
        this.emfAnimationVariables = emfAnimationVariables;
        this.allPartsBySingleAndFullHeirachicalId = allPartByName;
        EMFCalculator = MathExpressionParser.getOptimizedExpression(expressionString, false, this);
        this.emfAnimationVariables = null;
        this.allPartsBySingleAndFullHeirachicalId = null;
    }


    public float getLastResultOnly() {
        if (EMFAnimationHelper.getEMFEntity() == null) {
            return 0;
        }
        return prevResult.getFloat(EMFAnimationHelper.getEMFEntity().etf$getUuid());

    }

    public float getResultViaCalculate() {
        UUID id = EMFAnimationHelper.getEMFEntity() == null ? null : EMFAnimationHelper.getEMFEntity().etf$getUuid();
        if (id == null) {
            return 0;
        }

        float result = calculatorRun();
        result = result == Float.MIN_VALUE ? 0f : result;
        prevResult.put(id, result);
        return result;
    }


    public float calculatorRun() {
        return EMFCalculator.get();
    }

    public void calculateAndSet() {
        if (isVariable) {
            getResultViaCalculate();
        } else {
            handleResult(getResultViaCalculate());
        }
    }

    private void handleResult(float result) {
        //if(animKey.equals("left_rein2.visible")) System.out.println("result rein "+result+varToChange);
        if (variableToChange != null) {
            if (Double.isNaN(result)) {
                variableToChange.setValue(partToApplyTo, Float.MAX_VALUE);
            } else {
                variableToChange.setValue(partToApplyTo, result);
            }
        }
    }

    public boolean isValid() {
        return EMFCalculator != MathExpressionParser.NULL_EXPRESSION;
    }


}
