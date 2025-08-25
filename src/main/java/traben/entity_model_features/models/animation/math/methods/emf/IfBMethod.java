package traben.entity_model_features.models.animation.math.methods.emf;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.methods.optifine.IfMethod;

import java.util.List;

public class IfBMethod extends IfMethod {


    public IfBMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(args, isNegative, calculationInstance);

        //validate output is boolean
        supplier = () -> MathValue.validateBoolean(supplier.get());
        if (optimizedAlternativeToThis != null) {
            optimizedAlternativeToThis = () -> MathValue.validateBoolean(optimizedAlternativeToThis.getResult());
        }
    }
}
