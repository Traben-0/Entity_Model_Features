package traben.entity_model_features.models.animation.math.methods.optifine;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathMethod;

import java.util.List;
import java.util.Random;

public class RandomMethod extends MathMethod {

    private final boolean hasSeed;

    public RandomMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        hasSeed = args.size() == 1 && !args.get(0).isBlank();
        var rand = new Random();
        if (hasSeed) {
            var arg = parseArg(args.get(0), calculationInstance);
            setSupplierAndOptimize(() -> nextValue(rand, arg.getResult()), arg);
        } else {
            //true random
            setSupplierAndOptimize(() -> nextValue(rand));
        }
    }

    protected float nextValue(Random rand, float seed) {
        rand.setSeed((long) seed);
        return rand.nextFloat();
    }

    protected float nextValue(Random rand) {
        return rand.nextFloat();
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
