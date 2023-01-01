package traben.entity_model_features.models.jemJsonObjects;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EMF_JemData {
    public String texture = "";
    public int[] textureSize = null;
    public double shadow_size = 1.0;
    public EMF_ModelData[] models = {};
    private final Map<String, String> animationInstructionList = new HashMap<>();


    public void prepare(){
        for (EMF_ModelData model:
             models) {
            model.prepare(animationInstructionList,textureSize);
        }
    }

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
