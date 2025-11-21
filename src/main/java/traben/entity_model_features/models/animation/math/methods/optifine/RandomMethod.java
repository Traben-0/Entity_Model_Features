package traben.entity_model_features.models.animation.math.methods.optifine;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathMethod;

import java.util.List;
import java.util.Random;

public class RandomMethod extends MathMethod {

    private final boolean hasSeed;

    public RandomMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        hasSeed = args.size() == 1 && !args.get(0).isBlank();
        if (hasSeed) {
            var arg = parseArg(args.get(0), calculationInstance);
            setSupplierAndOptimize(() -> nextValue(arg.getResult()), arg);
        } else {
            //true random
            setSupplierAndOptimize(this::nextValue);
        }
    }

    protected float nextValue(float seed) {
        int hash = optifineIntHash(Float.floatToIntBits(seed));
        return (float)Math.abs(hash) / 2.14748365E9F;
    }

    protected float nextValue() {
        return (float) Math.random();
    }

    public static int optifineIntHash(int x) {
        x = x ^ 61 ^ x >> 16;
        x += x << 3;
        x ^= x >> 4;
        x *= 668265261;
        return x ^ x >> 15;
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
