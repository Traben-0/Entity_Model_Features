package traben.entity_model_features.models.anim.EMFParser;

import java.util.function.Supplier;

public class MathVariableConstant extends MathValue implements Supplier<Double> , MathComponent{

    Double hardCodedValue;

    public MathVariableConstant(double number, boolean isNegative){
        super(isNegative, null);
        hardCodedValue = number;
    }

    @Override
    public Supplier<Double> getSupplier() {
        return ()-> hardCodedValue;
    }

    @Override
    public String toString() {
        return get().toString();
    }
}
