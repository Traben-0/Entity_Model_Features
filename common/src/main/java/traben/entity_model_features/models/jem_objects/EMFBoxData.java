package traben.entity_model_features.models.jem_objects;

import traben.entity_model_features.EMF;
import traben.entity_model_features.utils.EMFUtils;

import java.util.Arrays;

@SuppressWarnings("CanBeFinal")
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

    //emf only
    public float sizeAddX = 0.0f;
    //emf only
    public float sizeAddY = 0.0f;
    //emf only
    public float sizeAddZ = 0.0f;

    public void prepare(boolean invertX, boolean invertY, boolean invertZ) {
        try {

            if (sizeAdd != 0.0f && sizeAddX == 0.0f && sizeAddY == 0.0f && sizeAddZ == 0.0f) {
                sizeAddX = sizeAdd;
                sizeAddY = sizeAdd;
                sizeAddZ = sizeAdd;
            }

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

            boolean offsetValid = textureOffset.length == 2;
            if (!offsetValid && textureOffset.length != 0) {
                throw new IllegalArgumentException("Invalid textureOffset data: " + Arrays.toString(textureOffset));
            }

            if (!offsetValid) {
                checkAndFixUVLegacyDirections();

                validateUV(uvDown, "uvDown");
                validateUV(uvUp, "uvUp");
                validateUV(uvNorth, "uvNorth");
                validateUV(uvSouth, "uvSouth");
                validateUV(uvWest, "uvWest");
                validateUV(uvEast, "uvEast");
            }
        }catch (Exception e){
            throw new IllegalArgumentException("Error preparing box data: " + e.getMessage(), e);
        }
    }

    private void validateUV(float[] uv, String name) {
        if (uv.length == 0) {
            return;//empty is fine
        }
        if (uv.length != 4) {
            throw new IllegalArgumentException("Invalid UV data for ["+name+"], must have 4 or 0 values: " + Arrays.toString(uv));
        }

        //first two should be integers
        if(EMF.config().getConfig().enforceOptiFineFloorUVs ) {
            uv[0] = (float) Math.floor(uv[0]);
            uv[1] = (float) Math.floor(uv[1]);
            uv[2] = (float) Math.floor(uv[2]);
            uv[3] = (float) Math.floor(uv[3]);
        } else if (uv[0] != Math.floor(uv[0]) || uv[1] != Math.floor(uv[1]) || uv[2] != Math.floor(uv[2]) || uv[3] != Math.floor(uv[3])) {
            EMFUtils.logWarn("Possibly invalid UV data for [" + name + "], all values should be integers (whole numbers), because OptiFine floors them, EMF can be set to floor these values in the OptiFine parity settings: " + Arrays.toString(uv));
        }

        if (Math.floor(uv[2]) == 0) {
            EMFUtils.logWarn("Possibly invalid UV data for ["+name+"], the third value should not floor to 0: " + Arrays.toString(uv));
        }
        if (Math.floor(uv[3]) == 0) {
            EMFUtils.logWarn("Possibly invalid UV data for ["+name+"], the fourth value should not floor to 0: " + Arrays.toString(uv));
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

}
