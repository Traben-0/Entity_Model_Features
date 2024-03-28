package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathExpressionParser;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.models.animation.math.variables.factories.GlobalVariableFactory;

import java.util.UUID;

import static traben.entity_model_features.models.animation.math.MathValue.FALSE;

public class EMFAnimation {

    public final String animKey;
    public final String expressionString;
    public final String modelName;
    public boolean isVariable(){
        return handleVariableResult != null;
    }
    private final EMFModelPart partToApplyTo;
    private final EMFModelOrRenderVariable modelOrRenderVariableToChange;
    private final Object2FloatOpenHashMap<UUID> prevResult = new Object2FloatOpenHashMap<>();

    public Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimationVariables = null;
    public Object2ObjectOpenHashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId = null;
    private MathComponent emfCalculator = MathExpressionParser.NULL_EXPRESSION;
    private final FloatConsumer handleVariableResult;

    public EMFAnimation(EMFModelPart partToApplyTo,
                        EMFModelOrRenderVariable modelOrRenderVariableToChange,
                        String animKey,
                        String initialExpression,
                        String modelName
    ) {
        this.modelName = modelName;
        this.animKey = animKey;
        boolean animKeyIsBoolean = (animKey.startsWith("global_varb") || animKey.startsWith("varb"));

        if (animKey.startsWith("global_var")){
            //global
            if (animKeyIsBoolean){
                //boolean
                handleVariableResult = value -> GlobalVariableFactory.setGlobalVariable(animKey, Float.isInfinite(value) ? value : FALSE);
            }else{
                //float
                handleVariableResult = value -> GlobalVariableFactory.setGlobalVariable(animKey, Float.isInfinite(value) ? 0 : value);
            }
        }else if (animKey.startsWith("var")){
            //entity
            if (animKeyIsBoolean){
                //boolean
                handleVariableResult = value -> EMFAnimationEntityContext.setEntityVariable(animKey, Float.isInfinite(value) ? value : FALSE);
            }else{
                //float
                handleVariableResult = value -> EMFAnimationEntityContext.setEntityVariable(animKey, Float.isInfinite(value) ? 0 : value);
            }
        } else {
            handleVariableResult = null;
        }

        this.modelOrRenderVariableToChange = isVariable() ? null : modelOrRenderVariableToChange;
        this.partToApplyTo = partToApplyTo;

        float defaultValue;
//        if (this.modelOrRenderVariableToChange != null) {
//            if (partToApplyTo == null) {
//                if (this.modelOrRenderVariableToChange.isRenderVariable()) {
//                    defaultValue = this.modelOrRenderVariableToChange.getValue();
//                } else {
//                    if (EMF.config().getConfig().logModelCreationData)
//                        EMFUtils.log("null part for " + animKey);
//                    defaultValue = 0;
//                }
//            } else {
//                defaultValue = this.modelOrRenderVariableToChange.getValue(partToApplyTo);
//            }
//        } else {
            defaultValue = animKeyIsBoolean ||
                    (modelOrRenderVariableToChange != null && modelOrRenderVariableToChange.isBoolean())
                    ? FALSE : 0;
//        }
        this.defaultValue = defaultValue;
        prevResult.defaultReturnValue(this.defaultValue);
        expressionString = initialExpression;
    }

    private final float defaultValue;


    @Override
    public String toString() {
        return animKey;
    }


    public void initExpression(Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimationVariables,
                               Object2ObjectOpenHashMap<String, EMFModelPart> allPartByName) {
        this.emfAnimationVariables = emfAnimationVariables;
        this.allPartsBySingleAndFullHeirachicalId = allPartByName;
        emfCalculator = MathExpressionParser.getOptimizedExpression(expressionString, false, this);
        this.emfAnimationVariables = null;
        this.allPartsBySingleAndFullHeirachicalId = null;
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
                handleResultNonVariable(getLastResultOnly());
            } else {
                calculateAndSetPostLod();
            }
    }

    private void calculateAndSetPostLod() {
        if (isVariable()) {
            handleVariableResult.accept(getResultViaCalculate());
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
