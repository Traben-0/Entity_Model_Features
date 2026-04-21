package traben.entity_model_features.models.animation.math.methods.optifine;

import net.minecraft.client.Minecraft;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathMethod;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.utils.EMFUtils;

import java.util.List;

public class PrintMethod extends MathMethod {

    private int printCount = 0;

    public PrintMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
        super(isNegative, context, args);

        if (args.size() == 1) {
            var expressionStr = args.get(0);
            MathComponent x = parsedArgs.get(0);
            setSupplierAndOptimize(() -> {
                float xVal = x.getResult();
                if (!Minecraft.getInstance().isPaused() && !EMFManager.getInstance().isAnimationValidationPhase) {
                    EMFUtils.log("print: [" + expressionStr + "] = " + xVal);
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
                EMFUtils.log("print: [" + id + "] = " + xVal);
            }
            return xVal;
        });

    }

    private int getPrintCount() {
        printCount++;
        return printCount;
    }

    @SuppressWarnings("unused")
    public static float printStaticOne(float x, String expressionStr) {
        if (!Minecraft.getInstance().isPaused() && !EMFManager.getInstance().isAnimationValidationPhase) {
            EMFUtils.log("print: [" + expressionStr + "] = " + x);
        }
        return x;
    }

    @SuppressWarnings("unused")
    public static float printStatic(float x, String id, float n, float counter) {
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
        vars.scopeFloat();
        if (rawArgs.size() == 1) {
            parsedArgs.get(0).asmVisit(mv, vars);
            mv.visitLdcInsn(rawArgs.get(0));
            ASMHelper.visitStaticFunctionASM(mv, "printStaticOne", PrintMethod.class);
        } else {
            parsedArgs.get(2).asmVisit(mv, vars);
            mv.visitLdcInsn(rawArgs.get(0));
            parsedArgs.get(1).asmVisit(mv, vars);
            vars.asmVisitFrameCounter(mv);
            ASMHelper.visitStaticFunctionASM(mv, "printStatic", PrintMethod.class);
        }
        vars.scopePop();
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
