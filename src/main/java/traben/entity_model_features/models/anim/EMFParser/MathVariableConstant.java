package traben.entity_model_features.models.anim.EMFParser;

public class MathVariableConstant extends MathValue implements MathComponent{

    float hardCodedValue;

    public MathVariableConstant(float number, boolean isNegative){
        super(isNegative);
        hardCodedValue = number;
    }
    public MathVariableConstant(float number){
        hardCodedValue = number;
    }
    @Override
    public ValueSupplier getSupplier() {
        return ()-> hardCodedValue;
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
        return isNegative ? -hardCodedValue : hardCodedValue;
    }
}
