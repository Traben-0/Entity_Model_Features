package traben.entity_model_features.models.jem_objects;

import java.util.Arrays;

public class EMFBoxData {

    // https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/cem_part.txt#L15

    public float[] textureOffset = {};
    public float[] uvDown = {};
    public float[] uvUp = {};

    public float[] uvFront = {};
    public float[] uvBack = {};
    public float[] uvLeft = {};
    public float[] uvRight = {};
    public float[] uvNorth = {};
    public float[] uvSouth = {};
    public float[] uvWest = {};
    public float[] uvEast = {};

    public float[] coordinates = {};
    public float sizeAdd = 0.0f; // just part dilation lol


    public void prepare(boolean invertX, boolean invertY, boolean invertZ) {
        checkAndFixUVLegacyDirections();


        //then invert?
        if (invertX) {
            coordinates[0] = -coordinates[0] - coordinates[3];
        }
        if (invertY) {
            coordinates[1] = -coordinates[1] - coordinates[4];
        }
        if (invertZ) {
            coordinates[2] = -coordinates[2] - coordinates[5];
        }

    }

    public void checkAndFixUVLegacyDirections() {
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
                ", uvSouth=" + Arrays.toString(uvSouth) +
                ", uvWest=" + Arrays.toString(uvWest) +
                ", uvEast=" + Arrays.toString(uvEast) +
                ", textureOffset=" + Arrays.toString(textureOffset) +
                ", sizeAdd=" + sizeAdd +
                '}';
    }

    @SuppressWarnings("unused")
    public static class EMFBoxPrinter {
        public int[] textureOffset = {};
        public float[] uvDown = {0, 0, 0, 0};
        public float[] uvUp = {0, 0, 0, 0};

        public float[] uvNorth, uvFront = {0, 0, 0, 0};
        public float[] uvSouth, uvBack = {0, 0, 0, 0};
        public float[] uvWest, uvLeft = {0, 0, 0, 0};
        public float[] uvEast, uvRight = {0, 0, 0, 0};

        public float[] coordinates = {0, 0, 0, 0, 0, 0};
        public float sizeAdd = 0.0f;
    }
}
