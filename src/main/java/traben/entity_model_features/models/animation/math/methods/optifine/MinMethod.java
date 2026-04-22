package traben.entity_model_features.models.animation.math.methods.optifine;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;

import java.util.ArrayList;
import java.util.List;

public class MinMethod extends MathMethod {


    public MinMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
        super(isNegative, context, args);

        var initial = parsedArgs.get(0);
        var theRest = new ArrayList<>(parsedArgs);
        theRest.remove(0);

        setSupplierAndOptimize(() -> {
            float min = initial.getResult();
            for (MathComponent parsedArg : theRest) {
                float val = parsedArg.getResult();
                if (val < min) {
                    min = val;
                }
            }
            return min;
        }, parsedArgs);
    }

    @Override
    public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        vars.scopeFloat();
        var initial = parsedArgs.get(0);
        var theRest = new ArrayList<>(parsedArgs);
        theRest.remove(0);

        initial.asmVisit(mv, vars);

        for (var arg : theRest) {
            arg.asmVisit(mv, vars);

            mv.visitMethodInsn(
                    org.objectweb.asm.Opcodes.INVOKESTATIC,
                    "java/lang/Math",
                    "min",
                    "(FF)F",
                    false
            );
        }
        vars.scopePop();
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount >= 2;
    }

}
