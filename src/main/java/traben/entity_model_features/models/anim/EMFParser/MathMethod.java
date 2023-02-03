package traben.entity_model_features.models.anim.EMFParser;

import net.minecraft.util.math.MathHelper;
import traben.entity_model_features.models.anim.AnimationCalculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MathMethod extends MathValue implements MathComponent{


    String methodName;
    public MathMethod(String methodName, String args, boolean isNegative, AnimationCalculation calculationInstance) throws EMFMathException {
        super(isNegative,calculationInstance);

        this.methodName = methodName;
        //first lets split the args into a list
        List<String> argsList = new ArrayList<>();

        int openBracketCount = 0;
        StringBuilder builder = new StringBuilder();
        for (char ch:
             args.toCharArray()) {
            switch (ch){
                case '(' -> {
                    openBracketCount++;
                    builder.append(ch);
                }
                case ')' -> {
                    openBracketCount--;
                    builder.append(ch);
                }
                case ',' -> {
                    if(openBracketCount == 0){
                        argsList.add(builder.toString());
                        builder = new StringBuilder();
                    }else{
                        builder.append(ch);
                    }
                }
                default -> builder.append(ch);
            }
        }
        argsList.add(builder.toString());
        //args list is now a list of top level arguments ready to be categorized into MathComponents depending on the method

        supplier = switch (methodName){
            case "if" -> EMF_IF(argsList);
            case "sin" -> SIN(argsList);
            case "asin" -> ASIN(argsList);
            case "cos" -> COS(argsList);
            case "acos" -> ACOS(argsList);
            case "tan" -> TAN(argsList);
            case "atan" -> ATAN(argsList);
            case "atan2" -> ATAN2(argsList);
            case "torad" -> TORAD(argsList);
            case "todeg" -> TODEG(argsList);
            case "min" -> MIN(argsList);
            case "max" -> MAX(argsList);
            case "clamp" -> CLAMP(argsList);
            case "abs" -> ABS(argsList);
            case "floor" -> FLOOR(argsList);
            case "ceil" -> CEIL(argsList);
            case "exp" -> EXP(argsList);
            case "frac" -> FRAC(argsList);
            case "log" -> LOG(argsList);
            case "pow" -> POW(argsList);
            case "random" -> RANDOM(argsList);
            case "round" -> ROUND(argsList);
            case "signum" -> SIGNUM(argsList);
            case "sqrt" -> SQRT(argsList);
            case "fmod" -> FMOD(argsList);
            case "lerp" -> LERP(argsList);
            case "print" -> PRINT(argsList);
            case "printb" -> PRINTB(argsList);
            case "between" -> BETWEEN(argsList);
            case "equals" -> EQUALS(argsList);
            case "in" -> IN(argsList);
            default ->
                throw new EMFMathException("ERROR: Unknown method ["+methodName+"], rejecting animation expression for ["+calculationInstance.animKey+"].");
             //()-> 0d;
        };

    }



    private ValueSupplier IN(List<String> args) throws EMFMathException {
        if(args.size() >= 3){
            MathExpression x = new MathExpression(args.get(0),false,calculationInstance);
            List<MathExpression> vals = new ArrayList<>();
            for (int i = 1; i < args.size(); i++) {
                vals.add(new MathExpression(args.get(i),false,calculationInstance));
            }
            return ()-> {
                float X = x.get();
                for (MathExpression expression:
                     vals) {
                    if(expression.get() == X){
                        return 1f;
                    }
                }
                return 0f;
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in IN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier EQUALS(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            MathExpression x = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression y = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression epsilon = new MathExpression(args.get(2),false,calculationInstance);
            return ()-> {
                float X = x.get();
                float Y = y.get();
                float BIGGER = Math.max(X,Y);
                float SMALLER = Math.min(X,Y);
                float EPSILON = epsilon.get();
                return Math.abs(BIGGER - SMALLER) <= EPSILON ? 1f : 0f;
               // return X >= Y - EPSILON ? 0 : (X <= Y + EPSILON ? 0 : 1f);
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in EQUALS method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier BETWEEN(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            MathExpression x = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression min = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression max = new MathExpression(args.get(2),false,calculationInstance);
            return ()-> {
                float X = x.get();
                float MAX = max.get();
                return X > MAX ? 0 : (X < min.get() ? 0 : 1f);
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in BETWEEN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private int printCount = 0;
    private int getPrintCount(){
        printCount++;
        return printCount;
    }
    private ValueSupplier PRINTB(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            String id = args.get(0);
            MathExpression n = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression x = new MathExpression(args.get(2),false,calculationInstance);
            return ()-> {
                float xVal = x.get();
                if(getPrintCount() % n.get() == 0){
                    print("EMF printb: ["+id+"] = "+(xVal == 1));
                }
                return xVal;
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in PRINTB method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier PRINT(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            String id = args.get(0);
            MathExpression n = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression x = new MathExpression(args.get(2),false,calculationInstance);
            return ()-> {
                float xVal = x.get();
                if(getPrintCount() % n.get() == 0){
                    print("EMF print: ["+id+"] = "+xVal);
                }
                return xVal;
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in PRINT method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier LERP(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            MathExpression k = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression x = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression y = new MathExpression(args.get(2),false,calculationInstance);
            return ()-> MathHelper.lerp(k.get(),x.get(),y.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in LERP method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier FMOD(List<String> args) throws EMFMathException {
        if(args.size() == 2){
            MathExpression x = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression y = new MathExpression(args.get(1),false,calculationInstance);
            return ()-> Math.floorMod((int) Math.floor(x.get()), (int) Math.floor(y.get()));
        }
        String s = "ERROR: wrong number of arguments "+ args +" in FMOD method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier SQRT(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> {
                double result = Math.sqrt(arg.get());
                if(Double.isNaN(result)){
                    print("ERROR: sqrt() returned NaN.");
                }
                return (float) result;
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in SQRT method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier SIGNUM(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.signum(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in SIGNUM method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier ROUND(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.round(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ROUND method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier RANDOM(List<String> args) throws EMFMathException {
        if(args.size() ==0 ){
            return ()-> new Random().nextFloat(1);
        }else if(args.size() == 1){
            MathExpression x = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> new Random((long) x.get()).nextFloat(1);
        }
        String s = "ERROR: wrong number of arguments "+ args +" in RANDOM method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier POW(List<String> args) throws EMFMathException {
        if(args.size() == 2){
            MathExpression x = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression y = new MathExpression(args.get(1),false,calculationInstance);
            return ()-> (float) Math.pow(x.get(),y.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in POW method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier LOG(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> (float) Math.log(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in LOG method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier FRAC(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()->{
                float d =arg.get();
                return d > 0 ? d - (float) Math.floor(d) : d + (float) Math.ceil(d);
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in FRAC method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier EXP(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> (float) Math.exp(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in EXP method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier CEIL(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> (float) Math.ceil(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in CEIL method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier FLOOR(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> (float) Math.floor(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in FLOOR method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier ABS(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.abs(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ABS method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier CLAMP(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression arg1 = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression arg2 = new MathExpression(args.get(2),false,calculationInstance);

            return ()->{
                float x = arg.get();
                float min = arg1.get();
                float max = arg2.get();
                if(calculationInstance.verboseMode) print("clamp="+x+", "+min+", "+max);
                return x > max ? max : (Math.max(x, min));
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in CLAMP method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier MAX(List<String> args) throws EMFMathException {
        if(args.size() >= 2){
            List<MathExpression> exps = new ArrayList<>();
            for (String arg:
                    args) {
                exps.add(new MathExpression(arg,false,calculationInstance));
            }
            return ()-> {
                float largest = Float.MIN_VALUE;
                for (MathExpression expression:
                        exps) {
                    float get =expression.get();
                    if(get > largest){
                        largest = get;
                    }
                }
                return largest;
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in MAX method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier MIN(List<String> args) throws EMFMathException {
        if(args.size() >= 2){
            List<MathExpression> exps = new ArrayList<>();
            for (String arg:
                 args) {
                exps.add(new MathExpression(arg,false,calculationInstance));
            }
            return ()-> {
                float smallest = Float.MAX_VALUE;
                for (MathExpression expression:
                     exps) {
                    float get =expression.get();
                    if(get < smallest){
                        smallest = get;
                    }
                }
                return smallest;
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in MIN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier TORAD(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()->{
                float x =arg.get();
                if(calculationInstance.verboseMode) print("torad ="+x);
                return (float) Math.toRadians(x);
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in TORAD method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier TODEG(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> (float) Math.toDegrees(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in TODEG method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier SIN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> {
                if(calculationInstance.verboseMode) print("sin = "+ arg.components);
                return (float) Math.sin(arg.get());
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in SIN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier ASIN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> (float) Math.asin(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ASIN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier COS(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> (float) Math.cos(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in COS method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier ACOS(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> (float) Math.acos(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ACOS method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier TAN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> (float) Math.tan(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in TAN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier ATAN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> (float) Math.atan(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ATAN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier ATAN2(List<String> args) throws EMFMathException {
        if(args.size() == 2){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression arg2 = new MathExpression(args.get(1),false,calculationInstance);
            return ()-> (float) Math.atan2(arg.get(), arg2.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ATAN2 method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }

    private ValueSupplier EMF_IF(List<String> args) throws EMFMathException {

        if(args.size() == 3){
            //easy if
            MathExpression bool = new MathExpression(args.get(0),false,calculationInstance);

            MathExpression tru = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression fals = new MathExpression(args.get(2),false,calculationInstance);

            return ()->{
                if(calculationInstance.verboseMode) print("if = "+bool.components+", "+tru.components+", "+fals.components);
                return bool.get() == 1 ? tru.get() : fals.get();
            };
        }else if(args.size() % 2 == 1){
            //elif
            List<MathExpression> expList = new ArrayList<>();
            for (String str:
                 args) {
                expList.add(new MathExpression(str,false,calculationInstance));
            }

            return ()->{
                boolean lastCondition = false;
                for (int i = 0; i < expList.size(); i++) {
                    if(i == expList.size()-1){
                        //last
                        if(calculationInstance.verboseMode) print("elif else = "+ expList.get(i).components);
                        return expList.get(i).get();
                    }else if(i % 2 == 0){
                        //boolean
                        if(calculationInstance.verboseMode) print("elif = "+ expList.get(i).components);
                        lastCondition = expList.get(i).get() == 1;
                    }else if(lastCondition){
                        //true condition to return
                        if(calculationInstance.verboseMode) print("elif true = "+ expList.get(i).components);
                        return expList.get(i).get();
                    }
                }
                String s = "ERROR: in IF method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
                System.out.println(s);
                return Float.NaN;
            };

        }
            //not odd invalid if
        String s = "ERROR: wrong number of arguments "+ args +" in IF method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);

    }

    @Override
    public String toString() {
        return methodName;
    }

    ValueSupplier supplier;
    @Override
    public ValueSupplier getSupplier() {
        return supplier;
    }
}
