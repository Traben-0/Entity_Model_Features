package traben.entity_model_features.models.animation.math.methods.optifine;

import net.minecraft.client.Minecraft;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.*;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathExpressionParser;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.utils.EMFUtils;

import java.util.List;

public class PrintBMethod extends MathMethod {

    private int printCount = 0;

    public PrintBMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
        super(isNegative, context, args);

        if (args.size() == 1) {
            var expressionStr = args.get(0);
            MathComponent x = MathExpressionParser.getOptimizedExpression(expressionStr, false, context);
            setSupplierAndOptimize(() -> {
                float xVal = x.getResult();
                if (!Minecraft.getInstance().isPaused() && !EMFManager.getInstance().isAnimationValidationPhase) {
                    EMFUtils.log("printb: [" + expressionStr + "] = " + MathValue.toBoolean(xVal));
                }
                return xVal;
            });
            return;
        }

        String id = args.get(0);
        MathComponent n = parsedArgs.get(1);
        MathComponent x = parsedArgs.get(2);

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

    @SuppressWarnings("unused")
    public static boolean printStaticOne(boolean x, String expressionStr) {
        if (!Minecraft.getInstance().isPaused() && !EMFManager.getInstance().isAnimationValidationPhase) {
            EMFUtils.log("print: [" + expressionStr + "] = " + x);
        }
        return x;
    }

    @SuppressWarnings("unused")
    public static boolean printStatic(boolean x, String id, float n, float counter) {
        if (n <= 0 || !Minecraft.getInstance().isPaused() && counter % (int) n == 0 && !EMFManager.getInstance().isAnimationValidationPhase) {
            EMFUtils.log("print: [" + id + "] = " + x);
        }
        return x;
    }

    @Override
    protected boolean canOptimizeForConstantArgs() {
        return false;
    }

    @Override
    public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        if (rawArgs.size() == 1) {
            vars.scopeBool();
            parsedArgs.get(0).asmVisit(mv, vars);
            vars.scopePop();
            mv.visitLdcInsn(rawArgs.get(0));
            ASMHelper.visitStaticFunctionASM(mv, "printStaticOne", PrintBMethod.class);
        } else {
            vars.scopeBool();
            parsedArgs.get(2).asmVisit(mv, vars);
            vars.scopePop();
            mv.visitLdcInsn(rawArgs.get(0));
            vars.scopeFloat();
            parsedArgs.get(1).asmVisit(mv, vars);
            vars.scopePop();
            vars.asmVisitFrameCounter(mv);
            ASMHelper.visitStaticFunctionASM(mv, "printStatic", PrintBMethod.class);
        }
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == 3 || argCount == 1;
    }

    @Override
    protected boolean isRawStringArg(int index) {
        return rawArgs.size() == 3 && index == 0;
    }

}
