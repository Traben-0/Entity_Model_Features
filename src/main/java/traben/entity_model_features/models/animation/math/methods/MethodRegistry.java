package traben.entity_model_features.models.animation.math.methods;

import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVisitable;
import traben.entity_model_features.models.animation.math.methods.emf.*;
import traben.entity_model_features.models.animation.math.methods.optifine.*;
import traben.entity_model_features.models.animation.math.methods.simple.BiFunctionMethods;
import traben.entity_model_features.models.animation.math.methods.simple.FunctionMethods;
import traben.entity_model_features.models.animation.math.methods.simple.MultiFunctionMethods;
import traben.entity_model_features.models.animation.math.methods.simple.TriFunctionMethods;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class MethodRegistry {

    private static final MethodRegistry INSTANCE = new MethodRegistry();
    private final Map<String, MethodFactory> methodFactories = new HashMap<>();

    private final Map<String, String> methodExplanationTranslationKeys = new HashMap<>();

    private MethodRegistry() {
        try {
            //optifine methods
            registerAndWrapMethodFactory("max", MaxMethod::new);
            registerAndWrapMethodFactory("min", MinMethod::new);
            registerAndWrapMethodFactory("random", RandomMethod::new);
            registerHelperMethodFactory("sin");
            registerHelperMethodFactory("asin");
            registerHelperMethodFactory("cos");
            registerHelperMethodFactory("acos");
            registerHelperMethodFactory("tan");
            registerHelperMethodFactory("atan");
            registerHelperMethodFactory("abs");
            registerHelperMethodFactory("floor");
            registerHelperMethodFactory("ceil");
            registerHelperMethodFactory("round");
            registerHelperMethodFactory("log");
            registerHelperMethodFactory("exp");
            registerHelperMethodFactory("torad");
            registerHelperMethodFactory("todeg");
            registerHelperMethodFactory("frac");
            registerHelperMethodFactory("signum");
            registerHelperMethodFactory("sqrt");
            registerHelperMethodFactory("fmod");
            registerHelperMethodFactory("pow");
            registerHelperMethodFactory("atan2");
            registerHelperMethodFactory("clamp");
            registerHelperMethodFactory("lerp");
            registerAndWrapMethodFactory("print", PrintMethod::new);
            registerAndWrapMethodFactory("printb", PrintBMethod::new);
            registerAndWrapMethodFactory("catch", CatchMethod::new);

            // booleans
            registerAndWrapMethodFactory("if", IfMethod::new);
            registerAndWrapMethodFactory("ifb", IfBMethod::new);
            registerAndWrapMethodFactory("randomb", RandomBMethod::new);
            registerAndWrapMethodFactory("in", InMethod::new);
            registerHelperMethodFactory("between");
            registerHelperMethodFactory("equals");


            //emf methods

            registerAndWrapMethodFactory("nbt", NBTMethod::new);
            registerAndWrapMethodFactory("keyframe", KeyframeMethod::new);
            registerAndWrapMethodFactory("keyframeloop", KeyframeloopMethod::new);
            registerHelperMethodFactory("wrapdeg");
            registerHelperMethodFactory("wraprad");
            registerHelperMethodFactory("degdiff");
            registerHelperMethodFactory("raddiff");

            //deprecated
            registerHelperMethodFactory("easeinout", "easeInOutSine");
            registerHelperMethodFactory("easein", "easeInSine");
            registerHelperMethodFactory("easeout", "easeOutSine");
            registerHelperMethodFactory("cubiceaseinout", "easeInOutCubic");
            registerHelperMethodFactory("cubiceasein", "easeInCubic");
            registerHelperMethodFactory("cubiceaseout", "easeOutCubic");

            //lerps
            registerHelperMethodFactory("catmullrom");
            registerHelperMethodFactory("quadbezier", "quadraticBezier");
            registerHelperMethodFactory("cubicbezier", "cubicBezier");
            registerHelperMethodFactory("hermite", "hermiteInterpolation");
            registerHelperMethodFactory("easeinoutexpo");
            registerHelperMethodFactory("easeinexpo");
            registerHelperMethodFactory("easeoutexpo");
            registerHelperMethodFactory("easeinoutcirc");
            registerHelperMethodFactory("easeincirc");
            registerHelperMethodFactory("easeoutcirc");
            registerHelperMethodFactory("easeinoutelastic");
            registerHelperMethodFactory("easeinelastic");
            registerHelperMethodFactory("easeoutelastic");
            registerHelperMethodFactory("easeinoutback");
            registerHelperMethodFactory("easeinback");
            registerHelperMethodFactory("easeoutback");
            registerHelperMethodFactory("easeinoutbounce");
            registerHelperMethodFactory("easeinbounce");
            registerHelperMethodFactory("easeoutbounce");
            registerHelperMethodFactory("easeinquad");
            registerHelperMethodFactory("easeoutquad");
            registerHelperMethodFactory("easeinoutquad");
            registerHelperMethodFactory("easeincubic");
            registerHelperMethodFactory("easeoutcubic");
            registerHelperMethodFactory("easeinoutcubic");
            registerHelperMethodFactory("easeinquart");
            registerHelperMethodFactory("easeoutquart");
            registerHelperMethodFactory("easeinoutquart");
            registerHelperMethodFactory("easeinquint");
            registerHelperMethodFactory("easeoutquint");
            registerHelperMethodFactory("easeinoutquint");
            registerHelperMethodFactory("easeinsine");
            registerHelperMethodFactory("easeoutsine");
            registerHelperMethodFactory("easeinoutsine");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String emfTranslationKey(String key) {
        return "entity_model_features.config.function_explanation." + key;

    }

    public static MethodRegistry getInstance() {
        return INSTANCE;
    }

    public Map<String, String> getMethodExplanationTranslationKeys() {
        return methodExplanationTranslationKeys;
    }

    private void registerHelperMethodFactory(String methodName) throws EMFMathException {
        registerSimpleMethodFactory(methodName, emfTranslationKey(methodName), ASMHelper.getHelperMethod(methodName), ASMHelper.getHelperMethodCompiler(methodName));
    }

    private void registerHelperMethodFactory(String methodName, String staticName) throws EMFMathException {
        registerSimpleMethodFactory(methodName, emfTranslationKey(methodName), ASMHelper.getHelperMethod(staticName), ASMHelper.getHelperMethodCompiler(methodName));
    }

    public void registerSimpleMethodFactory(String methodName, String explanationTranslationKey, Method staticMethod, @Nullable ASMVisitable asmCompiler) {
        register(methodName, explanationTranslationKey, StaticReflectMethods.makeFactory(methodName, staticMethod, asmCompiler));
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    public void registerSimpleMethodFactory(String methodName, String explanationTranslationKey, Function<Float, Float> function) throws EMFMathException {
        throw  new UnsupportedOperationException("deprecated");
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    public void registerSimpleMethodFactory(String methodName, String explanationTranslationKey, BiFunction<Float, Float, Float> function) throws EMFMathException {
        throw  new UnsupportedOperationException("deprecated");
    }


    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    public void registerSimpleMethodFactory(String methodName, String explanationTranslationKey, TriFunction<Float, Float, Float, Float> function) throws EMFMathException {
        throw  new UnsupportedOperationException("deprecated");
    }


    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    public void registerSimpleMultiMethodFactory(String methodName, String explanationTranslationKey, Function<List<Float>, Float> function) throws EMFMathException {
        throw  new UnsupportedOperationException("deprecated");
    }

    private void register(String methodName, String explanationTranslationKey, MethodFactory factory) {
        methodExplanationTranslationKeys.put(methodName, explanationTranslationKey);
        methodFactories.put(methodName, factory);
    }

    private void registerAndWrapMethodFactory(String methodName, MethodFactory factory) {
        registerAndWrapMethodFactory(methodName, emfTranslationKey(methodName), factory);
    }

    public void registerAndWrapMethodFactory(String methodName, String explanationTranslationKey, MethodFactory factory) {
        register(methodName, explanationTranslationKey, (a, b, c) -> {
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
        MathMethod getMethod(final List<String> args, final boolean isNegative, AnimSetupContext context)
                throws EMFMathException;
    }
}
