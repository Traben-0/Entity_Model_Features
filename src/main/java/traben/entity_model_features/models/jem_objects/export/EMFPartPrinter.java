package traben.entity_model_features.models.jem_objects.export;

import java.util.LinkedHashMap;
import java.util.LinkedList;

@SuppressWarnings("unused")
public class EMFPartPrinter {
    public String texture = "";
    public int[] textureSize = null;
    public String invertAxis = "xy";
    public float[] translate = {0, 0, 0};
    public float[] rotate = {0, 0, 0};
    public String mirrorTexture = "";
    public EMFBoxPrinter[] boxes = {};
//    public EMFSpriteData[] sprites = {};
    public EMFPartPrinter submodel = null;
    public LinkedList<EMFPartPrinter> submodels = new LinkedList<>();
    public String baseId = "";  //- Model parent ID, all parent properties are inherited
    public String model = "";  //- Part model jemJsonObjects, from which to load the part model definition
    public String id = "";            //- Model ID, can be used to reference the model as parent
    public String part = null;//"!!!!!";     //- Entity part to which the part model is attached
    public boolean attach = false; //- True: attach to the entity part, False: replace it
    public float scale = 1.0f;
    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, String>[] animations = new LinkedHashMap[]{};
}
