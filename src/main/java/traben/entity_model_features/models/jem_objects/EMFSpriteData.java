package traben.entity_model_features.models.jem_objects;

import java.util.Arrays;

public class EMFSpriteData {

   // https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/cem_part.txt#L15

    public int[] textureOffset = {};
    public double[] coordinates = {};
    public double[] sizeAdd = {}; // just part dilation lol

    public void prepare(){
        //nothing to do yet
    }

    @Override
    public String toString() {
        return "EMF_SpriteData{" +
                "textureOffset=" + Arrays.toString(textureOffset) +
                ", coordinates=" + Arrays.toString(coordinates) +
                ", sizeAdd=" + Arrays.toString(sizeAdd) +
                '}';
    }
}
