package traben.entity_model_features.models.anim.EMFParser;

import traben.entity_model_features.models.anim.AnimationCalculation;

import java.util.function.Supplier;

public abstract class MathValue implements Supplier<Double> , MathComponent{


    MathValue(boolean isNegative, AnimationCalculation calculationInstance){
        this.isNegative = isNegative;
        this.calculationInstance = calculationInstance;
    }

    final AnimationCalculation calculationInstance;
    public final boolean isNegative;

    abstract public Supplier<Double> getSupplier();

    @Override
    public Double get() {
        return isNegative ? -getSupplier().get() : getSupplier().get();
    }
}
