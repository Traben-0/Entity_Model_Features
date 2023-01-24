package traben.entity_model_features.models.anim.EMFParser;

import traben.entity_model_features.models.anim.AnimationCalculation;
import traben.entity_model_features.models.anim.AnimationCalculationEMFParser;

import java.util.function.Supplier;

public abstract class MathValue implements Supplier<Float> , MathComponent{


    MathValue(boolean isNegative, AnimationCalculation calculationInstance){
        this.isNegative = isNegative;
        this.calculationInstance = (AnimationCalculationEMFParser) calculationInstance;
    }

    final AnimationCalculationEMFParser calculationInstance;
    public final boolean isNegative;

    abstract public Supplier<Float> getSupplier();

    public void print(String str){
        if(calculationInstance != null)
            calculationInstance.animPrint(str);
    }

    @Override
    public Float get() {
        if(calculationInstance != null)
            calculationInstance.indentCount++;
        Float ret = isNegative ? -getSupplier().get() : getSupplier().get();
        if(calculationInstance != null)
            calculationInstance.indentCount--;
        return ret;
    }
}
