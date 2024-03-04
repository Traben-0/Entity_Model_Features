package traben.entity_model_features;

import net.minecraft.util.math.floatprovider.FloatSupplier;
import org.apache.commons.lang3.function.TriFunction;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.methods.MethodRegistry;
import traben.entity_model_features.models.animation.math.variables.VariableRegistry;
import traben.entity_model_features.models.animation.math.variables.factories.UniqueVariableFactory;
import traben.entity_model_features.utils.EMFUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * The main API for registering custom animation math expressions and variables.
 * This is the main entry point for modders to add their own custom math expressions and variables to the animation system.
 * This is a static class with static methods for registering custom math expressions and variables.
 */
@SuppressWarnings("unused")
public interface EMFAnimationApi {

    /**
     * Gets the current version of the EMF API.
     * Future versions of the api will endeavor to maintain backwards compatibility,
     * though may depreciate old methods by having them return null or do nothing.
     *
     * @return The current version of the EMF API.
     */
    static int getApiVersion() {
        return 1;
    }

    /**
     * Registers a singleton boolean variable for use in animation math expressions.
     *
     * @param sourceModId           The mod id of the mod registering the variable.
     * @param variableName          The name of the variable.
     * @param variableValueSupplier A supplier for the value of the variable.
     */
    static void registerSingletonAnimationVariable(String sourceModId, String variableName, BooleanSupplier variableValueSupplier) {
        if (sourceModId != null && variableName != null && variableValueSupplier != null) {
            VariableRegistry.getInstance().registerSimpleBoolVariable(variableName, variableValueSupplier);
            EMFUtils.log("Successful registration of singleton variable:" + variableName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of singleton variable:" + variableName + " from mod " + sourceModId);
        }
    }

    /**
     * Registers a singleton float variable for use in animation math expressions.
     *
     * @param sourceModId           The mod id of the mod registering the variable.
     * @param variableName          The name of the variable.
     * @param variableValueSupplier A supplier for the value of the variable.
     */
    static void registerSingletonAnimationVariable(String sourceModId, String variableName, FloatSupplier variableValueSupplier) {
        if (sourceModId != null && variableName != null && variableValueSupplier != null) {
            VariableRegistry.getInstance().registerSimpleFloatVariable(variableName, (MathValue.ResultSupplier) variableValueSupplier);
            EMFUtils.log("Successful registration of singleton variable:" + variableName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of singleton variable:" + variableName + " from mod " + sourceModId);
        }
    }

    /**
     * Registers a unique variable factory {@link UniqueVariableFactory} for use in animation math expressions.
     * you supply a factory to create these variables as needed as they are not singletons.
     * A unique variable refers to a variable with additional per-model context such as reading other model parts and
     * other model variables.
     * It also allows for more elaborate variable name matching as you must supply your own test to check the variable name
     *
     * @param sourceModId           The mod id of the mod registering the variable.
     * @param variableName          The name of the variable.
     * @param uniqueVariableFactory A factory for the variable.
     */
    static void registerUniqueAnimationVariableFactory(String sourceModId, String variableName, UniqueVariableFactory uniqueVariableFactory) {
        if (sourceModId != null && variableName != null && uniqueVariableFactory != null) {
            VariableRegistry.getInstance().registerContextVariable(uniqueVariableFactory);
            EMFUtils.log("Successful registration of unique variable:" + variableName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of unique variable:" + variableName + " from mod " + sourceModId);
        }
    }

    /**
     * Registers a simple function for use in animation math expressions.
     *
     * @param sourceModId The mod id of the mod registering the function.
     * @param methodName  The name of the function.
     * @param function    The function to register.
     */
    static void registerAnimationFunction(String sourceModId, String methodName, Function<Float, Float> function) {
        if (sourceModId != null && methodName != null && function != null) {
            MethodRegistry.getInstance().registerSimpleMethodFactory(methodName, function);
            EMFUtils.log("Successful registration of function:" + methodName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of function:" + methodName + " from mod " + sourceModId);
        }
    }

    /**
     * Registers a simple function for use in animation math expressions.
     *
     * @param sourceModId The mod id of the mod registering the function.
     * @param methodName  The name of the function.
     * @param biFunction  The function to register.
     */
    static void registerAnimationBiFunction(String sourceModId, String methodName, BiFunction<Float, Float, Float> biFunction) {
        if (sourceModId != null && methodName != null && biFunction != null) {
            MethodRegistry.getInstance().registerSimpleMethodFactory(methodName, biFunction);
            EMFUtils.log("Successful registration of bifunction:" + methodName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of bifunction:" + methodName + " from mod " + sourceModId);
        }
    }

    /**
     * Registers a simple function for use in animation math expressions.
     *
     * @param sourceModId The mod id of the mod registering the function.
     * @param methodName  The name of the function.
     * @param triFunction The function to register.
     */
    static void registerAnimationTriFunction(String sourceModId, String methodName, TriFunction<Float, Float, Float, Float> triFunction) {
        if (sourceModId != null && methodName != null && triFunction != null) {
            MethodRegistry.getInstance().registerSimpleMethodFactory(methodName, triFunction);
            EMFUtils.log("Successful registration of trifunction:" + methodName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of trifunction:" + methodName + " from mod " + sourceModId);
        }
    }

    /**
     * Registers a multiple parameter function for use in animation math expressions.
     * the List<Float> parameter is a list of the parameters in the order supplied to the function.
     *
     * @param sourceModId   The mod id of the mod registering the function.
     * @param methodName    The name of the function.
     * @param multiFunction The function to register.
     */
    static void registerAnimationMultiFunction(String sourceModId, String methodName, Function<List<Float>, Float> multiFunction) {
        if (sourceModId != null && methodName != null && multiFunction != null) {
            MethodRegistry.getInstance().registerSimpleMultiMethodFactory(methodName, multiFunction);
            EMFUtils.log("Successful registration of multifunction:" + methodName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of multifunction:" + methodName + " from mod " + sourceModId);
        }
    }

    /**
     * Registers a custom {@link traben.entity_model_features.models.animation.math.methods.MethodRegistry.MethodFactory}
     * for use in animation math expressions.
     * this is for more complex functions that require additional setup or context.
     * or for functions that require additional parameters that are not simple floats.
     * these functions can address the input strings from the animation directly if required, without having them
     * parsed to Float first.
     *
     * @param sourceModId The mod id of the mod registering the function.
     * @param methodName  The name of the function.
     * @param factory     The factory to register.
     */
    static void registerCustomFunctionFactory(String sourceModId, String methodName, MethodRegistry.MethodFactory factory) {
        if (sourceModId != null && methodName != null && factory != null) {
            MethodRegistry.getInstance().registerAndWrapMethodFactory(methodName, factory);
            EMFUtils.log("Successful registration of custom function:" + methodName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of custom function:" + methodName + " from mod " + sourceModId);
        }
    }


}
