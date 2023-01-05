package traben.entity_model_features.models;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.mXparser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AnimationCalculation {

    private final Expression calculator = new Expression();


    public Entity getEntity() {
        return entity;
    }
    public boolean isInWater() {
        return entity != null && entity.isTouchingWater();
    }
    public boolean isOnFire() {
        return entity != null && entity.isOnFire();
    }
    public boolean isRiding() {
        return entity != null && entity.hasVehicle();
    }

    public boolean isChild() {
        return entity != null && entity.isBaby();
    }
    public boolean isOnGround() {
        return entity != null && entity.isOnGround();
    }
    public float getSwingProgress() {
        return  entity == null ? 0 : entity.getHandSwingProgress(tickDelta);
    }

    public float getAge() {
        // return entity.age;
        return animationProgress;
    }

    public float getLimbAngle() {
        return limbAngle;
    }

    public float getLimbDistance() {
        return limbDistance;
    }

    public float getAnimationProgress() {
        return animationProgress;
    }

    public float getHeadYaw() {
        return headYaw;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    LivingEntity entity;
    float limbAngle=0;
    float limbDistance=0;
    float animationProgress=0;
    float headYaw=0;
    float headPitch=0;
    float tickDelta=0;

    private final EMF_CustomModelPart<LivingEntity> modelPart;

    private final EMF_CustomModel<LivingEntity> parent;
    private final AnimVar varToChange;
    private final String animKey;

    private final ObjectOpenHashSet<String> animKeysThatAreNeeded = new ObjectOpenHashSet<>();

    //private final String expressionString;
    AnimationCalculation(EMF_CustomModel<LivingEntity> parent,EMF_CustomModelPart<LivingEntity> part, AnimVar varToChange,String animKey,String initialExpression) {
        //expressionString = ;
        this.animKey = animKey;
        this.parent = parent;
        this.varToChange = varToChange;
        this.modelPart = part;
        //calculator = new Expression(initialExpression);
        addRequiredLogic(initialExpression);
    }

    //optimize so we dont calculate multiple times
    public double getResultOnly(LivingEntity entity0, float limbAngle0, float limbDistance0, float animationProgress0, float headYaw0, float headPitch0, float tickDelta0){
        //if we haven't already calculated a result this frame get another
        if (animationProgress0 != lastResultTick){
            entity = entity0;
            limbAngle = limbAngle0;
            limbDistance = limbDistance0;
            animationProgress = animationProgress0;
            headYaw = headYaw0;
            headPitch = headPitch0;
            tickDelta = tickDelta0;
            lastResultTick = animationProgress0;

            lastResult = calculator.calculate();
        }
        return lastResult;
    }

    public double getLastResult() {
        return lastResult;
    }

    private double lastResult = 0;
    private float lastResultTick = -1000;

    public void calculateAndSet(LivingEntity entity0, float limbAngle0, float limbDistance0, float animationProgress0, float headYaw0, float headPitch0, float tickDelta0){
        double result = getResultOnly( entity0,  limbAngle0,  limbDistance0,  animationProgress0,  headYaw0,  headPitch0,  tickDelta0);
        if(modelPart != null)
            varToChange.set(modelPart, Double.isNaN(result) ? null : result);

    }

    public boolean isValid(){
        boolean valid = this.calculator.checkSyntax();
        if(!valid) {
            calculator.setVerboseMode();
            mXparser.consolePrintln("EMF Animation syntax error:" );

            System.out.println(this.animKey +"="+ this.calculator.getExpressionString());

            this.calculator.checkSyntax();

            mXparser.consolePrintln(this.calculator.getErrorMessage());

            mXparser.consolePrintln("List of missing defined animation functions:");
            try {
                for (String fun : this.calculator.getMissingUserDefinedFunctions())
                    mXparser.consolePrintln("Function '" + fun + "' has to be defined");
            }catch (Exception e){
                System.out.println("issue = "+e);
            }
            mXparser.consolePrintln("List of missing defined animation arguments:");
            try {
                for (String arg : this.calculator.getMissingUserDefinedArguments())
                    mXparser.consolePrintln("Argument '" + arg + "' has to be defined");
            }catch (Exception e){
                System.out.println("issue = "+e);
            }

        }
        return valid;
    }

    private void addRequiredLogic(String expressionString) {
        //only add necessary extra functions to calculator
        if (expressionString.contains("torad(")) {
            calculator.addFunctions(new Function("torad(x) = x * 0.01745329251"));
        }
//        if (expressionString.contains("if(")) {
//            calculator.addFunctions(new Function("if(x,y,z,a,b) = iff(x,y;z,a;b)"));
//        }
//        if (expressionString.contains("if(")) {
//            calculator.addFunctions(new Function("if(x,y,z,a,b,c,d) = iff(x,y;z,a;b,c;d)"));
//        }
        if (expressionString.contains("clamp(")) {
            calculator.addFunctions(new Function("clamp(x,y,z) = if(x > y, if(x < z, x, z) ,y)"));
        }
        if (expressionString.contains("limb_swing")) {
            calculator.addConstants(constantOf("limb_swing", this::getLimbAngle));
        }
        if (expressionString.contains("frame_time")) {
            calculator.addConstants(constantOf("frame_time", this::getTickDelta));
        }
        if (expressionString.contains("limb_speed")) {
            calculator.addConstants(constantOf("limb_speed", this::getLimbDistance));
        }
        if (expressionString.contains("age")) {
            calculator.addConstants(constantOf("age", this::getAge));
        }
        if (expressionString.contains("head_pitch")) {
            calculator.addConstants(constantOf("head_pitch", this::getHeadPitch));
        }
        if (expressionString.contains("head_yaw")) {
            calculator.addConstants(constantOf("head_yaw", this::getHeadYaw));
        }
        if (expressionString.contains("swing_progress")) {
            calculator.addConstants(constantOf("swing_progress", this::getSwingProgress));
        }
        if (expressionString.contains("is_child")) {
            calculator.addConstants(constantOfBoolean("is_child", this::isChild));
        }
        if (expressionString.contains("is_in_water")) {
            calculator.addConstants(constantOfBoolean("is_in_water", this::isInWater));
        }
        if (expressionString.contains("is_riding")) {
            calculator.addConstants(constantOfBoolean("is_riding", this::isRiding));
        }
        if (expressionString.contains("is_on_ground")) {
            calculator.addConstants(constantOfBoolean("is_on_ground", this::isOnGround));
        }
        //todo extend these



        Matcher m = PATTERN_FOR_PART_VAR.matcher(expressionString);
        //System.out.println("matchedf");

        String newExpressionString = expressionString;
        ArrayList<String> foundStrings = new ArrayList<>();
        while(m.find()) {

            //check if already cached result
            String otherKey = m.group();
            if(!foundStrings.contains(otherKey)) {
                foundStrings.add(otherKey);
                String otherKeyReplace = otherKey.replace(".", "_");
                newExpressionString = newExpressionString.replaceAll(otherKey, otherKeyReplace);
                System.out.println("found and setup for otherKey :" + otherKey);
                if (otherKey.equals(this.animKey)) {
                    //todo copy vanilla part in future but use last value for now
                    calculator.addConstants(new Constant(otherKeyReplace + " = 0") {
                        @Override
                        public double getConstantValue() {
                            return getLastResult();
                        }
                    });

                } else {
                    calculator.addConstants(new Constant(otherKeyReplace + " = 0") {
                        @Override
                        public double getConstantValue() {
                            return parent.getAnimationResultOfKey(otherKey,
                                    (LivingEntity) getEntity(),
                                    getLimbAngle(),
                                    getLimbDistance(),
                                    getAnimationProgress(),
                                    getHeadYaw(),
                                    getHeadPitch(),
                                    getTickDelta());
                        }
                    });
                }
            }
        }
        calculator.setExpressionString(newExpressionString);
    }
    private static final Pattern PATTERN_FOR_PART_VAR = Pattern.compile("[a-zA-Z_-]+\\.[trs][xyz]");

    private Constant constantOf(String variableName, Supplier<Float> supplier) {
        return new Constant(variableName + " = 0") {
            @Override
            public double getConstantValue() {
                return supplier.get();
            }
        };
    }

    private Constant constantOfBoolean(String variableName, Supplier<Boolean> supplier) {
        return new Constant(variableName + " = 0") {
            @Override
            public double getConstantValue() {
                return supplier == null ? 0 : supplier.get() ? 1 : 0;
            }
        };
    }

    public enum AnimVar {
        tx,ty,tz,
        rx,ry,rz,
        sx,sy,sz,
        visible,
        visible_boxes,
        CUSTOM();


        public void set(EMF_CustomModelPart<?> part, Double value){
            switch (this){
                case tx -> {
                    part.tx = value;
                }
                case ty -> {
                    part.ty = value;
                }
                case tz -> {
                    part.tz = value;
                }
                case rx -> {
                    part.rx = value;
                }
                case ry -> {
                    part.ry = value;
                }
                case rz -> {
                    part.rz = value;
                }
                case sx -> {
                    part.sx = value;
                }
                case sy -> {
                    part.sy = value;
                }
                case sz -> {
                    part.sz = value;
                }
                case CUSTOM -> {
                    //todo pain.jpeg
                }
            }
        }

    }
}
