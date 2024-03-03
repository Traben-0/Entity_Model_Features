package traben.entity_model_features.models.animation.math.methods.optifine;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathMethod;

import java.util.List;

public class RandomMethod extends MathMethod {

    private final boolean hasSeed;

    public RandomMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        hasSeed = args.size() == 1 && !args.get(0).isBlank();

        if (hasSeed) {
            var arg = parseArg(args.get(0), calculationInstance);
            setSupplierAndOptimize(() -> new java.util.Random((long) arg.getResult()).nextFloat(1), arg);
        } else {
            //true random
            setSupplierAndOptimize(() -> (float) Math.random());
        }
    }

    @Override
    protected boolean canOptimizeForConstantArgs() {
        return hasSeed;
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == 1 || argCount == 0;
    }

}
