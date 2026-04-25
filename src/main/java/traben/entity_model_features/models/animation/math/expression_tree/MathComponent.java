package traben.entity_model_features.models.animation.math.expression_tree;

import org.objectweb.asm.MethodVisitor;

import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.asm.ASMVisitable;


public interface MathComponent extends ASMVisitable {

    float getResult();

    default boolean isConstant() {
        return false;
    }

    @Override
    void asmVisit(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException;

    default MathConstant toConstant() {
        return new MathConstant(getResult());
    }

    static MathComponent negativeDelegate(MathComponent delegate) {
        return new MathComponent() {
            @Override
            public float getResult() {
                return -delegate.getResult();
            }

            @Override
            public void asmVisit(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
                delegate.asmVisit(mv, vars);
                vars.asmNegateFloat(mv);
            }

            @Override
            public boolean isConstant() {
                return delegate.isConstant();
            }
        };
    }

    static MathComponent invertedBooleanDelegate(MathComponent delegate) {
        return new MathComponent() {
            @Override
            public float getResult() {
                return MathValue.invertBoolean(delegate.getResult());
            }

            @Override
            public void asmVisit(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
                delegate.asmVisit(mv, vars);
                vars.asmInvertBoolean(mv);
            }

            @Override
            public boolean isConstant() {
                return delegate.isConstant();
            }
        };
    }

    static MathComponent validateBooleanDelegate(MathComponent delegate) {
        return new MathComponent() {
            @Override
            public float getResult() {
                return MathValue.validateBoolean(delegate.getResult());
            }

            @Override
            public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) throws EMFMathException {
                delegate.asmVisit(mv, varNames); // will be boolean anyway
            }

            @Override
            public boolean isConstant() {
                return delegate.isConstant();
            }
        };
    }
}
