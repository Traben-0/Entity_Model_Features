package traben.entity_model_features.models.animation.math;

import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.variables.VariableRegistry;


public class MathVariable extends MathValue implements MathComponent {


    private final ResultSupplier resultSupplier;
    private final String name;

    public MathVariable(String variableName, boolean isNegative, ResultSupplier supplier) {
        super(isNegative);
        resultSupplier = supplier;
        name = variableName;
    }

    public MathVariable(String variableName, ResultSupplier supplier) {
        resultSupplier = supplier;
        name = variableName;
    }

    static MathComponent getOptimizedVariable(String variableName, boolean isNegative, EMFAnimation calculationInstance) {
        if (variableName.startsWith("-")) {//catch mistake of double negative
            return VariableRegistry.getInstance().getVariable(variableName.substring(1), true, calculationInstance);
        }
        return VariableRegistry.getInstance().getVariable(variableName, isNegative, calculationInstance);
    }

    @Override
    ResultSupplier getResultSupplier() {
        return resultSupplier;
    }

    @Override
    public String toString() {
        return "variable[" + name + "]=" + getResult();
    }

}
