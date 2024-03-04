package traben.entity_model_features.models.animation.math;

import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

public enum MathAction implements MathComponent {
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
            return ((first.getResult() == 1) && (second.getResult() == 1)) ? 1 : 0;
        }
    },
    OR {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return ((first.getResult() == 1) || (second.getResult() == 1)) ? 1 : 0;
        }
    },
    LARGER_THAN {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return (first.getResult() > second.getResult()) ? 1 : 0;
        }
    },
    SMALLER_THAN {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return (first.getResult() < second.getResult()) ? 1 : 0;
        }
    },

    LARGER_THAN_OR_EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return (first.getResult() >= second.getResult()) ? 1 : 0;
        }
    },
    SMALLER_THAN_OR_EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return (first.getResult() <= second.getResult()) ? 1 : 0;
        }
    },
    EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return (first.getResult() == second.getResult()) ? 1f : 0f;
        }
    },
    NOT_EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return (first.getResult() != second.getResult()) ? 1 : 0;
        }
    },
    BOOLEAN_CHAR;

    public static MathAction getAction(char ch) {
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
