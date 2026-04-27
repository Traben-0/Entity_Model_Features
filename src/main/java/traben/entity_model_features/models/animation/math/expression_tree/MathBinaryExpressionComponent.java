package traben.entity_model_features.models.animation.math.expression_tree;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;

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
        if (first.isConstant() && second.isConstant()) {

            if (second.getResult() == 0
                    && (action == MathOperator.DIVIDE || action == MathOperator.DIVISION_REMAINDER)) {
                // Don't optimize a constant from division result in setup phase
                return new MathBinaryExpressionComponent(first.toConstant(), action, second);
            }

            //result is always constant so return a constant instead
            return component.toConstant();
        }
        return component;
    }

    @Override
    public void asmVisit(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {

        boolean overrideScopeToBool;
        if (action.isEqualsType()) {
            // Little tricky here...
            // These could accept either float or bool comparisons but bytecode isn't that simple
            // Just gonna parse the current MathComponent values and take a guess //TODO

            var b = MathValue.isBoolean(first.getResult());
            var bb = MathValue.isBoolean(second.getResult());

            if (b && bb) {
                overrideScopeToBool = true; // Confident
            } else overrideScopeToBool = b || bb; // Less confident

        } else {
            overrideScopeToBool = false;
        }

        if (overrideScopeToBool || action.isFirstScopeBool()) vars.scopeBool();
        else vars.scopeFloat();
        first.asmVisit(mv, vars);
        vars.scopePop();

        if (overrideScopeToBool || action.isSecondScopeBool()) vars.scopeBool();
        else vars.scopeFloat();
        second.asmVisit(mv, vars);
        vars.scopePop();

        action.asmVisit(mv, vars);
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
