package traben.entity_model_features.models.anim.EMFParser;

import net.minecraft.util.math.MathHelper;
import traben.entity_model_features.models.anim.AnimationCalculation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

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
            default ->{
                throw new EMFMathException("ERROR: Unknown method ["+methodName+"], rejecting animation expression for ["+calculationInstance.animKey+"].");
            } //()-> 0d;
        };

    }



    private Supplier<Double> IN(List<String> args) throws EMFMathException {
        if(args.size() >= 3){
            MathExpression x = new MathExpression(args.get(0),false,calculationInstance);
            List<MathExpression> vals = new ArrayList<>();
            for (int i = 1; i < args.size(); i++) {
                vals.add(new MathExpression(args.get(i),false,calculationInstance));
            }
            return ()-> {
                double X = x.get();
                for (MathExpression expression:
                     vals) {
                    if(expression.get() == X){
                        return 1d;
                    }
                }
                return 0d;
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in IN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> EQUALS(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            MathExpression x = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression y = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression epsilon = new MathExpression(args.get(2),false,calculationInstance);
            return ()-> {
                double X = x.get();
                double Y = y.get();
                double EPSILON = epsilon.get();
                return X >= Y - EPSILON ? 0 : (X <= Y + EPSILON ? 0 : 1d);
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in EQUALS method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> BETWEEN(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            MathExpression x = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression min = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression max = new MathExpression(args.get(2),false,calculationInstance);
            return ()-> {
                double X = x.get();
                double MAX = max.get();
                return X > MAX ? 0 : (X < min.get() ? 0 : 1d);
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
    private Supplier<Double> PRINTB(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            String id = args.get(0);
            MathExpression n = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression x = new MathExpression(args.get(2),false,calculationInstance);
            return ()-> {
                double xVal = x.get();
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
    private Supplier<Double> PRINT(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            String id = args.get(0);
            MathExpression n = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression x = new MathExpression(args.get(2),false,calculationInstance);
            return ()-> {
                double xVal = x.get();
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
    private Supplier<Double> LERP(List<String> args) throws EMFMathException {
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
    private Supplier<Double> FMOD(List<String> args) throws EMFMathException {
        if(args.size() == 2){
            MathExpression x = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression y = new MathExpression(args.get(1),false,calculationInstance);
            return ()-> Double.valueOf(Math.floorMod((int) Math.floor(x.get()), (int) Math.floor(y.get())));
        }
        String s = "ERROR: wrong number of arguments "+ args +" in FMOD method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> SQRT(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.sqrt(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in SQRT method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> SIGNUM(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.signum(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in SIGNUM method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> ROUND(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Double.valueOf(Math.round(arg.get()));
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ROUND method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> RANDOM(List<String> args) throws EMFMathException {
        if(args.size() ==0 ){
            return ()-> new Random().nextDouble(1);
        }else if(args.size() == 1){
            MathExpression x = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> new Random(x.get().longValue()).nextDouble(1);
        }
        String s = "ERROR: wrong number of arguments "+ args +" in RANDOM method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> POW(List<String> args) throws EMFMathException {
        if(args.size() == 2){
            MathExpression x = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression y = new MathExpression(args.get(1),false,calculationInstance);
            return ()-> Math.pow(x.get(),y.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in POW method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> LOG(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.log(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in LOG method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> FRAC(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()->{
                double d =arg.get();
                return d > 0 ? d - Math.floor(d) : d + Math.ceil(d);
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in FRAC method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> EXP(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.exp(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in EXP method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> CEIL(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.ceil(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in CEIL method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> FLOOR(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.floor(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in FLOOR method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> ABS(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.abs(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ABS method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> CLAMP(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression arg1 = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression arg2 = new MathExpression(args.get(2),false,calculationInstance);

            return ()->{
                double x = arg.get();
                double min = arg1.get();
                double max = arg2.get();
                if(calculationInstance.verboseMode) print("clamp="+x+", "+min+", "+max);
                return x > max ? max : (Math.max(x, min));
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in CLAMP method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> MAX(List<String> args) throws EMFMathException {
        if(args.size() < 2){
            List<MathExpression> exps = new ArrayList<>();
            for (String arg:
                    args) {
                exps.add(new MathExpression(arg,false,calculationInstance));
            }
            return ()-> {
                double largest = Double.NaN;
                for (MathExpression expression:
                        exps) {
                    double get =expression.get();
                    if(Double.isNaN(largest)){
                        largest = get;
                    }else if(get > largest){
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
    private Supplier<Double> MIN(List<String> args) throws EMFMathException {
        if(args.size() < 2){
            List<MathExpression> exps = new ArrayList<>();
            for (String arg:
                 args) {
                exps.add(new MathExpression(arg,false,calculationInstance));
            }
            return ()-> {
                double smallest = Double.NaN;
                for (MathExpression expression:
                     exps) {
                    double get =expression.get();
                    if(Double.isNaN(smallest)){
                        smallest = get;
                    }else if(get < smallest){
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
    private Supplier<Double> TORAD(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()->{
                double x =arg.get();
                if(calculationInstance.verboseMode) print("torad ="+x);
                return Math.toRadians(x);
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in TORAD method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> TODEG(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.toDegrees(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in TODEG method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> SIN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> {
                if(calculationInstance.verboseMode) print("sin = "+ arg.components);
                return Math.sin(arg.get());
            };
        }
        String s = "ERROR: wrong number of arguments "+ args +" in SIN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> ASIN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.asin(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ASIN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> COS(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.cos(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in COS method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> ACOS(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.acos(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ACOS method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> TAN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.tan(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in TAN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> ATAN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.atan(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ATAN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> ATAN2(List<String> args) throws EMFMathException {
        if(args.size() == 2){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression arg2 = new MathExpression(args.get(1),false,calculationInstance);
            return ()-> Math.atan2(arg.get(), arg2.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ATAN2 method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }

    private Supplier<Double> EMF_IF(List<String> args) throws EMFMathException {

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
                return Double.NaN;
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

    Supplier<Double> supplier;
    @Override
    public Supplier<Double> getSupplier() {
        return supplier;
    }
}
