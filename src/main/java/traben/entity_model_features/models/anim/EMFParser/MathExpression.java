package traben.entity_model_features.models.anim.EMFParser;

import traben.entity_model_features.models.anim.AnimationCalculation;
import traben.entity_model_features.models.anim.AnimationCalculationEMFParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class MathExpression extends MathValue implements Supplier<Double>, MathComponent{

    LinkedList<MathComponent> components;



    public final String originalExpression;

    public boolean isValid(){
        if(caughtExceptionString != null){
            System.out.println(caughtExceptionString);
            return false;
        }
        return true;
    }

    private String caughtExceptionString = null;

    private boolean containsBooleans = false;
    private boolean containsMultiplicationLevel = false;
    private boolean containsAdditionLevel = false;
    private boolean containsOneComponent = false;




    public MathExpression(String expressionString, boolean isNegative, AnimationCalculation calculationInstance){
        super(isNegative, (AnimationCalculationEMFParser) calculationInstance);

        expressionString = expressionString.trim();
        expressionString = expressionString.replaceAll("\\s*", "");
        originalExpression = expressionString;

        components = new LinkedList<>();
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
                if(firstBooleanChar != null){
                    if(action == MathAction.BOOLEAN_CHAR){
                        action = switch (firstBooleanChar+""+ch){
                            case "==" -> MathAction.equals;
                            case "!=" -> MathAction.notEquals;
                            case "&&" -> MathAction.and;
                            case "||" -> MathAction.or;
                            case ">=" -> MathAction.largerThanOrEquals;
                            case "<=" -> MathAction.smallerThanOrEquals;
                            default -> throw new EMFMathException("ERROR: with boolean processing for operator ["+firstBooleanChar+""+ch+"] for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].");
                        };
                        //add complete double boolean action
                        components.add(action);
                        //now iterate once for a regular iteration
                        ch = charIterator.next();
                        action = MathAction.getAction(ch);
                    }else{
                        if(firstBooleanChar == '!') {
                            //likely a '!' for boolean variables so need to add to read
                            rollingRead.append(firstBooleanChar);
                        }else{
                            //add complete single char boolean action
                            components.add(switch (firstBooleanChar){
                                case '=' -> MathAction.equals;
                                case '&' -> MathAction.and;
                                case '|' -> MathAction.or;
                                case '<' -> MathAction.smallerThan;
                                case '>' -> MathAction.largerThan;
                                default -> throw new EMFMathException("ERROR: with boolean processing for operator ["+firstBooleanChar+"] for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].");
                            });
                        }
                    }
                    firstBooleanChar = null;
                }else if(action == MathAction.BOOLEAN_CHAR){
                    firstBooleanChar = ch;
                }
                //critical that elif stops here
                if (action == MathAction.subtract &&
                            ((components.isEmpty() && rollingRead.isEmpty())
                                    || (!components.isEmpty() && components.getLast() instanceof MathAction && rollingRead.isEmpty()))) {
                    isNegativeValueNext = true;
                } else if (action == MathAction.none) {
                    rollingRead.append(ch);
                } else {
                    if (action == MathAction.openBracket) {
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

                        if (functionName.isEmpty()) {
                            //just brackets
                            MathExpression brackets = new MathExpression(bracketContents.toString(), getNegativeNext(), this.calculationInstance);
                            components.add(brackets);
                        } else {
                            //method
                            MathMethod method = new MathMethod(functionName, bracketContents.toString(), getNegativeNext(), this.calculationInstance);
                            components.add(method);
                        }

                    } else {//action should be only + - * / ^
                        ////////////////////////////////////////////////
                        //discover rolling read value
                        String read = rollingRead.toString();
                        rollingRead = new StringBuilder();
                        if (!read.isEmpty()) {
                            Double asNumber = null;
                            try {
                                asNumber = Double.parseDouble(read);
                            } catch (NumberFormatException ignored) {
                            }

                            MathValue variable;
                            if (asNumber == null) {
                                variable = new MathVariableUpdatable(read, getNegativeNext(), this.calculationInstance);
                            } else {
                                variable = new MathVariableConstant(asNumber, getNegativeNext());
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
                rollingRead = new StringBuilder();
                Double asNumber = null;
                try {
                    asNumber = Double.parseDouble(read);
                } catch (NumberFormatException ignored) {
                }

                MathValue variable;
                if (asNumber == null) {
                    variable = new MathVariableUpdatable(read, getNegativeNext(), this.calculationInstance);
                } else {
                    variable = new MathVariableConstant(asNumber, getNegativeNext());
                }
                components.add(variable);
                ///////////////////////////////////////////
            }

            if(components.isEmpty()) throw new EMFMathException("ERROR: math components found to be empty for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"]");

            //assess and store content metadata
            if(components.size() == 1){
                this.containsOneComponent = true;
            }else {
                this.containsBooleans = (components.contains(MathAction.equals)
                        || components.contains(MathAction.and)
                        || components.contains(MathAction.largerThan)
                        || components.contains(MathAction.largerThanOrEquals)
                        || components.contains(MathAction.smallerThan)
                        || components.contains(MathAction.smallerThanOrEquals)
                        || components.contains(MathAction.notEquals)
                        || components.contains(MathAction.or));

                this.containsMultiplicationLevel = (components.contains(MathAction.multiply)
                        || components.contains(MathAction.divide)
                        || components.contains(MathAction.divisionRemainder));

                this.containsAdditionLevel = (components.contains(MathAction.add)
                        || components.contains(MathAction.subtract));
            }



           // System.out.println(components);
        }catch (EMFMathException e){
            caughtExceptionString = e.toString();
        }catch (Exception e){
            caughtExceptionString = "EMF animation ERROR: for ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"] cause ["+e.toString()+"].";
            e.printStackTrace();
        }
    }

    boolean isNegativeValueNext = false;

    private boolean getNegativeNext(){
        boolean neg = isNegativeValueNext;
        isNegativeValueNext = false;
        return neg;
    }

//    Double lastResultThisTick = null;
//    long lastResultCount = 0;


    LinkedList<MathComponent> componentsDuringCalculate;
    public double calculate() {

        try {

            //it is possible the expression is simply 1 value, just return the single component value in this case for efficiency
            if(containsOneComponent){
               // if (calculationInstance.verboseMode) print("finished calculating [" + originalExpression + "] as single component quick return.");
                return components.getLast().get();
            }

            if (calculationInstance.verboseMode) print("start calculating [" + originalExpression + "] as [" + components+"].");

            //reset every calculate
            componentsDuringCalculate = new LinkedList<>(components);

            //boolean pass
            if(containsBooleans) {
                LinkedList<MathComponent> newComponentsB = new LinkedList<>();
                Iterator<MathComponent> compIteratorB = componentsDuringCalculate.iterator();
                while (compIteratorB.hasNext()) {
                    MathComponent component = compIteratorB.next();
                    if (component instanceof MathAction action) {
                        switch (action) {
                            case equals -> {
                                MathComponent last = newComponentsB.getLast();
                                MathComponent next = compIteratorB.next();
                                newComponentsB.removeLast();
                                double lastD = last.get();
                                double nextD = next.get();
                                if (calculationInstance.verboseMode) print("equals=" + lastD + " == " + nextD);
                                newComponentsB.add(new MathVariableConstant(lastD == nextD ? 1 : 0, false));
                            }
                            case notEquals -> {
                                MathComponent last = newComponentsB.getLast();
                                MathComponent next = compIteratorB.next();
                                newComponentsB.removeLast();
                                double lastD = last.get();
                                double nextD = next.get();
                                if (calculationInstance.verboseMode) print("notEquals=" + lastD + " != " + nextD);
                                newComponentsB.add(new MathVariableConstant(lastD != nextD ? 1 : 0, false));
                            }
                            case and -> {
                                MathComponent last = newComponentsB.getLast();
                                MathComponent next = compIteratorB.next();
                                newComponentsB.removeLast();
                                double lastD = last.get();
                                double nextD = next.get();
                                if (calculationInstance.verboseMode)
                                    print("and=" + (lastD == 1) + " && " + (nextD == 1));
                                newComponentsB.add(new MathVariableConstant((lastD == 1) && (nextD == 1) ? 1 : 0, false));
                            }
                            case or -> {
                                MathComponent last = newComponentsB.getLast();
                                MathComponent next = compIteratorB.next();
                                newComponentsB.removeLast();
                                double lastD = last.get();
                                double nextD = next.get();
                                if (calculationInstance.verboseMode)
                                    print("or=" + (lastD == 1) + " || " + (nextD == 1));
                                newComponentsB.add(new MathVariableConstant((lastD == 1) || (nextD == 1) ? 1 : 0, false));
                            }
                            case largerThan -> {
                                MathComponent last = newComponentsB.getLast();
                                MathComponent next = compIteratorB.next();
                                newComponentsB.removeLast();
                                double lastD = last.get();
                                double nextD = next.get();
                                if (calculationInstance.verboseMode) print("largerThan=" + lastD + " > " + nextD);
                                newComponentsB.add(new MathVariableConstant(lastD > nextD ? 1 : 0, false));
                            }
                            case largerThanOrEquals -> {
                                MathComponent last = newComponentsB.getLast();
                                MathComponent next = compIteratorB.next();
                                newComponentsB.removeLast();
                                double lastD = last.get();
                                double nextD = next.get();
                                if (calculationInstance.verboseMode)
                                    print("largerThanOrEquals=" + lastD + " >= " + nextD);
                                newComponentsB.add(new MathVariableConstant(lastD >= nextD ? 1 : 0, false));
                            }
                            case smallerThan -> {
                                MathComponent last = newComponentsB.getLast();
                                MathComponent next = compIteratorB.next();
                                newComponentsB.removeLast();
                                double lastD = last.get();
                                double nextD = next.get();
                                if (calculationInstance.verboseMode) print("smallerThan=" + lastD + " < " + nextD);
                                newComponentsB.add(new MathVariableConstant(lastD < nextD ? 1 : 0, false));
                            }
                            case smallerThanOrEquals -> {
                                MathComponent last = newComponentsB.getLast();
                                MathComponent next = compIteratorB.next();
                                newComponentsB.removeLast();
                                double lastD = last.get();
                                double nextD = next.get();
                                if (calculationInstance.verboseMode)
                                    print("smallerThanOrEquals=" + lastD + " <= " + nextD);
                                newComponentsB.add(new MathVariableConstant(lastD <= nextD ? 1 : 0, false));
                            }
                            default -> newComponentsB.add(component);
                        }
                    } else {
                        newComponentsB.add(component);
                    }
                }
                componentsDuringCalculate = newComponentsB;
            }
            //multiply & divide pass
            if(containsMultiplicationLevel) {
                LinkedList<MathComponent> newComponents = new LinkedList<>();
                Iterator<MathComponent> compIterator = componentsDuringCalculate.iterator();
                while (compIterator.hasNext()) {
                    MathComponent component = compIterator.next();
                    if (component instanceof MathAction action) {
                        if (action == MathAction.multiply) {
                            MathComponent last = newComponents.getLast();
                            MathComponent next = compIterator.next();
                            newComponents.removeLast();
                            double lastD = last.get();
                            double nextD = next.get();
                            if (calculationInstance.verboseMode) print("multiply=" + lastD + " * " + nextD);
                            newComponents.add(new MathVariableConstant(lastD * nextD, false));
                        } else if (action == MathAction.divide) {
                            MathComponent last = newComponents.getLast();
                            MathComponent next = compIterator.next();
                            newComponents.removeLast();
                            double lastD = last.get();
                            double nextD = next.get();
                            if (calculationInstance.verboseMode) print("divide=" + lastD + " / " + nextD);
                            newComponents.add(new MathVariableConstant(lastD / nextD, false));
                        } else if (action == MathAction.divisionRemainder) {
                            MathComponent last = newComponents.getLast();
                            MathComponent next = compIterator.next();
                            newComponents.removeLast();
                            double lastD = last.get();
                            double nextD = next.get();
                            if (calculationInstance.verboseMode) print("divide remainder=" + lastD + " % " + nextD);
                            newComponents.add(new MathVariableConstant(lastD % nextD, false));
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
            if(containsAdditionLevel) {
                LinkedList<MathComponent> newComponents2 = new LinkedList<>();
                Iterator<MathComponent> compIterator2 = componentsDuringCalculate.iterator();
                while (compIterator2.hasNext()) {
                    MathComponent component = compIterator2.next();
                    if (component instanceof MathAction action) {
                        if (action == MathAction.add) {
                            MathComponent last = newComponents2.getLast();
                            MathComponent next = compIterator2.next();
                            newComponents2.removeLast();
                            double lastD = last.get();
                            double nextD = next.get();
                            if (calculationInstance.verboseMode) print("add=" + lastD + " + " + nextD);
                            newComponents2.add(new MathVariableConstant(lastD + nextD, false));
                        } else if (action == MathAction.subtract) {
                            MathComponent last = newComponents2.getLast();
                            MathComponent next = compIterator2.next();
                            newComponents2.removeLast();
                            double lastD = last.get();
                            double nextD = next.get();
                            if (calculationInstance.verboseMode) print("subtract=" + lastD + " - " + nextD);
                            newComponents2.add(new MathVariableConstant(lastD - nextD, false));
                        } else {
                            newComponents2.add(component);
                        }
                    } else {
                        newComponents2.add(component);
                    }
                }
                componentsDuringCalculate = newComponents2;
            }
            if (calculationInstance.verboseMode) print("finish calculating [" + originalExpression + "] as [" + components+"].");
            if (componentsDuringCalculate.size() == 1) {
                // lastResultCount = calculationInstance.calculationCount;
                //if(verboseMode) System.out.print("group result");
                double result = componentsDuringCalculate.getLast().get();
                //lastResultThisTick = result;
                if (calculationInstance.verboseMode) print(" = " + result);
                return result;
            } else {
                System.out.println("ERROR: calculation did not result in 1 component, found: " + componentsDuringCalculate.toString()+ " in ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"].");
                System.out.println("\texpression was ["+originalExpression+"].");
            }
        }catch (Exception e){
            System.out.println("EMF animation ERROR: expression error in ["+calculationInstance.animKey+"] in ["+calculationInstance.parentModel.modelPathIdentifier+"] caused by ["+e+"].");
        }

        return Double.NaN;
    }


    @Override
    public Supplier<Double> getSupplier() {
        return this::calculate;
    }

//    @Override
//    public Double get() {
//        return calculate();
//    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (MathComponent comp:
             components) {
            builder.append(comp.toString()).append(" ");
        }
        builder.append("}");
        return builder.toString();

    }
}
