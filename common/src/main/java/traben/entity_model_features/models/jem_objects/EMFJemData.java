package traben.entity_model_features.models.jem_objects;

import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.NotNull;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.utils.EMFUtils;

import java.util.*;

public class EMFJemData {
    public String texture = "";
    public int[] textureSize = null;
    public double shadow_size = 1.0;
    public LinkedList<EMFPartData> models = new LinkedList<>();
    public LinkedList<EMFPartData> originalModelsForReadingOnly;

    public final LinkedHashMap<String,String> finalAnimationsForModel = new LinkedHashMap<>();



    public String fileName = "none";
    public String mobName = "none";
    public void sendFileName(String fileName){
        this.fileName = fileName;
        this.mobName = fileName.replace("optifine/cem/","").replace(".jem","");
    }

    public void prepare(){
        originalModelsForReadingOnly = new LinkedList<>(models);

        if(!texture.isBlank()) {
            if (!this.texture.contains(".png")) this.texture = this.texture + ".png";
            //if no folder parenting assume it is relative to model
            if (!this.texture.contains("/")) this.texture = "optifine/cem/" + this.texture;
        }

        for (EMFPartData model:
             models) {
            model.prepare( 0,textureSize,texture, new float[]{0,0,0});
        }

        //attach logic
        LinkedList<EMFPartData> modelsAttach = new LinkedList<>();
        Iterator<EMFPartData> modelsIterator = models.iterator();
        while(modelsIterator.hasNext()){
            EMFPartData model  = modelsIterator.next();
            if(model.attach){
                modelsAttach.add(model);
                modelsIterator.remove();
            }
        }
        for (EMFPartData model:
                modelsAttach) {
            if(model.part != null){
                for (EMFPartData partData:
                     models) {
                    if(partData.part.equals(model.part)){
                        partData.submodels.add(model);//todo check if needs to be merge or just child add :/
                        model.part=null;
                    }
                }
            }else{
                //pls no
            }
        }

        //vanilla parenting adjustments
        Map<String, PartAndChildName> map = new HashMap<>();
        Set<String> foundChildren = new HashSet<>();
        if(mobName.equals("villager")){//todo
            map = Map.ofEntries(
                    getEntryOptifineWithChildList("head","head",List.of("hat","nose")),
                    getEntryOptifineWithChild("headwear","hat", "hat_rim"),
                    getEntryOptifineDifferent("headwear2", "hat_rim"),
                    getEntryOptifineDifferent("bodywear", "jacket"),
                    getEntryOptifineWithChild("body","body","jacket"),
                    getEntryOptifineSameAsVanilla("arms"),
                    getEntryOptifineSameAsVanilla("right_leg"),
                    getEntryOptifineSameAsVanilla("left_leg"),
                    getEntryOptifineSameAsVanilla("nose"));
        } else if(mobName.equals("iron_golem")){//iron_golem               head, body, left_arm, right_arm, left_leg, right_leg
            map = Map.ofEntries(
                    getEntryOptifineSameAsVanilla("head"),
                    getEntryOptifineSameAsVanilla("body"),
                    getEntryOptifineSameAsVanilla("left_arm"),
                    getEntryOptifineSameAsVanilla("right_arm"),
                    getEntryOptifineSameAsVanilla("left_leg"),
                    getEntryOptifineSameAsVanilla("right_leg")
            );
        } else if(mobName.equals("spider")){//head, neck, body, leg1, ... leg8
            map = Map.ofEntries(
                    getEntryOptifineSameAsVanilla("head"),
                    getEntryOptifineDifferent("neck","body0"),
                    getEntryOptifineDifferent("body","body1"),
                    getEntryOptifineDifferent("leg1","right_hind_leg"),
                    getEntryOptifineDifferent("leg2","left_hind_leg"),
                    getEntryOptifineDifferent("leg3","right_middle_hind_leg"),
                    getEntryOptifineDifferent("leg4","left_middle_hind_leg"),
                    getEntryOptifineDifferent("leg5","right_middle_front_leg"),
                    getEntryOptifineDifferent("leg6","left_middle_front_leg"),
                    getEntryOptifineDifferent("leg7","right_front_leg"),
                    getEntryOptifineDifferent("leg8","left_front_leg")
            );
        } else if(mobName.equals("sheep")) {
            map = Map.ofEntries(
                    getEntryOptifineSameAsVanilla("head"),
                    getEntryOptifineSameAsVanilla("body"),
                    getEntryOptifineDifferent("leg1", "right_hind_leg"),
                    getEntryOptifineDifferent("leg2", "left_hind_leg"),
                    getEntryOptifineDifferent("leg3", "right_front_leg"),
                    getEntryOptifineDifferent("leg4", "left_front_leg"));
        }else if(mobName.equals("cow")) {
            map = Map.ofEntries(
                    getEntryOptifineSameAsVanilla("head"),
                    getEntryOptifineSameAsVanilla("body"),
                    getEntryOptifineDifferent("leg1", "right_hind_leg"),
                    getEntryOptifineDifferent("leg2", "left_hind_leg"),
                    getEntryOptifineDifferent("leg3", "right_front_leg"),
                    getEntryOptifineDifferent("leg4", "left_front_leg"));
        }else if(mobName.equals("zombie")){
            // head, headwear, body, left_arm, right_arm, left_leg, right_leg
            map = Map.ofEntries(
                    getEntryOptifineSameAsVanilla("head"),
                    getEntryOptifineDifferent("headwear","hat"),
                    getEntryOptifineSameAsVanilla("body"),
                    getEntryOptifineSameAsVanilla("left_arm"),
                    getEntryOptifineSameAsVanilla("right_arm"),
                    getEntryOptifineSameAsVanilla("left_leg"),
                    getEntryOptifineSameAsVanilla("right_leg")
                    );

        }
        //change all part values to their vanilla counterparts
        for (EMFPartData partData:
                models) {
            if(map.containsKey(partData.part)) {
                String newPartName = map.get(partData.part).partName();
                if(partData.id.equals(partData.part)){
                    partData.id = newPartName;
                }
                partData.part = newPartName;

            }
        }

        //copy all children into their parents lists
        for (Map.Entry<String, PartAndChildName> entry:
             map.entrySet()) {

            if(entry.getValue().childNamesToExpect().size() >0){
                //found entry with child
                EMFPartData parent = getFirstPartInModels(entry.getValue().partName());
                if(parent != null){
                    for (String childName:
                            entry.getValue().childNamesToExpect()) {
                        EMFPartData child = getFirstPartInModels(childName);
                        if(child != null) {
                            parent.submodels.add(child);
                            foundChildren.add(childName);
                        }
                    }
                }
            }
        }
        //all children have been added to their parents time to remove children from the top level list
        models.removeIf(topLevelPart -> foundChildren.contains(topLevelPart.part));

        //now all parts follow exactly the vanilla model root parent structure
        //attaches have also been applied currently only as children

        ///prep animations
        SortedMap<String, EMFPartData> alphabeticalOrderedParts = new TreeMap<>(Comparator.naturalOrder());
        if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("originalModelsForReadingOnly #= "+ originalModelsForReadingOnly.size());
        for (EMFPartData partData:
             originalModelsForReadingOnly) {
            alphabeticalOrderedParts.put(partData.id, partData);
        }

        LinkedList<LinkedHashMap<String,String>>  allTopLevelPropertiesOrdered = new LinkedList<>();
        if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("alphabeticalOrderedParts = "+ alphabeticalOrderedParts);
        for (EMFPartData part :
                alphabeticalOrderedParts.values()) {
            if (part.animations != null && part.animations.length != 0) {
                //todo replace 'this' and parenting to represent actual model part
                allTopLevelPropertiesOrdered.addAll(Arrays.asList(part.animations));
            }
        }
        LinkedHashMap<String,String>  combinedPropertiesOrdered = new LinkedHashMap<>();
        if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("allTopLevelPropertiesOrdered = "+ allTopLevelPropertiesOrdered);
        for (LinkedHashMap<String,String> properties :
                allTopLevelPropertiesOrdered) {
            if (!properties.isEmpty()) {
                combinedPropertiesOrdered.putAll(properties);
            }
        }
        //LinkedHashMap<String,String>  finalNameFilteredPropertiesOrdered = new LinkedHashMap<>();
        if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("combinedPropertiesOrdered = "+ combinedPropertiesOrdered);
        for (Map.Entry<String,String> entry :
                combinedPropertiesOrdered.entrySet()) {
            if(entry.getKey()!=null && !entry.getKey().isEmpty()) {
                String animationKey = entry.getKey().replaceAll("\\s", "");
                String animationExpression = entry.getValue().replaceAll("\\s", "");


                //there is no way out of this we have to loop each mapping for each entry to cover all possible part pointers
                //todo can likely optimize further
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("map = "+ map);
                for (Map.Entry<String, PartAndChildName> optifineMapEntry :
                        map.entrySet()) {
                    String optifinePartName = optifineMapEntry.getKey();
                    String vanillaPartName = optifineMapEntry.getValue().partName;
                    if(!optifinePartName.equals(vanillaPartName)) {//skip if no change needed
                        if (animationKey.contains(optifinePartName)) {//this is faster than the lookbehind and ahead regex it will save us time if the string does not contain a part reference
                            animationKey = animationKey.replaceAll(
                                    REGEX_PREFIX + optifinePartName + REGEX_SUFFIX, vanillaPartName);//very costly but the look ahead and behind are essential
                        }
                        if (animationExpression.contains(optifinePartName)) {
                            animationExpression = animationExpression.replaceAll(
                                    REGEX_PREFIX + optifinePartName + REGEX_SUFFIX, vanillaPartName);//very costly
                        }
                    }
                }
                //expression and key now have vanilla part names and references as well as no spaces
                finalAnimationsForModel.put(animationKey, animationExpression);
            }else{
                System.out.println("null key 1346341");
            }
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("finalAnimationsForModel ="+ finalAnimationsForModel);
        }
        ///finished animations preprocess
    }
    private final String REGEX_PREFIX = "(?<=([^a-zA-Z0-9_]|^))";
    private final String REGEX_SUFFIX = "(?=([^a-zA-Z0-9_]|$))";


    private EMFPartData getFirstPartInModels(String partName){

        for (EMFPartData emfPartData:
             models) {
            if(emfPartData.part.equals(partName)) return emfPartData;
        }
        return null;
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

    private static  Map.Entry<String,PartAndChildName> getEntryOptifineSameAsVanilla(String name){
        return new MutablePair<>(name,_getPartAndChild(name));
    }
    private static  Map.Entry<String,PartAndChildName> getEntryOptifineDifferent(String name,String vanillaName){
        return new MutablePair<>(name,_getPartAndChild(vanillaName));
    }
    private static  Map.Entry<String,PartAndChildName> getEntryOptifineWithChild(String name,String vanillaName,String childName){
        return new MutablePair<>(name,_getPartAndChild( vanillaName,childName));
    }
    private static  Map.Entry<String,PartAndChildName> getEntryOptifineWithChildList(String name,String vanillaName,List<String> childNames){
        return new MutablePair<>(name,_getPartAndChild( vanillaName,childNames));
    }

    private record PartAndChildName(@NotNull String partName, @NotNull List<String> childNamesToExpect){

    }
    private static PartAndChildName _getPartAndChild(String partName, String childName){
        return new PartAndChildName(partName, Collections.singletonList(childName));
    }
    private static PartAndChildName _getPartAndChild(String partName){
        return new PartAndChildName(partName, new ArrayList<>());
    }
    private static PartAndChildName _getPartAndChild(String partName, List<String> childNamesToExpect){
        return new PartAndChildName(partName, childNamesToExpect);
    }
}
