package traben.entity_model_features.models.anim.EMFParser;

import traben.entity_model_features.models.anim.AnimationCalculation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class MathExpression extends MathValue implements Supplier<Double>, MathComponent{

    LinkedList<MathComponent> components;



    private final String originalExpression;

    public boolean isValid(){
        if(caughtExceptionString != null){
            System.out.println(caughtExceptionString);
            return false;
        }
        return true;
    }

    private String caughtExceptionString = null;



    public MathExpression(String expressionString, boolean isNegative, AnimationCalculation calculationInstance){
        super(isNegative,calculationInstance);

        expressionString = expressionString.trim();
        expressionString = expressionString.replaceAll("\\s", "");
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

    public double calculate() {
        try {
            //todo there is some optimization here for repeating code, but it wouldn't affect runtime :/
            //boolean pass
            LinkedList<MathComponent> componentsCopyB = new LinkedList<>(components);
            LinkedList<MathComponent> newComponentsB = new LinkedList<>();
            Iterator<MathComponent> compIteratorB = componentsCopyB.iterator();
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
                            if (calculationInstance.verboseMode) System.out.println("equals=" + lastD + " == " + nextD);
                            newComponentsB.add(new MathVariableConstant(lastD == nextD ? 1 : 0, false));
                        }
                        case notEquals -> {
                            MathComponent last = newComponentsB.getLast();
                            MathComponent next = compIteratorB.next();
                            newComponentsB.removeLast();
                            double lastD = last.get();
                            double nextD = next.get();
                            if (calculationInstance.verboseMode) System.out.println("notEquals=" + lastD + " != " + nextD);
                            newComponentsB.add(new MathVariableConstant(lastD != nextD ? 1 : 0, false));
                        }
                        case and -> {
                            MathComponent last = newComponentsB.getLast();
                            MathComponent next = compIteratorB.next();
                            newComponentsB.removeLast();
                            double lastD = last.get();
                            double nextD = next.get();
                            if (calculationInstance.verboseMode) System.out.println("and=" + (lastD == 1) + " && " + (nextD == 1));
                            newComponentsB.add(new MathVariableConstant((lastD == 1) && (nextD == 1) ? 1 : 0, false));
                        }
                        case or -> {
                            MathComponent last = newComponentsB.getLast();
                            MathComponent next = compIteratorB.next();
                            newComponentsB.removeLast();
                            double lastD = last.get();
                            double nextD = next.get();
                            if (calculationInstance.verboseMode) System.out.println("or=" + (lastD == 1) + " || " + (nextD == 1));
                            newComponentsB.add(new MathVariableConstant((lastD == 1) || (nextD == 1) ? 1 : 0, false));
                        }
                        case largerThan -> {
                            MathComponent last = newComponentsB.getLast();
                            MathComponent next = compIteratorB.next();
                            newComponentsB.removeLast();
                            double lastD = last.get();
                            double nextD = next.get();
                            if (calculationInstance.verboseMode) System.out.println("largerThan=" + lastD + " > " + nextD);
                            newComponentsB.add(new MathVariableConstant(lastD > nextD ? 1 : 0, false));
                        }
                        case largerThanOrEquals -> {
                            MathComponent last = newComponentsB.getLast();
                            MathComponent next = compIteratorB.next();
                            newComponentsB.removeLast();
                            double lastD = last.get();
                            double nextD = next.get();
                            if (calculationInstance.verboseMode) System.out.println("largerThanOrEquals=" + lastD + " >= " + nextD);
                            newComponentsB.add(new MathVariableConstant(lastD >= nextD ? 1 : 0, false));
                        }
                        case smallerThan -> {
                            MathComponent last = newComponentsB.getLast();
                            MathComponent next = compIteratorB.next();
                            newComponentsB.removeLast();
                            double lastD = last.get();
                            double nextD = next.get();
                            if (calculationInstance.verboseMode) System.out.println("smallerThan=" + lastD + " < " + nextD);
                            newComponentsB.add(new MathVariableConstant(lastD < nextD ? 1 : 0, false));
                        }
                        case smallerThanOrEquals -> {
                            MathComponent last = newComponentsB.getLast();
                            MathComponent next = compIteratorB.next();
                            newComponentsB.removeLast();
                            double lastD = last.get();
                            double nextD = next.get();
                            if (calculationInstance.verboseMode) System.out.println("smallerThanOrEquals=" + lastD + " <= " + nextD);
                            newComponentsB.add(new MathVariableConstant(lastD <= nextD ? 1 : 0, false));
                        }
                        default -> newComponentsB.add(component);
                    }
                } else {
                    newComponentsB.add(component);
                }
            }
            //multiply & divide pass
            LinkedList<MathComponent> componentsCopy = new LinkedList<>(newComponentsB);
            LinkedList<MathComponent> newComponents = new LinkedList<>();
            Iterator<MathComponent> compIterator = componentsCopy.iterator();
            while (compIterator.hasNext()) {
                MathComponent component = compIterator.next();
                if (component instanceof MathAction action) {
                    if (action == MathAction.multiply) {
                        MathComponent last = newComponents.getLast();
                        MathComponent next = compIterator.next();
                        newComponents.removeLast();
                        double lastD = last.get();
                        double nextD = next.get();
                        if (calculationInstance.verboseMode) System.out.println("multiply=" + lastD + " * " + nextD);
                        newComponents.add(new MathVariableConstant(lastD * nextD, false));
                    } else if (action == MathAction.divide) {
                        MathComponent last = newComponents.getLast();
                        MathComponent next = compIterator.next();
                        newComponents.removeLast();
                        double lastD = last.get();
                        double nextD = next.get();
                        if (calculationInstance.verboseMode) System.out.println("divide=" + lastD + " / " + nextD);
                        newComponents.add(new MathVariableConstant(lastD / nextD, false));
                    } else if (action == MathAction.divisionRemainder) {
                        MathComponent last = newComponents.getLast();
                        MathComponent next = compIterator.next();
                        newComponents.removeLast();
                        double lastD = last.get();
                        double nextD = next.get();
                        if (calculationInstance.verboseMode) System.out.println("divide remainder=" + lastD + " % " + nextD);
                        newComponents.add(new MathVariableConstant(lastD % nextD, false));
                    } else {
                        newComponents.add(component);
                    }
                } else {
                    newComponents.add(component);
                }

            }
            //add & subtract pass
            LinkedList<MathComponent> newComponentsCopy = new LinkedList<>(newComponents);
            LinkedList<MathComponent> newComponents2 = new LinkedList<>();
            Iterator<MathComponent> compIterator2 = newComponentsCopy.iterator();
            while (compIterator2.hasNext()) {
                MathComponent component = compIterator2.next();
                if (component instanceof MathAction action) {
                    if (action == MathAction.add) {
                        MathComponent last = newComponents2.getLast();
                        MathComponent next = compIterator2.next();
                        newComponents2.removeLast();
                        double lastD = last.get();
                        double nextD = next.get();
                        if (calculationInstance.verboseMode) System.out.println("add=" + lastD + " + " + nextD);
                        newComponents2.add(new MathVariableConstant(lastD + nextD, false));
                    } else if (action == MathAction.subtract) {
                        MathComponent last = newComponents2.getLast();
                        MathComponent next = compIterator2.next();
                        newComponents2.removeLast();
                        double lastD = last.get();
                        double nextD = next.get();
                        if (calculationInstance.verboseMode) System.out.println("subtract=" + lastD + " - " + nextD);
                        newComponents2.add(new MathVariableConstant(lastD - nextD, false));
                    } else {
                        newComponents.add(component);
                    }
                } else {
                    newComponents2.add(component);
                }

            }

            if (newComponents2.size() == 1) {

                //if(verboseMode) System.out.print("group result");
                double result = newComponents2.getLast().get();
                if (calculationInstance.verboseMode) System.out.println(" = " + result);
                return result;
            } else {
                System.out.println("ERROR: calculation did not result in 1 component, found: " + newComponents2.toString());
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
