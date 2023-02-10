package traben.entity_model_features.models.jem_objects;

import java.util.Arrays;

public class EMFJemData {
    public String texture = "";
    public int[] textureSize = null;
    public double shadow_size = 1.0;
    public EMFPartData[] models = {};


    public void prepare(){
        if(!texture.isBlank()) {
            if (!this.texture.contains(".png")) this.texture = this.texture + ".png";
            //if no folder parenting assume it is relative to model
            if (!this.texture.contains("/")) this.texture = "optifine/cem/" + this.texture;
        }

        for (EMFPartData model:
             models) {
            model.prepare( 0,textureSize,texture);
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
