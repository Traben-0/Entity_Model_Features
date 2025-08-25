package traben.entity_model_features.models.animation.math.variables;

import com.demonwav.mcdev.annotations.Translatable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Mth;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathConstant;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.MathVariable;
import traben.entity_model_features.models.animation.math.variables.factories.*;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

import static traben.entity_model_features.models.animation.math.MathValue.FALSE;
import static traben.entity_model_features.models.animation.math.MathValue.TRUE;

/**
 * This class is used to register all the variables that can be used in the math parser.
 * It also contains the logic to create the variables when they are used in the parser.
 * <p>
 * This primarily ensures that the variables are only created once, and that they are created with all the correct parameters.
 * While making it easy to register new variables to the parser.
 */
public final class VariableRegistry {

    private static final VariableRegistry INSTANCE = new VariableRegistry();
    private final Map<String, MathComponent> singletonVariables = new Object2ObjectOpenHashMap<>();
    private final Map<String, String> singletonVariableExplanationTranslationKeys = new Object2ObjectOpenHashMap<>();
    private final List<UniqueVariableFactory> uniqueVariableFactories = new ArrayList<>();

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
        registerSimpleFloatVariable("player_pos_x", emfTranslationKey("player_pos"), EMFAnimationEntityContext::getPlayerX);
        registerSimpleFloatVariable("player_pos_y", emfTranslationKey("player_pos"), EMFAnimationEntityContext::getPlayerY);
        registerSimpleFloatVariable("player_pos_z", emfTranslationKey("player_pos"), EMFAnimationEntityContext::getPlayerZ);
        registerSimpleFloatVariable("pos_x", emfTranslationKey("pos"), EMFAnimationEntityContext::getEntityX);
        registerSimpleFloatVariable("pos_y", emfTranslationKey("pos"), EMFAnimationEntityContext::getEntityY);
        registerSimpleFloatVariable("pos_z", emfTranslationKey("pos"), EMFAnimationEntityContext::getEntityZ);
        registerSimpleFloatVariable("player_rot_x", emfTranslationKey("player_rot"), EMFAnimationEntityContext::getPlayerRX);
        registerSimpleFloatVariable("player_rot_y", emfTranslationKey("player_rot"), EMFAnimationEntityContext::getPlayerRY);
        registerSimpleFloatVariable("rot_x", emfTranslationKey("rot"), EMFAnimationEntityContext::getEntityRX);
        registerSimpleFloatVariable("rot_y", emfTranslationKey("rot"), EMFAnimationEntityContext::getEntityRY);
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
        registerSimpleFloatVariable("height_above_ground", EMFAnimationEntityContext::getHeightAboveGround);
        registerSimpleFloatVariable("fluid_depth", EMFAnimationEntityContext::getFluidDepth);
        registerSimpleFloatVariable("fluid_depth_down", EMFAnimationEntityContext::getFluidDepthDown);
        registerSimpleFloatVariable("fluid_depth_up", EMFAnimationEntityContext::getFluidDepthUp);
        registerSimpleFloatVariable("nan", () -> EMFManager.getInstance().isAnimationValidationPhase ? 0 : Float.NaN);
        registerSimpleFloatVariable("distance", () -> {
            if (EMFAnimationEntityContext.getEMFEntity() == null) return 0;
            return EMFAnimationEntityContext.getEMFEntity().etf$distanceTo(Minecraft.getInstance().player);
        });
        registerSimpleFloatVariable("frame_counter", EMFAnimationEntityContext::getFrameCounter);


        //simple booleans
        registerSimpleBoolVariable("is_hovered", EMFAnimationEntityContext::isClientHovered);
        registerSimpleBoolVariable("is_paused", () -> Minecraft.getInstance().isPaused());
        registerSimpleBoolVariable("is_first_person_hand", () -> EMFAnimationEntityContext.isFirstPersonHand);
        registerSimpleBoolVariable("is_right_handed", () -> {
            if (EMFAnimationEntityContext.getEMFEntity() == null) return false;
            return EMFAnimationEntityContext.getEMFEntity() instanceof LivingEntity entity && entity.getMainArm() == HumanoidArm.RIGHT;
        });
        registerSimpleBoolVariable("is_swinging_right_arm", () -> EMFAnimationEntityContext.isSwingingArm(true));
        registerSimpleBoolVariable("is_swinging_left_arm", () -> EMFAnimationEntityContext.isSwingingArm(false));
        registerSimpleBoolVariable("is_holding_item_right", () -> EMFAnimationEntityContext.isHoldingItem(true));
        registerSimpleBoolVariable("is_holding_item_left", () -> EMFAnimationEntityContext.isHoldingItem(false));
        registerSimpleBoolVariable("is_using_item", EMFAnimationEntityContext::isUsingItem);
        registerSimpleBoolVariable("is_swimming", () -> {
            if (EMFAnimationEntityContext.getEMFEntity() == null) return false;
            return EMFAnimationEntityContext.getEMFEntity() instanceof Entity entity && entity.isSwimming();
        });

        registerSimpleBoolVariable("is_gliding", () -> {
            if (EMFAnimationEntityContext.getEMFEntity() == null) return false;
            return EMFAnimationEntityContext.getEMFEntity() instanceof LivingEntity entity && entity.isFallFlying();

        });


        registerSimpleBoolVariable("is_blocking", () -> {
            if (EMFAnimationEntityContext.getEMFEntity() == null) return false;
            return EMFAnimationEntityContext.getEMFEntity() instanceof LivingEntity livingEntity && livingEntity.isBlocking();
        });
        registerSimpleBoolVariable("is_crawling", () -> {
            if (EMFAnimationEntityContext.getEMFEntity() == null) return false;
            return EMFAnimationEntityContext.getEMFEntity() instanceof Entity entity && entity.isVisuallyCrawling();
        });
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
        registerSimpleBoolVariable("is_jumping", EMFAnimationEntityContext::isJumping);

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
    }

    private void registerSimpleSpellingErrorWarning(String[] warns){
        if (warns.length % 2 != 0) {
            throw new IllegalArgumentException("registerSimpleSpellingErrorWarning must have an even number of elements");
        }

        for (int i = 0; i < warns.length; i= i + 2) {
            String variableName = warns[i];
            String correction = warns[i+1];

            singletonVariables.put(variableName, new MathVariable(variableName, false, ()-> {
                EMFUtils.logError("Math spelling error: [" + variableName + "]. You probably meant: [" + correction + "].");
                return Float.NaN;
            }));
        }

    }

    private void registerSimpleBoolVariable(String variableName, BooleanSupplier boolGetter) {
        registerSimpleBoolVariable(variableName, emfTranslationKey(variableName), boolGetter);
    }

    public void registerSimpleBoolVariable(String variableName, @Translatable String explanationTranslationKey, BooleanSupplier boolGetter) {
        if (singletonVariables.containsKey(variableName)) {
            EMFUtils.log("Duplicate variable: " + variableName + ". ignoring duplicate");
            return;
        }
        singletonVariables.put(variableName, new MathVariable(variableName, () -> MathValue.fromBoolean(boolGetter)));
        singletonVariables.put("!" + variableName, new MathVariable("!" + variableName, () -> MathValue.invertBoolean(boolGetter)));
        singletonVariableExplanationTranslationKeys.put(variableName, explanationTranslationKey);
    }


    public MathComponent getVariable(String variableName, boolean isNegative, EMFAnimation calculationInstance) {
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
                        var supplier = uniqueVariableFactory.getSupplierOrNull(variableNameWithoutBooleanInvert, calculationInstance);
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
            EMFUtils.logError("Variable [" + variableName + "] not found in animation [" + calculationInstance.animKey + "] of model [" + calculationInstance.modelName + "]. EMF will treat the variable as zero.");
        } catch (Exception e) {
            EMFUtils.logError("Error finding variable: [" + variableName + "] in animation [" + calculationInstance.animKey + "] of model [" + calculationInstance.modelName + "]. EMF will treat the variable as zero.");
        }
        return variableName.startsWith("is_") ? MathConstant.FALSE_CONST : MathConstant.ZERO_CONST;
    }
}
