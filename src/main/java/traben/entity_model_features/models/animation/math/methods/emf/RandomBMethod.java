package traben.entity_model_features.models.animation.math.methods.emf;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.methods.optifine.RandomMethod;

import java.util.List;
import java.util.Random;

public class RandomBMethod extends RandomMethod {


    public RandomBMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(args, isNegative, calculationInstance);
    }

    @Override
    protected float nextValue(float seed) {
        return MathValue.fromBoolean(super.nextValue(seed) >= 0.5f);
    }

    @Override
    protected float nextValue() {
        return MathValue.fromBoolean(super.nextValue() >= 0.5f);
    }


}
