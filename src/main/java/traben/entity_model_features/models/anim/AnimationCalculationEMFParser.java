package traben.entity_model_features.models.anim;

import net.minecraft.client.model.ModelPart;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.EMF_ModelPart;
import traben.entity_model_features.models.anim.EMFParser.MathExpression;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class AnimationCalculationEMFParser extends AnimationCalculation{

    MathExpression EMFCalculator;

    //private final String expressionString;
    AnimationCalculationEMFParser(EMF_EntityModel<?> parent, EMF_ModelPart part, AnimVar varToChange, String animKey, String initialExpression) {
        super(parent,part,varToChange,animKey,initialExpression);
        EMFCalculator = new MathExpression(initialExpression,false, this);
    }

    public AnimationCalculationEMFParser(EMF_EntityModel<?> parent, ModelPart part, AnimVar varToChange, String animKey, String initialExpression) {
        super(parent,part,varToChange,animKey,initialExpression);
        //calculator = new Expression(initialExpression);
        EMFCalculator = new MathExpression(initialExpression,false, this);
    }

    @Override
    public void setVerbose(boolean val) {
        verboseMode = val;
    }

    @Override
    public double calculatorRun() {
        setVerbose(true);
        System.out.println("ran: "+EMFCalculator.originalExpression);
        return EMFCalculator.calculate();
    }

    @Override
    public boolean isValid(){
        return EMFCalculator.isValid() && !Double.isNaN( EMFCalculator.calculate());
    }







}
