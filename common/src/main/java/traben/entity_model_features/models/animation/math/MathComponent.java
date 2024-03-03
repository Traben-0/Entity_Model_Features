package traben.entity_model_features.models.animation.math;

public interface MathComponent {


    float getResult();


    default boolean isConstant() {
        return false;
    }


}
