package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.EMFModelPart3;
import traben.entity_model_features.models.animation.animation_math_parser.MathComponent;
import traben.entity_model_features.models.animation.animation_math_parser.MathExpressionParser;
import traben.entity_model_features.utils.EMFUtils;

import java.util.Random;
import java.util.UUID;

public class EMFAnimation {

    public final EMFModelPart3 partToApplyTo;
    public final EMFDefaultModelVariable variableToChange;
    public final String animKey;
    public final String expressionString;
    public final String modelName;
    public final EMFAnimationVariableSuppliers variableSuppliers;
    public final boolean isVariable;
    final float defaultValue;
    private final Random rand = new Random();
    public int indentCount = 0;
    public Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimationVariables = null;
    public Object2ObjectOpenHashMap<String, EMFModelPart3> allPartByName = null;
    //private boolean resultIsAngle = false;
    public boolean verboseMode = false;
    // Object2FloatOpenHashMap<UUID> prevPrevResults = new Object2FloatOpenHashMap<>();
    public final Object2FloatOpenHashMap<UUID> prevResult = new Object2FloatOpenHashMap<>();
    MathComponent EMFCalculator = MathExpressionParser.NULL_EXPRESSION;

    public EMFAnimation(EMFModelPart3 partToApplyTo,
                        EMFDefaultModelVariable variableToChange,
                        String animKey,
                        String initialExpression,
                        String modelName,
                        EMFAnimationVariableSuppliers variableSuppliers) {
        this.variableSuppliers = variableSuppliers;
        this.modelName = modelName;
        this.animKey = animKey;
        isVariable = animKey.startsWith("var");
        this.variableToChange = variableToChange;
        this.partToApplyTo = partToApplyTo;

        if (variableToChange != null) {
            //resultIsAngle = (varToChange == AnimationModelDefaultVariable.rx || varToChange == AnimationModelDefaultVariable.ry ||varToChange == AnimationModelDefaultVariable.rz);
            if (partToApplyTo == null) {
                System.out.println("null part for " + animKey);
                defaultValue = 0;
            } else {
                defaultValue = variableToChange.getDefaultFromModel(partToApplyTo);
            }
        } else {
            defaultValue = 0;
        }
        prevResult.defaultReturnValue(defaultValue);
        //prevPrevResults.defaultReturnValue(defaultValue);

        expressionString = initialExpression;
    }

    @Override
    public String toString() {
        return animKey;
    }


//    public float getResultInterpolateOnly(LivingEntity entity0){
//        if(vanillaModelPart != null){
//            return varToChange.getFromVanillaModel(vanillaModelPart);
//        }
//        if(entity0 == null) {
//            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) System.out.println("entity was null for getResultOnly, (okay for model init)");
//            return 0;
//        }
//
//        UUID id = entity0.getUuid();
//        if(resultIsAngle){
//            return MathHelper.lerpAngleDegrees(parentModel.currentAnimationDeltaForThisTick,prevPrevResults.getFloat(id), prevResults.getFloat(id));
//        }
//        return MathHelper.lerp(parentModel.currentAnimationDeltaForThisTick,prevPrevResults.getFloat(id), prevResults.getFloat(id));
//
//    }

    public void initExpression(Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimationVariables,
                               Object2ObjectOpenHashMap<String, EMFModelPart3> allPartByName) {
        this.emfAnimationVariables = emfAnimationVariables;
        this.allPartByName = allPartByName;
        EMFCalculator = MathExpressionParser.getOptimizedExpression(expressionString, false, this);
        this.emfAnimationVariables = null;
        this.allPartByName = null;
    }

    public void setVerbose(boolean val) {
        verboseMode = val;
    }

    public float getLastResultOnly(Entity entity0) {


        if (entity0 == null) {
            // if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) System.out.println("entity was null for getLastResultOnly, (okay for model init)");
            return 0;
        }

        return prevResult.getFloat(entity0.getUuid());

    }

    public float getResultViaCalculate(Entity entity0, boolean storeResult) {

        if (entity0 == null) {
            // if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) System.out.println("entity was null for getResultOnly, (okay for model init)");
            return 0;
        }


        float result = calculatorRun();

        //float oldResult = prevResults.getFloat(id);
        if (storeResult) {
            //prevPrevResults.put(id, oldResult);
            prevResult.put(entity0.getUuid(), result);
        }
        return result;
        //return oldResult;
    }

    public float getResultViaCalculate(Entity entity0) {
        return getResultViaCalculate(entity0, true);
    }

    //use float up at this level as minecraft uses it
    public float calculatorRun() {
//        try {
        if (
                EMFConfig.getConfig().printAllMaths &&
                        //  animKey.equals("var.pounce") &&
                        rand.nextInt(100) == 1) {
            setVerbose(true);

            //  System.out.println("vanilla body.rx ="+ parentModel.getAnimationResultOfKeyAsSupplier(null, "body.rx").get(entity0));
            double val = EMFCalculator.get();
            System.out.println(EMFCalculator.toString() + " is " + EMFCalculator.getClass());
            EMFUtils.EMFModMessage("animation result: " + animKey + " = " + val);
            return (float) val;
        } else {
            return EMFCalculator.get();
        }
//        }catch(MathComponent.EMFMathException e){
//            return Float.NaN;
//        }

    }

    public void calculateAndSet(Entity entity0) {
        //if(animKey.equals("var.potion")) System.out.println("potion "+getResultViaCalculate(entity0));
        if (isVariable) {
            getResultViaCalculate(entity0);
        } else {
            handleResult(getResultViaCalculate(entity0));
        }
    }

    private void handleResult(float result) {
        //if(animKey.equals("left_rein2.visible")) System.out.println("result rein "+result+varToChange);
        if (Double.isNaN(result)) {
            if (variableToChange != null)
                variableToChange.setValueIn3Model(partToApplyTo, Float.MAX_VALUE);
        } else if (partToApplyTo != null) {
            variableToChange.setValueIn3Model(partToApplyTo, result);
        }
    }

    public boolean isValid() {
        return EMFCalculator != MathExpressionParser.NULL_EXPRESSION;
    }

    public void animPrint(String str) {
        StringBuilder indent = new StringBuilder();
        indent.append("> ".repeat(Math.max(0, indentCount)));
        System.out.println(indent + str);
    }


}
