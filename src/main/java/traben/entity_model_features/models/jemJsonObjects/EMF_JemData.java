package traben.entity_model_features.models.jemJsonObjects;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EMF_JemData {
    public String texture = "";
    public int[] textureSize = null;
    public double shadow_size = 1.0;
    public EMF_ModelData[] models = {};
   // private final Map<String, String> animationInstructionList = new HashMap<>();


    public void prepare(){
        for (EMF_ModelData model:
             models) {
            model.prepare( 0,textureSize,"");
        }
    }

//    public void setInflateAll(float amount){
//        iterateInflate(models, amount);
//    }
//    private void iterateInflate(EMF_ModelData[] modelGroup,float amount){
//        for (EMF_ModelData model:
//                modelGroup) {
//            model.textureSize = textureSize;
//            for (EMF_BoxData box : model.boxes) {
//                box.sizeAdd = amount;
//            }
//            if (model.submodel != null) {
//                for (EMF_BoxData box : model.submodel.boxes) {
//                    box.sizeAdd = amount;
//                }
//            }
//            if (model.submodels.length != 0) {
//                iterateInflate(model.submodels,amount);
//            }
//        }
//    }

    @Override
    public String toString() {
        return "EMF_JemData{" +
                "texture='" + texture + '\'' +
                ", textureSize=" + Arrays.toString(textureSize) +
                ", shadow_size=" + shadow_size +
                ", models=" + Arrays.toString(models) +
                '}';
    }
}
