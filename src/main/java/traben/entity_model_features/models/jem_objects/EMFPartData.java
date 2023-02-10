package traben.entity_model_features.models.jem_objects;

import traben.entity_model_features.utils.EMFUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class EMFPartData {

    public String texture = "";
    public int[] textureSize = null;
    public String invertAxis = "";
    public float[] translate = {0,0,0};
    public float[] rotate = {0,0,0};
    public String mirrorTexture = "";
    public EMFBoxData[] boxes = {};
    public EMFSpriteData[] sprites = {};
    public EMFPartData submodel = null;
    public EMFPartData[] submodels = {};

    public String baseId = "";  //- Model parent ID, all parent properties are inherited
    public String model = "";  //- Part model jemJsonObjects, from which to load the part model definition
    public String id = "";            //- Model ID, can be used to reference the model as parent
    public String part = null;//"!!!!!";     //- Entity part to which the part model is atached
    public boolean attach = false; //- True: attach to the entity part, False: replace it
    public float scale = 1.0f;


    public LinkedHashMap<String,String>[] animations = null;

    private void copyFrom(EMFPartData jpmModel){
        this.submodels = jpmModel.submodels;
        this.submodel = jpmModel.submodel;
        this.textureSize = jpmModel.textureSize;
        this.texture = jpmModel.texture;
        this.invertAxis = jpmModel.invertAxis;
        this.translate = jpmModel.translate;
        this.rotate = jpmModel.rotate;
        this.mirrorTexture = jpmModel.mirrorTexture;
        this.boxes = jpmModel.boxes;
        this.sprites = jpmModel.sprites;
        this.scale = jpmModel.scale;
        this.animations = jpmModel.animations;

        this.baseId = jpmModel.baseId;//todo i'm not sure what this does yet, it probably should be defined outside the jpm and thus not copied here
    }

    public void prepare(int parentCount, int[] textureSize, String texture){

//        for (LinkedHashMap<String,String> map:
//             animations) {
//            System.out.println("{}{}{}{}{}");
//            map.forEach((key,val)->{
//                System.out.println(" >>>>> "+key);
//            });
//        }

        //check if we need to load a .jpm into this object
        if(!this.model.isEmpty()){
            EMFPartData jpmModel = EMFUtils.EMF_readModelPart(this.model);
            if(jpmModel != null){
                copyFrom(jpmModel);

               // EntityType.CREEPER.getHeight()
                //this object is now filled with data from the .JPM
//                    if (this.translate.length == 3) {
//                        this.translate[1] = this.translate[1] - 24;
//                    } else {
//                        this.translate = new float[]{0, 24, 0};
//                    }
            }
        }


        if(this.textureSize == null) this.textureSize = textureSize;
        if(this.texture.isBlank()){
            this.texture = texture;
        }else{
            if(!this.texture.contains(".png")) this.texture = this.texture + ".png";
            //if no folder parenting assume it is relative to model
            if(!this.texture.contains("/")) this.texture = "optifine/cem/" + this.texture;
        }

        //rotate[0] = rotate[0] /1.57079632679f;
        //rotate[1] = rotate[1] /1.57079632679f;
        //rotate[2] = rotate[2] /1.57079632679f;


//        for (LinkedHashMap<String,String> p:
//             animations) {
//            for (String s:
//                 p.stringPropertyNames()) {
//                animationInstructionList.put(s,p.getProperty(s));
//            }
//        }

        if (!model.equals("")){
            //TODO load json model part files to this object
        }
        for (EMFBoxData box:
             boxes) {
            box.prepare();
        }
        for (EMFSpriteData sprite:
                sprites) {
            sprite.prepare();
        }
        if (submodel !=null){
            submodel.prepare(parentCount+1, this.textureSize, this.texture);
        }
        for (EMFPartData model:
                submodels) {
            model.prepare(parentCount+1, this.textureSize, this.texture);
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
        for (EMFPartData model:
             submodels) {
            str.append(model.id+", ");
        }
        return str.toString().trim();
    }
}
