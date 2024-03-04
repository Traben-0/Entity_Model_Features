package traben.entity_model_features.models.animation.math.variables;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathConstant;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.MathVariable;
import traben.entity_model_features.models.animation.math.variables.factories.ModelPartVariableFactory;
import traben.entity_model_features.models.animation.math.variables.factories.ModelVariableFactory;
import traben.entity_model_features.models.animation.math.variables.factories.RenderVariableFactory;
import traben.entity_model_features.models.animation.math.variables.factories.UniqueVariableFactory;
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
        registerSimpleFloatVariable("limb_swing", EMFAnimationEntityContext::getLimbAngle);
        registerSimpleFloatVariable("frame_time", EMFAnimationEntityContext::getFrameTime);
        registerSimpleFloatVariable("limb_speed", EMFAnimationEntityContext::getLimbDistance);
        registerSimpleFloatVariable("age", EMFAnimationEntityContext::getAge);
        registerSimpleFloatVariable("head_pitch", EMFAnimationEntityContext::getHeadPitch);
        registerSimpleFloatVariable("head_yaw", EMFAnimationEntityContext::getHeadYaw);
        registerSimpleFloatVariable("swing_progress", EMFAnimationEntityContext::getSwingProgress);
        registerSimpleFloatVariable("hurt_time", EMFAnimationEntityContext::getHurtTime);
        registerSimpleFloatVariable("dimension", EMFAnimationEntityContext::getDimension);
        registerSimpleFloatVariable("time", EMFAnimationEntityContext::getTime);
        registerSimpleFloatVariable("player_pos_x", EMFAnimationEntityContext::getPlayerX);
        registerSimpleFloatVariable("player_pos_y", EMFAnimationEntityContext::getPlayerY);
        registerSimpleFloatVariable("player_pos_z", EMFAnimationEntityContext::getPlayerZ);
        registerSimpleFloatVariable("pos_x", EMFAnimationEntityContext::getEntityX);
        registerSimpleFloatVariable("pos_y", EMFAnimationEntityContext::getEntityY);
        registerSimpleFloatVariable("pos_z", EMFAnimationEntityContext::getEntityZ);
        registerSimpleFloatVariable("player_rot_x", EMFAnimationEntityContext::getPlayerRX);
        registerSimpleFloatVariable("player_rot_y", EMFAnimationEntityContext::getPlayerRY);
        registerSimpleFloatVariable("rot_x", EMFAnimationEntityContext::getEntityRX);
        registerSimpleFloatVariable("rot_y", EMFAnimationEntityContext::getEntityRY);
        registerSimpleFloatVariable("health", EMFAnimationEntityContext::getHealth);
        registerSimpleFloatVariable("death_time", EMFAnimationEntityContext::getDeathTime);
        registerSimpleFloatVariable("anger_time", EMFAnimationEntityContext::getAngerTime);
        registerSimpleFloatVariable("max_health", EMFAnimationEntityContext::getMaxHealth);
        registerSimpleFloatVariable("id", EMFAnimationEntityContext::getId);
        registerSimpleFloatVariable("day_time", EMFAnimationEntityContext::getDayTime);
        registerSimpleFloatVariable("day_count", EMFAnimationEntityContext::getDayCount);
        registerSimpleFloatVariable("rule_index", EMFAnimationEntityContext::getRuleIndex);
        registerSimpleFloatVariable("anger_time_start", EMFAnimationEntityContext::getAngerTimeStart);
        registerSimpleFloatVariable("move_forward", EMFAnimationEntityContext::getMoveForward);
        registerSimpleFloatVariable("move_strafing", EMFAnimationEntityContext::getMoveStrafe);
        registerSimpleFloatVariable("nan", () -> EMFManager.getInstance().isAnimationValidationPhase ? 0 : Float.NaN);

        //simple booleans
        registerSimpleBoolVariable("is_climbing", EMFAnimationEntityContext::isClimbing);
        registerSimpleBoolVariable("is_child", EMFAnimationEntityContext::isChild);
        registerSimpleBoolVariable("is_in_water", EMFAnimationEntityContext::isInWater);
        registerSimpleBoolVariable("is_riding", EMFAnimationEntityContext::isRiding);
        registerSimpleBoolVariable("is_on_ground", EMFAnimationEntityContext::isOnGround);
        registerSimpleBoolVariable("is_burning", EMFAnimationEntityContext::isBurning);
        registerSimpleBoolVariable("is_alive", EMFAnimationEntityContext::isAlive);
        registerSimpleBoolVariable("is_glowing", EMFAnimationEntityContext::isGlowing);
        registerSimpleBoolVariable("is_aggressive", EMFAnimationEntityContext::isAggressive);
        registerSimpleBoolVariable("is_hurt", EMFAnimationEntityContext::isHurt);
        registerSimpleBoolVariable("is_in_hand", EMFAnimationEntityContext::isInHand);
        registerSimpleBoolVariable("is_in_item_frame", EMFAnimationEntityContext::isInItemFrame);
        registerSimpleBoolVariable("is_in_ground", EMFAnimationEntityContext::isInGround);
        registerSimpleBoolVariable("is_in_gui", EMFAnimationEntityContext::isInGui);
        registerSimpleBoolVariable("is_in_lava", EMFAnimationEntityContext::isInLava);
        registerSimpleBoolVariable("is_invisible", EMFAnimationEntityContext::isInvisible);
        registerSimpleBoolVariable("is_on_head", EMFAnimationEntityContext::isOnHead);
        registerSimpleBoolVariable("is_on_shoulder", EMFAnimationEntityContext::isOnShoulder);
        registerSimpleBoolVariable("is_ridden", EMFAnimationEntityContext::isRidden);
        registerSimpleBoolVariable("is_sitting", EMFAnimationEntityContext::isSitting);
        registerSimpleBoolVariable("is_sneaking", EMFAnimationEntityContext::isSneaking);
        registerSimpleBoolVariable("is_sprinting", EMFAnimationEntityContext::isSprinting);
        registerSimpleBoolVariable("is_tamed", EMFAnimationEntityContext::isTamed);
        registerSimpleBoolVariable("is_wet", EMFAnimationEntityContext::isWet);

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

                //check if any of the unique variable factories can create this variable
                for (UniqueVariableFactory uniqueVariableFactory : uniqueVariableFactories) {
                    if (uniqueVariableFactory.createsThisVariable(invertBooleans ? variableName.substring(1) : variableName)) {
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
            //unknown variable, return zero constant
            EMFUtils.logWarn("Variable [" + variableName + "] not found in animation [" + calculationInstance.animKey + "] of model [" + calculationInstance.modelName + "]. EMF will treat the variable as zero.");
        } catch (Exception e) {
            EMFUtils.logWarn("Error finding variable: [" + variableName + "] in animation [" + calculationInstance.animKey + "] of model [" + calculationInstance.modelName + "]. EMF will treat the variable as zero.");
        }
        return MathConstant.ZERO;
    }
}
