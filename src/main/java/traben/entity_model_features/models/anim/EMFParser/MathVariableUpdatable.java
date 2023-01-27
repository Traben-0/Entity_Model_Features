package traben.entity_model_features.models.anim.EMFParser;

import traben.entity_model_features.models.EMF_ModelPart;
import traben.entity_model_features.models.anim.AnimationCalculation;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.regex.Matcher;



public class MathVariableUpdatable extends MathValue implements Supplier<Float> , MathComponent{


    Supplier<Float> valueSupplier;


    final String variableName;

    private boolean invertBooleans = false;

    public MathVariableUpdatable(String value, boolean isNegative, AnimationCalculation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance);

        variableName = value;
        //valueSupplier = ()->0d;

        if(value.startsWith("!")){
            value=value.replaceFirst("!","");
            invertBooleans = true;
        }
        //discover supplier needed
        valueSupplier = switch (value){
            case "limb_swing" -> calculationInstance::getLimbAngle;
            case "frame_time" -> calculationInstance::getTickDelta;
            case "limb_speed" -> calculationInstance::getLimbDistance;
            case "age" -> calculationInstance::getAge;
            case "head_pitch" -> calculationInstance::getHeadPitch;
            case "head_yaw" -> calculationInstance::getHeadYaw;
            case "swing_progress" -> calculationInstance::getSwingProgress;
            case "hurt_time" -> calculationInstance::getHurtTime;
            case "dimension" -> calculationInstance::getDimension;
            case "time" -> calculationInstance::getTime;
            case "player_pos_x" -> calculationInstance::getPlayerX;
            case "player_pos_y" -> calculationInstance::getPlayerY;
            case "player_pos_z" -> calculationInstance::getPlayerZ;
            case "pos_x" -> calculationInstance::getEntityX;
            case "pos_y" -> calculationInstance::getEntityY;
            case "pos_z" -> calculationInstance::getEntityZ;
            case "player_rot_x" -> calculationInstance::getPlayerRX;
            case "player_rot_y" -> calculationInstance::getPlayerRY;
            case "rot_x" -> calculationInstance::getEntityRX;
            case "rot_y" -> calculationInstance::getEntityRY;
            case "health" -> calculationInstance::getHealth;
            case "death_time" -> calculationInstance::getDeathTime;
            case "anger_time" -> calculationInstance::getAngerTime;
            case "max_health" -> calculationInstance::getMaxHealth;
            case "id" -> calculationInstance::getId;

            //constants
            case "pi" -> ()->(float)Math.PI;//3.1415926f;
            case "true" ->  ()-> invertBooleans ? 0f : 1f;
            case "false" -> ()-> invertBooleans ? 1f : 0f;

            //boolean
            case "is_child" -> getBooleanAsFloat(calculationInstance::isChild);
            case "is_in_water" -> getBooleanAsFloat(calculationInstance::isInWater);
            case "is_riding" -> getBooleanAsFloat(calculationInstance::isRiding);
            case "is_on_ground" -> getBooleanAsFloat(calculationInstance::isOnGround);
            case "is_burning" -> getBooleanAsFloat(calculationInstance::isBurning);
            case "is_alive" -> getBooleanAsFloat(calculationInstance::isAlive);
            case "is_glowing" -> getBooleanAsFloat(calculationInstance::isGlowing);
            case "is_aggressive" -> getBooleanAsFloat(calculationInstance::isAggressive);
            case "is_hurt" -> getBooleanAsFloat(calculationInstance::isHurt);
            case "is_in_hand" -> getBooleanAsFloat(calculationInstance::isInHand);
            case "is_in_item_frame" -> getBooleanAsFloat(calculationInstance::isInItemFrame);
            case "is_in_ground" -> getBooleanAsFloat(calculationInstance::isInGround);
            case "is_in_gui" -> getBooleanAsFloat(calculationInstance::isInGui);
            case "is_in_lava" -> getBooleanAsFloat(calculationInstance::isInLava);
            case "is_invisible" -> getBooleanAsFloat(calculationInstance::isInvisible);
            case "is_on_head" -> getBooleanAsFloat(calculationInstance::isOnHead);
            case "is_on_shoulder" -> getBooleanAsFloat(calculationInstance::isOnShoulder);
            case "is_ridden" -> getBooleanAsFloat(calculationInstance::isRidden);
            case "is_sitting" -> getBooleanAsFloat(calculationInstance::isSitting);
            case "is_sneaking" -> getBooleanAsFloat(calculationInstance::isSneaking);
            case "is_sprinting" -> getBooleanAsFloat(calculationInstance::isSprinting);
            case "is_tamed" -> getBooleanAsFloat(calculationInstance::isTamed);
            case "is_wet" -> getBooleanAsFloat(calculationInstance::isWet);

            //unknown variable
            default -> getVariable(value);
        };
    }


    private Supplier<Float> getVariable(String variableKey) throws EMFMathException {

        //todo there exists potential here to store the value here to prevent unnecessary repeated method calls with so many args, it is currently cached after the method calls

        //process model part variable   e.g.  head.rx
        if(variableKey.matches("[a-zA-Z0-9_]+\\.([trs][xyz]$|visible$|visible_boxes$)")){
            System.out.println("found and setup for otherKey :" + variableKey);
            if (variableKey.equals(calculationInstance.animKey)) {
                //todo check this
                if (calculationInstance.vanillaModelPart != null && calculationInstance.varToChange != null) {
                    return () -> (float) (calculationInstance.varToChange.getFromVanillaModel(calculationInstance.vanillaModelPart));
                }else{
                    return () -> (float) (calculationInstance.getEntity() == null ? 0 : calculationInstance.prevResults.getFloat(calculationInstance.getEntity().getUuid()));
                }
            } else {
                EMF_ModelPart partParent = calculationInstance.modelPart == null? null : calculationInstance.modelPart.parent;
                return () -> (float) calculationInstance.parentModel.getAnimationResultOfKey(partParent
                                ,variableKey,
                                calculationInstance.getEntity(),
                                calculationInstance.getLimbAngle(),
                                calculationInstance.getLimbDistance(),
                                calculationInstance.getAnimationProgress(),
                                calculationInstance.getHeadYaw(),
                                calculationInstance.getHeadPitch(),
                                calculationInstance.getTickDelta());
            }

        }
        //process float variable  e.g.   var.asdf
        if(variableKey.matches("var\\.\\w+")) {
            if (variableKey.equals(calculationInstance.animKey)) {
                return () -> (float) (calculationInstance.getEntity() == null ? 0 : calculationInstance.prevResults.getFloat(calculationInstance.getEntity().getUuid()));
            }else {
                EMF_ModelPart partParent = calculationInstance.modelPart == null ? null : calculationInstance.modelPart.parent;
                return () -> (float) calculationInstance.parentModel.getAnimationResultOfKey(partParent
                        , variableKey,
                        calculationInstance.getEntity(),
                        calculationInstance.getLimbAngle(),
                        calculationInstance.getLimbDistance(),
                        calculationInstance.getAnimationProgress(),
                        calculationInstance.getHeadYaw(),
                        calculationInstance.getHeadPitch(),
                        calculationInstance.getTickDelta());
            }

        }
        //process boolean variable  e.g.   varb.asdf
        if(variableKey.matches("varb\\.\\w+")) {
            if (variableKey.equals(calculationInstance.animKey)) {
                return () -> (float) (calculationInstance.getEntity() == null ? 0 : calculationInstance.prevResults.getFloat(calculationInstance.getEntity().getUuid()));
            }else {
                EMF_ModelPart partParent = calculationInstance.modelPart == null ? null : calculationInstance.modelPart.parent;
                return () -> (float) (calculationInstance.parentModel.getAnimationResultOfKey(partParent
                        , variableKey,
                        calculationInstance.getEntity(),
                        calculationInstance.getLimbAngle(),
                        calculationInstance.getLimbDistance(),
                        calculationInstance.getAnimationProgress(),
                        calculationInstance.getHeadYaw(),
                        calculationInstance.getHeadPitch(),
                        calculationInstance.getTickDelta()) == (invertBooleans ? 1 : 0) ? 0 : 1);
            }

        }
        String s = "ERROR: could not identify EMF animation variable ["+variableKey+"] for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }


    private Supplier<Float> getBooleanAsFloat(Supplier<Boolean> boolGetter){
        return ()->{
            boolean value = invertBooleans != boolGetter.get();

            return value ? 1f: 0f;
        };
    }

    @Override
    public Supplier<Float> getSupplier() {
        return ()->valueSupplier.get();
    }



    @Override
    public String toString() {
        return get()+"";
    }
}
