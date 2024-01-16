package traben.entity_model_features.models.jem_objects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFOptiFinePartNameMappings;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.OptifineMobNameForFileAndEMFMapId;

import java.util.*;

public class EMFJemData {

    public LinkedHashMap<String, LinkedHashMap<String, String>> getAllTopLevelAnimationsByVanillaPartName() {
        return allTopLevelAnimationsByVanillaPartName;
    }

    private final LinkedHashMap<String, LinkedHashMap<String, String>> allTopLevelAnimationsByVanillaPartName = new LinkedHashMap<>();
    public String texture = "";
    public int[] textureSize = null;
    public double shadow_size = 1.0;
    public LinkedList<EMFPartData> models = new LinkedList<>();

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public OptifineMobNameForFileAndEMFMapId getMobModelIDInfo() {
        return mobModelIDInfo;
    }

    public Identifier getCustomTexture() {
        return customTexture;
    }

    private String fileName = "none";
    private String filePath = "";
    private OptifineMobNameForFileAndEMFMapId mobModelIDInfo = null;

    //public String mobName = "none";
    private Identifier customTexture = null;

    @Nullable
    public Identifier validateJemTexture(String texture) {
        texture = texture.trim();
        if (!texture.isBlank()) {
            if (!texture.endsWith(".png")) texture = texture + ".png";
            //if no folder parenting assume it is relative to model
            if (!texture.contains("/")) {
                String[] directorySplit = fileName.split("/");
                if (directorySplit.length > 1) {
                    String lastDirectoryComponentOfFileName = directorySplit[directorySplit.length - 1];
                    String folderOfModel = fileName.replace(lastDirectoryComponentOfFileName, "");
                    texture = folderOfModel + texture;
                }
            }
            Identifier possibleTexture = new Identifier(texture);
            if (MinecraftClient.getInstance().getResourceManager().getResource(possibleTexture).isPresent()) {
                return possibleTexture;
            }
        }
        return null;
    }

    public void prepare(String fileName, OptifineMobNameForFileAndEMFMapId mobModelIDInfo) {
        this.mobModelIDInfo = mobModelIDInfo;
        this.fileName = fileName;

        String[] directorySplit = fileName.split("/");
        if (directorySplit.length > 1) {
            String lastDirectoryComponentOfFileName = directorySplit[directorySplit.length - 1];
            filePath = fileName.replace(lastDirectoryComponentOfFileName, "");

        }

        LinkedList<EMFPartData> originalModelsForReadingOnly = new LinkedList<>(models);

        customTexture = validateJemTexture(texture);

        String mapId = mobModelIDInfo.getMapId();
        Map<String, String> map = EMFOptiFinePartNameMappings.getMapOf(mapId, null);

        //change all part values to their vanilla counterparts
        for (EMFPartData partData :
                models) {
            if (partData.part != null) {
                if (map.containsKey(partData.part)) {
                    partData.part = map.get(partData.part);

                }
            }
        }

        for (EMFPartData model :
                models) {
            model.prepare(textureSize, this, customTexture);
        }

        ///prep animations
        SortedMap<String, EMFPartData> alphabeticalOrderedParts = new TreeMap<>(Comparator.naturalOrder());
        if (EMFConfig.getConfig().logModelCreationData)
            EMFUtils.log("originalModelsForReadingOnly #= " + originalModelsForReadingOnly.size());
        for (EMFPartData partData :
                originalModelsForReadingOnly) {
            //if two parts both with id of EMF_body the later will get renamed to copy first come first server approach that optifine seems to have
            String newId = EMFUtils.getIdUnique(alphabeticalOrderedParts.keySet(), partData.id);
            if (!newId.equals(partData.id)) partData.id = newId;
            alphabeticalOrderedParts.put(partData.id, partData);
        }

        if (EMFConfig.getConfig().logModelCreationData)
            EMFUtils.log("alphabeticalOrderedParts = " + alphabeticalOrderedParts);
        for (EMFPartData part :
                alphabeticalOrderedParts.values()) {
            if (part.animations != null) {
                for (LinkedHashMap<String, String> animation
                        : part.animations) {
                    LinkedHashMap<String, String> thisPartsAnimations = new LinkedHashMap<>();
                    animation.forEach((key, anim) -> {
                        key = key.trim().replaceAll("\\s", "");
                        anim = anim.trim().replaceAll("\\s", "");
                        if (key.startsWith("this.")) key = key.replaceFirst("this", part.id);
                        if (anim.contains("this.")) anim = anim.replaceAll("this", part.id);
                        if (!key.isBlank() && !anim.isBlank())
                            thisPartsAnimations.put(key, anim);
                    });
                    if (!thisPartsAnimations.isEmpty()) {
                        if (allTopLevelAnimationsByVanillaPartName.containsKey(part.part)) {
                            allTopLevelAnimationsByVanillaPartName.get(part.part).putAll(thisPartsAnimations);
                        } else {
                            allTopLevelAnimationsByVanillaPartName.put(part.part, thisPartsAnimations);
                        }
                    }
                }
            }
        }

        //place in a simple animation to set the shadow size
        if(shadow_size != 1.0){
            if (shadow_size < 0) shadow_size = 0;

            String rootPart = "EMF_root";
            LinkedHashMap<String, String> shadowAnimation = new LinkedHashMap<>();
            shadowAnimation.put("render.shadow_size", String.valueOf(shadow_size));
            if (allTopLevelAnimationsByVanillaPartName.containsKey(rootPart)) {
                allTopLevelAnimationsByVanillaPartName.get(rootPart).putAll(shadowAnimation);
            } else {
                allTopLevelAnimationsByVanillaPartName.put(rootPart, shadowAnimation);
            }
        }

        ///finished animations preprocess
    }


    @Override
    public String toString() {
        return "EMF_JemData{" +
                "texture='" + texture + '\'' +
                ", textureSize=" + Arrays.toString(textureSize) +
                ", shadow_size=" + shadow_size +
                ", models=" + models.toString() +
                '}';
    }

    public static class EMFJemPrinter {//todo use and assign values
        public String texture = "";
        public int[] textureSize = {16, 16};
        public double shadow_size = 1.0;
        public LinkedList<EMFPartData.EMFPartPrinter> models = new LinkedList<>();
    }
}
