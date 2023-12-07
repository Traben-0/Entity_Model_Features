package traben.entity_model_features.models.animation.animation_math_parser;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.utils.EMFUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MathExpressionParser extends MathValue implements MathComponent {

    public static final MathExpressionParser NULL_EXPRESSION = new MathExpressionParser("null") {
        @Override
        public float get() {
            return Float.NaN;
        }

        @Override
        public boolean isValid() {
            return false;
        }
    };
    public final String originalExpression;
    public boolean wasInvertedBooleanExpression = false;
    public MathComponent optimizedAlternativeToThis = null;
    CalculationList components;
    boolean isNegativeValueNext = false;
    CalculationList componentsDuringCalculate;
    private String caughtExceptionString = null;
    private boolean containsBooleansHigherOrder = false;
    //private boolean containsOneComponent = false;
    private boolean containsBooleansLowerOrder = false;
    private boolean containsMultiplicationLevel = false;
    private boolean containsAdditionLevel = false;

    private MathExpressionParser(String WARNING_ONLY_FOR_NULL_EXPRESSION) {
        originalExpression = WARNING_ONLY_FOR_NULL_EXPRESSION;
        components = new CalculationList();
    }

    private MathExpressionParser(String expressionString, boolean isNegative, EMFAnimation calculationInstance, boolean invertBoolean) {
        super(isNegative, calculationInstance);

        wasInvertedBooleanExpression = invertBoolean;

        expressionString = expressionString.trim();
        expressionString = expressionString.replaceAll("\\s*", "");
        originalExpression = expressionString;

        components = new CalculationList();
        try {

            StringBuilder rollingRead = new StringBuilder();
            List<Character> charList = new ArrayList<>();
            for (char ch :
                    expressionString.toCharArray()) {
                charList.add(ch);
            }
            Iterator<Character> charIterator = charList.iterator();
            Character firstBooleanChar = null;
            while (charIterator.hasNext()) {
                char ch = charIterator.next();
                MathAction action = MathAction.getAction(ch);
                if (firstBooleanChar != null) {
                    if (action == MathAction.BOOLEAN_CHAR) {
                        action = switch (firstBooleanChar + "" + ch) {
                            case "==" -> MathAction.EQUALS;
                            case "!=" -> MathAction.NOT_EQUALS;
                            case "&&" -> MathAction.AND;
                            case "||" -> MathAction.OR;
                            case ">=" -> MathAction.LARGER_THAN_OR_EQUALS;
                            case "<=" -> MathAction.SMALLER_THAN_OR_EQUALS;
                            default ->
                                    throw new EMFMathException("ERROR: with boolean processing for operator [" + firstBooleanChar + "" + ch + "] for [" + calculationInstance.animKey + "] in [" + calculationInstance.modelName + "].");
                        };
                        //add complete double boolean action
                        components.add(action);
                        //now iterate once for a regular iteration
                        ch = charIterator.next();
                        action = MathAction.getAction(ch);
                    } else {
                        if (firstBooleanChar == '!') {
                            //likely a '!' for boolean variables so need to add to read
                            rollingRead.append('!');
                        } else {
                            //add complete single char boolean action
                            components.add(switch (firstBooleanChar) {
                                case '=' -> MathAction.EQUALS;
                                case '&' -> MathAction.AND;
                                case '|' -> MathAction.OR;
                                case '<' -> MathAction.SMALLER_THAN;
                                case '>' -> MathAction.LARGER_THAN;
                                default ->
                                        throw new EMFMathException("ERROR: with boolean processing for operator [" + firstBooleanChar + "] for [" + calculationInstance.animKey + "] in [" + calculationInstance.modelName + "].");
                            });
                        }
                    }
                    firstBooleanChar = null;
                }
                if (action == MathAction.BOOLEAN_CHAR) {
                    firstBooleanChar = ch;
                }
                //critical that elif stops here
                if (action == MathAction.SUBTRACT &&
                        ((components.isEmpty() && rollingRead.isEmpty())
                                || (!components.isEmpty() && components.getLast() instanceof MathAction && rollingRead.isEmpty()))) {
                    isNegativeValueNext = true;
                } else if (action == MathAction.NONE) {
                    rollingRead.append(ch);
                } else {
                    if (action == MathAction.OPEN_BRACKET) {
                        String functionName = rollingRead.toString();
                        rollingRead = new StringBuilder();

                        StringBuilder bracketContents = new StringBuilder();
                        int bracketsCount = 0;
                        while (charIterator.hasNext()) {
                            char ch2 = charIterator.next();
                            if (ch2 == '(') {
                                bracketContents.append(ch2);
                                bracketsCount++;
                            } else if (ch2 == ')') {
                                if (bracketsCount == 0) {
                                    break;
                                } else {
                                    bracketContents.append(ch2);
                                    bracketsCount--;
                                }
                            } else {
                                bracketContents.append(ch2);
                            }
                        }

                        if (functionName.isEmpty() || "!".equals(functionName)) {
                            //just brackets
                            MathComponent brackets = MathExpressionParser.getOptimizedExpression(bracketContents.toString(), getNegativeNext(), this.calculationInstance, "!".equals(functionName));

                            components.add(brackets);
                        } else {
                            //method
                            MathComponent method = MathMethod.getOptimizedExpression(functionName, bracketContents.toString(), getNegativeNext(), this.calculationInstance);
                            components.add(method);
                        }

                    } else {//action should be only + - * / ^
                        ////////////////////////////////////////////////
                        //discover rolling read value
                        String read = rollingRead.toString();
                        rollingRead = new StringBuilder();
                        if (!read.isEmpty()) {
                            Float asNumber = null;
                            try {
                                asNumber = Float.parseFloat(read);
                            } catch (NumberFormatException ignored) {
                            }
//                            if(this.calculationInstance.animKey.equals("var.float")){
//                                System.out.println("variablename = "+read);
//                            }
                            MathComponent variable;
                            if (asNumber == null) {
                                variable = MathVariable.getOptimizedVariable(read, getNegativeNext(), this.calculationInstance);
                            } else {
                                variable = new MathConstant(asNumber, getNegativeNext());
                            }
                            components.add(variable);
                            ///////////////////////////////////////////
                        }
                        //finally add new math action
                        if (rollingRead.isEmpty() && action != MathAction.BOOLEAN_CHAR) {
                            components.add(action);
                        }
                    }
                }


            }
            //add last read data
            if (!rollingRead.isEmpty()) {
                ////////////////////////////////////////////////
                //discover rolling read value
                String read = rollingRead.toString();
                //rollingRead = new StringBuilder();
                Float asNumber = null;
                try {
                    asNumber = Float.parseFloat(read);
                } catch (NumberFormatException ignored) {
                }

                MathComponent variable;
                if (asNumber == null) {
                    variable = MathVariable.getOptimizedVariable(read, getNegativeNext(), this.calculationInstance);
                } else {
                    variable = new MathConstant(asNumber, getNegativeNext());
                }
                components.add(variable);
                ///////////////////////////////////////////
            }

            if (components.isEmpty())
                throw new EMFMathException("ERROR: math components found to be empty for [" + calculationInstance.animKey + "] in [" + calculationInstance.modelName + "]");

            //resolve unnecessary and unwanted math logic issue like 1 + +2
            CalculationList newComponents = new CalculationList();
            MathComponent lastComponent = null;
            for (MathComponent component :
                    components) {
                if (lastComponent instanceof MathAction && component instanceof MathAction action) {
                    if (action != MathAction.ADD) {
                        newComponents.add(component);
                    }
                    //do not include unneeded addition action as there is no reason to and will mess with parser logic
                } else {
                    newComponents.add(component);
                }
                lastComponent = component;
            }
            if (newComponents.size() != components.size()) components = newComponents;

            //assess and store content metadata
            if (components.size() == 1) {
                //this.containsOneComponent = true;


                MathComponent comp = components.getLast();
                if (comp instanceof MathConstant constnt) {
                    if (isNegative) comp = new MathConstant(-constnt.get());
                } else if (comp instanceof MathValue val) {
                    val.isNegative = isNegative != val.isNegative;
                }

                optimizedAlternativeToThis = comp;
            } else {

                if (components.get(0) == MathAction.ADD) {
                    components.remove(0);
                }

                this.containsBooleansHigherOrder = (components.contains(MathAction.EQUALS)
                        || components.contains(MathAction.LARGER_THAN)
                        || components.contains(MathAction.LARGER_THAN_OR_EQUALS)
                        || components.contains(MathAction.SMALLER_THAN)
                        || components.contains(MathAction.SMALLER_THAN_OR_EQUALS)
                        || components.contains(MathAction.NOT_EQUALS));

                this.containsBooleansLowerOrder =
                        components.contains(MathAction.AND)
                                || components.contains(MathAction.OR);

                this.containsMultiplicationLevel = (components.contains(MathAction.MULTIPLY)
                        || components.contains(MathAction.DIVIDE)
                        || components.contains(MathAction.DIVISION_REMAINDER));

                this.containsAdditionLevel = (components.contains(MathAction.ADD)
                        || components.contains(MathAction.SUBTRACT));


                //check if expression only contains constants, if so precalculate and save constant result

                //this needs to run
                isValid();

                //if (isValid()) {//method call will construct validation variant as

//this should now auto build an optimzed replacement


//                    boolean foundNonConstant = false;
//                    for (MathComponent comp :
//                            components) {
//                        if (comp instanceof MathMethod ||
//                                comp instanceof MathVariableUpdatable ||
//                                comp instanceof MathExpression) {
//                            foundNonConstant = true;
//                            break;
//                        }
//                    }
//                    if (!foundNonConstant) {
//                        //precalculate expression that only contains constants
//                        float constantResult = this.get();
//                        if(!Float.isNaN(constantResult))
//                            optimizedAlternativeToThis = new MathVariableConstant(constantResult,isNegative);
//                    }
                //}

            }


            // System.out.println(components);
        } catch (EMFMathException e) {
            caughtExceptionString = e.toString();
        } catch (Exception e) {
            caughtExceptionString = "EMF animation ERROR: for [" + calculationInstance.animKey + "] in [" + calculationInstance.modelName + "] cause [" + e + "].";
            e.printStackTrace();
        }
    }

    public static MathComponent getOptimizedExpression(String expressionString, boolean isNegative, EMFAnimation calculationInstance) {
        return getOptimizedExpression(expressionString, isNegative, calculationInstance, false);
    }

    public static MathComponent getOptimizedExpression(String expressionString, boolean isNegative, EMFAnimation calculationInstance, boolean invertBoolean) {
        MathExpressionParser expression = new MathExpressionParser(expressionString, isNegative, calculationInstance, invertBoolean);
        if (expression.optimizedAlternativeToThis == null) {
            if (expression.isValid()) {
                return expression;
            } else {
                EMFUtils.logWarn("null animation expression: [" + expressionString + "]");
                return NULL_EXPRESSION;
            }
        }
        MathComponent optimized = expression.optimizedAlternativeToThis;
        //just an anonymous boolean inverter
        if (expression.wasInvertedBooleanExpression) {
            return new MathValue() {
                @Override
                public float get() {
                    return optimized.get() == 1 ? 0 : 1;
                }

                @Override
                public ValueSupplier getSupplier() {
                    return null;
                }
            };
        }
        return optimized;
    }

    public boolean isValid() {
        if (caughtExceptionString != null) {
            EMFUtils.logWarn(caughtExceptionString);
            return false;
        }
        //must do both I depend on this method call for optimization
        if (Double.isNaN(this.validateCalculationAndOptimize())) {
            EMFUtils.logWarn("result was NaN, expression not valid: " + originalExpression);
            return false;
        }
        return true;
    }

    private boolean getNegativeNext() {
        boolean neg = isNegativeValueNext;
        isNegativeValueNext = false;
        return neg;
    }

    public float validateCalculationAndOptimize() {

        try {

            //it is possible the expression is simply 1 value, just return the single component value in this case for efficiency
//            if(containsOneComponent){
//               // if (calculationInstance.verboseMode) print("finished calculating [" + originalExpression + "] as single component quick return.");
//                return components.getLast().get();
//            }

//            if (EMFConfig.getConfig().logMathInRuntime)
//                print("start calculating [" + originalExpression + "] as [" + components + "].");

            //reset every calculate
            componentsDuringCalculate = new CalculationList(components);
            //multiply & divide pass
            if (containsMultiplicationLevel) {
                CalculationList newComponents = new CalculationList();
                Iterator<MathComponent> compIterator = componentsDuringCalculate.iterator();

                while (compIterator.hasNext()) {
                    MathComponent component = compIterator.next();
                    if (component instanceof MathAction action) {
                        if (action == MathAction.MULTIPLY) {
                            MathComponent last = newComponents.getLast();
                            MathComponent next = compIterator.next();
                            newComponents.removeLast();
//                            float lastD = last.get();
//                            float nextD = next.get();
//                            if (calculationInstance.verboseMode) print("multiply=" + lastD + " * " + nextD);
//                            newComponents.add(new MathVariableConstant(lastD * nextD));
                            newComponents.add(MathBinaryExpressionComponent.getOptimizedExpression(last, action, next));

                        } else if (action == MathAction.DIVIDE) {
                            MathComponent last = newComponents.getLast();
                            MathComponent next = compIterator.next();
                            newComponents.removeLast();
//                            float lastD = last.get();
//                            float nextD = next.get();
//                            if (calculationInstance.verboseMode) print("divide=" + lastD + " / " + nextD);
//                            newComponents.add(new MathVariableConstant(lastD / nextD));
                            newComponents.add(MathBinaryExpressionComponent.getOptimizedExpression(last, action, next));
                        } else if (action == MathAction.DIVISION_REMAINDER) {
                            MathComponent last = newComponents.getLast();
                            MathComponent next = compIterator.next();
                            newComponents.removeLast();
//                            float lastD = last.get();
//                            float nextD = next.get();
//                            if (calculationInstance.verboseMode) print("divide remainder=" + lastD + " % " + nextD);
//                            newComponents.add(new MathVariableConstant(lastD % nextD));
                            newComponents.add(MathBinaryExpressionComponent.getOptimizedExpression(last, action, next));
                        } else {
                            newComponents.add(component);
                        }
                    } else {
                        newComponents.add(component);
                    }
                }
                componentsDuringCalculate = newComponents;
            }
            //add & subtract pass
            if (containsAdditionLevel) {
                CalculationList newComponents2 = new CalculationList();
                Iterator<MathComponent> compIterator2 = componentsDuringCalculate.iterator();
                while (compIterator2.hasNext()) {
                    MathComponent component = compIterator2.next();
                    if (component instanceof MathAction action) {
                        if (action == MathAction.ADD) {
                            MathComponent last = newComponents2.getLast();
                            MathComponent next = compIterator2.next();
                            newComponents2.removeLast();
//                            float lastD = last.get();
//                            float nextD = next.get();
//                            if (calculationInstance.verboseMode) print("add=" + lastD + " + " + nextD);
//                            newComponents2.add(new MathVariableConstant(lastD + nextD));
                            newComponents2.add(MathBinaryExpressionComponent.getOptimizedExpression(last, action, next));
                        } else if (action == MathAction.SUBTRACT) {
                            MathComponent last = newComponents2.getLast();
                            MathComponent next = compIterator2.next();
                            newComponents2.removeLast();
//                            float lastD = last.get();
//                            float nextD = next.get();
//                            if (calculationInstance.verboseMode) print("subtract=" + lastD + " - " + nextD);
//                            newComponents2.add(new MathVariableConstant(lastD - nextD));
                            newComponents2.add(MathBinaryExpressionComponent.getOptimizedExpression(last, action, next));
                        } else {
                            newComponents2.add(component);
                        }
                    } else {
                        newComponents2.add(component);
                    }
                }
                componentsDuringCalculate = newComponents2;
            }

            //boolean pass
            if (containsBooleansHigherOrder) {
                CalculationList newComponentsB = new CalculationList();
                Iterator<MathComponent> compIteratorB = componentsDuringCalculate.iterator();
                while (compIteratorB.hasNext()) {
                    MathComponent component = compIteratorB.next();
                    if (component instanceof MathAction action) {
                        switch (action) {
                            case EQUALS, SMALLER_THAN_OR_EQUALS, SMALLER_THAN, LARGER_THAN_OR_EQUALS, LARGER_THAN, NOT_EQUALS -> {
                                MathComponent last = newComponentsB.getLast();
                                MathComponent next = compIteratorB.next();
                                newComponentsB.removeLast();
//                                float lastD = last.get();
//                                float nextD = next.get();
//                                if (calculationInstance.verboseMode) print("equals=" + lastD + " == " + nextD);
//                                newComponentsB.add(new MathVariableConstant(lastD == nextD ? 1 : 0));
                                newComponentsB.add(MathBinaryExpressionComponent.getOptimizedExpression(last, action, next));
                            }
                            default -> newComponentsB.add(component);
                        }
                    } else {
                        newComponentsB.add(component);
                    }
                }
                componentsDuringCalculate = newComponentsB;
            }
            if (containsBooleansLowerOrder) {
                CalculationList newComponentsB = new CalculationList();
                Iterator<MathComponent> compIteratorB = componentsDuringCalculate.iterator();
                while (compIteratorB.hasNext()) {
                    MathComponent component = compIteratorB.next();
                    if (component instanceof MathAction action) {
                        switch (action) {
                            case AND, OR -> {
                                MathComponent last = newComponentsB.getLast();
                                MathComponent next = compIteratorB.next();
                                newComponentsB.removeLast();
//                                float lastD = last.get();
//                                float nextD = next.get();
//                                if (calculationInstance.verboseMode)
//                                    print("and=" + (lastD == 1) + " && " + (nextD == 1));
//                                newComponentsB.add(new MathVariableConstant((lastD == 1) && (nextD == 1) ? 1 : 0, false));
                                newComponentsB.add(MathBinaryExpressionComponent.getOptimizedExpression(last, action, next));
                            }
                            default -> newComponentsB.add(component);
                        }
                    } else {
                        newComponentsB.add(component);
                    }
                }
                componentsDuringCalculate = newComponentsB;
            }


//            if (EMFConfig.getConfig().logMathInRuntime)
//                print("finish calculating [" + originalExpression + "] as [" + components + "].");
            if (componentsDuringCalculate.size() == 1) {
                //if(verboseMode) System.out.print("group result");
                float result = componentsDuringCalculate.getLast().get();
                //lastResultThisTick = result;
//                if (EMFConfig.getConfig().logMathInRuntime) print(" = " + result);
                if (Double.isNaN(result)) {
                    System.out.println(" result was NaN in [" + calculationInstance.modelName + "] for expression: " + originalExpression + " as " + components);
                } else {
                    //save optimized version of valid expression
                    optimizedAlternativeToThis = componentsDuringCalculate.getLast();
                    if (optimizedAlternativeToThis instanceof MathValue value) {
                        value.makeNegative(this.isNegative);
                    }
                }
                return result;
            } else {
                System.out.println("ERROR: calculation did not result in 1 component, found: " + componentsDuringCalculate + " in [" + calculationInstance.animKey + "] in [" + calculationInstance.modelName + "].");
                System.out.println("\texpression was [" + originalExpression + "].");
            }
        } catch (Exception e) {
            System.out.println("EMF animation ERROR: expression error in [" + calculationInstance.animKey + "] in [" + calculationInstance.modelName + "] caused by [" + e + "].");
        }

        return Float.NaN;
    }


    @Override
    public ValueSupplier getSupplier() {
        //EMFUtils.EMFModWarn("this should not happen this object should have been optimized: supplier");
        return this::get;
    }

    @Override
    public float get() {
        EMFUtils.logWarn("this should not happen this object should have been optimized");
        float value;
        if (wasInvertedBooleanExpression) {
            value = super.get() == 1 ? 0 : 1;
        } else {
            value = super.get();
        }
        return value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (MathComponent comp :
                components) {
            builder.append(comp.toString()).append(" ");
        }
        builder.append("}");
        return builder.toString();

    }


    private static class CalculationList extends ObjectArrayList<traben.entity_model_features.models.animation.animation_math_parser.MathComponent> {
        public CalculationList(CalculationList components) {
            super(components);
        }

        public CalculationList() {
        }

        public MathComponent getLast() {
            return super.get(size - 1);
        }

        public void removeLast() {
            super.remove(size - 1);
        }
    }


//    //old direct calculate logic
//    @Deprecated
//    private float calculateOLD() {
//
//        try {
//
//            //it is possible the expression is simply 1 value, just return the single component value in this case for efficiency
////            if(containsOneComponent){
////               // if (calculationInstance.verboseMode) print("finished calculating [" + originalExpression + "] as single component quick return.");
////                return components.getLast().get();
////            }
//
//            if (calculationInstance.verboseMode)
//                print("start calculating [" + originalExpression + "] as [" + components+"].");
//
//            //reset every calculate
//            componentsDuringCalculate = new CalculationList(components);
//            //multiply & divide pass
//            if(containsMultiplicationLevel) {
//                CalculationList newComponents = new CalculationList();
//                Iterator<MathComponent> compIterator = componentsDuringCalculate.iterator();
//
//                while (compIterator.hasNext()) {
//                    MathComponent component = compIterator.next();
//                    if (component instanceof MathAction action) {
//                        if (action == MathAction.multiply) {
//                            MathComponent last = newComponents.getLast();
//                            MathComponent next = compIterator.next();
//                            newComponents.removeLast();
//                            float lastD = last.get();
//                            float nextD = next.get();
//                            if (calculationInstance.verboseMode) print("multiply=" + lastD + " * " + nextD);
//                            newComponents.add(new MathVariableConstant(lastD * nextD));
//
//                        } else if (action == MathAction.divide) {
//                            MathComponent last = newComponents.getLast();
//                            MathComponent next = compIterator.next();
//                            newComponents.removeLast();
//                            float lastD = last.get();
//                            float nextD = next.get();
//                            if (calculationInstance.verboseMode) print("divide=" + lastD + " / " + nextD);
//                            newComponents.add(new MathVariableConstant(lastD / nextD));
//                        } else if (action == MathAction.divisionRemainder) {
//                            MathComponent last = newComponents.getLast();
//                            MathComponent next = compIterator.next();
//                            newComponents.removeLast();
//                            float lastD = last.get();
//                            float nextD = next.get();
//                            if (calculationInstance.verboseMode) print("divide remainder=" + lastD + " % " + nextD);
//                            newComponents.add(new MathVariableConstant(lastD % nextD));
//                        } else {
//                            newComponents.add(component);
//                        }
//                    } else {
//                        newComponents.add(component);
//                    }
//                }
//                componentsDuringCalculate = newComponents;
//            }
//            //add & subtract pass
//            if(containsAdditionLevel) {
//                CalculationList newComponents2 = new CalculationList();
//                Iterator<MathComponent> compIterator2 = componentsDuringCalculate.iterator();
//                while (compIterator2.hasNext()) {
//                    MathComponent component = compIterator2.next();
//                    if (component instanceof MathAction action) {
//                        if (action == MathAction.add) {
//                            MathComponent last = newComponents2.getLast();
//                            MathComponent next = compIterator2.next();
//                            newComponents2.removeLast();
//                            float lastD = last.get();
//                            float nextD = next.get();
//                            if (calculationInstance.verboseMode) print("add=" + lastD + " + " + nextD);
//                            newComponents2.add(new MathVariableConstant(lastD + nextD));
//                        } else if (action == MathAction.subtract) {
//                            MathComponent last = newComponents2.getLast();
//                            MathComponent next = compIterator2.next();
//                            newComponents2.removeLast();
//                            float lastD = last.get();
//                            float nextD = next.get();
//                            if (calculationInstance.verboseMode) print("subtract=" + lastD + " - " + nextD);
//                            newComponents2.add(new MathVariableConstant(lastD - nextD));
//                        } else {
//                            newComponents2.add(component);
//                        }
//                    } else {
//                        newComponents2.add(component);
//                    }
//                }
//                componentsDuringCalculate = newComponents2;
//            }
//
//            //boolean pass
//            if(containsBooleansHigherOrder) {
//                CalculationList newComponentsB = new CalculationList();
//                Iterator<MathComponent> compIteratorB = componentsDuringCalculate.iterator();
//                while (compIteratorB.hasNext()) {
//                    MathComponent component = compIteratorB.next();
//                    if (component instanceof MathAction action) {
//                        switch (action) {
//                            case equals -> {
//                                MathComponent last = newComponentsB.getLast();
//                                MathComponent next = compIteratorB.next();
//                                newComponentsB.removeLast();
//                                float lastD = last.get();
//                                float nextD = next.get();
//                                if (calculationInstance.verboseMode) print("equals=" + lastD + " == " + nextD);
//                                newComponentsB.add(new MathVariableConstant(lastD == nextD ? 1 : 0));
//                            }
//                            case notEquals -> {
//                                MathComponent last = newComponentsB.getLast();
//                                MathComponent next = compIteratorB.next();
//                                newComponentsB.removeLast();
//                                float lastD = last.get();
//                                float nextD = next.get();
//                                if (calculationInstance.verboseMode) print("notEquals=" + lastD + " != " + nextD);
//                                newComponentsB.add(new MathVariableConstant(lastD != nextD ? 1 : 0));
//                            }
//                            case largerThan -> {
//                                MathComponent last = newComponentsB.getLast();
//                                MathComponent next = compIteratorB.next();
//                                newComponentsB.removeLast();
//                                float lastD = last.get();
//                                float nextD = next.get();
//                                if (calculationInstance.verboseMode) print("largerThan=" + lastD + " > " + nextD);
//                                newComponentsB.add(new MathVariableConstant(lastD > nextD ? 1 : 0));
//                            }
//                            case largerThanOrEquals -> {
//                                MathComponent last = newComponentsB.getLast();
//                                MathComponent next = compIteratorB.next();
//                                newComponentsB.removeLast();
//                                float lastD = last.get();
//                                float nextD = next.get();
//                                if (calculationInstance.verboseMode)
//                                    print("largerThanOrEquals=" + lastD + " >= " + nextD);
//                                newComponentsB.add(new MathVariableConstant(lastD >= nextD ? 1 : 0));
//                            }
//                            case smallerThan -> {
//                                MathComponent last = newComponentsB.getLast();
//                                MathComponent next = compIteratorB.next();
//                                newComponentsB.removeLast();
//                                float lastD = last.get();
//                                float nextD = next.get();
//                                if (calculationInstance.verboseMode) print("smallerThan=" + lastD + " < " + nextD);
//                                newComponentsB.add(new MathVariableConstant(lastD < nextD ? 1 : 0));
//                            }
//                            case smallerThanOrEquals -> {
//                                MathComponent last = newComponentsB.getLast();
//                                MathComponent next = compIteratorB.next();
//                                newComponentsB.removeLast();
//                                float lastD = last.get();
//                                float nextD = next.get();
//                                if (calculationInstance.verboseMode)
//                                    print("smallerThanOrEquals=" + lastD + " <= " + nextD);
//                                newComponentsB.add(new MathVariableConstant(lastD <= nextD ? 1 : 0));
//                            }
//                            default -> newComponentsB.add(component);
//                        }
//                    } else {
//                        newComponentsB.add(component);
//                    }
//                }
//                componentsDuringCalculate = newComponentsB;
//            }
//            if(containsBooleansLowerOrder) {
//                CalculationList newComponentsB = new CalculationList();
//                Iterator<MathComponent> compIteratorB = componentsDuringCalculate.iterator();
//                while (compIteratorB.hasNext()) {
//                    MathComponent component = compIteratorB.next();
//                    if (component instanceof MathAction action) {
//                        switch (action) {
//                            case and -> {
//                                MathComponent last = newComponentsB.getLast();
//                                MathComponent next = compIteratorB.next();
//                                newComponentsB.removeLast();
//                                float lastD = last.get();
//                                float nextD = next.get();
//                                if (calculationInstance.verboseMode)
//                                    print("and=" + (lastD == 1) + " && " + (nextD == 1));
//                                newComponentsB.add(new MathVariableConstant((lastD == 1) && (nextD == 1) ? 1 : 0, false));
//                            }
//                            case or -> {
//                                MathComponent last = newComponentsB.getLast();
//                                MathComponent next = compIteratorB.next();
//                                newComponentsB.removeLast();
//                                float lastD = last.get();
//                                float nextD = next.get();
//                                if (calculationInstance.verboseMode)
//                                    print("or=" + (lastD == 1) + " || " + (nextD == 1));
//                                newComponentsB.add(new MathVariableConstant((lastD == 1) || (nextD == 1) ? 1 : 0, false));
//                            }
//                            default -> newComponentsB.add(component);
//                        }
//                    } else {
//                        newComponentsB.add(component);
//                    }
//                }
//                componentsDuringCalculate = newComponentsB;
//            }
//
//
//            if (calculationInstance.verboseMode) print("finish calculating [" + originalExpression + "] as [" + components+"].");
//            if (componentsDuringCalculate.size() == 1) {
//                //if(verboseMode) System.out.print("group result");
//                float result = componentsDuringCalculate.getLast().get();
//                //lastResultThisTick = result;
//                if (calculationInstance.verboseMode) print(" = " + result);
//                if(Float.isNaN(result)) print(" result was NaN for expression: " + originalExpression+" as "+ components);
//                return result;
//            } else {
//                System.out.println("ERROR: calculation did not result in 1 component, found: " + componentsDuringCalculate+ " in ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].");
//                System.out.println("\texpression was ["+originalExpression+"].");
//            }
//        }catch (Exception e){
//            System.out.println("EMF animation ERROR: expression error in ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"] caused by ["+e+"].");
//        }
//
//        return Float.NaN;
//    }
}
