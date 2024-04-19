package traben.entity_model_features.models.animation.math.methods.emf;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathExpressionParser;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.utils.EMFUtils;

import java.util.List;

public class CatchMethod extends MathMethod {

    public CatchMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        MathComponent x = MathExpressionParser.getOptimizedExpression(args.get(0), false, calculationInstance);
        MathComponent c = MathExpressionParser.getOptimizedExpression(args.get(1), false, calculationInstance);

        final String print;
        if (args.size() == 3 && !args.get(2).isBlank()) {
            print = args.get(2);
        } else {
            print = null;
        }

        setSupplierAndOptimize(() -> {
            try {
                float result = x.getResult();
                if (Float.isNaN(result)) {
                    if (print != null) {
                        EMFUtils.log("print: catch(" + print + ") found NaN in x.");
                    }
                    return c.getResult();
                }
                return result;
            } catch (Exception e) {
                //EMFUtils.log("Caught exception: " + e.getMessage());
                if (print != null) {
                    EMFUtils.log("print: catch(" + print + ") found Exception in x: " + e.getMessage());
                }
                return c.getResult();
            }
        }, List.of(x, c));

    }

    @Override
    protected boolean canOptimizeForConstantArgs() {
        return false;
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == 2 || argCount == 3;
    }

}
