package traben.entity_model_features.models.animation.math;

public class EMFMathException extends Exception {

    final String errorMsg;

    public EMFMathException(String s) {
        errorMsg = s;
    }

    @Override
    public String toString() {
        return errorMsg;
    }
}
