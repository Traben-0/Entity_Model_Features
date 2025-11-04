package traben.entity_model_features.models.animation.math.methods.optifine;

import net.minecraft.client.Minecraft;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.*;
import traben.entity_model_features.utils.EMFUtils;

import java.util.List;

public class PrintBMethod extends MathMethod {

    private int printCount = 0;

    public PrintBMethod(final List<String> args, final boolean isNegative, final EMFAnimation calculationInstance) throws EMFMathException {
        super(isNegative, calculationInstance, args.size());

        if (args.size() == 1) {
            var expressionStr = args.get(0);
            MathComponent x = MathExpressionParser.getOptimizedExpression(expressionStr, false, calculationInstance);
            setSupplierAndOptimize(() -> {
                float xVal = x.getResult();
                if (!Minecraft.getInstance().isPaused() && !EMFManager.getInstance().isAnimationValidationPhase) {
                    EMFUtils.log("print: [" + expressionStr + "] = " + MathValue.toBoolean(xVal));
                }
                return xVal;
            });
            return;
        }

        String id = args.get(0);
        MathComponent n = MathExpressionParser.getOptimizedExpression(args.get(1), false, calculationInstance);
        MathComponent x = MathExpressionParser.getOptimizedExpression(args.get(2), false, calculationInstance);

        setSupplierAndOptimize(() -> {
            float xVal = x.getResult();
            if (!Minecraft.getInstance().isPaused() && getPrintCount() % (int) n.getResult() == 0 && !EMFManager.getInstance().isAnimationValidationPhase) {
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
        return argCount == 3 || argCount == 1;
    }

}
