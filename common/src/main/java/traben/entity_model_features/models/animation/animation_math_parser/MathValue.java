package traben.entity_model_features.models.animation.animation_math_parser;

import traben.entity_model_features.models.animation.EMFAnimation;

public abstract class MathValue implements MathComponent {


    final EMFAnimation calculationInstance;
    public boolean isNegative;

    MathValue(boolean isNegative, EMFAnimation calculationInstance) {
        this.isNegative = isNegative;
        this.calculationInstance = calculationInstance;
    }

    MathValue(boolean isNegative) {
        this.isNegative = isNegative;
        this.calculationInstance = null;
    }

    MathValue() {
        this.isNegative = false;
        this.calculationInstance = null;
    }

    abstract public ValueSupplier getSupplier();

    public void print(String str) {
        if (calculationInstance != null)
            calculationInstance.animPrint(str);
    }

    @Override
    public float get() {
        if (calculationInstance != null)
            calculationInstance.indentCount++;
        float ret = isNegative ? -getSupplier().get() : getSupplier().get();
        if (calculationInstance != null)
            calculationInstance.indentCount--;
        return ret;
    }

    public void makeNegative(boolean become) {
        if (become) isNegative = !isNegative;
    }

    public interface ValueSupplier {
        float get();
    }


}
