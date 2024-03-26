package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathExpressionParser;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.models.animation.math.variables.factories.GlobalVariableFactory;
import traben.entity_model_features.utils.EMFUtils;

import java.util.UUID;

public class EMFAnimation {

    public final String animKey;
    public final String expressionString;
    public final String modelName;
    public final boolean isVariable;
    private final EMFModelPart partToApplyTo;
    private final EMFModelOrRenderVariable modelOrRenderVariableToChange;
    private final Object2FloatOpenHashMap<UUID> prevResult = new Object2FloatOpenHashMap<>();

    public Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimationVariables = null;
    public Object2ObjectOpenHashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId = null;
    private MathComponent EMFCalculator = MathExpressionParser.NULL_EXPRESSION;
    private FloatConsumer handleVariableResult = null;

    public EMFAnimation(EMFModelPart partToApplyTo,
                        EMFModelOrRenderVariable modelOrRenderVariableToChange,
                        String animKey,
                        String initialExpression,
                        String modelName
    ) {
        this.modelName = modelName;
        this.animKey = animKey;
        isVariable = animKey.startsWith("var") || animKey.startsWith("global_var");
        if (isVariable) {
            handleVariableResult = animKey.startsWith("global_var") ?
                    value -> GlobalVariableFactory.setGlobalVariable(animKey, value)
            :       value -> EMFAnimationEntityContext.setEntityVariable(animKey, value);
        }
        this.modelOrRenderVariableToChange = isVariable ? null : modelOrRenderVariableToChange;
        this.partToApplyTo = partToApplyTo;

        float defaultValue;
        if (this.modelOrRenderVariableToChange != null) {
            if (partToApplyTo == null) {
                if (this.modelOrRenderVariableToChange.isRenderVariable()) {
                    defaultValue = this.modelOrRenderVariableToChange.getValue();
                } else {
                    if (EMF.config().getConfig().logModelCreationData)
                        EMFUtils.log("null part for " + animKey);
                    defaultValue = 0;
                }
            } else {
                defaultValue = this.modelOrRenderVariableToChange.getValue(partToApplyTo);
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
        if (EMFAnimationEntityContext.getEMFEntity() == null) {
            return 0;
        }
        return prevResult.getFloat(EMFAnimationEntityContext.getEMFEntity().etf$getUuid());

    }

    public float getResultViaCalculate() {
        UUID id = EMFAnimationEntityContext.getEMFEntity() == null ? null : EMFAnimationEntityContext.getEMFEntity().etf$getUuid();
        if (id == null) {
            return 0;
        }

        float result = calculatorRun();
        if (Float.isNaN(result) || result == Float.MIN_VALUE) {
            prevResult.put(id, 0);
            return 0;
        } else {
            prevResult.put(id, result);
            return result;
        }
    }


    private float calculatorRun() {
        return EMFCalculator.getResult();
    }


    public void calculateAndSet() {
        if (EMFAnimationEntityContext.isLODSkippingThisFrame()) {
            handleResult(getLastResultOnly());
        } else {
            calculateAndSetPostLod();
        }
    }

    private void calculateAndSetPostLod() {
        if (isVariable) {
            if (handleVariableResult != null) {
                handleVariableResult.accept(getResultViaCalculate());
            } else {
                EMFUtils.logError(animKey + ": variable did not have result handler in: " + modelName);
               // getResultViaCalculate();
            }
        } else {
            handleResult(getResultViaCalculate());
        }
    }

    private void handleResult(float result) {
        //if(animKey.equals("left_rein2.visible")) System.out.println("result rein "+result+varToChange);
        if (modelOrRenderVariableToChange != null) {
            if (Float.isNaN(result)) {
                modelOrRenderVariableToChange.setValue(partToApplyTo, Float.MAX_VALUE);
            } else {
                modelOrRenderVariableToChange.setValue(partToApplyTo, result);
            }
        }
    }

    public boolean isValid() {
        return EMFCalculator != MathExpressionParser.NULL_EXPRESSION;
    }


}
