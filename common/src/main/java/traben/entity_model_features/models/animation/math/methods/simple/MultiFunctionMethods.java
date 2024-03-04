package traben.entity_model_features.models.animation.math.methods.simple;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.models.animation.math.methods.MethodRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MultiFunctionMethods extends MathMethod {

    private final int argCount;

    public MultiFunctionMethods(final List<String> args,
                                final boolean isNegative,
                                final EMFAnimation calculationInstance,
                                final Function<List<Float>, Float> function) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        argCount = args.size();
        var parsedArgs = parseAllArgs(args, calculationInstance);
        setSupplierAndOptimize(() -> {
            List<Float> results = new ArrayList<>();
            for (var parsedArg : parsedArgs) {
                results.add(parsedArg.getResult());
            }
            return function.apply(results);
        }, parsedArgs);
    }

    public static MethodRegistry.MethodFactory makeFactory(final String methodName, final Function<List<Float>, Float> function) {
        return (args, isNegative, calculationInstance) -> {
            try {
                return new MultiFunctionMethods(args, isNegative, calculationInstance, function);
            } catch (Exception e) {
                throw new EMFMathException("Failed to create " + methodName + "() method, because: " + e);
            }
        };

    }


    public static float quadraticBezier(float t, float p0, float p1, float p2) {
        float oneMinusT = 1f - t;
        return oneMinusT * oneMinusT * p0 + 2f * oneMinusT * t * p1 + t * t * p2;
    }

    public static float cubicBezier(float t, float p0, float p1, float p2, float p3) {
        float oneMinusT = 1f - t;
        float oneMinusTSquared = oneMinusT * oneMinusT;
        float tSquared = t * t;
        return oneMinusTSquared * oneMinusT * p0 + 3f * oneMinusTSquared * t * p1 + 3f * oneMinusT * tSquared * p2 + tSquared * t * p3;
    }

    public static float hermiteInterpolation(float t, float p0, float p1, float m0, float m1) {
        float tSquared = t * t;
        float tCubed = tSquared * t;

        float h00 = 2 * tCubed - 3 * tSquared + 1;
        float h10 = tCubed - 2 * tSquared + t;
        float h01 = -2 * tCubed + 3 * tSquared;
        float h11 = tCubed - tSquared;

        return h00 * p0 + h10 * m0 + h01 * p1 + h11 * m1;
    }


    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == this.argCount;
    }
}
