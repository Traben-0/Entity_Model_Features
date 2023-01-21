package traben.entity_model_features.models.anim.EMFParser;

import traben.entity_model_features.models.anim.AnimationCalculation;
import traben.entity_model_features.models.anim.AnimationCalculationEMFParser;

import java.util.function.Supplier;

public abstract class MathValue implements Supplier<Double> , MathComponent{


    MathValue(boolean isNegative, AnimationCalculation calculationInstance){
        this.isNegative = isNegative;
        this.calculationInstance = (AnimationCalculationEMFParser) calculationInstance;
    }

    final AnimationCalculationEMFParser calculationInstance;
    public final boolean isNegative;

    abstract public Supplier<Double> getSupplier();

    public void print(String str){
        if(calculationInstance != null)
            calculationInstance.animPrint(str);
    }

    @Override
    public Double get() {
        if(calculationInstance != null)
            calculationInstance.indentCount++;
        Double ret = isNegative ? -getSupplier().get() : getSupplier().get();
        if(calculationInstance != null)
            calculationInstance.indentCount--;
        return ret;
    }
}
