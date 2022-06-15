package traben.entity_model_features.models.jemJsonObjects;

import net.minecraft.client.model.*;
import traben.entity_model_features.models.EMF_CustomModelPart;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EMF_ModelData {

    public String texture = "";
    public int[] textureSize = {64,32};
    public String invertAxis = "";
    public float[] translate = {0,0,0};
    public float[] rotate = {0,0,0};
    public String mirrorTexture = "";
    public EMF_BoxData[] boxes = {};
    public EMF_SpriteData[] sprites = {};
    public EMF_ModelData submodel = null;
    public EMF_ModelData[] submodels = {};

    public String baseId = "";  //- Model parent ID, all parent properties are inherited
    public String model = "";  //- Part model jemJsonObjects, from which to load the part model definition
    public String id = "";            //- Model ID, can be used to reference the model as parent
    public String part = "!!!!!";     //- Entity part to which the part model is atached
    public boolean attach = false; //- True: attach to the entity part, False: replace it
    public float scale = 1.0f;


    public Properties[] animations = {};

    public void prepare(Map<String, String> animationInstructionList){

        //rotate[0] = rotate[0] /1.57079632679f;
        //rotate[1] = rotate[1] /1.57079632679f;
        //rotate[2] = rotate[2] /1.57079632679f;


        for (Properties p:
             animations) {
            for (String s:
                 p.stringPropertyNames()) {
                animationInstructionList.put(s,p.getProperty(s));
            }
        }

        if (!model.equals("")){
            //TODO load json model part files to this object
        }
        for (EMF_BoxData box:
             boxes) {
            box.prepare();
        }
        for (EMF_SpriteData sprite:
                sprites) {
            sprite.prepare();
        }
        if (submodel !=null){
            submodel.prepare(animationInstructionList);
        }
        for (EMF_ModelData model:
                submodels) {
            model.prepare(animationInstructionList);
        }
    }

    @Override
    public String toString() {
        return toString(false);
    }


    public String toString(boolean printFullChild) {
        return "EMF_ModelData{" +
                "baseId='" + baseId + '\'' +
                ", model='" + model + '\'' +
                ", id='" + id + '\'' +
                ", part='" + part + '\'' +
                ", attach=" + attach +
                ", scale=" + scale +
                ", animations=" + Arrays.toString(animations) +
                ", texture='" + texture + '\'' +
                ", textureSize=" + Arrays.toString(textureSize) +
                ", invertAxis='" + invertAxis + '\'' +
                ", translate=" + Arrays.toString(translate) +
                ", rotate=" + Arrays.toString(rotate) +
                ", mirrorTexture='" + mirrorTexture + '\'' +
                ", boxes=" + Arrays.toString(boxes) +
                ", sprites=" + Arrays.toString(sprites) +
                ", submodel=" + submodel +
                ", submodels=" +(
                        printFullChild ?
                            Arrays.toString(submodels) :
                            printChildNamesOnly())+
                '}';
    }

    private String printChildNamesOnly(){
        StringBuilder str = new StringBuilder();
        for (EMF_ModelData model:
             submodels) {
            str.append(model.id+", ");
        }
        return str.toString().trim();
    }
}
