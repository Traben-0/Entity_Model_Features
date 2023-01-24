package traben.entity_model_features.models.anim;

import net.minecraft.client.model.ModelPart;
import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.mXparser;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.EMF_ModelPart;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.regex.Matcher;

public class AnimationCalculationMXParser extends AnimationCalculation{

    private final Expression calculator = new Expression();

    //private final String expressionString;


    public AnimationCalculationMXParser(EMF_EntityModel<?> parent, ModelPart part, AnimVar varToChange, String animKey, String initialExpression) {
        super(parent,part,varToChange,animKey);
        //calculator = new Expression(initialExpression);
        addRequiredLogic(initialExpression);
    }

    //optimize so we dont calculate multiple times

    @Override
    float calculatorRun() {
        return (float) calculator.calculate();
    }

    @Override
    public void setVerbose(boolean val) {
        if(val)
            calculator.setVerboseMode();
        else
            calculator.setSilentMode();
    }
    @Override
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

    private String iterateProcessIfs(String originalExpression){
        //optifine if(multiple) logic must be exrapolated into MXparser iff logic
///optifine  if(cond, val, cond2, val2, val_else)
///MXParser  iff(cond, val;cond2, val2;cond3, val3)

        //get first instance of if( and all contents to one string also calculate top lvl spliting
        StringBuilder ifBuilder = new StringBuilder();ifBuilder.append("if(");
        String expressionStaringAtIf = originalExpression.replaceFirst(".*?if\\(","");
        int foundOpenBrackets = 0;
        for (char ch:
             expressionStaringAtIf.toCharArray()) {
            if(ch == ')'){
                if (foundOpenBrackets == 0){
                    //ifBuilder.append(')');
                    break;
                }else{
                    --foundOpenBrackets;
                }
            }else if(ch == '(') {
                ++foundOpenBrackets;
            }

            if(ch == ','){
                ifBuilder.append(foundOpenBrackets == 0 ? CONFIRMED_TOP_LVL_SPLIT_SYMBOL : ch);
            }else{
                    ifBuilder.append(ch);
            }
        }
        //here we have if contents with ™ replacing the split points
        //BELOW IS "if(a,b,c,d,e,f..." no ')'
        String originalIfContentString = ifBuilder.toString().replaceAll(String.valueOf(CONFIRMED_TOP_LVL_SPLIT_SYMBOL),",");// + ")";
        String[] ifContents = ifBuilder.toString().replaceFirst("if\\(","").split(String.valueOf(CONFIRMED_TOP_LVL_SPLIT_SYMBOL));
       // get each if contents to one string and parse then replace all of them
        if(ifContents.length == 3){
            //safe normal optifine if statement
           // System.out.println("correct optifine if =");
            for (String str:
                 ifContents) {
              //  System.out.println("="+str);
            }
            originalExpression = originalExpression.replaceFirst("if\\(",CONFIRMED_IF_SYMBOL+'(');
        }else if (ifContents.length % 2 == 1 && ifContents.length > 3){
            //convert
            ///optifine  if(cond, val, cond2, val2, val_else)
            ///MXParser iff(cond, val;cond2, val2;cond3, val3)
            StringBuilder newIffBuilder = new StringBuilder();
            newIffBuilder.append(CONFIRMED_IFF_SYMBOL+"(");
            for (int i = 0; i < ifContents.length; i++) {
                if(i == ifContents.length-1){
                    //add last leg if condition mxparser requires
                    newIffBuilder.append("1==1, ").append(ifContents[i]);
                }else if(i % 2 == 1){
                    //if(i != ifContents.length-1)
                        newIffBuilder.append(ifContents[i]).append(';');
                }else{
                    newIffBuilder.append(ifContents[i]).append(',');
                }
            }
            //here "if(a,b,c,d,e" should have become
            //  "¥EMF¥(a,b;c,d;1==1,e"
           // System.out.println("replacing IFF =\n"+originalExpression+"\n"+ originalIfContentString+"\n"+newIffBuilder.toString());
            originalExpression = originalExpression.replace(originalIfContentString,newIffBuilder.toString());
        }else{
            System.out.println("if statement broken in animation "+ animKey+"found =");
            for (String str:
                 ifContents) {
                System.out.println("= "+str);
            }

            return "broken_if_statement";
        }
        //here the first if statement has been fully replaced with a valid mxparser iff except with CONFIRMED SYMBOLS replacing iff

        //iterate cannot have top level split replacements
        if(originalExpression.contains("if(")){
            //iterate endlessly until all ifs resolved
            originalExpression = iterateProcessIfs(originalExpression);
        }

        return originalExpression;
    }

    private String processIfs(String originalExpression){
       // System.out.println("EMF iff fix start = " + originalExpression);
        String processedString = iterateProcessIfs(originalExpression);
        //all ifs verified
        //replace my replacment placeholders
        processedString = processedString.replaceAll(CONFIRMED_IF_SYMBOL,"if").replaceAll(CONFIRMED_IFF_SYMBOL,"iff");
       // System.out.println("EMF iff fix result = " + processedString);
        return processedString;
    }

    private final char CONFIRMED_TOP_LVL_SPLIT_SYMBOL = '™';
    private final String CONFIRMED_IF_SYMBOL = "¶EMF¶";
    private final String CONFIRMED_IFF_SYMBOL = "¥EMF¥";

    private void addRequiredLogic(String expressionString) {
        //only add necessary extra functions to calculator

        //optifine if(multiple) logic must be exrapolated into MXparser iff logic
///optifine  if(cond, val, cond2, val2, val_else)
///MXParser  iff(cond, val;cond2, val2;cond3, val3)
        if(expressionString.contains("if(")){
            expressionString = processIfs(expressionString);
        }

        if (expressionString.contains("torad(")) {
            calculator.addFunctions(new Function("torad(x) = x * 0.01745329251"));
        }
        if (expressionString.contains("between(")) {
            calculator.addFunctions(new Function("between(x,y,z) = if(x < y, 0, if(x > z, 0, 1))"));
        }
        if (expressionString.contains("equals(")) {
            calculator.addFunctions(new Function("equals(x,y,z) = if(x <= y -z, 0, if(x >= y -z, 0, 1))"));
        }
        if (expressionString.contains("in(")) {
            //todo needs better implementation
            calculator.addFunctions(new Function("in(x,y,z) = if(x==y, 1, if(x==z, 1, 0))"));
           // calculator.addFunctions(new Function("in(x,y,z,a) = if(x==y, 1, if(x==z, 1, if(x==a, 1, 0))"));
           // calculator.addFunctions(new Function("in(x,y,z,a,b) = if(x==y, 1, if(x==z, 1, if(x==a, 1,  if(x==b, 1, 0))"));
           // calculator.addFunctions(new Function("in(x,y,z,a,b,c) = if(x==y, 1, if(x==z, 1, if(x==a, 1,  if(x==b, 1, if(x==c, 1, 0))"));
           // calculator.addFunctions(new Function("in(x,y,z,a,b,c,d) = if(x==y, 1, if(x==z, 1, if(x==a, 1,  if(x==b, 1, if(x==c, 1,  if(x==d, 1, 0)))"));
           // calculator.addFunctions(new Function("in(x,y,z,a,b,c,d,e) = if(x==y, 1, if(x==z, 1, if(x==a, 1,  if(x==b, 1, if(x==c, 1,  if(x==d, 1, if(x==e, 1, 0))))"));
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
        if (expressionString.contains("hurt_time")) {
            calculator.addConstants(constantOf("hurt_time", this::getHurtTime));
        }
        if (expressionString.contains("dimension")) {
            calculator.addConstants(constantOf("dimension", this::getDimension));
        }
        if (expressionString.contains("time")) {
            calculator.addConstants(constantOf("time", this::getTime));
        }
        if (expressionString.contains("player_pos_x")) {
            calculator.addConstants(constantOf("player_pos_x", this::getPlayerX));
        }
        if (expressionString.contains("player_pos_y")) {
            calculator.addConstants(constantOf("player_pos_y", this::getPlayerY));
        }
        if (expressionString.contains("player_pos_z")) {
            calculator.addConstants(constantOf("player_pos_z", this::getPlayerZ));
        }
        if (expressionString.contains("pos_x")) {
            calculator.addConstants(constantOf("pos_x", this::getEntityX));
        }
        if (expressionString.contains("pos_y")) {
            calculator.addConstants(constantOf("pos_y", this::getEntityY));
        }
        if (expressionString.contains("pos_z")) {
            calculator.addConstants(constantOf("pos_z", this::getEntityZ));
        }
        if (expressionString.contains("player_rot_x")) {
            calculator.addConstants(constantOf("player_rot_x", this::getPlayerRX));
        }
        if (expressionString.contains("player_rot_y")) {
            calculator.addConstants(constantOf("player_rot_y", this::getPlayerRY));
        }
        if (expressionString.contains("rot_x")) {
            calculator.addConstants(constantOf("rot_x", this::getEntityRX));
        }
        if (expressionString.contains("rot_y")) {
            calculator.addConstants(constantOf("rot_y", this::getEntityRY));
        }
        if (expressionString.contains("health")) {
            calculator.addConstants(constantOf("health", this::getHealth));
        }
        if (expressionString.contains("death_time")) {
            calculator.addConstants(constantOf("death_time", this::getDeathTime));
        }
        if (expressionString.contains("anger_time")) {
            calculator.addConstants(constantOf("anger_time", this::getAngerTime));
        }
        if (expressionString.contains("max_health")) {
            calculator.addConstants(constantOf("max_health", this::getMaxHealth));
        }
        if (expressionString.contains("id")) {
            calculator.addConstants(constantOf("id", this::getId));
        }

        //////booleans
        expressionString = expressionString.replaceAll("!(?=[\\w])","NOT_");
        if (expressionString.contains("is_child")) {
            constantAddBooleans("is_child", this::isChild);
        }
        if (expressionString.contains("is_in_water")) {
            constantAddBooleans("is_in_water", this::isInWater);
        }
        if (expressionString.contains("is_riding")) {
            constantAddBooleans("is_riding", this::isRiding);
        }
        if (expressionString.contains("is_on_ground")) {
            constantAddBooleans("is_on_ground", this::isOnGround);
        }
        if (expressionString.contains("is_burning")) {
            constantAddBooleans("is_burning", this::isBurning);
        }
        if (expressionString.contains("is_alive")) {
            constantAddBooleans("is_alive", this::isAlive);
        }
        if (expressionString.contains("is_glowing")) {
            constantAddBooleans("is_glowing", this::isGlowing);
        }
        if (expressionString.contains("is_aggressive")) {
            constantAddBooleans("is_aggressive", this::isAggressive);
        }
        if (expressionString.contains("is_hurt")) {
            constantAddBooleans("is_hurt", this::isHurt);
        }
        if (expressionString.contains("is_in_hand")) {
            constantAddBooleans("is_in_hand", this::isInHand);
        }
        if (expressionString.contains("is_in_item_frame")) {
            constantAddBooleans("is_in_item_frame", this::isInItemFrame);
        }
        if (expressionString.contains("is_in_ground")) {
            constantAddBooleans("is_in_ground", this::isInGround);
        }
        if (expressionString.contains("is_in_gui")) {
            constantAddBooleans("is_in_gui", this::isInGui);
        }
        if (expressionString.contains("is_in_lava")) {
            constantAddBooleans("is_in_lava", this::isInLava);
        }
        if (expressionString.contains("is_invisible")) {
            constantAddBooleans("is_invisible", this::isInvisible);
        }
        if (expressionString.contains("is_on_head")) {
            constantAddBooleans("is_on_head", this::isOnHead);
        }
        if (expressionString.contains("is_on_shoulder")) {
            constantAddBooleans("is_on_shoulder", this::isOnShoulder);
        }
        if (expressionString.contains("is_ridden")) {
            constantAddBooleans("is_ridden", this::isRidden);
        }
        if (expressionString.contains("is_sitting")) {
            constantAddBooleans("is_sitting", this::isSitting);
        }
        if (expressionString.contains("is_sneaking")) {
            constantAddBooleans("is_sneaking", this::isSneaking);
        }
        if (expressionString.contains("is_sprinting")) {
            constantAddBooleans("is_sprinting", this::isSprinting);
        }
        if (expressionString.contains("is_tamed")) {
            constantAddBooleans("is_tamed", this::isTamed);
        }
        if (expressionString.contains("is_wet")) {
            constantAddBooleans("is_wet", this::isWet);
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
                    calculator.addConstants(new Constant(otherKeyReplace + " = 0") {
                        @Override
                        public double getConstantValue() {
                            return getEntity() == null? 0 : prevResults.getFloat(getEntity().getUuid());
                        }
                    });

                } else {
                    //AnimationCalculation instance = this;
                    calculator.addConstants(new Constant(otherKeyReplace + " = 0") {
                        @Override
                        public double getConstantValue() {

                            //                            instance::getEntity,
//                                    instance::getLimbAngle,
//                                    instance::getLimbDistance,
//                                    instance::getAnimationProgress,
//                                    instance::getHeadYaw,
//                                    instance::getHeadPitch,
//                                    instance::getTickDelta);
                            //if(otherKey.contains("head.")){

                               // System.out.println("get other key value returns ="+ otherKey+" = " + returned);
                            //}

                            EMF_ModelPart partParent = modelPart == null? null : modelPart.parent;

                            return parentModel.getAnimationResultOfKey(partParent
                                    ,otherKey,
                                    getEntity(),
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
        Matcher m2 = PATTERN_FOR_STANDALONE_VAR.matcher(expressionString);
        foundStrings = new ArrayList<>();
        while(m2.find()) {

            //check if already cached result
            String varKey = m2.group();
            if(!foundStrings.contains(varKey)) {
                foundStrings.add(varKey);

                String varKeyReplace = varKey.replace(".", "_");

                newExpressionString = newExpressionString.replaceAll(varKey, varKeyReplace);
                //AnimationCalculation instance = this;
                calculator.addConstants(new Constant(varKeyReplace + " = 0") {
                    @Override
                    public double getConstantValue() {
                        EMF_ModelPart partParent = modelPart == null? null : modelPart.parent;

                        return parentModel.getAnimationResultOfKey(partParent
                                ,varKey,
                                getEntity(),
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
        //todo this might work implicitly as booleans seem to be handled as 0|1, check though
        Matcher m3 = PATTERN_FOR_STANDALONE_VAR_B.matcher(expressionString);
        foundStrings = new ArrayList<>();
        while(m3.find()) {

            //check if already cached result
            String varKey = m3.group();
            if(!foundStrings.contains(varKey)) {
                foundStrings.add(varKey);

                String varKeyReplace = varKey.replace(".", "_");

                newExpressionString = newExpressionString.replaceAll(varKey, varKeyReplace);
                //AnimationCalculation instance = this;
                calculator.addConstants(new Constant(varKeyReplace + " = 0") {
                    @Override
                    public double getConstantValue() {
                        EMF_ModelPart partParent = modelPart == null? null : modelPart.parent;

                        return parentModel.getAnimationResultOfKey(partParent
                                ,varKey,
                                getEntity(),
                                getLimbAngle(),
                                getLimbDistance(),
                                getAnimationProgress(),
                                getHeadYaw(),
                                getHeadPitch(),
                                getTickDelta()) == 0? 0: 1;//todo actually test this
                    }
                });
            }
        }
        calculator.setExpressionString(newExpressionString);
    }

    private Constant constantOf(String variableName, Supplier<Float> supplier) {
        return new Constant(variableName + " = 0") {
            @Override
            public double getConstantValue() {
                double result =supplier.get();
               // System.out.println("get variable value returns ="+ variableName+" = " + result);
                return result;
            }
        };
    }

    private void constantAddBooleans(String variableName, Supplier<Boolean> supplier) {
        calculator.addConstants(new Constant(variableName + " = 0") {
            @Override
            public double getConstantValue() {
                if(supplier == null) return 0;
                boolean val = supplier.get();
               // System.out.println("get variable value returns ="+ variableName+" = " + val);
                return  val ? 1 : 0;
            }
        }
        );
        //add !not version
        calculator.addConstants(new Constant("NOT_"+variableName + " = 0") {
                                    @Override
                                    public double getConstantValue() {
                                        if(supplier == null) return 1;
                                        boolean val = supplier.get();
                                       // System.out.println("get variable value returns ="+ variableName+" = " + val);
                                        return  val ? 0 : 1;
                                    }
                                }
        );
    }
}
