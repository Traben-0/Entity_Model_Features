package traben.entity_model_features.models.anim;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.EMF_ModelPart;
import traben.entity_model_features.models.anim.EMFParser.MathComponent;
import traben.entity_model_features.models.anim.EMFParser.MathExpression;
import traben.entity_model_features.utils.EMFUtils;

import java.util.UUID;

public class AnimationCalculation {


    public int indentCount = 0;
    MathComponent EMFCalculator;


    public EMF_ModelPart modelPart = null;
     public ModelPart vanillaModelPart = null;

     public final EMF_EntityModel<?> parentModel;
     public final AnimationModelDefaultVariable varToChange;
     public final String animKey;


     final float defaultValue;


    public AnimationCalculation(EMF_EntityModel<?> parent, ModelPart part, AnimationModelDefaultVariable varToChange, String animKey, String initialExpression) {

        this.animKey = animKey;
        isVariable =animKey.startsWith("var");
        this.parentModel = parent;
        this.varToChange = varToChange;
        if(part instanceof EMF_ModelPart emf)
            this.modelPart = emf;
        else
            this.vanillaModelPart = part;

        if(varToChange != null) {
            resultIsAngle = (varToChange == AnimationModelDefaultVariable.rx || varToChange == AnimationModelDefaultVariable.ry ||varToChange == AnimationModelDefaultVariable.rz);
            if(part == null){
                System.out.println("null part for "+ animKey+" in "+ parentModel.modelPathIdentifier);
                defaultValue = 0;
            }else {
                defaultValue = varToChange.getDefaultFromModel(part);
            }
            if(this.modelPart != null)
                varToChange.setValueAsAnimated(this.modelPart);
        } else {
            defaultValue = 0;
        }
        prevResults.defaultReturnValue(defaultValue);
        prevPrevResults.defaultReturnValue(defaultValue);

        EMFCalculator = MathExpression.getOptimizedExpression(initialExpression,false, this);


    }

    public final boolean isVariable;

    private boolean resultIsAngle = false;
    public boolean verboseMode = false;

    public void setVerbose(boolean val) {
        verboseMode = val;
    }





    public float getResultInterpolateOnly(LivingEntity entity0){
        if(vanillaModelPart != null){
            return varToChange.getFromVanillaModel(vanillaModelPart);
        }
        if(entity0 == null) {
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) System.out.println("entity was null for getResultOnly, (okay for model init)");
            return 0;
        }

        UUID id = entity0.getUuid();
        if(resultIsAngle){
            return MathHelper.lerpAngleDegrees(parentModel.currentAnimationDeltaForThisTick,prevPrevResults.getFloat(id), prevResults.getFloat(id));
        }
        return MathHelper.lerp(parentModel.currentAnimationDeltaForThisTick,prevPrevResults.getFloat(id), prevResults.getFloat(id));

    }

    public float getLastResultOnly(LivingEntity entity0){

        if(vanillaModelPart != null){
            return varToChange.getFromVanillaModel(vanillaModelPart);
        }
        if(entity0 == null) {
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) System.out.println("entity was null for getLastResultOnly, (okay for model init)");
            return 0;
        }

       return prevResults.getFloat(entity0.getUuid());

    }

    public final AnimationGetters animationGetters = new AnimationGetters();
    public float getResultViaCalculate(LivingEntity entity0, float limbAngle0, float limbDistance0,
                                       float animationProgress0, float headYaw0, float headPitch0, float tickDelta0, boolean storeResult){

        if(vanillaModelPart != null){
            return varToChange.getFromVanillaModel(vanillaModelPart);
        }
        if(entity0 == null) {
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) System.out.println("entity was null for getResultOnly, (okay for model init)");
            return 0;
        }

        UUID id = entity0.getUuid();

        animationGetters.entity = entity0;
        animationGetters.limbAngle = limbAngle0;
        animationGetters.limbDistance = limbDistance0;
        animationGetters.animationProgress = animationProgress0;
        animationGetters.headYaw = headYaw0;
        animationGetters.headPitch = headPitch0;
        animationGetters.tickDelta = tickDelta0;
        animationGetters.riding = parentModel.riding;

        animationGetters.child = parentModel.child;
            float result = calculatorRun();

            float oldResult = prevResults.getFloat(id);
            if(storeResult) {
                prevPrevResults.put(id, oldResult);
                prevResults.put(id, result);
            }
            return oldResult;
    }

    public float getResultViaCalculate(LivingEntity entity0, float limbAngle0, float limbDistance0,
                                       float animationProgress0, float headYaw0, float headPitch0, float tickDelta0){
        return  getResultViaCalculate(entity0, limbAngle0, limbDistance0, animationProgress0, headYaw0, headPitch0, tickDelta0,true);
    }



    public float calculatorRun() {
//        try {
            if (EMFData.getInstance().getConfig().printAllMaths) {
                setVerbose(true);
                float val = EMFCalculator.get();
                EMFUtils.EMF_modMessage("animation result: " + animKey + " = " + val);
                return val;
            } else {
                return EMFCalculator.get();
            }
//        }catch(MathComponent.EMFMathException e){
//            return Float.NaN;
//        }

    }



    Object2FloatOpenHashMap<UUID> prevPrevResults = new Object2FloatOpenHashMap<>();
     public Object2FloatOpenHashMap<UUID> prevResults = new Object2FloatOpenHashMap<>();



    public void calculateAndSet(LivingEntity entity0, float limbAngle0, float limbDistance0, float animationProgress0, float headYaw0, float headPitch0, float tickDelta0){
        if (parentModel.calculateForThisAnimationTick) {
            handleResult(getResultViaCalculate(entity0,  limbAngle0,  limbDistance0,  animationProgress0,  headYaw0,  headPitch0,  tickDelta0));
        }else if (!isVariable){
            handleResult(getResultInterpolateOnly(entity0));
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
        return EMFCalculator != MathExpression.NULL_EXPRESSION;
    }

    public void animPrint(String str){
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < indentCount; i++) {
            indent.append("> ");
        }
        System.out.println(indent+ str);
    }


}
