package traben.entity_model_features.models.animation.animation_math_parser;

public class MathConstant extends MathValue implements MathComponent {

    float hardCodedValue;

    public MathConstant(float number, boolean isNegative) {
        //super(isNegative);

        hardCodedValue = isNegative ? -number : number;
        //reciprocal = 1/hardCodedValue;
    }

    //public float reciprocal;


    public MathConstant(float number) {
        hardCodedValue = number;
        //reciprocal = 1/ hardCodedValue;
    }

    @Override
    public ValueSupplier getSupplier() {
        System.out.println("EMF math constant: this shouldn't happen!");
        return () -> hardCodedValue;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public void makeNegative(boolean become) {
        if (become) hardCodedValue = -hardCodedValue;
        //reciprocal = 1 / hardCodedValue;
    }

    @Override
    public String toString() {
        return String.valueOf(get());
    }

    @Override // make fastest return
    public float get() {
        //if(calculationInstance != null)
        //calculationInstance.indentCount++;
        //if(calculationInstance != null)
        //calculationInstance.indentCount--;
        return hardCodedValue;
    }
}
