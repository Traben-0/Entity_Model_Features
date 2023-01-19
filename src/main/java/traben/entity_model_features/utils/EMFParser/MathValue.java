package traben.entity_model_features.utils.EMFParser;

import java.util.function.Supplier;

public abstract class MathValue implements Supplier<Double> , MathComponent{


    MathValue(boolean isNegative){
        this.isNegative = isNegative;
    }

    public boolean isNegative;

    public Supplier<Double> getSupplier() {
        return ()-> 0d;
    }

    @Override
    public Double get() {
        return isNegative ? -getSupplier().get() : getSupplier().get();
    }
}
