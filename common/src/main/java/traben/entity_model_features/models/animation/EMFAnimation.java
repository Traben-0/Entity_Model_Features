package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathExpressionParser;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.utils.EMFUtils;

import java.util.UUID;

public class EMFAnimation {

    public final String animKey;
    public final String expressionString;
    public final String modelName;
    public final boolean isVariable;
    private final EMFModelPart partToApplyTo;
    private final EMFModelOrRenderVariable variableToChange;
    private final Object2FloatOpenHashMap<UUID> prevResult = new Object2FloatOpenHashMap<>();
    private final Object2IntOpenHashMap<UUID> lodTimer = new Object2IntOpenHashMap<>();
    public Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimationVariables = null;
    public Object2ObjectOpenHashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId = null;
    private MathComponent EMFCalculator = MathExpressionParser.NULL_EXPRESSION;
    private EMFAnimation trueVariableToSet = null;

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

        float defaultValue;
        if (this.variableToChange != null) {
            if (partToApplyTo == null) {
                if (this.variableToChange.isRenderVariable()) {
                    defaultValue = this.variableToChange.getValue();
                } else {
                    if (EMFConfig.getConfig().logModelCreationData)
                        EMFUtils.log("null part for " + animKey);
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

    public void setTrueVariableSource(EMFAnimation trueVariableSource) {
        this.trueVariableToSet = trueVariableSource;
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
        result = result == Float.MIN_VALUE ? 0f : result;
        prevResult.put(id, result);
        return result;
    }


    private float calculatorRun() {
        return EMFCalculator.getResult();
    }

    private void sendValueToTrueVariable(float value) {
        if (EMFAnimationEntityContext.getEMFEntity() == null) return;
        UUID id = EMFAnimationEntityContext.getEMFEntity().etf$getUuid();
        prevResult.put(id, value);
    }

    public void calculateAndSet() {
        if (EMFConfig.getConfig().animationLODDistance == 0) {
            calculateAndSetPostLod();
            return;
        }
        int lodTimer = this.lodTimer.getInt(EMFAnimationEntityContext.getEMFEntity().etf$getUuid());
        int lodResult;
        //check lod
        if (lodTimer < 1) {
            lodResult = EMFAnimationEntityContext.getLODFactorOfEntity();
        } else {
            lodResult = lodTimer - 1;
        }
        this.lodTimer.put(EMFAnimationEntityContext.getEMFEntity().etf$getUuid(), lodResult);
        handleResult(lodResult > 0 ? getLastResultOnly() : getResultViaCalculate());
    }

    private void calculateAndSetPostLod() {
        if (isVariable) {
            if (trueVariableToSet != null) {
                trueVariableToSet.sendValueToTrueVariable(getResultViaCalculate());
            } else {
                getResultViaCalculate();
            }
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
