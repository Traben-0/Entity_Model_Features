package traben.entity_model_features.models.animation.EMFAnimationMathParser;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationVariableSuppliers;
import traben.entity_model_features.models.animation.EMFDefaultModelVariable;
import traben.entity_model_features.utils.EMFModelPart3;
import traben.entity_model_features.utils.EMFUtils;


public class MathVariable extends MathValue implements  MathComponent{


    ValueSupplier valueSupplier;


    final String variableName;

    public boolean isOtherAnimVariable = false;

    private boolean invertBooleans = false;

    public static MathComponent getOptimizedVariable(String value, boolean isNegative, EMFAnimation calculationInstance) throws EMFMathException{
        MathVariable method = new MathVariable(value, isNegative, calculationInstance);
        if(method.optimizedAlternativeToThis == null)
            return method;
        return method.optimizedAlternativeToThis;
    }

    public MathComponent optimizedAlternativeToThis = null;

    private MathVariable(String value, boolean isNegative, EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance);

        variableName = value;
        //valueSupplier = ()->0d;

        if(value.startsWith("!")){
            value=value.replaceFirst("!","");
            invertBooleans = true;
        }

        EMFAnimationVariableSuppliers getter = calculationInstance.variableSuppliers;

        //discover supplier needed
        valueSupplier = switch (value){
            case "limb_swing" -> getter::getLimbAngle;
            case "frame_time" -> getter::getFrameTime;
            case "limb_speed" -> getter::getLimbDistance;
            case "age" -> getter::getAge;
            case "head_pitch" -> getter::getHeadPitch;
            case "head_yaw" -> getter::getHeadYaw;
            case "swing_progress" -> getter::getSwingProgress;
            case "hurt_time" -> getter::getHurtTime;
            case "dimension" -> getter::getDimension;
            case "time" -> getter::getTime;
            case "player_pos_x" -> getter::getPlayerX;
            case "player_pos_y" -> getter::getPlayerY;
            case "player_pos_z" -> getter::getPlayerZ;
            case "pos_x" -> getter::getEntityX;
            case "pos_y" -> getter::getEntityY;
            case "pos_z" -> getter::getEntityZ;
            case "player_rot_x" -> getter::getPlayerRX;
            case "player_rot_y" -> getter::getPlayerRY;
            case "rot_x" -> getter::getEntityRX;
            case "rot_y" -> getter::getEntityRY;
            case "health" -> getter::getHealth;
            case "death_time" -> getter::getDeathTime;
            case "anger_time" -> getter::getAngerTime;
            case "max_health" -> getter::getMaxHealth;
            case "id" -> getter::getId;


            case "collisionX" -> getter::getClosestCollisionX;
            case "collisionY" -> getter::getClosestCollisionY;
            case "collisionZ" -> getter::getClosestCollisionZ;

            case "is_climbing" -> getBooleanAsFloat(getter::isClimbing);
            //constants
//            case "pi" -> ()->PI;//3.1415926f;
//            case "true" ->  ()-> invertBooleans ? 0f : 1f;
//            case "false" -> ()-> invertBooleans ? 1f : 0f;

            //boolean
            case "is_child" -> getBooleanAsFloat(getter::isChild);
            case "is_in_water" -> getBooleanAsFloat(getter::isInWater);
            case "is_riding" -> getBooleanAsFloat(getter::isRiding);
            case "is_on_ground" -> getBooleanAsFloat(getter::isOnGround);
            case "is_burning" -> getBooleanAsFloat(getter::isBurning);
            case "is_alive" -> getBooleanAsFloat(getter::isAlive);
            case "is_glowing" -> getBooleanAsFloat(getter::isGlowing);
            case "is_aggressive" -> getBooleanAsFloat(getter::isAggressive);
            case "is_hurt" -> getBooleanAsFloat(getter::isHurt);
            case "is_in_hand" -> getBooleanAsFloat(getter::isInHand);
            case "is_in_item_frame" -> getBooleanAsFloat(getter::isInItemFrame);
            case "is_in_ground" -> getBooleanAsFloat(getter::isInGround);
            case "is_in_gui" -> getBooleanAsFloat(getter::isInGui);
            case "is_in_lava" -> getBooleanAsFloat(getter::isInLava);
            case "is_invisible" -> getBooleanAsFloat(getter::isInvisible);
            case "is_on_head" -> getBooleanAsFloat(getter::isOnHead);
            case "is_on_shoulder" -> getBooleanAsFloat(getter::isOnShoulder);
            case "is_ridden" -> getBooleanAsFloat(getter::isRidden);
            case "is_sitting" -> getBooleanAsFloat(getter::isSitting);
            case "is_sneaking" -> getBooleanAsFloat(getter::isSneaking);
            case "is_sprinting" -> getBooleanAsFloat(getter::isSprinting);
            case "is_tamed" -> getBooleanAsFloat(getter::isTamed);
            case "is_wet" -> getBooleanAsFloat(getter::isWet);

            //unknown variable
            default -> getVariable(value,getter);
        };
    }

    //final float PI = (float) Math.PI;


    private static final MathConstant TRUE_CONSTANT = new MathConstant(1);
    private static final MathConstant FALSE_CONSTANT = new MathConstant(0);
    private static final MathConstant PI_CONSTANT = new MathConstant(Math.PI);
    private static final MathConstant PI_NEGATIVE_CONSTANT = new MathConstant(Math.PI,true);

    private ValueSupplier getVariable(String variableKey, EMFAnimationVariableSuppliers getter) throws EMFMathException {
//            case "pi" -> ()->PI;//3.1415926f;
//            case "true" ->  ()-> invertBooleans ? 0f : 1f;
//            case "false" -> ()-> invertBooleans ? 1f : 0f;
        switch(variableKey){
            case "pi"-> {
                optimizedAlternativeToThis = isNegative ? PI_NEGATIVE_CONSTANT : PI_CONSTANT;
                return ()-> Math.PI;
            }
            case "true"-> {
                float bool = invertBooleans ? 0f : 1f;
                //optimizedAlternativeToThis = new MathConstant(bool);
                optimizedAlternativeToThis = invertBooleans ? FALSE_CONSTANT : TRUE_CONSTANT;
                return ()-> bool;
            }
            case "false"-> {
                float bool = invertBooleans ? 1f : 0f;
                //optimizedAlternativeToThis = new MathConstant(bool);
                optimizedAlternativeToThis = invertBooleans ? TRUE_CONSTANT : FALSE_CONSTANT;
                return ()-> bool;
            }
            default -> {
                //process model part variable   e.g.  head.rx
                if(variableKey.matches("[a-zA-Z0-9_]+\\.([trs][xyz]$|visible$|visible_boxes$)")){
                    String[] split = variableKey.split("\\.");//todo only works with one split point
                    String partName = split[0];
                    EMFDefaultModelVariable partVariable = EMFDefaultModelVariable.get(split[1]);
                    EMFModelPart3 part = calculationInstance.allPartByName.get(partName);
                    if(partVariable != null && part!= null){
                        return ()-> partVariable.getFrom3Model(part, calculationInstance.partToApplyTo );
                    }else{
                        EMFUtils.EMF_modError("no part variable found for: ["+variableKey+"] in ["+calculationInstance.modelName+"] + "+ calculationInstance.allPartByName.keySet());
                        return ()-> 0;
                        //throw new EMFMathException("no part variable found for: ["+variableKey+"] in ["+calculationInstance.modelName+"] + "+ calculationInstance.allPartByName.keySet());
                    }

                }
                //process float variable  e.g.   var.asdf
                if(variableKey.matches("(var|varb)\\.\\w+")) {
                    EMFAnimation variableCalculator = calculationInstance.emfAnimationVariables.get(variableKey);
                    if(variableCalculator != null){
                        return ()-> variableCalculator.getLastResultOnly(getter.getEntity());
                    }else{
                        EMFUtils.EMF_modError("no variable animation found for: ["+variableKey+"] in ["+calculationInstance.modelName+"] + "+ calculationInstance.emfAnimationVariables.keySet());
                        return ()-> 0;
                        //throw new EMFMathException("no variable animation found for: ["+variableKey+"] in ["+calculationInstance.modelName+"] + "+ calculationInstance.emfAnimationVariables.keySet());
                    }
                }
//                //process boolean variable  e.g.   varb.asdf
//                if(variableKey.matches("varb\\.\\w+")) {
//                    EMFAnimation variableCalculator = calculationInstance.emfAnimationVariables.get(variableKey);
//                    if(variableCalculator != null){
//                        return ()-> variableCalculator.getLastResultOnly(getter.getEntity());
//                    }else{
//                        throw new EMFMathException("no part variable found for: ["+variableKey+"]");
//                    }
//                }
                String s = "ERROR: could not identify EMF animation variable ["+variableKey+"] for ["+calculationInstance.animKey+"] in ["+calculationInstance.modelName+"].";
                System.out.println(s);
                throw new EMFMathException(s);
            }
        }
    }


    private ValueSupplier getBooleanAsFloat(BoolSupplierPrimitive boolGetter){
        return ()->{
            boolean value = invertBooleans != boolGetter.get();

            return value ? 1d: 0d;
        };
    }

    @Override
    public ValueSupplier getSupplier() {
        return ()->valueSupplier.get();
    }



    @Override
    public String toString() {
        return variableName+"="+get();
    }

    private interface BoolSupplierPrimitive{
        boolean get();
    }
}
