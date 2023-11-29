package traben.entity_model_features.models.animation.animation_math_parser;

import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationHelper;
import traben.entity_model_features.models.animation.EMFModelOrRenderVariable;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

import java.util.ArrayList;
import java.util.Objects;


public class MathVariable extends MathValue implements MathComponent {


    final String variableName;
    public MathComponent optimizedAlternativeToThis = null;

    //final float PI = (float) Math.PI;
    ValueSupplier valueSupplier;
    private boolean invertBooleans = false;

    private MathVariable(String value, boolean isNegative, EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance);

        variableName = value;
        //valueSupplier = ()->0d;

        if (value.startsWith("!")) {
            value = value.replaceFirst("!", "");
            invertBooleans = true;
        }

        //EMFAnimationHelper getter = calculationInstance.variableSuppliers;

        //discover supplier needed
        valueSupplier = switch (value) {
            case "limb_swing" -> EMFAnimationHelper::getLimbAngle;
            case "frame_time" -> EMFAnimationHelper::getFrameTime;
            case "limb_speed" -> EMFAnimationHelper::getLimbDistance;
            case "age" -> EMFAnimationHelper::getAge;
            case "head_pitch" -> EMFAnimationHelper::getHeadPitch;
            case "head_yaw" -> EMFAnimationHelper::getHeadYaw;
            case "swing_progress" -> EMFAnimationHelper::getSwingProgress;
            case "hurt_time" -> EMFAnimationHelper::getHurtTime;
            case "dimension" -> EMFAnimationHelper::getDimension;
            case "time" -> EMFAnimationHelper::getTime;
            case "player_pos_x" -> EMFAnimationHelper::getPlayerX;
            case "player_pos_y" -> EMFAnimationHelper::getPlayerY;
            case "player_pos_z" -> EMFAnimationHelper::getPlayerZ;
            case "pos_x" -> EMFAnimationHelper::getEntityX;
            case "pos_y" -> EMFAnimationHelper::getEntityY;
            case "pos_z" -> EMFAnimationHelper::getEntityZ;
            case "player_rot_x" -> EMFAnimationHelper::getPlayerRX;
            case "player_rot_y" -> EMFAnimationHelper::getPlayerRY;
            case "rot_x" -> EMFAnimationHelper::getEntityRX;
            case "rot_y" -> EMFAnimationHelper::getEntityRY;
            case "health" -> EMFAnimationHelper::getHealth;
            case "death_time" -> EMFAnimationHelper::getDeathTime;
            case "anger_time" -> EMFAnimationHelper::getAngerTime;
            case "max_health" -> EMFAnimationHelper::getMaxHealth;
            case "id" -> EMFAnimationHelper::getId;

            case "day_time" -> EMFAnimationHelper::getDayTime;
            case "day_count" -> EMFAnimationHelper::getDayCount;
            case "rule_index" -> EMFAnimationHelper::getRuleIndex;
            case "anger_time_start" -> EMFAnimationHelper::getAngerTimeStart;

            case "move_forward" -> EMFAnimationHelper::getMoveForward;
            case "move_strafing" -> EMFAnimationHelper::getMoveStrafe;

            case "nan" -> () -> EMFManager.getInstance().isAnimationValidationPhase ? 0 : Float.NaN;


//            case "collisionX" -> getter::getClosestCollisionX;
//            case "collisionY" -> getter::getClosestCollisionY;
//            case "collisionZ" -> getter::getClosestCollisionZ;

            case "is_climbing" -> getBooleanAsFloat(EMFAnimationHelper::isClimbing);
            //constants
//            case "pi" -> ()->PI;//3.1415926f;
//            case "true" ->  ()-> invertBooleans ? 0f : 1f;
//            case "false" -> ()-> invertBooleans ? 1f : 0f;

            //boolean
            case "is_child" -> getBooleanAsFloat(EMFAnimationHelper::isChild);
            case "is_in_water" -> getBooleanAsFloat(EMFAnimationHelper::isInWater);
            case "is_riding" -> getBooleanAsFloat(EMFAnimationHelper::isRiding);
            case "is_on_ground" -> getBooleanAsFloat(EMFAnimationHelper::isOnGround);
            case "is_burning" -> getBooleanAsFloat(EMFAnimationHelper::isBurning);
            case "is_alive" -> getBooleanAsFloat(EMFAnimationHelper::isAlive);
            case "is_glowing" -> getBooleanAsFloat(EMFAnimationHelper::isGlowing);
            case "is_aggressive" -> getBooleanAsFloat(EMFAnimationHelper::isAggressive);
            case "is_hurt" -> getBooleanAsFloat(EMFAnimationHelper::isHurt);
            case "is_in_hand" -> getBooleanAsFloat(EMFAnimationHelper::isInHand);
            case "is_in_item_frame" -> getBooleanAsFloat(EMFAnimationHelper::isInItemFrame);
            case "is_in_ground" -> getBooleanAsFloat(EMFAnimationHelper::isInGround);
            case "is_in_gui" -> getBooleanAsFloat(EMFAnimationHelper::isInGui);
            case "is_in_lava" -> getBooleanAsFloat(EMFAnimationHelper::isInLava);
            case "is_invisible" -> getBooleanAsFloat(EMFAnimationHelper::isInvisible);
            case "is_on_head" -> getBooleanAsFloat(EMFAnimationHelper::isOnHead);
            case "is_on_shoulder" -> getBooleanAsFloat(EMFAnimationHelper::isOnShoulder);
            case "is_ridden" -> getBooleanAsFloat(EMFAnimationHelper::isRidden);
            case "is_sitting" -> getBooleanAsFloat(EMFAnimationHelper::isSitting);
            case "is_sneaking" -> getBooleanAsFloat(EMFAnimationHelper::isSneaking);
            case "is_sprinting" -> getBooleanAsFloat(EMFAnimationHelper::isSprinting);
            case "is_tamed" -> getBooleanAsFloat(EMFAnimationHelper::isTamed);
            case "is_wet" -> getBooleanAsFloat(EMFAnimationHelper::isWet);

            //unknown variable
            default -> getVariable(value);//, EMFAnimationHelper);
        };
    }

    public static MathComponent getOptimizedVariable(String value, boolean isNegative, EMFAnimation calculationInstance) throws EMFMathException {
        MathVariable method = new MathVariable(value, isNegative, calculationInstance);
        return Objects.requireNonNullElse(method.optimizedAlternativeToThis, method);
    }

    private ValueSupplier getVariable(String variableKey) throws EMFMathException {

        switch (variableKey) {
            case "pi" -> {
                optimizedAlternativeToThis = isNegative ? MathConstant.PI_CONSTANT_NEGATIVE : MathConstant.PI_CONSTANT;
                return () -> (float) Math.PI;
            }
            case "true" -> {
                float bool = invertBooleans ? 0f : 1f;
                //optimizedAlternativeToThis = new MathConstant(bool);
                optimizedAlternativeToThis = invertBooleans ? MathConstant.ZERO : MathConstant.ONE;
                return () -> bool;
            }
            case "false" -> {
                float bool = invertBooleans ? 1f : 0f;
                //optimizedAlternativeToThis = new MathConstant(bool);
                optimizedAlternativeToThis = invertBooleans ? MathConstant.ONE : MathConstant.ZERO;
                return () -> bool;
            }
            default -> {
                //process model part variable   e.g.  head.rx
                if (variableKey.matches("[a-zA-Z0-9_]+\\.([trs][xyz]$|visible$|visible_boxes$)")) {
                    String[] split = variableKey.split("\\.");//todo only works with one split point
                    String partName = split[0];
                    EMFModelOrRenderVariable partVariable = EMFModelOrRenderVariable.get(split[1]);
                    EMFModelPart part = EMFManager.getModelFromHierarchichalId(partName, calculationInstance.allPartsBySingleAndFullHeirachicalId);
                    if (partVariable != null && part != null) {
                        return () -> partVariable.getValue(part);
                    } else {
                        EMFUtils.logError("no part found for: [" + variableKey + "] in [" + calculationInstance.modelName + "]. Available parts were: " + calculationInstance.allPartsBySingleAndFullHeirachicalId.keySet());
                        optimizedAlternativeToThis = MathConstant.ZERO;
                        return () -> 0;
                        //throw new EMFMathException("no part variable found for: ["+variableKey+"] in ["+calculationInstance.modelName+"] + "+ calculationInstance.allPartByName.keySet());
                    }

                }
                //process float variable  e.g.   var.asdf
                if (variableKey.matches("(var|varb)\\.\\w+")) {
                    EMFAnimation variableCalculator = calculationInstance.emfAnimationVariables.get(variableKey);
                    if (variableCalculator != null) {
                        if (invertBooleans && variableKey.startsWith("varb.")) {
                            return () -> variableCalculator.getLastResultOnly() == 1 ? 0 : 1;
                        } else {
                            return variableCalculator::getLastResultOnly;
                        }
                    } else {
                        ArrayList<String> vars = new ArrayList<>();
                        for (String var :
                                calculationInstance.emfAnimationVariables.keySet()) {
                            if (var.startsWith("var.") || var.startsWith("varb.")) vars.add(var);
                        }
                        EMFUtils.logError("no animation variable found for: [" + variableKey + "] in [" + calculationInstance.modelName + "]. Available variables were: " + vars);
                        optimizedAlternativeToThis = MathConstant.ZERO;
                        return () -> 0;
                        //throw new EMFMathException("no variable animation found for: ["+variableKey+"] in ["+calculationInstance.modelName+"] + "+ calculationInstance.emfAnimationVariables.keySet());
                    }
                }

                //process render variable  e.g.   render.shadow_size
                if (variableKey.matches("(render)\\.\\w+")) {
                    EMFAnimation renderVariableCalculator = calculationInstance.emfAnimationVariables.get(variableKey);
                    if (renderVariableCalculator != null) {
                        return renderVariableCalculator::getLastResultOnly;
                    } else {
                        //try get default
                        EMFModelOrRenderVariable variable = EMFModelOrRenderVariable.getRenderVariable(variableKey);
                        if (variable != null && variable.isRenderVariable())
                            return variable::getValue;
                        EMFUtils.logError("no render variable found for: [" + variableKey + "]");
                        optimizedAlternativeToThis = MathConstant.ZERO;
                        return () -> 0;
                        //throw new EMFMathException("no variable animation found for: ["+variableKey+"] in ["+calculationInstance.modelName+"] + "+ calculationInstance.emfAnimationVariables.keySet());
                    }
                }

                String s = "ERROR: could not identify EMF animation variable [" + variableKey + "] for [" + calculationInstance.animKey + "] in [" + calculationInstance.modelName + "].";
                System.out.println(s);
                throw new EMFMathException(s);
            }
        }
    }


    private ValueSupplier getBooleanAsFloat(BoolSupplierPrimitive boolGetter) {
        return () -> (invertBooleans != boolGetter.get()) ? 1f : 0f;
    }

    @Override
    public ValueSupplier getSupplier() {
        return () -> valueSupplier.get();
    }


    @Override
    public String toString() {
        return variableName + "=" + get();
    }

    private interface BoolSupplierPrimitive {
        boolean get();
    }
}
