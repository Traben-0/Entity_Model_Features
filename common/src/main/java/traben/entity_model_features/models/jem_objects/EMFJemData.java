package traben.entity_model_features.models.jem_objects;

import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EMFJemData {
    public String texture = "";
    public int[] textureSize = null;
    public double shadow_size = 1.0;
    public LinkedList<EMFPartData> models = new LinkedList<>();

    String fileName = "none";
    String mobName = "none";
    public void sendFileName(String fileName){
        this.fileName = fileName;
        this.mobName = fileName.replace("optifine/cem/","").replace(".jem","");
    }

    public void prepare(){
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
        } else if(mobName.equals("sheep")) {
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
            if(map.containsKey(partData.part)) partData.part = map.get(partData.part).partName();
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

        //todo remap all animation part names to use vanilla with the optifine map
    }


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
