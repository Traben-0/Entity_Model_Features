package traben.entity_model_features.models.anim.EMFParser;

import traben.entity_model_features.models.anim.AnimationCalculation;

public abstract class MathValue implements  MathComponent{


    MathValue(boolean isNegative,AnimationCalculation calculationInstance){
        this.isNegative = isNegative;
        this.calculationInstance =  calculationInstance;
    }
    MathValue(boolean isNegative){
        this.isNegative = isNegative;
        this.calculationInstance =  null;
    }
    MathValue(){
        this.isNegative = false;
        this.calculationInstance =  null;
    }

    final AnimationCalculation calculationInstance;
    public boolean isNegative;

    abstract public ValueSupplier getSupplier();

    public void print(String str){
        if(calculationInstance != null)
            calculationInstance.animPrint(str);
    }

    @Override
    public double get() {
        if(calculationInstance != null)
            calculationInstance.indentCount++;
        double ret = isNegative ? -getSupplier().get() : getSupplier().get();
        if(calculationInstance != null)
            calculationInstance.indentCount--;
        return ret;
    }

    public void makeNegative(boolean become){
        if(become) isNegative = !isNegative;
    }

    public interface ValueSupplier{
        double get();
    }
}
