package traben.entity_model_features.utils.EMFParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class MathExpression extends MathValue implements Supplier<Double>, MathComponent{

    LinkedList<MathComponent> components;

    MathExpression(String expressionString, boolean isNegative){
        super(isNegative);
        expressionString = expressionString.trim();
        expressionString = expressionString.replaceAll("\\s","");



        components = new LinkedList<>();

        StringBuilder rollingRead = new StringBuilder();
        List<Character> charList = new ArrayList<>();
        for (char ch:
             expressionString.toCharArray()) {
            charList.add(ch);
        }
        Iterator<Character> charIterator = charList.iterator();

        while (charIterator.hasNext()) {
            char ch = charIterator.next();
            MathAction action = MathAction.getAction(ch);
            if(action == MathAction.subtract &&
                    (components.isEmpty() || components.getLast() instanceof MathAction)) {
                isNegativeValueNext = true;
            }else if(action == MathAction.none){
                rollingRead.append(ch);
            }else{
                if(action == MathAction.openBracket){
                    String functionName = rollingRead.toString();
                    rollingRead = new StringBuilder();

                    StringBuilder bracketContents = new StringBuilder();
                    int bracketsCount = 0;
                    while (charIterator.hasNext()){
                        char ch2 = charIterator.next();
                        if (ch2 == '(') {
                            bracketContents.append(ch2);
                            bracketsCount++;
                        }else if(ch2 == ')'){
                            if(bracketsCount == 0 ){
                                break;
                            }else{
                                bracketContents.append(ch2);
                                bracketsCount--;
                            }
                        }else{
                            bracketContents.append(ch2);
                        }
                    }

                    if(functionName.isEmpty()){
                        //just brackets
                        MathExpression brackets = new MathExpression(bracketContents.toString(),getNegativeNext());
                        components.add(brackets);
                    }else{
                        //method
                        MathMethod method = new MathMethod(functionName,bracketContents.toString(),getNegativeNext());
                        components.add(method);
                    }

                }else {//action should be only + - * / ^
                    ////////////////////////////////////////////////
                    //discover rolling read value
                    String read = rollingRead.toString();
                    rollingRead = new StringBuilder();
                    if(!read.isEmpty()) {
                        Double asNumber = null;
                        try {
                            asNumber = Double.parseDouble(read);
                        } catch (NumberFormatException ignored) {
                        }

                        MathVariable variable;
                        if (asNumber == null) {
                            variable = new MathVariable(read, getNegativeNext());
                        } else {
                            variable = new MathVariable(asNumber, getNegativeNext());
                        }
                        components.add(variable);
                        ///////////////////////////////////////////
                    }
                    //finally add new math action
                    if(rollingRead.isEmpty()){
                        components.add(action);
                    }
                }
            }

        }
        //add last read data
        if(!rollingRead.isEmpty()) {
            ////////////////////////////////////////////////
            //discover rolling read value
            String read = rollingRead.toString();
            rollingRead = new StringBuilder();
            Double asNumber = null;
            try {
                asNumber = Double.parseDouble(read);
            } catch (NumberFormatException ignored) {
            }

            MathVariable variable;
            if (asNumber == null) {
                variable = new MathVariable(read, getNegativeNext());
            } else {
                variable = new MathVariable(asNumber, getNegativeNext());
            }
            components.add(variable);
            ///////////////////////////////////////////
        }




    }

    boolean isNegativeValueNext = false;

    private boolean getNegativeNext(){
        boolean neg = isNegativeValueNext;
        isNegativeValueNext = false;
        return neg;
    }

    public double calculate(){
        //multiply & divide pass
        LinkedList<MathComponent> componentsCopy = new LinkedList<>(components);
        LinkedList<MathComponent> newComponents = new LinkedList<>();
        Iterator<MathComponent> compIterator = componentsCopy.iterator();
        while (compIterator.hasNext()){
            MathComponent component = compIterator.next();
            if (component instanceof MathAction action){
                if(action == MathAction.multiply){
                    MathComponent last = newComponents.getLast();
                    MathComponent next = compIterator.next();
                    newComponents.removeLast();
                    double lastD =last.get();
                    double nextD = next.get();
                    System.out.println("multiply="+lastD+" * "+ nextD);
                    newComponents.add(new MathVariable(lastD * nextD,false));
                }else if(action == MathAction.divide){
                    MathComponent last = newComponents.getLast();
                    MathComponent next = compIterator.next();
                    newComponents.removeLast();
                    double lastD =last.get();
                    double nextD = next.get();
                    System.out.println("divide="+lastD+" / "+ nextD);
                    newComponents.add(new MathVariable(lastD / nextD,false));
                }else if(action == MathAction.divisionRemainder){
                    MathComponent last = newComponents.getLast();
                    MathComponent next = compIterator.next();
                    newComponents.removeLast();
                    double lastD =last.get();
                    double nextD = next.get();
                    System.out.println("divide remainder="+lastD+" % "+ nextD);
                    newComponents.add(new MathVariable(lastD % nextD,false));
                }else{
                    newComponents.add(component);
                }
            }else{
                newComponents.add(component);
            }

        }
        //add & subtract pass
        LinkedList<MathComponent> newComponentsCopy = new LinkedList<>(newComponents);
        LinkedList<MathComponent> newComponents2 = new LinkedList<>();
        Iterator<MathComponent> compIterator2 = newComponentsCopy.iterator();
        while (compIterator2.hasNext()){
            MathComponent component = compIterator2.next();
            if (component instanceof MathAction action){
                if(action == MathAction.add){
                    MathComponent last = newComponents2.getLast();
                    MathComponent next = compIterator2.next();
                    newComponents2.removeLast();
                    double lastD =last.get();
                    double nextD = next.get();
                    System.out.println("add="+lastD+" + "+ nextD);
                    newComponents2.add(new MathVariable(lastD + nextD,false));
                }else if(action == MathAction.subtract){
                    MathComponent last = newComponents2.getLast();
                    MathComponent next = compIterator2.next();
                    newComponents2.removeLast();
                    double lastD =last.get();
                    double nextD = next.get();
                    System.out.println("subtract="+lastD+" - "+ nextD);
                    newComponents2.add(new MathVariable(lastD - nextD,false));
                }else{
                    newComponents.add(component);
                }
            }else{
                newComponents2.add(component);
            }

        }
        if (newComponents2.size() == 1){

            System.out.print("section result");
            double result = newComponents2.getLast().get();
            System.out.println("="+result);
            return result;
        }else{
            System.out.println("section wrong="+newComponents2.toString());
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
