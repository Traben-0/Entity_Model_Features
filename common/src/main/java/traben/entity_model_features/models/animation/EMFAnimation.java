package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathExpressionParser;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.models.animation.math.variables.factories.GlobalVariableFactory;

import java.util.UUID;

import static traben.entity_model_features.models.animation.math.MathValue.FALSE;

public class EMFAnimation {

    public final String animKey;
    public final String expressionString;
    public final String modelName;
    private final EMFModelPart partToApplyTo;
    private final EMFModelOrRenderVariable modelOrRenderVariableToChange;
    private final Object2FloatOpenHashMap<UUID> prevResult = new Object2FloatOpenHashMap<>();
    private final FloatConsumer variableResultConsumer;
    private final float defaultValue;
    public Object2ObjectLinkedOpenHashMap<String, EMFAnimation> temp_emfAnimationVariables = null;
    public Object2ObjectOpenHashMap<String, EMFModelPart> temp_allPartsBySingleAndFullHeirachicalId = null;
    @NotNull
    private MathComponent emfCalculator = MathExpressionParser.NULL_EXPRESSION;

    public EMFAnimation(EMFModelPart partToApplyTo,
                        EMFModelOrRenderVariable modelOrRenderVariableToChange,
                        String animKey,
                        String initialExpression,
                        String modelName
    ) {
        this.modelName = modelName;
        this.animKey = animKey;
        boolean animKeyIsBoolean = (animKey.startsWith("global_varb") || animKey.startsWith("varb"));

        if (animKey.startsWith("global_var")) {
            //global
            if (animKeyIsBoolean) {
                //boolean
                variableResultConsumer = value -> GlobalVariableFactory.setGlobalVariable(animKey,
                        MathValue.isBoolean(value) ? value : FALSE);
            } else {
                //float
                variableResultConsumer = value -> GlobalVariableFactory.setGlobalVariable(animKey,
                        MathValue.isBoolean(value) ? 0 : value);
            }
        } else if (animKey.startsWith("var")) {
            //entity
            if (animKeyIsBoolean) {
                //boolean
                variableResultConsumer = value -> EMFAnimationEntityContext.setEntityVariable(animKey,
                        MathValue.isBoolean(value) ? value : FALSE);
            } else {
                //float
                variableResultConsumer = value -> EMFAnimationEntityContext.setEntityVariable(animKey,
                        MathValue.isBoolean(value) ? 0 : value);
            }
        } else {
            variableResultConsumer = null;
        }

        this.modelOrRenderVariableToChange = isVar() ? null : modelOrRenderVariableToChange;
        this.partToApplyTo = partToApplyTo;

        this.defaultValue = animKeyIsBoolean ||
                (modelOrRenderVariableToChange != null && modelOrRenderVariableToChange.isBoolean())
                ? FALSE : 0;
        prevResult.defaultReturnValue(this.defaultValue);
        expressionString = initialExpression;
    }

    public boolean isVar() {
        return variableResultConsumer != null;
    }

    @Override
    public String toString() {
        return animKey;
    }


    public void initExpression(Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimationVariables,
                               Object2ObjectOpenHashMap<String, EMFModelPart> allPartByName) {
        this.temp_emfAnimationVariables = emfAnimationVariables;
        this.temp_allPartsBySingleAndFullHeirachicalId = allPartByName;
        emfCalculator = MathExpressionParser.getOptimizedExpression(expressionString, false, this);
        this.temp_emfAnimationVariables = null;
        this.temp_allPartsBySingleAndFullHeirachicalId = null;
    }


    public float getLastResultOnly() {
        if (EMFAnimationEntityContext.getEMFEntity() == null) {
            return defaultValue;
        }
        return prevResult.getFloat(EMFAnimationEntityContext.getEMFEntity().etf$getUuid());

    }

    public float getResultViaCalculate() {
        UUID id = EMFAnimationEntityContext.getEMFEntity() == null ? null : EMFAnimationEntityContext.getEMFEntity().etf$getUuid();
        if (id == null) {
            return defaultValue;
        }

        float result = calculatorRun();

        prevResult.put(id, result);
        return result;
    }


    private float calculatorRun() {
        float result = emfCalculator.getResult();
        if (Float.isNaN(result) || Math.abs(result) == Float.MIN_VALUE) {
            return defaultValue;
        } else {
            return result;
        }
    }


    public void calculateAndSet() {
        if (EMFAnimationEntityContext.isLODSkippingThisFrame()) {
            if (!isVar()) handleResultNonVariable(getLastResultOnly());
        } else {
            calculateAndSetNotLod();
        }
    }

    private void calculateAndSetNotLod() {
        if (isVar()) {
            variableResultConsumer.accept(getResultViaCalculate());
        } else {
            handleResultNonVariable(getResultViaCalculate());
        }
    }

    private void handleResultNonVariable(float result) {
        if (modelOrRenderVariableToChange != null) {
            //todo potentially could validate boolean and non boolean values here, but that could represent a performance hit
            //modelOrRenderVariableToChange.isBoolean()... etc
            modelOrRenderVariableToChange.setValue(partToApplyTo, result);
        }
    }

    public boolean isValid() {
        return emfCalculator != MathExpressionParser.NULL_EXPRESSION;
    }


}
