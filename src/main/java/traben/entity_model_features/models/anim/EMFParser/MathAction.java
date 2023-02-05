package traben.entity_model_features.models.anim.EMFParser;

public enum MathAction  implements MathComponent{
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

    public static MathAction getAction(char ch){
        return switch (ch){
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

    @Override
    public float get(){
        System.out.println("ERROR: math action incorrectly called ["+this+"].");
        return Float.NaN;
    }
}
