package traben.entity_model_features.models.animation.math.methods.optifine;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class InMethod extends MathMethod {


    public InMethod(final List<String> args, final boolean isNegative, AnimSetupContext context) throws EMFMathException {
        super(isNegative, context, args);


        MathComponent x = parsedArgs.get(0);
        List<MathComponent> vals = new ArrayList<>(parsedArgs);
        vals.remove(0);

        setSupplierAndOptimize(() -> {
            float X = x.getResult();
            for (MathComponent expression :
                    vals) {
                if (expression.getResult() == X) {
                    return TRUE;
                }
            }
            return FALSE;
        }, parsedArgs);
    }


    @Override
    public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        vars.scopeFloat();

        // compute X once
        parsedArgs.get(0).asmVisit(mv, vars);
        int xSlot = vars.getLocalVarIndex();
        mv.visitVarInsn(FSTORE, xSlot);

        Label endTrue = new Label();
        Label end = new Label();

        for (int i = 1; i < parsedArgs.size(); i++) {

            // load X
            mv.visitVarInsn(FLOAD, xSlot);

            // load args[i]
            parsedArgs.get(i).asmVisit(mv, vars);

            // if equal -> true
            mv.visitInsn(FCMPL);

            // if equal → 0 → jump
            mv.visitJumpInsn(IFEQ, endTrue);

            // else continue loop
        }

        // false
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, end);

        // true
        mv.visitLabel(endTrue);
        mv.visitInsn(ICONST_1);

        mv.visitLabel(end);
        vars.popLocalVarIndex(xSlot);
        vars.scopePop();
    }


    @Override
    protected boolean hasCorrectArgCount(final int argCount) {
        return argCount >= 2;
    }

}
