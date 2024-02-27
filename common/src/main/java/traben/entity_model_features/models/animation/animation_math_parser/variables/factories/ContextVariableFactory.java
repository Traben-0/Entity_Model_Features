package traben.entity_model_features.models.animation.animation_math_parser.variables.factories;

import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.animation_math_parser.MathValue;

public abstract class ContextVariableFactory {

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj.getClass().equals(this.getClass());
    }

    @Nullable
    abstract public MathValue.ResultSupplier getSupplierOrNull(String variableKey, EMFAnimation calculationInstance);

    abstract public boolean createsThisVariable(String variableKey);

}
