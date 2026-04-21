package traben.entity_model_features.models.animation;

import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.animation.math.asm.ASMParser;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathExpressionParser;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.models.animation.math.variables.factories.GlobalVariableFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static traben.entity_model_features.models.animation.math.MathExpressionParser.NULL_EXPRESSION;
import static traben.entity_model_features.models.animation.math.MathValue.FALSE;

public class EMFAnimation {

    public final String animKey;
    public final String expressionString;
    public final String modelName;
    private final EMFModelPart partToApplyTo;
    private final EMFModelOrRenderVariable modelOrRenderVariableToChange;
    private final Map<UUID, Float> prevResult;
    private final Consumer<Float> variableResultConsumer;
    private final float defaultValue;
    @NotNull
    private MathComponent emfCalculator = NULL_EXPRESSION;

    private ASMParser.ASMExecutorFloat asmCalcFloat = null;
    private ASMParser.ASMExecutorBool asmCalcBool = null;
    private int asmVarIndexOfThis = -1;

    private final boolean isBoolean;
    private boolean isASM = false;

    public EMFAnimation(EMFModelPart partToApplyTo,
                        EMFModelOrRenderVariable modelOrRenderVariableToChange,
                        String animKey,
                        String initialExpression,
                        String modelName
    ) {
        this.modelName = modelName;
        this.animKey = animKey;
        boolean animKeyIsBoolean = (animKey.startsWith("global_varb") || animKey.startsWith("varb"));

        isBoolean = animKeyIsBoolean || (modelOrRenderVariableToChange != null && modelOrRenderVariableToChange.isBoolean());

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
        if (partToApplyTo != null){
            partToApplyTo.isSetByAnimation = true;
        }

        this.defaultValue = animKeyIsBoolean ||
                (modelOrRenderVariableToChange != null && modelOrRenderVariableToChange.isBoolean())
                ? FALSE : 0;

        prevResult = new HashMap<>() {
            @Override
            public Float get(Object key) {
                return super.getOrDefault(key, defaultValue);
            }
        };

        expressionString = initialExpression;
    }

    public boolean isVar() {
        return variableResultConsumer != null;
    }

    @Override
    public String toString() {
        return animKey;
    }


    public void initExpression(AnimSetupContext context, ASMVariableHandler asmVariableHandler) {
        context.animKey = animKey;
        emfCalculator = MathExpressionParser.getOptimizedExpression(expressionString, false, context);
        context.animKey = null;

        if (isValid() && EMF.config().getConfig().asmMaths) {
            if (isBoolean) asmCalcBool = ASMParser.compileBoolOrNull(emfCalculator, asmVariableHandler, modelName +"::"+animKey+"="+expressionString);
            else asmCalcFloat = ASMParser.compileFloatOrNull(emfCalculator, asmVariableHandler, modelName +"::"+animKey+"="+expressionString);
            // mark as written to as we won't know until the end if its read and write
            asmVarIndexOfThis = asmVariableHandler.getVarIndexFromOutsideParse(animKey, false, isBoolean);
            isASM = asmCalcBool != null || asmCalcFloat != null;
        }
    }


    public float getLastResultOnly() {
        if (EMFAnimationEntityContext.getEMFEntity() == null) {
            return defaultValue;
        }
        return prevResult.get(EMFAnimationEntityContext.getEMFEntity().etf$getUuid());

    }

    private float getResultViaCalculate(ASMVariableHandler.AnimVars vars) {
        UUID id = EMFAnimationEntityContext.getEMFEntity() == null ? null : EMFAnimationEntityContext.getEMFEntity().etf$getUuid();
        if (id == null) {
            return defaultValue;
        }

        float result = calculatorRun(vars);

        prevResult.put(id, result);
        return result;
    }

    private static Float altInfinity(float result) {
        // half of Float.MAX_VALUE
        return result > 0 ? 1.7014117E38f : -1.7014117E38f; // TODO temp while still using old maths bools
    }

    private boolean failedDuringRuntime = false;

    private float calculatorRun(ASMVariableHandler.AnimVars vars) {
        float result;
        try {
            if (isASM) {
                if (isBoolean) {
                    boolean bool = asmCalcBool.execute(vars.floats(), vars.bools());
                    vars.bools()[asmVarIndexOfThis] = bool;
                    result = MathValue.fromBoolean(bool);
                } else {
                    result = asmCalcFloat.execute(vars.floats(), vars.bools());
                    vars.floats()[asmVarIndexOfThis] = result;
                    if (Float.isInfinite(result))
                        result = altInfinity(result);
                }
            } else {
                result = emfCalculator.getResult();
            }
        } catch (Throwable t) {
            failedDuringRuntime = true;
            asmCalcFloat = null;
            asmCalcBool = null;
            emfCalculator = NULL_EXPRESSION;
            return defaultValue;
        }

        if (Float.isNaN(result) || Math.abs(result) == Float.MIN_VALUE) {
            return defaultValue;
        } else {
            return result;
        }
    }

    public void calculateAndSetIfNotPaused(@NotNull final ModelPart[] paused, ASMVariableHandler.AnimVars vars) {
        if (failedDuringRuntime) return;
        for (ModelPart part : paused) {
            if (partToApplyTo == part) return;
        }
        calculateAndSet(vars);
    }

    public void calculateAndSet(ASMVariableHandler.AnimVars vars) {
        if (failedDuringRuntime) return;
        if (EMFAnimationEntityContext.isLODSkippingThisFrame(modelName)) {
            if (!isVar()) handleResultNonVariable(getLastResultOnly());
        } else {
            calculateAndSetNotLod(vars);
        }
    }

    private void calculateAndSetNotLod(ASMVariableHandler.AnimVars vars) {
        if (isVar()) {
            variableResultConsumer.accept(getResultViaCalculate(vars));
        } else {
            handleResultNonVariable(getResultViaCalculate(vars));
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
        return emfCalculator != NULL_EXPRESSION;
    }


}
