package traben.entity_model_features.models.animation.math.asm;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.math.EMFMathException;

public interface ASMVisitable {
    void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) throws EMFMathException;
}
