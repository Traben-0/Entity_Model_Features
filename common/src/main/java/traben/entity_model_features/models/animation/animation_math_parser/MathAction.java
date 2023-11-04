package traben.entity_model_features.models.animation.animation_math_parser;

import traben.entity_model_features.utils.EMFManager;

public enum MathAction implements MathComponent {
    ADD {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return first.get() + second.get();
        }
    },
    SUBTRACT {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return first.get() - second.get();
        }
    },
    MULTIPLY {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return first.get() * second.get();
        }
    },
    DIVIDE {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            float sec = second.get();
            //if value is a variable it likely defaults to 0 during animation validation so here we intercept that and prevent invalidating the animation
            if (sec == 0 && !second.isConstant() && EMFManager.getInstance().isAnimationValidationPhase) {
                return first.get();
            }
            return first.get() / sec;
        }
    },
    DIVISION_REMAINDER {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            float sec = second.get();
            //if value is a variable it likely defaults to 0 during animation validation so here we intercept that and prevent invalidating the animation
            if (sec == 0 && !second.isConstant() && EMFManager.getInstance().isAnimationValidationPhase) {
                return first.get();
            }
            return first.get() % sec;
        }
    },
    // power,
    COMMA,
    OPEN_BRACKET,
    CLOSED_BRACKET,
    NONE,
    AND {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return ((first.get() == 1) && (second.get() == 1)) ? 1 : 0;
        }
    },
    OR {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return ((first.get() == 1) || (second.get() == 1)) ? 1 : 0;
        }
    },
    LARGER_THAN {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return (first.get() > second.get()) ? 1 : 0;
        }
    },
    SMALLER_THAN {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return (first.get() < second.get()) ? 1 : 0;
        }
    },

    LARGER_THAN_OR_EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return (first.get() >= second.get()) ? 1 : 0;
        }
    },
    SMALLER_THAN_OR_EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return (first.get() <= second.get()) ? 1 : 0;
        }
    },
    EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return (first.get() == second.get()) ? 1f : 0f;
        }
    },
    NOT_EQUALS {
        @Override
        public float execute(MathComponent first, MathComponent second) {
            return (first.get() != second.get()) ? 1 : 0;
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
    public float get() {
        System.out.println("ERROR: math action incorrectly called [" + this + "].");
        return Float.NaN;
    }
}
