package traben.entity_model_features.models.animation.math.variables;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.CameraType;
import net.minecraft.util.Mth;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMath;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathConstant;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.models.animation.math.expression_tree.MathVariable;
import traben.entity_model_features.models.animation.math.variables.factories.*;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_model_features.utils.EMFUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

import static traben.entity_model_features.models.animation.math.expression_tree.MathValue.FALSE;
import static traben.entity_model_features.models.animation.math.expression_tree.MathValue.TRUE;

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
    private final Map<String, String> singletonVariableExplanationTranslationKeys = new HashMap<>();
    private final List<UniqueVariableFactory> uniqueVariableFactories = new ArrayList<>();

    // Dropping the expression tree boilerplate for asm
    public final Map<String, BooleanSupplier> singletonASMVariablesBool = new HashMap<>();
    public final Map<String, MathValue.ResultSupplier> singletonASMVariablesFloat = new HashMap<>();

    public BooleanSupplier getASMVarBoolOrDefault(String varKey, AnimSetupContext context) {
        var simple =  singletonASMVariablesBool.get(varKey);
        if (simple == null) { // More complex variable?
            // Check if any of the unique variable factories can create this variable
            for (UniqueVariableFactory uniqueVariableFactory : uniqueVariableFactories) {
                if (uniqueVariableFactory.createsThisVariable(varKey)) {
                    var supplier = uniqueVariableFactory.getASMBoolSupplierOrNull(varKey, context);
                    if (supplier != null) {
                        return supplier;
                    }
                }
            }
        } else {
            return simple;
        }
        return ()-> false;
    }

    public MathValue.ResultSupplier getASMVarFloatOrDefault(String varKey, AnimSetupContext context) {
        var simple =  singletonASMVariablesFloat.get(varKey);
        if (simple == null) { // More complex variable?
            // Check if any of the unique variable factories can create this variable
            for (UniqueVariableFactory uniqueVariableFactory : uniqueVariableFactories) {
                if (uniqueVariableFactory.createsThisVariable(varKey)) {
                    var supplier = uniqueVariableFactory.getASMFloatSupplierOrNull(varKey, context);
                    if (supplier != null) {
                        return supplier;
                    }
                }
            }
        } else {
            return simple;
        }
        return ()-> 0f;
    }

    @SuppressWarnings("deprecation")
    private VariableRegistry() {

        //these constants are better hardcoded
        singletonVariables.put("pi", new MathConstant(Mth.PI));
        singletonVariables.put("-pi", new MathConstant(-Mth.PI));
        singletonVariableExplanationTranslationKeys.put("pi", emfTranslationKey("pi"));
        singletonVariables.put("e", new MathConstant((float) Math.E));
        singletonVariables.put("-e", new MathConstant((float) -Math.E));
        singletonVariableExplanationTranslationKeys.put("e", emfTranslationKey("e"));
        singletonVariables.put("true", new MathConstant(TRUE));
        singletonVariables.put("!true", new MathConstant(FALSE));
        singletonVariableExplanationTranslationKeys.put("true", emfTranslationKey("true"));
        singletonVariables.put("false", new MathConstant(FALSE));
        singletonVariables.put("!false", new MathConstant(TRUE));
        singletonVariableExplanationTranslationKeys.put("false", emfTranslationKey("false"));


        //simple floats
        registerSimpleFloatVariable("limb_swing", EMFMath::getLimbAngle);
        registerSimpleFloatVariable("frame_time", EMFMath::getFrameTime);
        registerSimpleFloatVariable("limb_speed", EMFMath::getLimbDistance);
        registerSimpleFloatVariable("age", EMFMath::getAge);
        registerSimpleFloatVariable("head_pitch", EMFMath::getHeadPitch);
        registerSimpleFloatVariable("head_yaw", EMFMath::getHeadYaw);
        registerSimpleFloatVariable("swing_progress", EMFMath::getSwingProgress);
        registerSimpleFloatVariable("hurt_time", EMFMath::getHurtTime);
        registerSimpleFloatVariable("dimension", EMFMath::getDimension);
        registerSimpleFloatVariable("time", ()-> EMFMath.getTime(EMFState.state()));
        registerSimpleFloatVariable("player_pos_x", emfTranslationKey("player_pos"), EMFMath::getPlayerX);
        registerSimpleFloatVariable("player_pos_y", emfTranslationKey("player_pos"), EMFMath::getPlayerY);
        registerSimpleFloatVariable("player_pos_z", emfTranslationKey("player_pos"), EMFMath::getPlayerZ);
        registerSimpleFloatVariable("pos_x", emfTranslationKey("pos"), EMFMath::getEntityX);
        registerSimpleFloatVariable("pos_y", emfTranslationKey("pos"), EMFMath::getEntityY);
        registerSimpleFloatVariable("pos_z", emfTranslationKey("pos"), EMFMath::getEntityZ);
        registerSimpleFloatVariable("player_rot_x", emfTranslationKey("player_rot"), EMFMath::getPlayerRX);
        registerSimpleFloatVariable("player_rot_y", emfTranslationKey("player_rot"), EMFMath::getPlayerRY);
        registerSimpleFloatVariable("rot_x", emfTranslationKey("rot"), EMFMath::getEntityRX);
        registerSimpleFloatVariable("rot_y", emfTranslationKey("rot"), EMFMath::getEntityRY);
        registerSimpleFloatVariable("health", EMFMath::getHealth);
        registerSimpleFloatVariable("death_time", EMFMath::getDeathTime);
        registerSimpleFloatVariable("anger_time", EMFMath::getAngerTime);
        registerSimpleFloatVariable("max_health", EMFMath::getMaxHealth);
        registerSimpleFloatVariable("id", EMFMath::getId);
        registerSimpleFloatVariable("day_time", EMFMath::getDayTime);
        registerSimpleFloatVariable("day_count", EMFMath::getDayCount);
        registerSimpleFloatVariable("rule_index", EMFMath::getRuleIndex);
        registerSimpleFloatVariable("anger_time_start", EMFMath::getAngerTimeStart);
        registerSimpleFloatVariable("move_forward", EMFMath::getMoveForward);
        registerSimpleFloatVariable("move_strafing", EMFMath::getMoveStrafe);
        registerSimpleFloatVariable("height_above_ground", EMFMath::getHeightAboveGround);
        registerSimpleFloatVariable("fluid_depth", EMFMath::getFluidDepth);
        registerSimpleFloatVariable("fluid_depth_down", EMFMath::getFluidDepthDown);
        registerSimpleFloatVariable("fluid_depth_up", EMFMath::getFluidDepthUp);
        registerSimpleFloatVariable("nan", () -> EMFManager.getInstance().isAnimationValidationPhase ? 0 : Float.NaN);
        registerSimpleFloatVariable("distance", () -> {
            if (EMFState.emfEntity() == null) return 0;
            return EMFState.emfEntity().etf$distanceTo(Minecraft.getInstance().player);
        });
        registerSimpleFloatVariable("frame_counter", EMFState::getFrameCounter);


        //simple booleans
        registerSimpleBoolVariable("is_hovered", EMFMath::isClientHovered);
        registerSimpleBoolVariable("is_paused", () -> Minecraft.getInstance().isPaused());
        registerSimpleBoolVariable("is_first_person_hand", () -> {
            var state = EMFState.state();
            return state != null && state.isFirstPersonHand();
        });
        registerSimpleBoolVariable("is_right_handed", () -> {
            if (EMFState.emfEntity() == null) return false;
            return EMFState.emfEntity() instanceof LivingEntity entity && entity.getMainArm() == HumanoidArm.RIGHT;
        });
        registerSimpleBoolVariable("is_swinging_right_arm", () -> EMFMath.isSwingingArm(true));
        registerSimpleBoolVariable("is_swinging_left_arm", () -> EMFMath.isSwingingArm(false));
        registerSimpleBoolVariable("is_holding_item_right", () -> EMFMath.isHoldingItem(true));
        registerSimpleBoolVariable("is_holding_item_left", () -> EMFMath.isHoldingItem(false));
        registerSimpleBoolVariable("is_using_item", EMFMath::isUsingItem);
        registerSimpleBoolVariable("is_swimming", () -> {
            if (EMFState.emfEntity() == null) return false;
            return EMFState.emfEntity() instanceof Entity entity && entity.isSwimming();
        });

        registerSimpleBoolVariable("is_gliding", () -> {
            if (EMFState.emfEntity() == null) return false;
            return EMFState.emfEntity() instanceof LivingEntity entity && entity.isFallFlying();

        });


        registerSimpleBoolVariable("is_blocking", () -> {
            if (EMFState.emfEntity() == null) return false;
            return EMFState.emfEntity() instanceof LivingEntity livingEntity && livingEntity.isBlocking();
        });
        registerSimpleBoolVariable("is_crawling", () -> {
            if (EMFState.emfEntity() == null) return false;
            return EMFState.emfEntity() instanceof Entity entity && entity.isVisuallyCrawling();
        });
        registerSimpleBoolVariable("is_climbing", EMFMath::isClimbing);
        registerSimpleBoolVariable("is_child", EMFMath::isChild);
        registerSimpleBoolVariable("is_in_water", EMFMath::isInWater);
        registerSimpleBoolVariable("is_riding", EMFMath::isRiding);
        registerSimpleBoolVariable("is_on_ground", EMFMath::isOnGround);
        registerSimpleBoolVariable("is_burning", EMFMath::isBurning);
        registerSimpleBoolVariable("is_alive", EMFMath::isAlive);
        registerSimpleBoolVariable("is_glowing", EMFMath::isGlowing);
        registerSimpleBoolVariable("is_aggressive", EMFMath::isAggressive);
        registerSimpleBoolVariable("is_hurt", EMFMath::isHurt);
        registerSimpleBoolVariable("is_in_hand", EMFMath::isInHand);
        registerSimpleBoolVariable("is_in_item_frame", EMFMath::isInItemFrame);
        registerSimpleBoolVariable("is_in_ground", EMFMath::isInGround);
        registerSimpleBoolVariable("is_in_gui", EMFMath::isInGui);
        registerSimpleBoolVariable("is_in_lava", EMFMath::isInLava);
        registerSimpleBoolVariable("is_invisible", EMFMath::isInvisible);
        registerSimpleBoolVariable("is_on_head", EMFMath::isOnHead);
        registerSimpleBoolVariable("is_on_shoulder", EMFMath::isOnShoulder);
        registerSimpleBoolVariable("is_ridden", EMFMath::isRidden);
        registerSimpleBoolVariable("is_sitting", EMFMath::isSitting);
        registerSimpleBoolVariable("is_sneaking", EMFMath::isSneaking);
        registerSimpleBoolVariable("is_sprinting", EMFMath::isSprinting);
        registerSimpleBoolVariable("is_tamed", EMFMath::isTamed);
        registerSimpleBoolVariable("is_wet", EMFMath::isWet);
        registerSimpleBoolVariable("is_jumping", EMFMath::isJumping);
        registerSimpleBoolVariable("is_player_first_person", ()-> Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON);
        registerSimpleBoolVariable("is_player_third_person", ()-> Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_BACK);
        registerSimpleBoolVariable("is_player_third_person_reversed", ()-> Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT);

        //context variables
        // these variables require a context to be created, and are not constants
        // additionally they do not have static names
        registerContextVariable(new ModelPartVariableFactory());
        registerContextVariable(new ModelVariableFactory());
        registerContextVariable(new RenderVariableFactory());
        registerContextVariable(new GlobalVariableFactory());


        //just some handy log warnings for some spelling errors I've seen people make that can be tricky to debug
        registerSimpleSpellingErrorWarning(new String[]{
                "is_agressive","is_aggressive",
                "is_aggresive","is_aggressive",
                "is_agresive","is_aggressive",
                "is_riden","is_ridden",
                "frame_count","frame_counter",});
    }

    private static String emfTranslationKey(String key) {
        return "entity_model_features.config.variable_explanation." + key;

    }

    public static VariableRegistry getInstance() {
        return INSTANCE;
    }

    public Map<String, String> getSingletonVariableExplanationTranslationKeys() {
        return singletonVariableExplanationTranslationKeys;
    }

    public List<UniqueVariableFactory> getUniqueVariableFactories() {
        return uniqueVariableFactories;
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

    private void registerSimpleFloatVariable(String variableName, MathValue.ResultSupplier supplier) {
        registerSimpleFloatVariable(variableName, emfTranslationKey(variableName), supplier);
    }

    public void registerSimpleFloatVariable(String variableName, @Translatable String explanationTranslationKey, MathValue.ResultSupplier supplier) {
        if (singletonVariables.containsKey(variableName)) {
            EMFUtils.log("Duplicate variable: " + variableName + ". ignoring duplicate");
            return;
        }
        singletonVariables.put(variableName, new MathVariable(variableName, false, supplier));
        singletonVariables.put("-" + variableName, new MathVariable("-" + variableName, true, supplier));
        singletonVariableExplanationTranslationKeys.put(variableName, explanationTranslationKey);

        singletonASMVariablesFloat.put(variableName, supplier);
    }

    private void registerSimpleSpellingErrorWarning(String[] warns){
        if (warns.length % 2 != 0) {
            throw new IllegalArgumentException("registerSimpleSpellingErrorWarning must have an even number of elements");
        }

        for (int i = 0; i < warns.length; i= i + 2) {
            String variableName = warns[i];
            String correction = warns[i+1];

            singletonVariables.put(variableName, new MathVariable(variableName, false, ()-> {
                if (printing()) EMFUtils.logError("Math spelling error: [" + variableName + "]. You probably meant: [" + correction + "].");
                return Float.NaN;
            }));
        }
    }

    private void registerSimpleBoolVariable(String variableName, BooleanSupplier boolGetter) {
        registerSimpleBoolVariable(variableName, emfTranslationKey(variableName), boolGetter);
    }

    public void registerSimpleBoolVariable(String variableName, @Translatable String explanationTranslationKey, BooleanSupplier boolGetter) {
        if (singletonVariables.containsKey(variableName)) {
            if (printing()) EMFUtils.log("Duplicate variable: " + variableName + ". ignoring duplicate");
            return;
        }
        singletonVariables.put(variableName, new MathVariable(variableName, () -> MathValue.fromBoolean(boolGetter)));
        singletonVariables.put("!" + variableName, new MathVariable("!" + variableName, () -> MathValue.invertBoolean(boolGetter)));
        singletonVariableExplanationTranslationKeys.put(variableName, explanationTranslationKey);

        singletonASMVariablesBool.put(variableName, boolGetter);
    }


    public MathComponent getVariable(String variableName, boolean isNegative, AnimSetupContext context) {
        try {
            String variableWithNegative = isNegative ? "-" + variableName : variableName;
            if (singletonVariables.containsKey(variableWithNegative)) {
                return singletonVariables.get(variableWithNegative);
            } else {
                // context dependant variable.
                // uses EMFAnimation object for context to create a new variable instance
                boolean invertBooleans = variableName.startsWith("!");
                String variableNameWithoutBooleanInvert = invertBooleans ? variableName.substring(1) : variableName;
                //check if any of the unique variable factories can create this variable
                for (UniqueVariableFactory uniqueVariableFactory : uniqueVariableFactories) {
                    if (uniqueVariableFactory.createsThisVariable(variableNameWithoutBooleanInvert)) {
                        var supplier = uniqueVariableFactory.getSupplierOrNull(variableNameWithoutBooleanInvert, context);
                        if (supplier != null) {
                            return new MathVariable(variableName, isNegative,
                                    invertBooleans ?
                                            () -> MathValue.invertBoolean(supplier)
                                            : supplier);
                        }
                    }
                }
            }
            //unknown variable, return zero constant
            if (printing()) EMFUtils.logError("Variable [" + variableName + "] not found in animation [" + context.animKey + "] of model [" + context.modelName + "]. EMF will treat the variable as zero.");
        } catch (Exception e) {
            if (printing()) EMFUtils.logError("Error finding variable: [" + variableName + "] in animation [" + context.animKey + "] of model [" + context.modelName + "]. EMF will treat the variable as zero.");
        }
        return new MathVariable(variableName, isNegative,
                variableName.startsWith("is_") ? MathConstant.FALSE_CONST.getResultSupplier() : MathConstant.ZERO_CONST.getResultSupplier());
    }

    private boolean printing() {
        return EMF.config().getConfig().logModelCreationData;
    }
}
