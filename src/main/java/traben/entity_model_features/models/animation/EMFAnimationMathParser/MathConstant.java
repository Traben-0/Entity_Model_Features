package traben.entity_model_features.models.animation.EMFAnimationMathParser;

public class MathConstant extends MathValue implements MathComponent{

    double hardCodedValue;

    public MathConstant(double number, boolean isNegative){
        //super(isNegative);

        hardCodedValue = isNegative ? -number : number;
    }
    public MathConstant(double number){
        hardCodedValue = number;
    }
    @Override
    public ValueSupplier getSupplier() {
        System.out.println("EMF math constant: this shouldn't happen!");
        return ()-> hardCodedValue;
    }

    @Override
    public boolean isConstant() {
        return true;
    }
    @Override
    public void makeNegative(boolean become){
        if(become) hardCodedValue = -hardCodedValue;
    }
    @Override
    public String toString() {
        return String.valueOf(get());
    }

    @Override // make fastest return
    public double get() {
        //if(calculationInstance != null)
            //calculationInstance.indentCount++;
        //if(calculationInstance != null)
            //calculationInstance.indentCount--;
        return hardCodedValue;
    }
}
