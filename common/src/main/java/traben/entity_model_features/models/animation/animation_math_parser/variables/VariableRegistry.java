package traben.entity_model_features.models.animation.animation_math_parser.variables;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationHelper;
import traben.entity_model_features.models.animation.animation_math_parser.MathComponent;
import traben.entity_model_features.models.animation.animation_math_parser.MathConstant;
import traben.entity_model_features.models.animation.animation_math_parser.MathValue;
import traben.entity_model_features.models.animation.animation_math_parser.MathVariable;
import traben.entity_model_features.models.animation.animation_math_parser.variables.factories.UniqueVariableFactory;
import traben.entity_model_features.models.animation.animation_math_parser.variables.factories.ModelPartVariableFactory;
import traben.entity_model_features.models.animation.animation_math_parser.variables.factories.ModelVariableFactory;
import traben.entity_model_features.models.animation.animation_math_parser.variables.factories.RenderVariableFactory;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * This class is used to register all the variables that can be used in the math parser.
 * It also contains the logic to create the variables when they are used in the parser.
 * <p>
 * This primarily ensures that the variables are only created once, and that they are created with all the correct parameters.
 * While making it easy to register new variables to the parser.
 */
public final class VariableRegistry {

    private static final VariableRegistry INSTANCE = new VariableRegistry();
    private final Map<String, MathComponent> singletonVariables = new HashMap<>();
    private final List<UniqueVariableFactory> uniqueVariableFactories = new ArrayList<>();
    private VariableRegistry() {

        //these constants are better hardcoded
        singletonVariables.put("pi", new MathConstant((float) Math.PI));
        singletonVariables.put("-pi", new MathConstant((float) -Math.PI));
        singletonVariables.put("e", new MathConstant((float) Math.E));
        singletonVariables.put("-e", new MathConstant((float) -Math.E));
        singletonVariables.put("true", MathConstant.ONE);
        singletonVariables.put("!true", MathConstant.ZERO);
        singletonVariables.put("false", MathConstant.ZERO);
        singletonVariables.put("!false", MathConstant.ONE);


        //simple floats
        registerSimpleFloatVariable("limb_swing", EMFAnimationHelper::getLimbAngle);
        registerSimpleFloatVariable("frame_time", EMFAnimationHelper::getFrameTime);
        registerSimpleFloatVariable("limb_speed", EMFAnimationHelper::getLimbDistance);
        registerSimpleFloatVariable("age", EMFAnimationHelper::getAge);
        registerSimpleFloatVariable("head_pitch", EMFAnimationHelper::getHeadPitch);
        registerSimpleFloatVariable("head_yaw", EMFAnimationHelper::getHeadYaw);
        registerSimpleFloatVariable("swing_progress", EMFAnimationHelper::getSwingProgress);
        registerSimpleFloatVariable("hurt_time", EMFAnimationHelper::getHurtTime);
        registerSimpleFloatVariable("dimension", EMFAnimationHelper::getDimension);
        registerSimpleFloatVariable("time", EMFAnimationHelper::getTime);
        registerSimpleFloatVariable("player_pos_x", EMFAnimationHelper::getPlayerX);
        registerSimpleFloatVariable("player_pos_y", EMFAnimationHelper::getPlayerY);
        registerSimpleFloatVariable("player_pos_z", EMFAnimationHelper::getPlayerZ);
        registerSimpleFloatVariable("pos_x", EMFAnimationHelper::getEntityX);
        registerSimpleFloatVariable("pos_y", EMFAnimationHelper::getEntityY);
        registerSimpleFloatVariable("pos_z", EMFAnimationHelper::getEntityZ);
        registerSimpleFloatVariable("player_rot_x", EMFAnimationHelper::getPlayerRX);
        registerSimpleFloatVariable("player_rot_y", EMFAnimationHelper::getPlayerRY);
        registerSimpleFloatVariable("rot_x", EMFAnimationHelper::getEntityRX);
        registerSimpleFloatVariable("rot_y", EMFAnimationHelper::getEntityRY);
        registerSimpleFloatVariable("health", EMFAnimationHelper::getHealth);
        registerSimpleFloatVariable("death_time", EMFAnimationHelper::getDeathTime);
        registerSimpleFloatVariable("anger_time", EMFAnimationHelper::getAngerTime);
        registerSimpleFloatVariable("max_health", EMFAnimationHelper::getMaxHealth);
        registerSimpleFloatVariable("id", EMFAnimationHelper::getId);
        registerSimpleFloatVariable("day_time", EMFAnimationHelper::getDayTime);
        registerSimpleFloatVariable("day_count", EMFAnimationHelper::getDayCount);
        registerSimpleFloatVariable("rule_index", EMFAnimationHelper::getRuleIndex);
        registerSimpleFloatVariable("anger_time_start", EMFAnimationHelper::getAngerTimeStart);
        registerSimpleFloatVariable("move_forward", EMFAnimationHelper::getMoveForward);
        registerSimpleFloatVariable("move_strafing", EMFAnimationHelper::getMoveStrafe);
        registerSimpleFloatVariable("nan", () -> EMFManager.getInstance().isAnimationValidationPhase ? 0 : Float.NaN);

        //simple booleans
        registerSimpleBoolVariable("is_climbing", EMFAnimationHelper::isClimbing);
        registerSimpleBoolVariable("is_child", EMFAnimationHelper::isChild);
        registerSimpleBoolVariable("is_in_water", EMFAnimationHelper::isInWater);
        registerSimpleBoolVariable("is_riding", EMFAnimationHelper::isRiding);
        registerSimpleBoolVariable("is_on_ground", EMFAnimationHelper::isOnGround);
        registerSimpleBoolVariable("is_burning", EMFAnimationHelper::isBurning);
        registerSimpleBoolVariable("is_alive", EMFAnimationHelper::isAlive);
        registerSimpleBoolVariable("is_glowing", EMFAnimationHelper::isGlowing);
        registerSimpleBoolVariable("is_aggressive", EMFAnimationHelper::isAggressive);
        registerSimpleBoolVariable("is_hurt", EMFAnimationHelper::isHurt);
        registerSimpleBoolVariable("is_in_hand", EMFAnimationHelper::isInHand);
        registerSimpleBoolVariable("is_in_item_frame", EMFAnimationHelper::isInItemFrame);
        registerSimpleBoolVariable("is_in_ground", EMFAnimationHelper::isInGround);
        registerSimpleBoolVariable("is_in_gui", EMFAnimationHelper::isInGui);
        registerSimpleBoolVariable("is_in_lava", EMFAnimationHelper::isInLava);
        registerSimpleBoolVariable("is_invisible", EMFAnimationHelper::isInvisible);
        registerSimpleBoolVariable("is_on_head", EMFAnimationHelper::isOnHead);
        registerSimpleBoolVariable("is_on_shoulder", EMFAnimationHelper::isOnShoulder);
        registerSimpleBoolVariable("is_ridden", EMFAnimationHelper::isRidden);
        registerSimpleBoolVariable("is_sitting", EMFAnimationHelper::isSitting);
        registerSimpleBoolVariable("is_sneaking", EMFAnimationHelper::isSneaking);
        registerSimpleBoolVariable("is_sprinting", EMFAnimationHelper::isSprinting);
        registerSimpleBoolVariable("is_tamed", EMFAnimationHelper::isTamed);
        registerSimpleBoolVariable("is_wet", EMFAnimationHelper::isWet);

        //context variables
        // these variables require a context to be created, and are not constants
        registerContextVariable(new ModelPartVariableFactory());
        registerContextVariable(new ModelVariableFactory());
        registerContextVariable(new RenderVariableFactory());

    }

    public static VariableRegistry getInstance() {
        return INSTANCE;
    }

    public void registerContextVariable(UniqueVariableFactory factory) {
        if (factory == null) {
            EMFUtils.logWarn("Tried to register a null context variable factory");
            return;
        }
        if (uniqueVariableFactories.contains(factory)) {
            EMFUtils.logWarn("Tried to register a duplicate context variable factory: " + factory.getClass().getName());
            return;
        }
        uniqueVariableFactories.add(factory);
    }

    public void registerSimpleFloatVariable(String variableName, MathValue.ResultSupplier supplier) {
        if (singletonVariables.containsKey(variableName)) {
            EMFUtils.log("Duplicate variable: " + variableName + ". ignoring duplicate");
            return;
        }
        singletonVariables.put(variableName, new MathVariable(variableName, false, supplier));
        singletonVariables.put("-" + variableName, new MathVariable("-" + variableName, true, supplier));
    }

    public void registerSimpleBoolVariable(String variableName, BooleanSupplier boolGetter) {
        if (singletonVariables.containsKey(variableName)) {
            EMFUtils.log("Duplicate variable: " + variableName + ". ignoring duplicate");
            return;
        }
        singletonVariables.put(variableName, new MathVariable(variableName, () -> (boolGetter.getAsBoolean()) ? 1f : 0f));
        singletonVariables.put("!" + variableName, new MathVariable("!" + variableName, () -> (boolGetter.getAsBoolean()) ? 0f : 1f));
    }


    public MathComponent getVariable(String variableName, boolean isNegative, EMFAnimation calculationInstance) {
        try {
            String variableKey = isNegative ? "-" + variableName : variableName;
            if (singletonVariables.containsKey(variableKey)) {
                return singletonVariables.get(variableKey);
            } else {
                // context dependant variable.
                // uses EMFAnimation object for context to create a new variable instance
                boolean invertBooleans = variableName.startsWith("!");

                String variableTest;
                if (isNegative || invertBooleans) {
                    variableTest = variableName.substring(1);
                } else {
                    variableTest = variableName;
                }
                //check if any of the unique variable factories can create this variable
                for (UniqueVariableFactory uniqueVariableFactory : uniqueVariableFactories) {
                    if (uniqueVariableFactory.createsThisVariable(variableTest)) {
                        var supplier = uniqueVariableFactory.getSupplierOrNull(variableName, calculationInstance);
                        if (supplier != null) {
                            return new MathVariable(variableName, isNegative,
                                    invertBooleans ?
                                            () -> supplier.get() == 1 ? 0 : 1
                                            : supplier);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        //unknown variable, return zero constant
        EMFUtils.logError("Unknown variable: " + variableName + " in [" + calculationInstance.modelName + "]. EMF will treat the variable as zero.");
        return MathConstant.ZERO;
    }
}
