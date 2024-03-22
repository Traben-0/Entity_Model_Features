package traben.entity_model_features.models.jem_objects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.utils.EMFOptiFinePartNameMappings;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.OptifineMobNameForFileAndEMFMapId;

import java.util.*;

@SuppressWarnings("CanBeFinal")
public class EMFJemData {

    private final LinkedHashMap<String, LinkedHashMap<String, String>> allTopLevelAnimationsByVanillaPartName = new LinkedHashMap<>();
    public String texture = "";
    public int[] textureSize = null;
    public double shadow_size = 1.0;
    public LinkedList<EMFPartData> models = new LinkedList<>();
    private String fileName = "none";
    private String filePath = "";
    private OptifineMobNameForFileAndEMFMapId mobModelIDInfo = null;
    private Identifier customTexture = null;

    public LinkedHashMap<String, LinkedHashMap<String, String>> getAllTopLevelAnimationsByVanillaPartName() {
        return allTopLevelAnimationsByVanillaPartName;
    }

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

    @Nullable
    public Identifier validateJemTexture(String textureIn) {// "textures/entity/trident.png"
        if (textureIn == null) return null;

        String textureTest = textureIn.trim();
        if (!textureTest.isBlank()) {

            //todo add support for trident no idea why it breaks currently
            if (textureTest.endsWith("/trident.jem")) {
                EMFUtils.logWarn("trident textureTest overrides are not supported currently, they will be ignored.");
                return null;
            }

            if (!textureTest.contains(":")) {
                if (!textureTest.endsWith(".png")) textureTest = textureTest + ".png";

                //if no folder parenting assume it is relative to model
                if (!textureTest.contains("/") || textureTest.startsWith("./")) {
                    textureTest = filePath + textureTest;
                } else if (textureTest.startsWith("~/")) {
                    textureTest = "optifine/" + textureTest;
                }
            }
            if (Identifier.isValid(textureTest)) {
                Identifier possibleTexture = new Identifier(textureTest);
                if (MinecraftClient.getInstance().getResourceManager().getResource(possibleTexture).isPresent()) {
                    return possibleTexture;
                }
            } else {
                EMFUtils.logWarn("Invalid texture identifier: " + textureTest + " for " + fileName);

            }
        }
        return null;
    }

    private String workingDirectory() {
        String[] directorySplit = fileName.split("/");
        if (directorySplit.length > 1) {
            String lastDirectoryComponentOfFileName = directorySplit[directorySplit.length - 1];
            return fileName.replaceAll(lastDirectoryComponentOfFileName + "$", "");
        }
        return "optifine/cem/";
    }


    public void prepare(String fileName, OptifineMobNameForFileAndEMFMapId mobModelIDInfo) {
        if (textureSize != null && textureSize.length != 2) {
            textureSize = new int[]{64, 32};
            EMFUtils.logWarn("No textureSize provided for: " + fileName + ". Defaulting to 64x32 texture size for model.");
        }

        this.mobModelIDInfo = mobModelIDInfo;
        this.fileName = fileName;

        filePath = workingDirectory();

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
        if (EMF.config().getConfig().logModelCreationData)
            EMFUtils.log("originalModelsForReadingOnly #= " + originalModelsForReadingOnly.size());
        for (EMFPartData partData :
                originalModelsForReadingOnly) {
            //if two parts both with id of EMF_body the later will get renamed to copy first come first server approach that optifine seems to have
            String newId = EMFUtils.getIdUnique(alphabeticalOrderedParts.keySet(), partData.id);
            if (!newId.equals(partData.id)) partData.id = newId;
            alphabeticalOrderedParts.put(partData.id, partData);
        }

        if (EMF.config().getConfig().logModelCreationData)
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
        if (shadow_size != 1.0) {
            if (shadow_size < 0) shadow_size = 0;

            String rootPart = "root";
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

    @SuppressWarnings("unused")
    public static class EMFJemPrinter {//todo use and assign values
        public String texture = "";
        public int[] textureSize = {16, 16};
        public double shadow_size = 1.0;
        public LinkedList<EMFPartData.EMFPartPrinter> models = new LinkedList<>();
    }
}
