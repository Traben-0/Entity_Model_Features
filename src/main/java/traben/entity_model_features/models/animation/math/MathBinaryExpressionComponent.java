package traben.entity_model_features.models.animation.math;

public class MathBinaryExpressionComponent extends MathValue implements MathComponent {


    private final MathComponent first;
    private final MathOperator action;
    private final MathComponent second;


    private MathBinaryExpressionComponent(MathComponent first, MathOperator action, MathComponent second) {
        this.first = first;
        this.action = action;
        this.second = second;
    }

    public static MathComponent getOptimizedExpression(MathComponent first, MathOperator action, MathComponent second) {
        MathBinaryExpressionComponent component = new MathBinaryExpressionComponent(first, action, second);
        if (component.first.isConstant() && component.second.isConstant()) {
            //result is always constant so return a constant instead
            return new MathConstant(component.getResult(), false);
        }
        return component;
    }


    @Override
    ResultSupplier getResultSupplier() {
        return null;
    }

    @Override
    public float getResult() {
        float value = action.execute(first, second);
        return isNegative ? -value : value;
    }

    @Override
    public String toString() {
        return "[oExp:{" + first + ", " + action + ", " + second + "}=" + getResult() + "]";
    }


}
