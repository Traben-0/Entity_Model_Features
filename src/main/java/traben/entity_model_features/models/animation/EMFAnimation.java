package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.EMFGenericEntityEntityModel;
import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.animation.EMFAnimationMathParser.MathComponent;
import traben.entity_model_features.models.animation.EMFAnimationMathParser.MathExpressionParser;
import traben.entity_model_features.utils.EMFUtils;

import java.util.Random;
import java.util.UUID;

public class EMFAnimation {


    public int indentCount = 0;
    MathComponent EMFCalculator = MathExpressionParser.NULL_EXPRESSION;


    public EMFModelPart modelPart = null;
     public ModelPart vanillaModelPart = null;

     public final EMFGenericEntityEntityModel<?> parentModel;
     public final EMFDefaultModelVariable varToChange;
     public final String animKey;

     private final String expressionString;

     final float defaultValue;


    public EMFAnimation(EMFGenericEntityEntityModel<?> parent, ModelPart part, EMFDefaultModelVariable varToChange, String animKey, String initialExpression) {

        this.animKey = animKey;
        isVariable = animKey.startsWith("var");
        this.parentModel = parent;
        this.varToChange = varToChange;
        if (part instanceof EMFModelPart emf)
            this.modelPart = emf;
        else
            this.vanillaModelPart = part;

        if (varToChange != null) {
            //resultIsAngle = (varToChange == AnimationModelDefaultVariable.rx || varToChange == AnimationModelDefaultVariable.ry ||varToChange == AnimationModelDefaultVariable.rz);
            if (part == null) {
                System.out.println("null part for " + animKey + " in " + parentModel.modelPathIdentifier);
                defaultValue = 0;
            } else {
                defaultValue = varToChange.getDefaultFromModel(part);
            }
            if (this.modelPart != null)
                varToChange.setValueAsAnimated(this.modelPart);
        } else {
            defaultValue = 0;
        }
        prevResult.defaultReturnValue(defaultValue);
        //prevPrevResults.defaultReturnValue(defaultValue);

        expressionString = initialExpression;
    }
    public void initExpression(){
        EMFCalculator = MathExpressionParser.getOptimizedExpression(expressionString,false, this);
    }

    public final boolean isVariable;

    //private boolean resultIsAngle = false;
    public boolean verboseMode = false;

    public void setVerbose(boolean val) {
        verboseMode = val;
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

    public float getLastResultOnly(LivingEntity entity0){

        if(vanillaModelPart != null){
            return varToChange.getFromVanillaModel(vanillaModelPart);
        }
        if(entity0 == null) {
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) System.out.println("entity was null for getLastResultOnly, (okay for model init)");
            return 0;
        }

       return prevResult.getFloat(entity0.getUuid());

    }


    public float getResultViaCalculate(LivingEntity entity0, boolean storeResult){

        if(vanillaModelPart != null){
            return varToChange.getFromVanillaModel(vanillaModelPart);
        }
        if(entity0 == null) {
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) System.out.println("entity was null for getResultOnly, (okay for model init)");
            return 0;
        }


        float result = calculatorRun();

        //float oldResult = prevResults.getFloat(id);
            if(storeResult) {
                //prevPrevResults.put(id, oldResult);
                prevResult.put(entity0.getUuid(), result);
            }
            return result;
            //return oldResult;
    }

    public float getResultViaCalculate(LivingEntity entity0){
        return  getResultViaCalculate(entity0,true);
    }


    private final Random rand = new Random();

    //use float up at this level as minecraft uses it
    public float calculatorRun() {
//        try {
            if (EMFData.getInstance().getConfig().printAllMaths && rand.nextInt(100) == 1) {
                setVerbose(true);
                double val = EMFCalculator.get();
                System.out.println(EMFCalculator.toString() +" is "+ EMFCalculator.getClass());
                EMFUtils.EMF_modMessage("animation result: " + animKey + " = " + val);
                return (float) val;
            } else {
                return (float) EMFCalculator.get();
            }
//        }catch(MathComponent.EMFMathException e){
//            return Float.NaN;
//        }

    }



   // Object2FloatOpenHashMap<UUID> prevPrevResults = new Object2FloatOpenHashMap<>();
     public Object2FloatOpenHashMap<UUID> prevResult = new Object2FloatOpenHashMap<>();



    public void calculateAndSet(LivingEntity entity0){
        if (isVariable) {
            getResultViaCalculate(entity0);
        } else {
            handleResult(getResultViaCalculate(entity0));
        }
    }

    private void handleResult(float result){
        if(Float.isNaN(result)){
            if(varToChange != null)
                varToChange.set(modelPart, Float.MAX_VALUE);
        }else if(modelPart != null){
            varToChange.set(modelPart, result);
        }
    }

    public boolean isValid(){
        return EMFCalculator != MathExpressionParser.NULL_EXPRESSION;
    }

    public void animPrint(String str){
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < indentCount; i++) {
            indent.append("> ");
        }
        System.out.println(indent+ str);
    }


}
