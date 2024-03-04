package traben.entity_model_features.models.animation.math.methods.simple;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.models.animation.math.methods.MethodRegistry;

import java.util.List;
import java.util.function.Function;

public class FunctionMethods extends MathMethod {


    protected FunctionMethods(final List<String> args,
                              final boolean isNegative,
                              final EMFAnimation calculationInstance,
                              final Function<Float, Float> function) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        var arg = parseArg(args.get(0), calculationInstance);
        setSupplierAndOptimize(() -> function.apply(arg.getResult()), arg);
    }

    public static MethodRegistry.MethodFactory makeFactory(final String methodName, final Function<Float, Float> function) {
        return (args, isNegative, calculationInstance) -> {
            try {
                return new FunctionMethods(args, isNegative, calculationInstance, function);
            } catch (Exception e) {
                throw new EMFMathException("Failed to create " + methodName + "() method, because: " + e);
            }
        };

    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == 1;
    }

}
