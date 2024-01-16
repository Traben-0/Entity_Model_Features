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


    @Override
    public float get() {
        return isNegative ? -getSupplier().get() : getSupplier().get();
    }

    public void makeNegative(boolean become) {
        if (become) isNegative = !isNegative;
    }

    public interface ValueSupplier {
        float get();
    }


}
