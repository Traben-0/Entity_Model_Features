package traben.entity_model_features.models.animation.math.methods;

import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.function.TriFunction;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.models.animation.math.methods.emf.KeyframeMethod;
import traben.entity_model_features.models.animation.math.methods.emf.KeyframeloopMethod;
import traben.entity_model_features.models.animation.math.methods.optifine.*;
import traben.entity_model_features.models.animation.math.methods.simple.BiFunctionMethods;
import traben.entity_model_features.models.animation.math.methods.simple.FunctionMethods;
import traben.entity_model_features.models.animation.math.methods.simple.MultiFunctionMethods;
import traben.entity_model_features.models.animation.math.methods.simple.TriFunctionMethods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class MethodRegistry {

    private static final MethodRegistry INSTANCE = new MethodRegistry();
    private final Map<String, MethodFactory> methodFactories = new HashMap<>();

    private MethodRegistry() {

        //optifine methods
        registerAndWrapMethodFactory("if", IfMethod::new);
        registerAndWrapMethodFactory("max", MaxMethod::new);
        registerAndWrapMethodFactory("min", MinMethod::new);
        registerAndWrapMethodFactory("random", RandomMethod::new);
        registerSimpleMethodFactory("sin", (v) -> (float) Math.sin(v));
        registerSimpleMethodFactory("asin", (v) -> (float) Math.asin(v));
        registerSimpleMethodFactory("cos", (v) -> (float) Math.cos(v));
        registerSimpleMethodFactory("acos", (v) -> (float) Math.acos(v));
        registerSimpleMethodFactory("tan", (v) -> (float) Math.tan(v));
        registerSimpleMethodFactory("atan", (v) -> (float) Math.atan(v));
        registerSimpleMethodFactory("abs", Math::abs);
        registerSimpleMethodFactory("floor", (v) -> (float) Math.floor(v));
        registerSimpleMethodFactory("ceil", (v) -> (float) Math.ceil(v));
        registerSimpleMethodFactory("round", (v) -> (float) Math.round(v));
        registerSimpleMethodFactory("log", (v) -> (float) Math.log(v));
        registerSimpleMethodFactory("exp", (v) -> (float) Math.exp(v));
        registerSimpleMethodFactory("torad", (v) -> (float) Math.toRadians(v));
        registerSimpleMethodFactory("todeg", (v) -> (float) Math.toDegrees(v));
        registerSimpleMethodFactory("frac", MathHelper::fractionalPart);
        registerSimpleMethodFactory("signum", Math::signum);
        registerSimpleMethodFactory("sqrt", (v) -> (float) Math.sqrt(v));
        registerSimpleMethodFactory("fmod", (v, w) -> (float) Math.floorMod((int) (float) v, (int) (float) w));
        registerSimpleMethodFactory("pow", (v, w) -> (float) Math.pow(v, w));
        registerSimpleMethodFactory("atan2", (v, w) -> (float) Math.atan2(v, w));
        registerSimpleMethodFactory("clamp", MathHelper::clamp);
        registerSimpleMethodFactory("between", (a, b, c) -> a > c ? 0 : (a < b ? 0 : 1f));
        registerSimpleMethodFactory("equals", (x, y, epsilon) -> Math.abs(y - x) <= epsilon ? 1f : 0f);
        registerSimpleMethodFactory("lerp", MathHelper::lerp);
        registerAndWrapMethodFactory("in", InMethod::new);
        registerAndWrapMethodFactory("print", PrintMethod::new);
        registerAndWrapMethodFactory("printb", PrintBMethod::new);


        //emf methods

        registerAndWrapMethodFactory("keyframe", KeyframeMethod::new);
        registerAndWrapMethodFactory("keyframeloop", KeyframeloopMethod::new);
        registerSimpleMethodFactory("wrapdeg", MathHelper::wrapDegrees);
        registerSimpleMethodFactory("wraprad", (v) -> (float) Math.toRadians(MathHelper.wrapDegrees(Math.toDegrees(v))));
        registerSimpleMethodFactory("degdiff", MathHelper::angleBetween);
        registerSimpleMethodFactory("raddiff", (v, w) -> (float) Math.toRadians(MathHelper.angleBetween((float) Math.toDegrees(v), (float) Math.toDegrees(w))));

        //deprecated
        registerSimpleMethodFactory("easeinout", TriFunctionMethods::easeInOutSine);
        registerSimpleMethodFactory("easein", TriFunctionMethods::easeInSine);
        registerSimpleMethodFactory("easeout", TriFunctionMethods::easeOutSine);
        registerSimpleMethodFactory("cubiceaseinout", TriFunctionMethods::easeInOutCubic);
        registerSimpleMethodFactory("cubiceasein", TriFunctionMethods::easeInCubic);
        registerSimpleMethodFactory("cubiceaseout", TriFunctionMethods::easeOutCubic);

        //lerps
        registerSimpleMultiMethodFactory("catmullrom", (args) -> MathHelper.catmullRom(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4)));
        registerSimpleMultiMethodFactory("quadbezier", (args) -> MultiFunctionMethods.quadraticBezier(args.get(0), args.get(1), args.get(2), args.get(3)));
        registerSimpleMultiMethodFactory("cubicbezier", (args) -> MultiFunctionMethods.cubicBezier(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4)));
        registerSimpleMultiMethodFactory("hermite", (args) -> MultiFunctionMethods.hermiteInterpolation(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4)));
        registerSimpleMethodFactory("easeinoutexpo", TriFunctionMethods::easeInOutExpo);
        registerSimpleMethodFactory("easeinexpo", TriFunctionMethods::easeInExpo);
        registerSimpleMethodFactory("easeoutexpo", TriFunctionMethods::easeOutExpo);
        registerSimpleMethodFactory("easeinoutcirc", TriFunctionMethods::easeInOutCirc);
        registerSimpleMethodFactory("easeincirc", TriFunctionMethods::easeInCirc);
        registerSimpleMethodFactory("easeoutcirc", TriFunctionMethods::easeOutCirc);
        registerSimpleMethodFactory("easeinoutelastic", TriFunctionMethods::easeInOutElastic);
        registerSimpleMethodFactory("easeinelastic", TriFunctionMethods::easeInElastic);
        registerSimpleMethodFactory("easeoutelastic", TriFunctionMethods::easeOutElastic);
        registerSimpleMethodFactory("easeinoutback", TriFunctionMethods::easeInOutBack);
        registerSimpleMethodFactory("easeinback", TriFunctionMethods::easeInBack);
        registerSimpleMethodFactory("easeoutback", TriFunctionMethods::easeOutBack);
        registerSimpleMethodFactory("easeinoutbounce", TriFunctionMethods::easeInOutBounce);
        registerSimpleMethodFactory("easeinbounce", TriFunctionMethods::easeInBounce);
        registerSimpleMethodFactory("easeoutbounce", TriFunctionMethods::easeOutBounce);
        registerSimpleMethodFactory("easeinquad", TriFunctionMethods::easeInQuad);
        registerSimpleMethodFactory("easeoutquad", TriFunctionMethods::easeOutQuad);
        registerSimpleMethodFactory("easeinoutquad", TriFunctionMethods::easeInOutQuad);
        registerSimpleMethodFactory("easeincubic", TriFunctionMethods::easeInCubic);
        registerSimpleMethodFactory("easeoutcubic", TriFunctionMethods::easeOutCubic);
        registerSimpleMethodFactory("easeinoutcubic", TriFunctionMethods::easeInOutCubic);
        registerSimpleMethodFactory("easeinquart", TriFunctionMethods::easeInQuart);
        registerSimpleMethodFactory("easeoutquart", TriFunctionMethods::easeOutQuart);
        registerSimpleMethodFactory("easeinoutquart", TriFunctionMethods::easeInOutQuart);
        registerSimpleMethodFactory("easeinquint", TriFunctionMethods::easeInQuint);
        registerSimpleMethodFactory("easeoutquint", TriFunctionMethods::easeOutQuint);
        registerSimpleMethodFactory("easeinoutquint", TriFunctionMethods::easeInOutQuint);
        registerSimpleMethodFactory("easeinsine", TriFunctionMethods::easeInSine);
        registerSimpleMethodFactory("easeoutsine", TriFunctionMethods::easeOutSine);
        registerSimpleMethodFactory("easeinoutsine", TriFunctionMethods::easeInOutSine);
    }

    public static MethodRegistry getInstance() {
        return INSTANCE;
    }

    public void registerSimpleMethodFactory(String methodName, Function<Float, Float> function) {
        methodFactories.put(methodName, FunctionMethods.makeFactory(methodName, function));
    }

    public void registerSimpleMethodFactory(String methodName, BiFunction<Float, Float, Float> function) {
        methodFactories.put(methodName, BiFunctionMethods.makeFactory(methodName, function));
    }

    public void registerSimpleMethodFactory(String methodName, TriFunction<Float, Float, Float, Float> function) {
        methodFactories.put(methodName, TriFunctionMethods.makeFactory(methodName, function));
    }

    public void registerSimpleMultiMethodFactory(String methodName, Function<List<Float>, Float> function) {
        methodFactories.put(methodName, MultiFunctionMethods.makeFactory(methodName, function));
    }

    public void registerAndWrapMethodFactory(String methodName, MethodFactory factory) {
        methodFactories.put(methodName, (a, b, c) -> {
            try {
                return factory.getMethod(a, b, c);
            } catch (Exception e) {
                throw new EMFMathException("Failed to create " + methodName + "() method, because:" + e);
            }
        });
    }

    public boolean containsMethod(String methodName) {
        return methodFactories.containsKey(methodName);
    }

    public MethodFactory getMethodFactory(String methodName) {
        return methodFactories.get(methodName);
    }


    /**
     * A factory for supplying or creating {@link MathMethod} instances.
     */
    public interface MethodFactory {
        MathMethod getMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance)
                throws EMFMathException;
    }
}
