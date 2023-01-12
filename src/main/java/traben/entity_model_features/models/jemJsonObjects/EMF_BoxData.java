package traben.entity_model_features.models.jemJsonObjects;

import java.util.Arrays;

public class EMF_BoxData {

   // https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/cem_part.txt#L15

    public float[] textureOffset = {};
    public float[] uvDown = {};
    public float[] uvUp = {};

    public float[] uvNorth, uvFront = {};
    public float[] uvSouth, uvBack = {};
    public float[] uvWest, uvLeft = {};
    public float[] uvEast, uvRight = {};

    public float[] coordinates = {};
    public float sizeAdd = 0.0f; // just part dilation lol


    public void prepare(){
        checkAndFixUVLegacyDirections();
        //TODO maybe build modelPart box here
    }

    public void checkAndFixUVLegacyDirections(){
            if (uvFront.length == 4)
                uvNorth = uvFront;
            if (uvBack.length == 4)
                uvWest = uvBack;
            if (uvLeft.length == 4)
                uvNorth = uvLeft;
            if (uvRight.length == 4)
                uvEast = uvRight;
    }


    @Override
    public String toString() {
        return "EMF_BoxData{" +
                "coordinates=" + Arrays.toString(coordinates) +
                ", uvDown=" + Arrays.toString(uvDown) +
                ", uvUp=" + Arrays.toString(uvUp) +
                ", uvNorth=" + Arrays.toString(uvNorth) +
                ", uvFront=" + Arrays.toString(uvFront) +
                ", uvSouth=" + Arrays.toString(uvSouth) +
                ", uvFBack=" + Arrays.toString(uvBack) +
                ", uvWest=" + Arrays.toString(uvWest) +
                ", uvLeft=" + Arrays.toString(uvLeft) +
                ", uvEast=" + Arrays.toString(uvEast) +
                ", uvRight=" + Arrays.toString(uvRight) +
                ", textureOffset=" + Arrays.toString(textureOffset) +
                ", sizeAdd=" + sizeAdd +
                '}';
    }
}
