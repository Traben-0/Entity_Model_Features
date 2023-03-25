package traben.entity_model_features.models.jem_objects;

import traben.entity_model_features.utils.EMFUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

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
    public LinkedList<EMFPartData> submodels = new LinkedList<>();

    public String baseId = "";  //- Model parent ID, all parent properties are inherited
    public String model = "";  //- Part model jemJsonObjects, from which to load the part model definition
    public String id = "";            //- Model ID, can be used to reference the model as parent

    public String part = null;//"!!!!!";     //- Entity part to which the part model is atached
    public boolean attach = false; //- True: attach to the entity part, False: replace it
    public float scale = 1.0f;


    public static final EMFPartData BLANK_PART_DATA = new EMFPartData(){{
        id = "EMF_BLANK_MODEL_PART";
        texture = "EMF_BLANK_MODEL_PART";
        invertAxis = "EMF_BLANK_MODEL_PART";
        textureSize = new int[]{1, 1};
        scale = 0;
    }};

    public static EMFPartData getBlankPartWithIDOf(String id2){
        return new EMFPartData(){{
            id = id2;
            part= id2;
            textureSize = new int[]{1, 1};
            scale = 0;
        }};
    }
    public static EMFPartData getBlankPartWithIDOfAndChildren(String id2, List<String> children){
        return new EMFPartData(){{
            id = id2;
            part= id2;
            textureSize = new int[]{1, 1};
            scale = 0;
            submodels = new LinkedList<>();
            children.forEach((name)->submodels.add(getBlankPartWithIDOf(name)));
        }};
    }

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
    public float[] parentModified = {0,0,0};
    public boolean topLevelModel = false;
    public boolean underATopLevelModel = false;
    public void prepare(int parentCount, int[] textureSize, String texture, float[] modifyyTranslates){

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

        boolean invX = invertAxis.contains("x");
        boolean invY = invertAxis.contains("y");
        boolean invZ = invertAxis.contains("z");


        //selfModelData.

        //these ones need to change due to some unknown bullshit
        float translateX= translate[0];
        float translateY= translate[1];
        float translateZ= translate[2];

        float rotateX= (float) Math.toRadians(rotate[0]);
        float rotateY= (float) Math.toRadians(rotate[1]);
        float rotateZ= (float) Math.toRadians(rotate[2]);


        //figure out the bullshit
        if( invX){
            rotateX = -rotateX;
            translateX = -translateX;
        }else{
            //nothing? just an invert?
        }
        if( invY){
            rotateY = -rotateY;
            translateY = -translateY;
        }
        if( invZ){
            rotateZ = -rotateZ;
            translateZ = -translateZ;
        }

        float[] nextModify = {0,0,0};
        if(part!= null) parentCount = 0;
        if(parentCount == 0){// && selfModelData.boxes.length == 0){
            //sendToFirstChild = new float[]{translateX, translateY, translateZ};
            nextModify = new float[]{translate[0], translate[1], translate[2]};

            topLevelModel = true;
            translate[0] = -translateX;
            translate[1] = 24 -translateY;
            translate[2] = -translateZ;//todo this negative might be an inverse due to FA's xy inverting :/
//                    pivotX = translateX;//0;
//            pivotY = 24 - translateY ;//24;//0; 24 makes it look nice normally but animations need to include it separately
//            pivotZ = translateZ;//0;
        }else// if(parentCount == 1 )
        {
//////////////            translate[0] = translateX - modifyyTranslates[0];
//////////////            translate[1] = translateY - modifyyTranslates[1];
//////////////            translate[2] = translateZ + modifyyTranslates[2];
            translate[0] = translateX +(invX ? -modifyyTranslates[0] : modifyyTranslates[0]);
            translate[1] = translateY +(invY ? -modifyyTranslates[1] : modifyyTranslates[1]);
            translate[2] = translateZ +(invZ ? -modifyyTranslates[2] : modifyyTranslates[2]);

            underATopLevelModel = parentCount == 1;
            parentModified = new float[]{invX ? -modifyyTranslates[0] : modifyyTranslates[0], (invY ? -modifyyTranslates[1] : modifyyTranslates[1]), invZ ? -modifyyTranslates[2] : modifyyTranslates[2]};
//            float parent0sTX = fromFirstChild[0];
//            float parent0sTY = fromFirstChild[1];
//            float parent0sTZ = fromFirstChild[2];
//            pivotX = parent0sTX + translateX;
//            pivotY = parent0sTY + translateY;// pivotModifyForParNum1Only[1];
//            pivotZ = parent0sTZ + translateZ;
        }
//        else{// of course it just suddenly acts normal after the first 2 :L
//            translate[0] = translateX;
//            translate[1] = translateY;
//            translate[2] = translateZ;
//        }

        rotate[0] = rotateX;
        rotate[1] = rotateY;
        rotate[2] = rotateZ;


        for (EMFBoxData box:
                boxes) {
            box.prepare(invX,invY,invZ,nextModify[0],nextModify[1],nextModify[2]);
        }

        for (EMFSpriteData sprite:
                sprites) {
            sprite.prepare();
        }
        if (submodel !=null){
            submodel.prepare(parentCount+1, this.textureSize, this.texture,nextModify);
        }
        for (EMFPartData model:
                submodels) {
            model.prepare(parentCount+1, this.textureSize, this.texture,nextModify);
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
                            submodels.toString() :
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
