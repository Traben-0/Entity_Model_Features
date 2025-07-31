package traben.entity_model_features;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.methods.MethodRegistry;
import traben.entity_model_features.models.animation.math.variables.VariableRegistry;
import traben.entity_model_features.models.animation.math.variables.factories.UniqueVariableFactory;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.models.parts.EMFModelPartCustom;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_model_features.utils.EMFUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.util.valueproviders.SampledFloat;

/**
 * The main API for registering custom animation math expressions and variables.
 * This is the main entry point for modders to add their own custom math expressions and variables to the animation system.
 * This is a static class with static methods for registering custom math expressions and variables.
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public interface EMFAnimationApi {



    /**
     * Gets the current version of the EMF API.
     * Future versions of the api will endeavor to maintain backwards compatibility,
     * though may depreciate old methods by having them return null or do nothing.
     *
     * @return The current version of the EMF API.
     */
    @SuppressWarnings("SameReturnValue")
    static int getApiVersion() {
        return 4;
    }

    /**
     * Gets current rendered entity.
     * This may be either a {@link net.minecraft.world.entity.Entity} or {@link net.minecraft.world.level.block.entity.BlockEntity} or null.
     *
     * @return the currently rendered entity
     */
    static @Nullable EMFEntity getCurrentEntity() {
        return EMFAnimationEntityContext.getEMFEntity();
    }

    /**
     * Registers a singleton boolean variable for use in animation math expressions.
     *
     * @param sourceModId                             The mod id of the mod registering the variable.
     * @param variableName                            The name of the variable.
     * @param variableExplanationTranslationKeyOrText The explanation of the variable.
     * @param variableValueSupplier                   A supplier for the value of the variable.
     */
    static void registerSingletonAnimationVariable(String sourceModId, String variableName, String variableExplanationTranslationKeyOrText, BooleanSupplier variableValueSupplier) {
        if (sourceModId != null && variableName != null && variableValueSupplier != null && variableExplanationTranslationKeyOrText != null) {
            VariableRegistry.getInstance().registerSimpleBoolVariable(variableName, variableExplanationTranslationKeyOrText, variableValueSupplier);
            EMFUtils.log("Successful registration of singleton variable:" + variableName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of singleton variable:" + variableName + " from mod " + sourceModId);
        }
    }


    /**
     * Registers a singleton float variable for use in animation math expressions.
     *
     * @param sourceModId                             The mod id of the mod registering the variable.
     * @param variableName                            The name of the variable.
     * @param variableExplanationTranslationKeyOrText The explanation of the variable.
     * @param variableValueSupplier                   A supplier for the value of the variable.
     */
    static void registerSingletonAnimationVariable(String sourceModId, String variableName, String variableExplanationTranslationKeyOrText, SampledFloat variableValueSupplier) {
        if (sourceModId != null && variableName != null && variableValueSupplier != null && variableExplanationTranslationKeyOrText != null) {
            VariableRegistry.getInstance().registerSimpleFloatVariable(variableName, variableExplanationTranslationKeyOrText, (MathValue.ResultSupplier) variableValueSupplier);
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
     * @param sourceModId                           The mod id of the mod registering the function.
     * @param methodName                            The name of the function.
     * @param methodExplanationTranslationKeyOrText The explanation of the function.
     * @param function                              The function to register.
     */
    static void registerAnimationFunction(String sourceModId, String methodName, String methodExplanationTranslationKeyOrText, Function<Float, Float> function) {
        if (sourceModId != null && methodName != null && function != null && methodExplanationTranslationKeyOrText != null) {
            MethodRegistry.getInstance().registerSimpleMethodFactory(methodName, methodExplanationTranslationKeyOrText, function);
            EMFUtils.log("Successful registration of function:" + methodName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of function:" + methodName + " from mod " + sourceModId);
        }
    }

    /**
     * Registers a simple function for use in animation math expressions.
     *
     * @param sourceModId                           The mod id of the mod registering the function.
     * @param methodName                            The name of the function.
     * @param methodExplanationTranslationKeyOrText The explanation of the function.
     * @param biFunction                            The function to register.
     */
    static void registerAnimationBiFunction(String sourceModId, String methodName, String methodExplanationTranslationKeyOrText, BiFunction<Float, Float, Float> biFunction) {
        if (sourceModId != null && methodName != null && biFunction != null && methodExplanationTranslationKeyOrText != null) {
            MethodRegistry.getInstance().registerSimpleMethodFactory(methodName, methodExplanationTranslationKeyOrText, biFunction);
            EMFUtils.log("Successful registration of bifunction:" + methodName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of bifunction:" + methodName + " from mod " + sourceModId);
        }
    }

    /**
     * Registers a simple function for use in animation math expressions.
     *
     * @param sourceModId                           The mod id of the mod registering the function.
     * @param methodName                            The name of the function.
     * @param methodExplanationTranslationKeyOrText The explanation of the function.
     * @param triFunction                           The function to register.
     */
    static void registerAnimationTriFunction(String sourceModId, String methodName, String methodExplanationTranslationKeyOrText, TriFunction<Float, Float, Float, Float> triFunction) {
        if (sourceModId != null && methodName != null && triFunction != null && methodExplanationTranslationKeyOrText != null) {
            MethodRegistry.getInstance().registerSimpleMethodFactory(methodName, methodExplanationTranslationKeyOrText, triFunction);
            EMFUtils.log("Successful registration of trifunction:" + methodName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of trifunction:" + methodName + " from mod " + sourceModId);
        }
    }

    /**
     * Registers a multiple parameter function for use in animation math expressions.
     * the List<Float> parameter is a list of the parameters in the order supplied to the function.
     *
     * @param sourceModId                           The mod id of the mod registering the function.
     * @param methodName                            The name of the function.
     * @param methodExplanationTranslationKeyOrText The explanation of the function.
     * @param multiFunction                         The function to register.
     */
    static void registerAnimationMultiFunction(String sourceModId, String methodName, String methodExplanationTranslationKeyOrText, Function<List<Float>, Float> multiFunction) {
        if (sourceModId != null && methodName != null && multiFunction != null && methodExplanationTranslationKeyOrText != null) {
            MethodRegistry.getInstance().registerSimpleMultiMethodFactory(methodName, methodExplanationTranslationKeyOrText, multiFunction);
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
     * @param sourceModId                           The mod id of the mod registering the function.
     * @param methodName                            The name of the function.
     * @param methodExplanationTranslationKeyOrText The explanation of the function.
     * @param factory                               The factory to register.
     */
    static void registerCustomFunctionFactory(String sourceModId, String methodName, String methodExplanationTranslationKeyOrText, MethodRegistry.MethodFactory factory) {
        if (sourceModId != null && methodName != null && factory != null && methodExplanationTranslationKeyOrText != null) {
            MethodRegistry.getInstance().registerAndWrapMethodFactory(methodName, methodExplanationTranslationKeyOrText, factory);
            EMFUtils.log("Successful registration of custom function:" + methodName + " from mod " + sourceModId);
        } else {
            EMFUtils.logError("Invalid registration of custom function:" + methodName + " from mod " + sourceModId);
        }
    }


    /**
     * @param entity Entity to cast to EMFEntity
     * @return the EMFEntity of the entity
     */
    static EMFEntity emfEntityOf (Entity entity){
        return (EMFEntity) entity;
    }
    /**
     * @param blockEntity BlockEntity to cast to EMFEntity
     * @return the EMFEntity of the BlockEntity
     */
    static EMFEntity emfEntityOf (BlockEntity blockEntity){
        return (EMFEntity) blockEntity;
    }

    /**
     * @param entityOrBlockEntity The entity or block entity to pause animations for.
     * @return true if valid inputs were supplied and the entity's animations were set to pause.
     */
    static boolean pauseAllCustomAnimationsForEntity(EMFEntity entityOrBlockEntity) {
        if (entityOrBlockEntity == null || entityOrBlockEntity.etf$getUuid() == null) {
            return false;
        }
        EMFAnimationEntityContext.entitiesPaused.add(entityOrBlockEntity.etf$getUuid());
        return true;
    }

    /**
     * @param entityOrBlockEntity The entity or block entity to resume animations for.
     * @return true if valid inputs were supplied and the entity's animations were set to resume.
     */
    static boolean resumeAllCustomAnimationsForEntity(EMFEntity entityOrBlockEntity) {
        if (entityOrBlockEntity == null || entityOrBlockEntity.etf$getUuid() == null) {
            return false;
        }
        EMFAnimationEntityContext.entitiesPaused.remove(entityOrBlockEntity.etf$getUuid());
        EMFAnimationEntityContext.entitiesPausedParts.remove(entityOrBlockEntity.etf$getUuid());
        return true;
    }

    /**
     * @param entityOrBlockEntity The entity or block entity to pause animations for.
     * @param parts               The parts of the entity to pause animations for.
     * @return true if valid inputs were supplied and the entity's animations were set to pause.
     */
    static boolean pauseCustomAnimationsForThesePartsOfEntity(EMFEntity entityOrBlockEntity, ModelPart... parts) {
        if (entityOrBlockEntity == null || entityOrBlockEntity.etf$getUuid() == null
                || parts == null || parts.length == 0) {
            return false;
        }
        EMFAnimationEntityContext.entitiesPausedParts.put(entityOrBlockEntity.etf$getUuid(), parts);
        return true;
    }

    /**
     * @param entityOrBlockEntity The entity or block entity to be forced into their vanilla model.
     * @return true if valid inputs were supplied and the entity was marked to use the vanilla model.
     */
    static boolean lockEntityToVanillaModel(EMFEntity entityOrBlockEntity){
        if (entityOrBlockEntity == null || entityOrBlockEntity.etf$getUuid() == null) {
            return false;
        }
        EMFAnimationEntityContext.entitiesToForceVanillaModel.add(entityOrBlockEntity.etf$getUuid());
        return true;
    }

    /**
     * @param entityOrBlockEntity The entity or block entity to be re-allowed to variate.
     * @return true if valid inputs were supplied and the entity was marked to use their variants again.
     */
    static boolean unlockEntityToVanillaModel(EMFEntity entityOrBlockEntity){
        if (entityOrBlockEntity == null || entityOrBlockEntity.etf$getUuid() == null) {
            return false;
        }
        EMFAnimationEntityContext.entitiesToForceVanillaModel.remove(entityOrBlockEntity.etf$getUuid());
        return true;
    }


    /**
     * Get the current emf variant of the model.
     * Returns -1 if the model is not an EMF model or is null.
     * Returns 0 if the model is an EMF model but has no variants, or hasn't been set yet.
     *
     * @param model the model
     * @return the int
     */
    static int getCurrentEMFVariantOfModel(EntityModel<?> model){
        if (!isModelCustomizedByEMF(model)) {
            return -1;
        }
        return ((IEMFModel) model).emf$getEMFRootModel().currentModelVariant;
    }


    /**
     * Checks if the model has custom EMF animations.
     * Returns false if the model is not an EMF model or is null.
     *
     * @param model the model
     * @return the boolean
     */
    static boolean isModelAnimatedByEMF(EntityModel<?> model){
        if (!isModelCustomizedByEMF(model)) {
            return false;
        }
        return ((IEMFModel) model).emf$getEMFRootModel().hasAnimation();
    }

    /**
     * Is this model a custom EMF model.
     * Returns false if the model is null.
     *
     * @param model the model
     * @return the boolean
     */
    static boolean isModelCustomizedByEMF(EntityModel<?> model){
        if (model == null) {
            return false;
        }
        return ((IEMFModel) model).emf$isEMFModel();
    }

    /**
     * Is this model part is an extraneous part added by EMF, and does not represent any actual normal vanilla parts.
     * Returns false if the modelPart is null.
     *
     * @param modelPart the model part
     * @return the boolean
     */
    static boolean isModelPartCustomToEMF(ModelPart modelPart){
        if (modelPart == null) {
            return false;
        }
        return modelPart instanceof EMFModelPartCustom;
    }

    /**
     * Is this model part animated by EMF.
     * Returns false if the modelPart is null.
     * Be warned this will not tell you if a parent part of the model is animated.
     *
     * @param modelPart the model part
     * @return the boolean
     */
    static boolean isModelPartAnimatedByEMF(ModelPart modelPart){
        if (modelPart == null) {
            return false;
        }
        return modelPart instanceof EMFModelPart emf && emf.isSetByAnimation;
    }

    @Deprecated(since = "api v2")
    static void registerSingletonAnimationVariable(String sourceModId, String variableName, BooleanSupplier variableValueSupplier) {
        EMFUtils.logWarn("Invalid registration of singleton variable:" + variableName + " from mod " + sourceModId);
        registerSingletonAnimationVariable(sourceModId, variableName, variableName, variableValueSupplier);
    }

    @Deprecated(since = "api v2")
    static void registerSingletonAnimationVariable(String sourceModId, String variableName, SampledFloat variableValueSupplier) {
        EMFUtils.logWarn("Invalid registration of singleton variable:" + variableName + " from mod " + sourceModId);
        registerSingletonAnimationVariable(sourceModId, variableName, variableName, variableValueSupplier);
    }


    @Deprecated(since = "api v2")
    static void registerAnimationFunction(String sourceModId, String methodName, Function<Float, Float> function) {
        EMFUtils.logWarn("Invalid registration of function:" + methodName + " from mod " + sourceModId);
        registerAnimationFunction(sourceModId, methodName, methodName, function);
    }

    @Deprecated(since = "api v2")
    static void registerAnimationBiFunction(String sourceModId, String methodName, BiFunction<Float, Float, Float> biFunction) {
        EMFUtils.logWarn("Invalid registration of bifunction:" + methodName + " from mod " + sourceModId);
        registerAnimationBiFunction(sourceModId, methodName, methodName, biFunction);
    }

    @Deprecated(since = "api v2")
    static void registerAnimationTriFunction(String sourceModId, String methodName, TriFunction<Float, Float, Float, Float> triFunction) {
        EMFUtils.logWarn("Invalid registration of trifunction:" + methodName + " from mod " + sourceModId);
        registerAnimationTriFunction(sourceModId, methodName, methodName, triFunction);
    }

    @Deprecated(since = "api v2")
    static void registerAnimationMultiFunction(String sourceModId, String methodName, Function<List<Float>, Float> multiFunction) {
        EMFUtils.logWarn("Invalid registration of multifunction:" + methodName + " from mod " + sourceModId);
        registerAnimationMultiFunction(sourceModId, methodName, methodName, multiFunction);
    }

    @Deprecated(since = "api v2")
    static void registerCustomFunctionFactory(String sourceModId, String methodName, MethodRegistry.MethodFactory factory) {
        EMFUtils.logWarn("Invalid registration of custom function:" + methodName + " from mod " + sourceModId);
        registerCustomFunctionFactory(sourceModId, methodName, methodName, factory);
    }
}
