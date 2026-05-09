package traben.entity_model_features.models.animation.math.expression_tree;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.utils.EMFUtils;

import static org.objectweb.asm.Opcodes.*;

public enum MathOperator implements MathComponent {
    ADD {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return first.getResult() + second.getResult();
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            mv.visitInsn(FADD);
        }
    },
    SUBTRACT {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return first.getResult() - second.getResult();
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            mv.visitInsn(FSUB);
        }
    },
    MULTIPLY {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return first.getResult() * second.getResult();
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            mv.visitInsn(FMUL);
        }
    },
    DIVIDE {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            float sec = second.getResult();
            //if value is a variable it likely defaults to 0 during animation validation so here we intercept that and prevent invalidating the animation
            if (sec == 0 && !second.isConstant() && EMFManager.getInstance().isAnimationValidationPhase) {
                return first.getResult();
            }
            return first.getResult() / sec;
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            mv.visitInsn(FDIV);
        }
    },
    DIVISION_REMAINDER {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            float sec = second.getResult();
            //if value is a variable it likely defaults to 0 during animation validation so here we intercept that and prevent invalidating the animation
            if (sec == 0 && !second.isConstant() && EMFManager.getInstance().isAnimationValidationPhase) {
                return first.getResult();
            }
            return first.getResult() % sec;
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            mv.visitInsn(FREM);
        }
    },
    COMMA,
    OPEN_BRACKET,
    CLOSED_BRACKET,
    NONE,
    AND {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(MathValue.toBoolean(first.getResult())
                    && (MathValue.toBoolean(second.getResult())));
        }

        @Override
        public boolean isScopeBool() {
            return true;
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            mv.visitInsn(IAND);
        }
    },
    OR {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(MathValue.toBoolean(first.getResult())
                    || MathValue.toBoolean(second.getResult()));
        }

        @Override
        public boolean isScopeBool() {
            return true;
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            mv.visitInsn(IOR);
        }
    },
    LARGER_THAN {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(first.getResult() > second.getResult());
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            mv.visitInsn(FCMPG);
            asmCompare(mv, IFGT);
        }
    },
    SMALLER_THAN {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(first.getResult() < second.getResult());
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            mv.visitInsn(FCMPG);
            asmCompare(mv, IFLT);
        }
    },

    LARGER_THAN_OR_EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(first.getResult() >= second.getResult());
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            mv.visitInsn(FCMPG);
            asmCompare(mv, IFGE);
        }
    },
    SMALLER_THAN_OR_EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(first.getResult() <= second.getResult());
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            mv.visitInsn(FCMPG);
            asmCompare(mv, IFLE);
        }
    },
    EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(first.getResult() == second.getResult());
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            if (varNames.isScopeFloat()) {
                mv.visitInsn(FCMPL);
                asmCompare(mv, IFEQ);
            } else {
                asmCompare(mv, IF_ICMPEQ);
            }
        }
    },
    NOT_EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(first.getResult() != second.getResult());
        }

        @Override
        public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
            if (varNames.isScopeFloat()) {
                mv.visitInsn(FCMPL);
                asmCompare(mv, IFNE);
            } else {
                asmCompare(mv, IF_ICMPNE);
            }
        }
    },
    BOOLEAN_CHAR;

    public static MathOperator getAction(char ch) {
        return switch (ch) {
            case '+' -> ADD;
            case '-' -> SUBTRACT;
            case '*' -> MULTIPLY;
            case '/' -> DIVIDE;
            // case '^' -> power;
            case ',' -> COMMA;
            case '(' -> OPEN_BRACKET;
            case ')' -> CLOSED_BRACKET;
            case '%' -> DIVISION_REMAINDER;
            case '&', '|', '>', '<', '=', '!' -> BOOLEAN_CHAR;
            default -> NONE;
        };
    }

    public float execute(MathComponent first, MathComponent second) {
        EMFUtils.logError("math action execute() incorrectly called [" + this + "].");
        return Float.NaN;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public float getResult() {
        EMFUtils.logError("math action incorrectly called [" + this + "].");
        return Float.NaN;
    }

    public boolean isScopeBool() { return false; }

    public boolean isEqualsType() {
        return this == EQUALS || this == NOT_EQUALS;
    }

    @Override
    public void asmVisit(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
        throw new UnsupportedOperationException(this + " operator shouldn't have called this.");
    }

    public static void asmCompare(MethodVisitor mv, int opCode) {
        var t = new Label();
        var end = new Label();

        mv.visitJumpInsn(opCode, t);
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, end);

        mv.visitLabel(t);
        mv.visitInsn(ICONST_1);

        mv.visitLabel(end);
    }
}
