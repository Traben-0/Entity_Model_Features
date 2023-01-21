package traben.entity_model_features.models.anim;

import net.minecraft.client.model.ModelPart;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.EMF_ModelPart;
import traben.entity_model_features.models.anim.EMFParser.MathExpression;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class AnimationCalculationEMFParser extends AnimationCalculation{

    MathExpression EMFCalculator;

    //private final String expressionString;

    AnimationCalculationMXParser mxpThis = null;

    public AnimationCalculationEMFParser(EMF_EntityModel<?> parent, ModelPart part, AnimVar varToChange, String animKey, String initialExpression) {
        super(parent,part,varToChange,animKey);
        mxpThis = new AnimationCalculationMXParser(parent,part,varToChange,animKey,initialExpression);
        //calculator = new Expression(initialExpression);
        EMFCalculator = new MathExpression(initialExpression,false, this);
    }

    @Override
    public void setVerbose(boolean val) {
        verboseMode = val;
    }

    @Override
    public double calculatorRun() {
       // calculationCount++;
        //setVerbose(true);
        //System.out.println("ran: "+EMFCalculator.originalExpression);
        if(EMFData.getInstance().getConfig().printAllMaths && animKey.equals("rotation.rx")) {
            setVerbose(true);
            System.out.println("mxparser run/////////////////////////////////");
            mxpThis.setVerbose(true);
            System.out.println("mxparser ="+ mxpThis.calculatorRun());
            System.out.println("start EMF///////////////////////////////////");
            double val = EMFCalculator.calculate();
            System.out.println("EMF = "+val+" ///////////////////////////////////");
            return val;
        }else{
            return EMFCalculator.calculate();
        }

    }

    public int indentCount = 0;
   // public long calculationCount = 0L;

    public void animPrint(String str){
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < indentCount; i++) {
            indent.append("> ");
        }
        System.out.println(indent+ str);
    }

    @Override
    public boolean isValid(){
        return EMFCalculator.isValid() && !Double.isNaN( EMFCalculator.calculate());
    }







}
