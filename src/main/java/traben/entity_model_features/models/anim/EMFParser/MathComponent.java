package traben.entity_model_features.models.anim.EMFParser;

public interface MathComponent{


     double get();// throws EMFMathException;


     default boolean isConstant(){return false;}
     class EMFMathException extends Exception {

          public EMFMathException(String s) {
                errorMsg=s;
          }

          String errorMsg;

          @Override
          public String toString() {
               return errorMsg;
          }
     }


}
