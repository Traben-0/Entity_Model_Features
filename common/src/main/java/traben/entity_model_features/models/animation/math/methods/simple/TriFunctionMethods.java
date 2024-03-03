package traben.entity_model_features.models.animation.math.methods.simple;

import org.apache.commons.lang3.function.TriFunction;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.models.animation.math.methods.MethodRegistry;

import java.util.List;

public class TriFunctionMethods extends MathMethod {


    protected TriFunctionMethods(final List<String> args,
                                 final boolean isNegative,
                                 final EMFAnimation calculationInstance,
                                 final TriFunction<Float, Float, Float, Float> function) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        var arg = parseArg(args.get(0), calculationInstance);
        var arg2 = parseArg(args.get(1), calculationInstance);
        var arg3 = parseArg(args.get(2), calculationInstance);
        setSupplierAndOptimize(() -> function.apply(arg.getResult(), arg2.getResult(), arg3.getResult()), List.of(arg, arg2, arg3));
    }

    public static MethodRegistry.MethodFactory makeFactory(final String methodName, final TriFunction<Float, Float, Float, Float> function) {
        return (args, isNegative, calculationInstance) -> {
            try {
                return new TriFunctionMethods(args, isNegative, calculationInstance, function);
            } catch (Exception e) {
                throw new EMFMathException("Failed to create " + methodName + "() method, because: " + e);
            }
        };

    }


    public static float easeInQuad(float t, float start, float end) {
        float delta = end - start;
        return start + delta * t * t;
    }

    public static float easeOutQuad(float t, float start, float end) {
        float delta = end - start;
        return start + delta * -t * (t - 2);
    }

    public static float easeInOutQuad(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 0.5) {
            return start + delta * (2 * t * t);
        } else {
            return start + delta * (-2 * t * (t - 2) - 1);
        }
    }

    public static float easeInCubic(float t, float start, float end) {
        float delta = end - start;
        return start + delta * t * t * t;
    }

    public static float easeOutCubic(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (t = t - 1) * t * t + 1;
    }

    public static float easeInOutCubic(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 0.5) {
            return start + delta * 4 * t * t * t;
        } else {
            return start + delta * (t = t - 1) * (2 * t * t + 2) + 1;
        }
    }

    public static float easeInQuart(float t, float start, float end) {
        float delta = end - start;
        return start + delta * t * t * t * t;
    }

    public static float easeOutQuart(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (t = t - 1) * t * t * t + 1;
    }

    public static float easeInOutQuart(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 0.5) {
            return start + delta * 8 * t * t * t * t;
        } else {
            return start + delta * (t = t - 1) * (8 * t * t * t + 1) + 1;
        }
    }

    public static float easeInQuint(float t, float start, float end) {
        float delta = end - start;
        return start + delta * t * t * t * t * t;
    }

    public static float easeOutQuint(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (t = t - 1) * t * t * t * t + 1;
    }

    public static float easeInOutQuint(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 0.5) {
            return start + delta * 16 * t * t * t * t * t;
        } else {
            return start + delta * (t = t - 1) * (16 * t * t * t * t + 1) + 1;
        }
    }

    public static float easeInSine(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (1 - (float) Math.cos(t * Math.PI / 2));
    }

    public static float easeOutSine(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) Math.sin(t * Math.PI / 2);
    }

    public static float easeInOutSine(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) (-0.5 * (Math.cos(Math.PI * t) - 1));
    }

    public static float easeInExpo(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) Math.pow(2, 10 * (t - 1));
    }

    public static float easeOutExpo(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) (-Math.pow(2, -10 * t) + 1);
    }

    public static float easeInOutExpo(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 1) {
            return start + delta * (float) (0.5 * Math.pow(2, 10 * (t - 1)));
        } else {
            return start + delta * (float) (0.5 * (-Math.pow(2, -10 * --t) + 2));
        }
    }

    public static float easeInCirc(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) -(Math.sqrt(1 - t * t) - 1);
    }

    public static float easeOutCirc(float t, float start, float end) {
        float delta = end - start;
        float tMinus1 = t - 1;
        return start + delta * (float) Math.sqrt(1 - tMinus1 * tMinus1);
    }

    public static float easeInOutCirc(float t, float start, float end) {
        float delta = end - start;
        float tTimes2 = t * 2;
        if (tTimes2 < 1) {
            return start + delta * (float) (-0.5 * (Math.sqrt(1 - tTimes2 * tTimes2) - 1));
        } else {
            float tTimes2Minus2 = tTimes2 - 2;
            return start + delta * (float) (0.5 * (Math.sqrt(1 - tTimes2Minus2 * tTimes2Minus2) + 1));
        }
    }

    public static float easeInElastic(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) (-Math.pow(2, 10 * (t -= 1)) * Math.sin((t - 0.3 / 4) * (2 * Math.PI) / 0.3));
    }

    public static float easeOutElastic(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (float) (Math.pow(2, -10 * t) * Math.sin((t - 0.3 / 4) * (2 * Math.PI) / 0.3) + 1);
    }

    public static float easeInOutElastic(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 0.5) {
            return start + delta * (float) (-0.5 * Math.pow(2, 10 * (t -= 1)) * Math.sin((t - 0.225 / 4) * (2 * Math.PI) / 0.45));
        } else {
            return start + delta * (float) (0.5 * Math.pow(2, -10 * (t -= 1)) * Math.sin((t - 0.225 / 4) * (2 * Math.PI) / 0.45) + 1);
        }
    }

    public static float easeInBounce(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (1 - easeOutBounce(1 - t, 0, 1));
    }

    public static float easeOutBounce(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < (1 / 2.75)) {
            return start + delta * (7.5625f * t * t);
        } else if (t < (2 / 2.75)) {
            return (float) (start + delta * (7.5625f * (t -= (float) (1.5 / 2.75)) * t + .75));
        } else if (t < (2.5 / 2.75)) {
            return (float) (start + delta * (7.5625f * (t -= (float) (2.25 / 2.75)) * t + .9375));
        } else {
            return (float) (start + delta * (7.5625f * (t -= (float) (2.625 / 2.75)) * t + .984375));
        }
    }

    public static float easeInOutBounce(float t, float start, float end) {
        float delta = end - start;
        if (t < 0.5) {
            return start + delta * (0.5f * easeInBounce(t * 2, 0, 1));
        } else {
            return start + delta * (0.5f * easeOutBounce(t * 2 - 1, 0, 1) + 0.5f);
        }
    }

    public static float easeInBack(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (t * t * (2.70158f * t - 1.70158f));
    }

    public static float easeOutBack(float t, float start, float end) {
        float delta = end - start;
        return start + delta * (--t * t * (2.70158f * t + 1.70158f) + 1);
    }

    public static float easeInOutBack(float t, float start, float end) {
        float delta = end - start;
        t /= 1;
        if (t < 0.5) {
            return start + delta * (t * t * (7 * t - 2.5f) * 2);
        } else {
            return start + delta * ((--t * t * (7 * t + 2.5f) + 2) * 2);
        }
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == 3;
    }
}
