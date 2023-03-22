package traben.entity_model_features.models.jem_objects;

import java.util.Arrays;

public class EMFBoxData {

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


    public void prepare(boolean invertX,boolean invertY,boolean invertZ,
                        float modifyX,float modifyY,float modifyZ){
        checkAndFixUVLegacyDirections();

        //float[] coOrds = coordinates;

        //this should be added
        //if(parentZero) {
        coordinates[0] += modifyX;
        coordinates[1] += modifyY;
        coordinates[2] +=modifyZ;
        //}
        //then invert?
        if(invertX){
            coordinates[0] = -coordinates[0] - coordinates[3];
        }
        if(invertY){
            coordinates[1] = -coordinates[1]- coordinates[4];
        }
        if(invertZ){//todo check this as not used in fresh animations
            coordinates[2] = -coordinates[2]- coordinates[5];
        }

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
                ", uvSouth=" + Arrays.toString(uvSouth) +
                ", uvWest=" + Arrays.toString(uvWest) +
                ", uvEast=" + Arrays.toString(uvEast) +
                ", textureOffset=" + Arrays.toString(textureOffset) +
                ", sizeAdd=" + sizeAdd +
                '}';
    }
}
