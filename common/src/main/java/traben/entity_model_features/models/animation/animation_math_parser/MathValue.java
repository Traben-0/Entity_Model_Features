package traben.entity_model_features.models.animation.animation_math_parser;

import traben.entity_model_features.models.animation.EMFAnimation;

public abstract class MathValue implements MathComponent {


    final EMFAnimation calculationInstance;
    boolean isNegative;

    MathValue(boolean isNegative, EMFAnimation calculationInstance) throws EMFMathException {
        this.isNegative = isNegative;
        this.calculationInstance = calculationInstance;
        if (calculationInstance == null)
            throw new EMFMathException("calculationInstance cannot be null if declared");//todo check if still needed
    }

    MathValue(boolean isNegative) {
        this.isNegative = isNegative;
        this.calculationInstance = null;
    }

    MathValue() {
        this.isNegative = false;
        this.calculationInstance = null;
    }

    abstract ResultSupplier getResultSupplier();


    @Override
    public float getResult() {
        return isNegative ? -getResultSupplier().get() : getResultSupplier().get();
    }

    public void makeNegative(boolean become) {
        if (become) isNegative = !isNegative;
    }

    public interface ResultSupplier {
        float get();
    }


}
