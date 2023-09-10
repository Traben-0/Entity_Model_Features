package traben.entity_model_features.models.jem_objects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFOptiFinePartNameMappings;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.OptifineMobNameForFileAndEMFMapId;

import java.io.File;
import java.util.*;

public class EMFJemData {

    public final LinkedHashMap<String, String> finalAnimationsForModel = new LinkedHashMap<>();
    public String texture = "";
    public int[] textureSize = null;
    public double shadow_size = 1.0;
    public LinkedList<EMFPartData> models = new LinkedList<>();
    public LinkedList<EMFPartData> originalModelsForReadingOnly;

    public String fileName = "none";
    public OptifineMobNameForFileAndEMFMapId mobModelIDInfo = null;

    //public String mobName = "none";
    public Identifier customTexture = null;

    @Nullable
    public static Identifier validateJemTexture(String texture, OptifineMobNameForFileAndEMFMapId mobModelIDInfo) {
        texture = texture.trim();
        if (!texture.isBlank()) {
            if (!texture.endsWith(".png")) texture = texture + ".png";
            //if no folder parenting assume it is relative to model
            if (!texture.contains("/")) {
                String folderOfModel = new File(mobModelIDInfo.getfileName()).getParent();
                if (folderOfModel != null)
                    texture = folderOfModel + '/' + texture;
            }
            Identifier possibleTexture = new Identifier(texture);
            if (MinecraftClient.getInstance().getResourceManager().getResource(possibleTexture).isPresent()) {
                return possibleTexture;
            }
        }
        return null;
    }

    public void sendFileName(String fileName, OptifineMobNameForFileAndEMFMapId mobModelIDInfo) {
        this.mobModelIDInfo = mobModelIDInfo;
        this.fileName = fileName;
    }

    public void prepare() {
        originalModelsForReadingOnly = new LinkedList<>(models);

        customTexture = validateJemTexture(texture, mobModelIDInfo);


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
            model.prepare(textureSize, mobModelIDInfo);
        }

        ///prep animations
        SortedMap<String, EMFPartData> alphabeticalOrderedParts = new TreeMap<>(Comparator.naturalOrder());
        if (EMFConfig.getConfig().printModelCreationInfoToLog)
            EMFUtils.EMFModMessage("originalModelsForReadingOnly #= " + originalModelsForReadingOnly.size());
        for (EMFPartData partData :
                originalModelsForReadingOnly) {
            //if two parts both with id of EMF_body the later will get renamed to copy first come first server approach that optifine seems to have
            String newId = EMFUtils.getIdUnique(alphabeticalOrderedParts.keySet(), partData.id);
            if(!newId.equals(partData.id)) partData.id =newId;
            alphabeticalOrderedParts.put( partData.id, partData);
        }

        LinkedList<LinkedHashMap<String, String>> allTopLevelPropertiesOrdered = new LinkedList<>();
        if (EMFConfig.getConfig().printModelCreationInfoToLog)
            EMFUtils.EMFModMessage("alphabeticalOrderedParts = " + alphabeticalOrderedParts);
        for (EMFPartData part :
                alphabeticalOrderedParts.values()) {
            if (part.animations != null) {
                for (LinkedHashMap<String, String> animation
                        : part.animations) {
                    LinkedHashMap<String, String> newAnimation = new LinkedHashMap<>();
                    animation.forEach((key, anim) -> {
                        if (key.startsWith("this.")) key = key.replaceFirst("this", part.id);
                        if (anim.contains("this.")) anim = anim.replaceAll("this", part.id);
                        newAnimation.put(key, anim);
                    });
                    allTopLevelPropertiesOrdered.add(newAnimation);
                }

                //allTopLevelPropertiesOrdered.addAll(Arrays.asList(part.animations));
            }
        }
        LinkedHashMap<String, String> combinedPropertiesOrdered = new LinkedHashMap<>();
        if (EMFConfig.getConfig().printModelCreationInfoToLog)
            EMFUtils.EMFModMessage("allTopLevelPropertiesOrdered = " + allTopLevelPropertiesOrdered);
        for (LinkedHashMap<String, String> properties :
                allTopLevelPropertiesOrdered) {
            if (!properties.isEmpty()) {
                combinedPropertiesOrdered.putAll(properties);
            }
        }
        //LinkedHashMap<String,String>  finalNameFilteredPropertiesOrdered = new LinkedHashMap<>();
        if (EMFConfig.getConfig().printModelCreationInfoToLog)
            EMFUtils.EMFModMessage("combinedPropertiesOrdered = " + combinedPropertiesOrdered.toString());
        for (Map.Entry<String, String> entry :
                combinedPropertiesOrdered.entrySet()) {
            if (entry.getKey() != null && !entry.getKey().isEmpty()) {
                String animationKey = entry.getKey().trim().replaceAll("\\s", "");
                String animationExpression = entry.getValue().trim().replaceAll("\\s", "");


                //there is no way out of this we have to loop each mapping for each entry to cover all possible part pointers
                //todo can likely optimize further
//                if (EMFConfig.getConfig().printModelCreationInfoToLog) EMFUtils.EMFModMessage("map = " + map);
//                for (Map.Entry<String, String> optifineMapEntry :
//                        map.entrySet()) {
//                    String optifinePartName = optifineMapEntry.getKey();
//                    String vanillaPartName = optifineMapEntry.getValue();
//                    if (!optifinePartName.equals(vanillaPartName)) {//skip if no change needed
//                        if (animationKey.startsWith(optifinePartName)) {//this is faster than the lookbehind and ahead regex it will save us time if the string does not contain a part reference
//                            animationKey = animationKey.replaceAll(
//                                    REGEX_PREFIX + optifinePartName + REGEX_SUFFIX, vanillaPartName);//very costly but the look ahead and behind are essential
//                        }
//                        if (animationExpression.contains(optifinePartName)) {
//                            animationExpression = animationExpression.replaceAll(
//                                    REGEX_PREFIX + optifinePartName + REGEX_SUFFIX, vanillaPartName);//very costly
//                        }
//                    }
//                }
                //expression and key now have vanilla part names and references as well as no spaces
                finalAnimationsForModel.put(animationKey, animationExpression);
            } else {
                System.out.println("null key 1346341");
            }
            if (EMFConfig.getConfig().printModelCreationInfoToLog)
                EMFUtils.EMFModMessage("finalAnimationsForModel =" + finalAnimationsForModel);
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
