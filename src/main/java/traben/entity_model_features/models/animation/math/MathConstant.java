package traben.entity_model_features.models.animation.math;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.utils.EMFUtils;

import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;

public class MathConstant extends MathValue implements MathComponent {

    public static final MathConstant ZERO_CONST = new MathConstant(0);
    public static final MathConstant FALSE_CONST = new MathConstant(FALSE);
    private final float hardCodedValue;



    public MathConstant(float number, boolean isNegative) {
        hardCodedValue = isNegative ? -number : number;
    }

    public MathConstant(float number) {
        hardCodedValue = number;
    }

    @Override
    public ResultSupplier getResultSupplier() {
        EMFUtils.logError("EMF math constant called supplier: this shouldn't happen!");
        return this::getResult;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public String toString() {
        return String.valueOf(getResult());
    }

    @Override // make faster return
    public float getResult() {
        return hardCodedValue;
    }

    @Override
    public void asmVisit(MethodVisitor mv, ASMVariableHandler vars) {

        if (vars.isScopeBool()) {
            mv.visitInsn(MathValue.toBoolean(hardCodedValue) ? ICONST_1 : ICONST_0);
        } else {
            mv.visitLdcInsn(hardCodedValue);
        }
    }
}
