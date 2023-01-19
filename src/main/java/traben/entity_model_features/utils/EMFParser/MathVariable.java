package traben.entity_model_features.utils.EMFParser;

import java.util.function.Supplier;

public class MathVariable extends MathValue implements Supplier<Double> , MathComponent{


    Supplier<Double> valueSupplier;
    public boolean isNegative = false;
    Double hardCodedValue = null;



    public MathVariable(String value, boolean isNegative){
        super(isNegative);
        System.out.println("variable setup for " +value);
        valueSupplier = ()->0d;
        //todo
       // hardCodedValue = value;
        //valueSupplier = this::getHardCodedValue;
    }
    public MathVariable(double number, boolean isNegative){
        super(isNegative);
        hardCodedValue = number;
        valueSupplier = this::getHardCodedValue;
    }

    @Override
    public Supplier<Double> getSupplier() {
        return valueSupplier;
    }

    private Double getHardCodedValue(){
        return hardCodedValue;
    }



    @Override
    public String toString() {
        return get().toString();
    }
}
