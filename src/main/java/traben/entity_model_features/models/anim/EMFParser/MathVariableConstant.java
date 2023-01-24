package traben.entity_model_features.models.anim.EMFParser;

import java.util.function.Supplier;

public class MathVariableConstant extends MathValue implements Supplier<Float> , MathComponent{

    Float hardCodedValue;

    public MathVariableConstant(float number, boolean isNegative){
        super(isNegative, null);
        hardCodedValue = number;
    }

    @Override
    public Supplier<Float> getSupplier() {
        return ()-> hardCodedValue;
    }

    @Override
    public String toString() {
        return get().toString();
    }
}
