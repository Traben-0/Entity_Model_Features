package traben.entity_model_features.models.animation.math;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public abstract class MathValue implements MathComponent {


    public static final float TRUE = Float.POSITIVE_INFINITY;

    //    MathValue(boolean isNegative ){
//        this.isNegative = isNegative;
//        this.calculationInstance = calculationInstance;
//        if (calculationInstance == null)
//            throw new EMFMathException("calculationInstance cannot be null if declared");//todo check if still needed
//    }
    public static final float FALSE = Float.NEGATIVE_INFINITY;
    //    final EMFAnimation calculationInstance;
    boolean isNegative;


    MathValue(boolean isNegative) {
        this.isNegative = isNegative;
//        this.calculationInstance = null;
    }

    MathValue() {
        this.isNegative = false;
//        this.calculationInstance = null;
    }

    /**
     * The singular method for EMF to parse a boolean into its math equivalent float
     *
     * @param value the boolean to convert
     * @return the float
     */
    public static float fromBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    /**
     * The singular method for EMF to parse a float into its boolean equivalent
     *
     * @param value the float to convert
     * @return the boolean
     */
    public static boolean toBoolean(float value) {
        if (value == FALSE) return false;
        if (value == TRUE) return true;
        throw new IllegalArgumentException("Value [" + value + "] is not a boolean");
    }

    public static float validateBoolean(float value) {
        //noinspection ResultOfMethodCallIgnored
        toBoolean(value);
        return value;
    }

    public static float invertBoolean(boolean value) {
        return fromBoolean(!value);
    }

    public static float invertBoolean(float value) {
        return fromBoolean(!toBoolean(value));
    }

    public static float invertBoolean(ResultSupplier value) {
        return fromBoolean(!toBoolean(value.get()));
    }

    public static float fromBoolean(BooleanSupplier value) {
        return fromBoolean(value.getAsBoolean());
    }

    public static float invertBoolean(BooleanSupplier value) {
        return invertBoolean(value.getAsBoolean());
    }

    public static boolean isBoolean(float value) {
        return value == TRUE || value == FALSE;
    }

    abstract ResultSupplier getResultSupplier();

    @Override
    public float getResult() {
        return isNegative ? -getResultSupplier().get() : getResultSupplier().get();
    }

    public MathValue getNegative() {
        isNegative = !isNegative;
        return this;
    }

    /**
     * This is simply a {@link Supplier<Float>}
     * It is declared separately for the sake of code clarity and to avoid confusion with {@link Supplier<Float>} & {@link MathComponent}
     * <p>
     * It is used to supply the result of a {@link MathValue} as that super class will then handle the negation of the value if needed
     */
    public interface ResultSupplier {
        float get();
    }

}
