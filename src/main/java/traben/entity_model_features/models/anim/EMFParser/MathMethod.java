package traben.entity_model_features.models.anim.EMFParser;

import traben.entity_model_features.models.anim.AnimationCalculation;

import java.util.ArrayList;
import java.util.List;
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
            default -> ()-> 0d;
        };

    }
    private Supplier<Double> TORAD(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.toRadians(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in TORAD statement for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> TODEG(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.toDegrees(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in TODEG statement for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> SIN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.sin(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in SIN statement for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> ASIN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.asin(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ASIN statement for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> COS(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.cos(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in COS statement for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> ACOS(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.acos(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ACOS statement for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> TAN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.tan(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in TAN statement for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> ATAN(List<String> args) throws EMFMathException {
        if(args.size() == 1){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            return ()-> Math.atan(arg.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ATAN statement for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }
    private Supplier<Double> ATAN2(List<String> args) throws EMFMathException {
        if(args.size() == 2){
            MathExpression arg = new MathExpression(args.get(0),false,calculationInstance);
            MathExpression arg2 = new MathExpression(args.get(1),false,calculationInstance);
            return ()-> Math.atan2(arg.get(), arg2.get());
        }
        String s = "ERROR: wrong number of arguments "+ args +" in ATAN2 statement for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);
    }

    private Supplier<Double> EMF_IF(List<String> args) throws EMFMathException {

        if(args.size() == 3){
            //easy if
            MathExpression bool = new MathExpression(args.get(0),false,calculationInstance);

            MathExpression tru = new MathExpression(args.get(1),false,calculationInstance);
            MathExpression fals = new MathExpression(args.get(2),false,calculationInstance);

            return ()-> bool.get() == 1 ? tru.get() : fals.get();
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
                        return expList.get(i).get();
                    }else if(i % 2 == 1){
                        //boolean
                        lastCondition = expList.get(i).get() == 1;
                    }else if(lastCondition){
                        //true condition to return
                        return expList.get(i).get();
                    }
                }
                String s = "ERROR: in IF statement for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
                System.out.println(s);
                return Double.NaN;
            };

        }
            //not odd invalid if
        String s = "ERROR: wrong number of arguments "+ args +" in IF statement for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].";
        System.out.println(s);
        throw new EMFMathException(s);

    }



    Supplier<Double> supplier;
    @Override
    public Supplier<Double> getSupplier() {
        return supplier;
    }
}
