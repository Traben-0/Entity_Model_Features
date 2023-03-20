package traben.entity_model_features.models.animation.EMFAnimationMathParser;

public class MathBinaryExpressionComponent extends MathValue implements MathComponent{


    private final MathComponent first ;
    private final MathAction action ;
    private final MathComponent second;


    private MathBinaryExpressionComponent(MathComponent first, MathAction action, MathComponent second, boolean isNegative){
        super(isNegative);
        this.first = first;
        this.action = action;
        this.second = second;
       // supplier = action.getBinaryRunnable(first,second);
    }

    public static MathComponent getOptimizedExpression(MathComponent first, MathAction action, MathComponent second){
        return getOptimizedExpression(first, action, second,false);
    }
    public static MathComponent getOptimizedExpression(MathComponent first, MathAction action, MathComponent second, boolean isnegative){
        MathBinaryExpressionComponent component = new MathBinaryExpressionComponent(first,action,second,isnegative);
        if (component.first.isConstant() && component.second.isConstant()) {
            //result is always constant so return the constant result instead
            return new MathConstant(component.get(),isnegative);
        }
        return component;
    }

    //private final ValueSupplier supplier;
    @Override
    public ValueSupplier getSupplier() {
        return null;
    }

    @Override
    public double get() {
        return isNegative ? -action.run(first,second) : action.run(first,second);

        //return isNegative ? -supplier.get() : supplier.get();

    }

    @Override
    public String toString() {
        return "[oExp:{"+first+", "+action+", "+second+"}="+ get()+"]";
    }


}
