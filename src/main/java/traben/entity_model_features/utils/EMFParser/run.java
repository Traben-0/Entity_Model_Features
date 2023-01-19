package traben.entity_model_features.utils.EMFParser;

public class run {
    public static void main(String[] args) {
        String ex = "(0.8 + 0.6 / (18+4)-8 * - 4.9) % 3";
        System.out.println("parse expression: "+ex);
        MathExpression exp = new MathExpression(ex,false);
        System.out.println(exp.toString());
        System.out.println("result = "+exp.get());
    }
}
