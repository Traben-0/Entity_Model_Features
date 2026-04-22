package traben.entity_model_features.models.animation.math.methods.optifine;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;

import java.util.ArrayList;
import java.util.List;

public class MaxMethod extends MathMethod {



    public MaxMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
        super(isNegative, context, args);

        var initial = parsedArgs.get(0);
        var theRest = new ArrayList<>(parsedArgs);
        theRest.remove(0);

        setSupplierAndOptimize(() -> {
            float max = initial.getResult();
            for (MathComponent parsedArg : theRest) {
                float val = parsedArg.getResult();
                if (val > max) {
                    max = val;
                }
            }
            return max;
        }, parsedArgs);
    }


    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount >= 2;
    }

    @Override
    public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        vars.scopeFloat();
        parsedArgs.get(0).asmVisit(mv, vars);

        var theRest = new ArrayList<>(parsedArgs);
        theRest.remove(0);

        for (var arg : theRest) {
            arg.asmVisit(mv, vars);

            mv.visitMethodInsn(
                    org.objectweb.asm.Opcodes.INVOKESTATIC,
                    "java/lang/Math",
                    "max",
                    "(FF)F",
                    false
            );
        }
        vars.scopePop();
    }
}
