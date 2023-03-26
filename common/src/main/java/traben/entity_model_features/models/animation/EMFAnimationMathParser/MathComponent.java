package traben.entity_model_features.models.animation.EMFAnimationMathParser;

public interface MathComponent {


    double get();// throws EMFMathException;


    default boolean isConstant() {
        return false;
    }

    class EMFMathException extends Exception {

        String errorMsg;

        public EMFMathException(String s) {
            errorMsg = s;
        }

        @Override
        public String toString() {
            return errorMsg;
        }
    }


}
