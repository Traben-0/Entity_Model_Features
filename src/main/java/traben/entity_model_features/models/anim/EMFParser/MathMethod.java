package traben.entity_model_features.models.anim.EMFParser;

import net.minecraft.util.math.MathHelper;
import traben.entity_model_features.models.anim.AnimationCalculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MathMethod extends MathValue implements MathComponent{


    String methodName;

    public static MathComponent getOptimizedExpression(String methodName, String args, boolean isNegative, AnimationCalculation calculationInstance) throws EMFMathException{
        MathMethod method = new MathMethod(methodName, args, isNegative, calculationInstance);
        if(method.optimizedAlternativeToThis == null)
            return method;
        return method.optimizedAlternativeToThis;
    }

    public MathComponent optimizedAlternativeToThis = null;

    private MathMethod(String methodName, String args, boolean isNegative, AnimationCalculation calculationInstance) throws EMFMathException {
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


    private void setOptimizedIfPossible(List<MathComponent> allComponents, ValueSupplier supplier){
        //check if method only contains constants, if so precalculate the result and replace this with a constant
            boolean foundNonConstant = false;
            for (MathComponent comp :
                    allComponents) {
                if (!comp.isConstant()) {
                    foundNonConstant = true;
                    break;
                }
            }
            if (!foundNonConstant) {
                //precalculate expression that only contains constants
                double constantResult = supplier.get();
                if (!Double.isNaN(constantResult))
                    optimizedAlternativeToThis = new MathVariableConstant(constantResult,isNegative);
            }
    }


    private ValueSupplier IN(List<String> args) throws EMFMathException {
        if(args.size() >= 3){
            MathComponent x = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            List<MathComponent> vals = new ArrayList<>();
            for (int i = 1; i < args.size(); i++) {
                vals.add(MathExpressionParser.getOptimizedExpression(args.get(i),false,calculationInstance));
            }

            ValueSupplier valueSupplier = ()-> {
                double X = x.get();
                for (MathComponent expression:
                        vals) {
                    if(expression.get() == X){
                        return 1f;
                    }
                }
                return 0f;
            };
            List<MathComponent> comps = new ArrayList<>(vals);
            comps.add(x);
            setOptimizedIfPossible(comps,valueSupplier);

            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in IN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier EQUALS(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            MathComponent x = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            MathComponent y = MathExpressionParser.getOptimizedExpression(args.get(1),false,calculationInstance);
            MathComponent epsilon = MathExpressionParser.getOptimizedExpression(args.get(2),false,calculationInstance);

            ValueSupplier valueSupplier =()-> {
                double X = x.get();
                double Y = y.get();
                double BIGGER = Math.max(X,Y);
                double SMALLER = Math.min(X,Y);
                double EPSILON = epsilon.get();
                return Math.abs(BIGGER - SMALLER) <= EPSILON ? 1f : 0f;
                // return X >= Y - EPSILON ? 0 : (X <= Y + EPSILON ? 0 : 1f);
            };
            List<MathComponent> comps = List.of(x,y,epsilon);
            setOptimizedIfPossible(comps,valueSupplier);

            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in EQUALS method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier BETWEEN(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            MathComponent x = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            MathComponent min = MathExpressionParser.getOptimizedExpression(args.get(1),false,calculationInstance);
            MathComponent max = MathExpressionParser.getOptimizedExpression(args.get(2),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> {
                double X = x.get();
                double MAX = max.get();
                return X > MAX ? 0 : (X < min.get() ? 0 : 1f);
            };

            List<MathComponent> comps = List.of(x,min,max);
            setOptimizedIfPossible(comps,valueSupplier);

            return valueSupplier;
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
            MathComponent n = MathExpressionParser.getOptimizedExpression(args.get(1),false,calculationInstance);
            MathComponent x = MathExpressionParser.getOptimizedExpression(args.get(2),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> {
                double xVal = x.get();
                if(getPrintCount() % n.get() == 0){
                    print("EMF printb: ["+id+"] = "+(xVal == 1));
                }
                return xVal;
            };
            List<MathComponent> comps = List.of(n,x);
            setOptimizedIfPossible(comps,valueSupplier);

            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in PRINTB method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier PRINT(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            String id = args.get(0);
            MathComponent n = MathExpressionParser.getOptimizedExpression(args.get(1),false,calculationInstance);
            MathComponent x = MathExpressionParser.getOptimizedExpression(args.get(2),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> {
                double xVal = x.get();
                if(getPrintCount() % n.get() == 0){
                    print("EMF print: ["+id+"] = "+xVal);
                }
                return xVal;
            };
            List<MathComponent> comps = List.of(n,x);
            setOptimizedIfPossible(comps,valueSupplier);

            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in PRINT method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier LERP(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            MathComponent k = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            MathComponent x = MathExpressionParser.getOptimizedExpression(args.get(1),false,calculationInstance);
            MathComponent y = MathExpressionParser.getOptimizedExpression(args.get(2),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> MathHelper.lerp(k.get(),x.get(),y.get());
            List<MathComponent> comps = List.of(k,x,y);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in LERP method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier FMOD(List<String> args) throws EMFMathException {
        if(args.size() == 2){
            MathComponent x = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            MathComponent y = MathExpressionParser.getOptimizedExpression(args.get(1),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> Math.floorMod((int) Math.floor(x.get()), (int) Math.floor(y.get()));
            List<MathComponent> comps = List.of(x,y);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in FMOD method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier SQRT(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> {
                double result = Math.sqrt(arg.get());
                if(Double.isNaN(result)){
                    print("ERROR: sqrt() returned NaN.");
                }
                return (float) result;
            };
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in SQRT method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier SIGNUM(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> Math.signum(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in SIGNUM method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier ROUND(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> Math.round(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ROUND method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }

    private final Random random = new Random();
    private ValueSupplier RANDOM(List<String> args) throws EMFMathException {
        if(args.size() ==0 ){
            //cannot optimize further
            return random::nextFloat;
        }else if(args.size() == 1){
            MathComponent x = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> new Random((long) x.get()).nextFloat(1);
            List<MathComponent> comps = List.of(x);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in RANDOM method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier POW(List<String> args) throws EMFMathException {
        if(args.size() == 2){
            MathComponent x = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            MathComponent y = MathExpressionParser.getOptimizedExpression(args.get(1),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> (float) Math.pow(x.get(),y.get());
            List<MathComponent> comps = List.of(x,y);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in POW method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier LOG(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> (float) Math.log(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in LOG method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier FRAC(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()->{
                double d =arg.get();
                return d > 0 ? d - (float) Math.floor(d) : d + (float) Math.ceil(d);
            };
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in FRAC method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier EXP(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> (float) Math.exp(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in EXP method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier CEIL(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> (float) Math.ceil(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in CEIL method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier FLOOR(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> (float) Math.floor(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in FLOOR method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier ABS(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> Math.abs(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ABS method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier CLAMP(List<String> args) throws EMFMathException {
        if(args.size() == 3){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            MathComponent arg1 = MathExpressionParser.getOptimizedExpression(args.get(1),false,calculationInstance);
            MathComponent arg2 = MathExpressionParser.getOptimizedExpression(args.get(2),false,calculationInstance);

            ValueSupplier valueSupplier = ()->{
                double x = arg.get();
                double min = arg1.get();
                double max = arg2.get();
                if(calculationInstance.verboseMode) print("clamp="+x+", "+min+", "+max);
                return x > max ? max : (Math.max(x, min));
            };
            List<MathComponent> comps = List.of(arg1,arg2,arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in CLAMP method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier MAX(List<String> args) throws EMFMathException {
        if(args.size() >= 2){
            List<MathComponent> exps = new ArrayList<>();
            for (String arg:
                    args) {
                exps.add(MathExpressionParser.getOptimizedExpression(arg,false,calculationInstance));
            }
            ValueSupplier valueSupplier = ()-> {
                double largest = Float.MIN_VALUE;
                for (MathComponent expression:
                        exps) {
                    double get =expression.get();
                    if(get > largest){
                        largest = get;
                    }
                }
                return largest;
            };
            setOptimizedIfPossible(exps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in MAX method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier MIN(List<String> args) throws EMFMathException {
        if(args.size() >= 2){
            List<MathComponent> exps = new ArrayList<>();
            for (String arg:
                 args) {
                exps.add(MathExpressionParser.getOptimizedExpression(arg,false,calculationInstance));
            }
            ValueSupplier valueSupplier = ()-> {
                double smallest = Float.MAX_VALUE;
                for (MathComponent expression:
                     exps) {
                    double get =expression.get();
                    if(get < smallest){
                        smallest = get;
                    }
                }
                return smallest;
            };

            setOptimizedIfPossible(exps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in MIN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier TORAD(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()->{
                double x =arg.get();
                if(calculationInstance.verboseMode) print("torad ="+x);
                return (float) Math.toRadians(x);
            };
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in TORAD method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier TODEG(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier  = ()-> (float) Math.toDegrees(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in TODEG method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier SIN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> {
                //if(calculationInstance.verboseMode) print("sin = "+ arg);
                return (float) Math.sin(arg.get());
            };
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in SIN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier ASIN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> (float) Math.asin(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ASIN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier COS(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> (float) Math.cos(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in COS method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier ACOS(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> (float) Math.acos(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ACOS method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier TAN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> (float) Math.tan(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in TAN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier ATAN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> (float) Math.atan(arg.get());
            List<MathComponent> comps = List.of(arg);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ATAN method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private ValueSupplier ATAN2(List<String> args) throws EMFMathException {
        if(args.size() == 2){
            MathComponent arg = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);
            MathComponent arg2 = MathExpressionParser.getOptimizedExpression(args.get(1),false,calculationInstance);
            ValueSupplier valueSupplier = ()-> (float) Math.atan2(arg.get(), arg2.get());
            List<MathComponent> comps = List.of(arg,arg2);
            setOptimizedIfPossible(comps,valueSupplier);
            return valueSupplier;
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ATAN2 method for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }

    private ValueSupplier EMF_IF(List<String> args) throws EMFMathException {

        if(args.size() == 3){
            //easy if
            MathComponent bool = MathExpressionParser.getOptimizedExpression(args.get(0),false,calculationInstance);

            MathComponent tru = MathExpressionParser.getOptimizedExpression(args.get(1),false,calculationInstance);
            MathComponent fals = MathExpressionParser.getOptimizedExpression(args.get(2),false,calculationInstance);
//            if(calculationInstance.animKey.equals("var.float")){
//                System.out.println("var.float = " + args.get(0));
//                System.out.println("var.float as string = "+ bool.toString());
//                System.out.println("var.float .get() = "+ bool.get());
//            }

            // different constant optimization for if statement
            // this should never happen unless the pack maker is 0 iq
            if (bool.isConstant()) {
                optimizedAlternativeToThis = bool.get() == 1 ? tru : fals;
            }
            return ()->{
                //if(calculationInstance.verboseMode) print("if = "+bool+", "+tru+", "+fals);
                return bool.get() == 1 ? tru.get() : fals.get();
            };
        }else if(args.size() % 2 == 1){
            //elif
            List<MathComponent> expList = new ArrayList<>();
            for (String str:
                 args) {
                expList.add(MathExpressionParser.getOptimizedExpression(str,false,calculationInstance));
            }

            return ()->{
                boolean lastCondition = false;
                for (int i = 0; i < expList.size(); i++) {
                    if(i == expList.size()-1){
                        //last
                        //if(calculationInstance.verboseMode) print("elif else = "+ expList.get(i));
                        return expList.get(i).get();
                    }else if(i % 2 == 0){
                        //boolean
                        //if(calculationInstance.verboseMode) print("elif = "+ expList.get(i));
                        lastCondition = expList.get(i).get() == 1;
                    }else if(lastCondition){
                        //true condition to return
                        //if(calculationInstance.verboseMode) print("elif true = "+ expList.get(i));
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
