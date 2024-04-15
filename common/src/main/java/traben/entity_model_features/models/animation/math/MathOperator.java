package traben.entity_model_features.models.animation.math;

import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

public enum MathOperator implements MathComponent {
    ADD {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return first.getResult() + second.getResult();
        }
    },
    SUBTRACT {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return first.getResult() - second.getResult();
        }
    },
    MULTIPLY {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return first.getResult() * second.getResult();
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
    },
    OR {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(MathValue.toBoolean(first.getResult())
                    || MathValue.toBoolean(second.getResult()));
        }
    },
    LARGER_THAN {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(first.getResult() > second.getResult());
        }
    },
    SMALLER_THAN {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(first.getResult() < second.getResult());
        }
    },

    LARGER_THAN_OR_EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(first.getResult() >= second.getResult());
        }
    },
    SMALLER_THAN_OR_EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(first.getResult() <= second.getResult());
        }
    },
    EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(first.getResult() == second.getResult());
        }
    },
    NOT_EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return MathValue.fromBoolean(first.getResult() != second.getResult());
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
}
