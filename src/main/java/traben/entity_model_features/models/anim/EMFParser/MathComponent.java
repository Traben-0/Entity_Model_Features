package traben.entity_model_features.models.anim.EMFParser;

import java.util.IllegalFormatException;

public interface MathComponent{


     Float get() throws EMFMathException;


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
