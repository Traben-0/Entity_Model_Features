package traben.entity_model_features.models.animation.math;

import net.minecraft.util.math.floatprovider.FloatSupplier;
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

    /**
     * This is simply a {@link FloatSupplier}
     * It is declared separately for the sake of code clarity and to avoid confusion with {@link FloatSupplier} & {@link MathComponent}
     * <p>
     * It is used to supply the result of a {@link MathValue} as that super class will then handle the negation of the value if needed
     */
    public interface ResultSupplier {
        float get();
    }


}
