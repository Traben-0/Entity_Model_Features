package traben.entity_model_features.models.animation.animation_math_parser;

public class MathBinaryExpressionComponent extends MathValue implements MathComponent {


    private final MathComponent first;
    private final MathAction action;
    private final MathComponent second;


    private MathBinaryExpressionComponent(MathComponent first, MathAction action, MathComponent second, boolean isNegative) {
        super(isNegative);
        this.first = first;
        this.action = action;
        this.second = second;
        ValueSupplier supplier = action.getBinaryRunnable(first, second);
    }

    public static MathComponent getOptimizedExpression(MathComponent first, MathAction action, MathComponent second) {
        return getOptimizedExpression(first, action, second, false);
    }

    public static MathComponent getOptimizedExpression(MathComponent first, MathAction action, MathComponent second, boolean isnegative) {
        MathBinaryExpressionComponent component = new MathBinaryExpressionComponent(first, action, second, isnegative);
        if (component.first.isConstant() && component.second.isConstant()) {
            //result is always constant so return the constant result instead
            return new MathConstant(component.get(), isnegative);
        }
        return component;
    }

    @Override
    public ValueSupplier getSupplier() {
        return null;
    }

    @Override
    public float get() {
        return isNegative ? -action.run(first, second) : action.run(first, second);

       // return isNegative ? -supplier.get() : supplier.get();

    }

    @Override
    public String toString() {
        return "[oExp:{" + first + ", " + action + ", " + second + "}=" + get() + "]";
    }


}
