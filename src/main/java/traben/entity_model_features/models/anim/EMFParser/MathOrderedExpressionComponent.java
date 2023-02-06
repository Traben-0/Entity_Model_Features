package traben.entity_model_features.models.anim.EMFParser;

public class MathOrderedExpressionComponent extends MathValue implements MathComponent{


    private final MathComponent first ;
    private final MathAction action ;
    private final MathComponent second;


    private MathOrderedExpressionComponent(MathComponent first, MathAction action, MathComponent second, boolean isNegative){
        super(isNegative);
        this.first = first;
        this.action = action;
        this.second = second;
        supplier = ()-> action.run(first,second);
    }

    public static MathComponent getOptimizedExpression(MathComponent first, MathAction action, MathComponent second){
        return getOptimizedExpression(first, action, second,false);
    }
    public static MathComponent getOptimizedExpression(MathComponent first, MathAction action, MathComponent second, boolean isnegative){
        MathOrderedExpressionComponent component = new MathOrderedExpressionComponent(first,action,second,isnegative);
        if (component.first.isConstant() && component.second.isConstant()) {
            //result is always constant so return the constant result instead
            return new MathVariableConstant(component.get(),isnegative);
        }
        return component;
    }

    private final ValueSupplier supplier;
    @Override
    public ValueSupplier getSupplier() {
        return supplier;
    }

    @Override
    public String toString() {
        return String.valueOf(get());
    }


}
