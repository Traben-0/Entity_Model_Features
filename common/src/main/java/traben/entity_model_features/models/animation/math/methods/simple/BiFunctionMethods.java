package traben.entity_model_features.models.animation.math.methods.simple;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.models.animation.math.methods.MethodRegistry;

import java.util.List;
import java.util.function.BiFunction;

public class BiFunctionMethods extends MathMethod {


    protected BiFunctionMethods(final List<String> args,
                                final boolean isNegative,
                                final EMFAnimation calculationInstance,
                                final BiFunction<Float, Float, Float> function) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        var arg = parseArg(args.get(0), calculationInstance);
        var arg2 = parseArg(args.get(1), calculationInstance);
        setSupplierAndOptimize(() -> function.apply(arg.getResult(), arg2.getResult()), List.of(arg, arg2));
    }

    public static MethodRegistry.MethodFactory makeFactory(final String methodName, final BiFunction<Float, Float, Float> function) {
        return (args, isNegative, calculationInstance) -> {
            try {
                return new BiFunctionMethods(args, isNegative, calculationInstance, function);
            } catch (Exception e) {
                throw new EMFMathException("Failed to create " + methodName + "() method, because: " + e);
            }
        };

    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == 2;
    }


}
