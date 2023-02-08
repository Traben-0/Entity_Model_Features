package traben.entity_model_features.models.anim.EMFParser;

import traben.entity_model_features.models.EMF_ModelPart;
import traben.entity_model_features.models.anim.AnimationCalculation;
import traben.entity_model_features.models.anim.AnimationGetters;


public class MathVariableUpdatable extends MathValue implements  MathComponent{


    ValueSupplier valueSupplier;


    final String variableName;

    public boolean isOtherAnimVariable = false;

    private boolean invertBooleans = false;

    public static MathComponent getOptimizedVariable(String value, boolean isNegative, AnimationCalculation calculationInstance) throws EMFMathException{
        MathVariableUpdatable method = new MathVariableUpdatable(value, isNegative, calculationInstance);
        if(method.optimizedAlternativeToThis == null)
            return method;
        return method.optimizedAlternativeToThis;
    }

    public MathComponent optimizedAlternativeToThis = null;

    private MathVariableUpdatable(String value, boolean isNegative, AnimationCalculation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance);

        variableName = value;
        //valueSupplier = ()->0d;

        if(value.startsWith("!")){
            value=value.replaceFirst("!","");
            invertBooleans = true;
        }

        AnimationGetters getter = calculationInstance.parentModel.animationGetters;

        //discover supplier needed
        valueSupplier = switch (value){
            case "limb_swing" -> getter::getLimbAngle;
            case "frame_time" -> getter::getTickDelta;
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


    private ValueSupplier getVariable(String variableKey, AnimationGetters getter) throws EMFMathException {
//            case "pi" -> ()->PI;//3.1415926f;
//            case "true" ->  ()-> invertBooleans ? 0f : 1f;
//            case "false" -> ()-> invertBooleans ? 1f : 0f;
        switch(variableKey){
            case "pi"-> {
                optimizedAlternativeToThis = new MathVariableConstant(Math.PI,isNegative);
                return ()->Math.PI;
            }
            case "true"-> {
                float bool = invertBooleans ? 0f : 1f;
                optimizedAlternativeToThis = new MathVariableConstant(bool);
                return ()-> bool;
            }
            case "false"-> {
                float bool = invertBooleans ? 1f : 0f;
                optimizedAlternativeToThis = new MathVariableConstant(bool);
                return ()-> bool;
            }
            default -> {
                //process model part variable   e.g.  head.rx
                if(variableKey.matches("[a-zA-Z0-9_]+\\.([trs][xyz]$|visible$|visible_boxes$)")){
                    //System.out.println("found and setup for otherKey :" + variableKey);
//            if (variableKey.equals(calculationInstance.animKey)) {
//                //todo check this
//                if (calculationInstance.vanillaModelPart != null && calculationInstance.varToChange != null) {
//                    return () -> (float) (calculationInstance.varToChange.getFromVanillaModel(calculationInstance.vanillaModelPart));
//                }else{
//                    return () -> (float) (calculationInstance.getEntity() == null ? 0 : calculationInstance.prevResults.getFloat(calculationInstance.getEntity().getUuid()));
//                }
//            } else {
                    EMF_ModelPart partParent = calculationInstance.modelPart == null? null : calculationInstance.modelPart.parent;
                    isOtherAnimVariable = true;
                    AnimationValueSupplier SUPPLIER = calculationInstance.parentModel.getAnimationResultOfKeyAsSupplier(partParent, variableKey);
                    return () -> SUPPLIER.get(getter.getEntity());

//            }

                }
                //process float variable  e.g.   var.asdf
                if(variableKey.matches("var\\.\\w+")) {
                    if (variableKey.equals(calculationInstance.animKey)) {
                        return () ->  (getter.getEntity() == null ? 0 : calculationInstance.prevResult.getFloat(getter.getEntity().getUuid()));
                    }else {
                        // EMF_ModelPart partParent = calculationInstance.modelPart == null ? null : calculationInstance.modelPart.parent;
                        isOtherAnimVariable = true;
                        AnimationValueSupplier SUPPLIER = calculationInstance.parentModel.getAnimationResultOfKeyOptimiseForVariableAsSupplier(variableKey );
                        return () -> SUPPLIER.get(getter.getEntity());
                    }

                }
                //process boolean variable  e.g.   varb.asdf
                if(variableKey.matches("varb\\.\\w+")) {
                    if (variableKey.equals(calculationInstance.animKey)) {
                        return () -> (getter.getEntity() == null ? 0 : calculationInstance.prevResult.getFloat(getter.getEntity().getUuid()));
                    }else {
                        //EMF_ModelPart partParent = calculationInstance.modelPart == null ? null : calculationInstance.modelPart.parent;
                        isOtherAnimVariable = true;
                        AnimationValueSupplier SUPPLIER = calculationInstance.parentModel.getAnimationResultOfKeyOptimiseForVariableAsSupplier(  variableKey);
                        return () ->  SUPPLIER.get(getter.getEntity()) == (invertBooleans ? 1 : 0) ? 0 : 1;
                    }

                }
                String s = "ERROR: could not identify EMF animation variable ["+variableKey+"] for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
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
