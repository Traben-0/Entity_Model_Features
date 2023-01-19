package traben.entity_model_features.utils.EMFParser;

import java.util.List;
import java.util.function.Supplier;

public class MathMethod extends MathValue implements MathComponent{

    public MathMethod(String methodName, String args, boolean isNegative) {
        super(isNegative);

    }

    @Override
    public Supplier<Double> getSupplier() {
        return ()->0d;
    }
}
