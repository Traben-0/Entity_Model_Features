package traben.entity_model_features.models.animation.math.methods.emf;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.utils.EMFUtils;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class CatchMethod extends MathMethod {

    public CatchMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
        super(isNegative, context, args);

        MathComponent x = parsedArgs.get(0);
        MathComponent c = parsedArgs.get(1);

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
    public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        Label tryStart = new Label();
        Label tryEnd = new Label();
        Label catchBlock = new Label();
        Label end = new Label();

        vars.scopeFloat();

        mv.visitTryCatchBlock(
                tryStart,
                tryEnd,
                catchBlock,
                null
        );

        // try
        mv.visitLabel(tryStart);
        parsedArgs.get(0).asmVisit(mv, vars); // x
        mv.visitJumpInsn(GOTO, end);

        // catch
        mv.visitLabel(tryEnd);

        mv.visitLabel(catchBlock);
        parsedArgs.get(1).asmVisit(mv, vars); // c
        // end
        mv.visitLabel(end);

        vars.scopePop();
    }

    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount == 2 || argCount == 3;
    }

}
