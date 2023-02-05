package traben.entity_model_features.models.anim.EMFParser;

public interface MathComponent{


     float get();// throws EMFMathException;


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
