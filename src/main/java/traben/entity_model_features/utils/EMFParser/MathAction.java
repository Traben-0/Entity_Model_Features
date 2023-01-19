package traben.entity_model_features.utils.EMFParser;

public enum MathAction  implements MathComponent{
    add,
    subtract,
    multiply,
    divide,
    divisionRemainder,
    power,
    comma,
    openBracket,
    closedBracket,
    none;

    public static MathAction getAction(char ch){
        return switch (ch){
            case '+' -> add;
            case '-' -> subtract;
            case '*' -> multiply;
            case '/' -> divide;
            case '^' -> power;
            case ',' -> comma;
            case '(' -> openBracket;
            case ')' -> closedBracket;
            case '%' -> divisionRemainder;
            default -> none;
        };
    }

    @Override
    public Double get() {
        return Double.NaN;
    }
}
