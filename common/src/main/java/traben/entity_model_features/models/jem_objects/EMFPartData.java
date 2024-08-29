package traben.entity_model_features.models.jem_objects;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Mth;
import traben.entity_model_features.models.animation.EMFAttachments;
import traben.entity_model_features.utils.EMFUtils;

import java.util.*;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("CanBeFinal")
public class EMFPartData {


    public String texture = "";
    public int[] textureSize = null;
    public String invertAxis = "";
    public float[] translate = {0, 0, 0};
    public float[] rotate = {0, 0, 0};
    public String mirrorTexture = "";
    public EMFBoxData[] boxes = {};
//todo    public EMFSpriteData[] sprites = {};
    public EMFPartData submodel = null;
    public LinkedList<EMFPartData> submodels = new LinkedList<>();
    public String baseId = "";  //- Model parent ID, all parent properties are inherited
    public String model = "";  //- Part model jemJsonObjects, from which to load the part model definition
    public String id = "";            //- Model ID, can be used to reference the model as parent
    public String part = null;//"!!!!!";     //- Entity part to which the part model is attached
    public boolean attach = false; //- True: attach to the entity part, False: replace it
    public float scale = 1.0f;

    public Object2ObjectOpenHashMap<String, float[]> attachments = new Object2ObjectOpenHashMap<>();

    public LinkedList<LinkedHashMap<String, String>> animations = null;
    private ResourceLocation customTexture = null;

    public ResourceLocation getCustomTexture() {
        return customTexture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EMFPartData partData = (EMFPartData) o;
        return  /*Arrays.equals(sprites, partData.sprites) &&*/ attach == partData.attach && Float.compare(partData.scale, scale) == 0 && Objects.equals(texture, partData.texture) && Arrays.equals(textureSize, partData.textureSize) && Objects.equals(invertAxis, partData.invertAxis) && Arrays.equals(translate, partData.translate) && Arrays.equals(rotate, partData.rotate) && Objects.equals(mirrorTexture, partData.mirrorTexture) && Arrays.equals(boxes, partData.boxes) && Objects.equals(submodel, partData.submodel) && Objects.equals(submodels, partData.submodels) && Objects.equals(baseId, partData.baseId) && Objects.equals(model, partData.model) && Objects.equals(id, partData.id) && Objects.equals(part, partData.part) && Objects.equals(animations, partData.animations) && Objects.equals(customTexture, partData.customTexture);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(texture, invertAxis, mirrorTexture, submodel, submodels, baseId, model, id, part, attach, scale, customTexture, animations);
        result = 31 * result + Arrays.hashCode(textureSize);
        result = 31 * result + Arrays.hashCode(translate);
        result = 31 * result + Arrays.hashCode(rotate);
        result = 31 * result + Arrays.hashCode(boxes);
//        result = 31 * result + Arrays.hashCode(sprites);
        //result = 31 * result + Arrays.hashCode(animations);
        return result;
    }

    private void copyFrom(EMFPartData jpmModel) {
        //no part and attach
        if (submodels.isEmpty())
            this.submodels = jpmModel.submodels;
        if (submodel == null)
            this.submodel = jpmModel.submodel;
        if (textureSize == null)
            this.textureSize = jpmModel.textureSize;
        if (texture.isBlank())
            this.texture = jpmModel.texture;
        if (invertAxis.isBlank())
            this.invertAxis = jpmModel.invertAxis;
        if (translate[0] == 0 && translate[1] == 0 && translate[2] == 0)
            this.translate = jpmModel.translate;
        if (rotate[0] == 0 && rotate[1] == 0 && rotate[2] == 0)
            this.rotate = jpmModel.rotate;
        if (mirrorTexture.isBlank())
            this.mirrorTexture = jpmModel.mirrorTexture;
        if (boxes.length == 0)
            this.boxes = jpmModel.boxes;
//        if (sprites.length == 0)
//            this.sprites = jpmModel.sprites;
        if (scale == 1f)
            this.scale = jpmModel.scale;
        if (animations == null || animations.isEmpty())
            this.animations = jpmModel.animations;
        if (baseId.isBlank())
            this.baseId = jpmModel.baseId;//todo i'm not sure what this does yet, it probably should be defined outside the jpm and thus not copied here
    }

    public List<Consumer<PoseStack>> getAttachments() {
        var list = new ArrayList<Consumer<PoseStack>>();
        boolean invX = invertAxis.contains("x");
        boolean invY = invertAxis.contains("y");
        boolean invZ = invertAxis.contains("z");

        for (Map.Entry<String, float[]> entry : attachments.entrySet()) {
            String s = entry.getKey();
            float[] floats = entry.getValue();
            if (floats != null && floats.length == 3) {
                try {
                    EMFAttachments attachment = EMFAttachments.valueOf(s);
                    list.add(attachment.getConsumerWithTranslates(
                            floats[0] * (invX ? -1 : 1),
                            floats[1] * (invY ? -1 : 1),
                            floats[2] * (invZ ? -1 : 1)
                    ));
                } catch (IllegalArgumentException e) {
                    EMFUtils.log("Unknown attachment point: " + s);
                }
            }
        }
        return list;
    }

    public void prepare(int[] textureSize, EMFJemData jem){
        this.id = "EMF_" + (this.id.isBlank() ? hashCode() : this.id);

        //check if we need to load a .jpm into this object
        if (!model.isEmpty()) {
            Optional.ofNullable(EMFUtils.readModelPart(model, jem.directoryContext))
                    .ifPresent(this::copyFrom);
        }

        if (this.textureSize == null || this.textureSize.length != 2) this.textureSize = textureSize;
        this.customTexture = jem.validateJemTexture(texture);

        boolean invX = invertAxis.contains("x");
        boolean invY = invertAxis.contains("y");
        boolean invZ = invertAxis.contains("z");

        translate[0] = invX ? -translate[0] : translate[0];
        translate[1] = invY ? -translate[1] : translate[1];
        translate[2] = invZ ? -translate[2] : translate[2];

        rotate[0] = (invX ? -rotate[0] : rotate[0]) * Mth.DEG_TO_RAD;
        rotate[1] = (invY ? -rotate[1] : rotate[1]) * Mth.DEG_TO_RAD;
        rotate[2] = (invZ ? -rotate[2] : rotate[2]) * Mth.DEG_TO_RAD;

        for (EMFBoxData box : boxes) {
            box.prepare(invX, invY, invZ);
        }

//todo
//        for (EMFSpriteData sprite : sprites) {
//            sprite.prepare();
//        }

        if (submodel != null) {
            submodel.prepare(this.textureSize, jem);
            if (!submodels.contains(submodel)) {
                submodels.add(submodel);
                submodel = null;
            }
        }
        for (EMFPartData model : submodels) {
            model.prepare(this.textureSize, jem);
        }
    }

    @Override
    public String toString() {
        return "modelData{ id='" + id + "', part='" + part + "', submodels=" + submodels.size() + "', anims=" + (animations == null ? "0" : animations.size()) + '}';
    }


}
