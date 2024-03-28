package traben.entity_model_features.models.animation.math.methods.optifine;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.*;
import traben.entity_model_features.utils.EMFUtils;

import java.util.List;

public class PrintBMethod extends MathMethod {

    private int printCount = 0;

    public PrintBMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        String id = args.get(0);
        MathComponent n = MathExpressionParser.getOptimizedExpression(args.get(1), false, calculationInstance);
        MathComponent x = MathExpressionParser.getOptimizedExpression(args.get(2), false, calculationInstance);

        setSupplierAndOptimize(() -> {
            float xVal = x.getResult();
            if (getPrintCount() % (int) n.getResult() == 0) {
                EMFUtils.log("printb: [" + id + "] = " + MathValue.toBoolean(xVal));
            }
            return xVal;
        });

    }


    private int getPrintCount() {
        printCount++;
        return printCount;
    }

    @Override
    protected boolean canOptimizeForConstantArgs() {
        return false;
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == 3;
    }

}
