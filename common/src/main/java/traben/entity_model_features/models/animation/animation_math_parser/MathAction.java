package traben.entity_model_features.models.animation.animation_math_parser;

import traben.entity_model_features.utils.EMFManager;

public enum MathAction implements MathComponent {
    add,
    subtract,
    multiply,
    divide,
    divisionRemainder,
    // power,
    comma,
    openBracket,
    closedBracket,
    none,
    and,
    or,
    largerThan,
    smallerThan,

    largerThanOrEquals,
    smallerThanOrEquals,
    equals,
    notEquals,
    BOOLEAN_CHAR;

    public static MathAction getAction(char ch) {
        return switch (ch) {
            case '+' -> add;
            case '-' -> subtract;
            case '*' -> multiply;
            case '/' -> divide;
            // case '^' -> power;
            case ',' -> comma;
            case '(' -> openBracket;
            case ')' -> closedBracket;
            case '%' -> divisionRemainder;
            case '&', '|', '>', '<', '=', '!' -> BOOLEAN_CHAR;
            default -> none;
        };
    }

    public float run(MathComponent first, MathComponent second) {

        //if(EMFData.getInstance().getConfig().printAllMaths) System.out.println("run: "+first+this+second+"="+result);
        return switch (this) {
            //doubles
            case add -> first.get() + second.get();
            case subtract -> first.get() - second.get();
            case multiply -> first.get() * second.get();
            case divide ->// first.get() / second.get();
            {
                float sec = second.get();
                //if value is a variable it likely defaults to 0 during animation validation so here we intercept that and prevent invalidating the animation
                if (sec == 0 && !second.isConstant() && EMFManager.getInstance().isAnimationValidationPhase) {
                    yield first.get();
                }
                yield first.get() / sec;
            }
            case divisionRemainder -> {
                float sec = second.get();
                //if value is a variable it likely defaults to 0 during animation validation so here we intercept that and prevent invalidating the animation
                if (sec == 0 && !second.isConstant() && EMFManager.getInstance().isAnimationValidationPhase) {
                    yield first.get();
                }
                yield first.get() % sec;
            }
            //boolean results
            case largerThan -> (first.get() > second.get()) ? 1 : 0;
            case largerThanOrEquals -> (first.get() >= second.get()) ? 1 : 0;
            case smallerThan -> (first.get() < second.get()) ? 1 : 0;
            case smallerThanOrEquals -> (first.get() <= second.get()) ? 1 : 0;
            case equals -> (first.get() == second.get()) ? 1 : 0;
            case notEquals -> (first.get() != second.get()) ? 1 : 0;
            //boolean result and inputs
            case and -> ((first.get() == 1) && (second.get() == 1)) ? 1 : 0;
            case or -> ((first.get() == 1) || (second.get() == 1)) ? 1 : 0;
            //NaN
            default -> Float.NaN;
        };
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
