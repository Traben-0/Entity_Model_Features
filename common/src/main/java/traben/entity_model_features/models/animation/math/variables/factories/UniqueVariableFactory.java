package traben.entity_model_features.models.animation.math.variables.factories;

import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.MathValue;

/**
 * A factory for creating unique math animation variables.
 */
public abstract class UniqueVariableFactory {

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj.getClass().equals(this.getClass());
    }

    /**
     * Gets the supplier for the variable with the given key.
     *
     * @param variableKey         The key of the variable.
     * @param calculationInstance the object handling this animation calculation, there is useful context available
     *                            within it such as model name and model parts.
     * @return The supplier for the variable with the given key.
     */
    @Nullable
    abstract public MathValue.ResultSupplier getSupplierOrNull(String variableKey, EMFAnimation calculationInstance);

    /**
     * Checks if this factory creates the variable with the given key.
     * <p>
     * e.g. if the key is "time" and this factory creates the time variable, then this method should return true.
     * <p>
     * e.g. if this factory creates any variable with "timmy" at the start of the key, then this method should return
     * true for "timmy" and "timmy2" etc.
     *
     * @param variableKey The key of the variable.
     * @return True if this factory creates the variable with the given key, otherwise false.
     */
    abstract public boolean createsThisVariable(String variableKey);

}
