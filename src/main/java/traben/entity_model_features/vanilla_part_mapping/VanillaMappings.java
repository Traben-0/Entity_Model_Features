package traben.entity_model_features.vanilla_part_mapping;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.mixin.accessor.*;
import traben.entity_model_features.mixin.accessor.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VanillaMappings {

    //hashmap for mappers
    private static final HashMap<Integer, VanillaMapper> mappers = new HashMap<>();

    VanillaMappings(){
        //todo init vanilla mappers to lessen actual runtime load
    }




    public static VanillaMapper getVanillaModelPartsMapSupplier(int entityTypeHash, EntityModel<?> vanillaModel){
        if(!mappers.containsKey(entityTypeHash)){
            mappers.put(entityTypeHash,discoverMappingToUse(vanillaModel));
        }
        return mappers.get(entityTypeHash);
    }

    private static VanillaMapper discoverMappingToUse(EntityModel<?> vanillaModel){
//        if (vanillaModel instanceof AllayEntityModel) {
//            return VanillaMappings::getAllayMap;
//        }

        if (vanillaModel instanceof SlimeEntityModel) {
            return VanillaMappings::getSlimeMap;
        }
        if (vanillaModel instanceof HoglinEntityModel) {
            return VanillaMappings::getHoglinMap;
        }
        if (vanillaModel instanceof RavagerEntityModel) {
            return VanillaMappings::getRavagerMap;
        }
        if (vanillaModel instanceof WolfEntityModel) {
            return VanillaMappings::getWolfMap;
        }
        if (vanillaModel instanceof MagmaCubeEntityModel) {
            return VanillaMappings::getMagmaCubeMap;
        }
        if (vanillaModel instanceof LlamaEntityModel) {
            return VanillaMappings::getLlamaMap;
        }
        if (vanillaModel instanceof FoxEntityModel) {
            return VanillaMappings::getFoxMap;
        }
        if (vanillaModel instanceof GuardianEntityModel) {
            return VanillaMappings::getGuardianMap;
        }
        if (vanillaModel instanceof HorseEntityModel) {
            return VanillaMappings::getHorseMap;
        }
        if (vanillaModel instanceof CreeperEntityModel) {
            return VanillaMappings::getCreeperMap;
        }
        if (vanillaModel instanceof ChickenEntityModel) {
            return VanillaMappings::getChickenMap;
        }
        if (vanillaModel instanceof OcelotEntityModel) {
            return VanillaMappings::getGenericOcelotMap;
        }
        if (vanillaModel instanceof BlazeEntityModel) {
            return VanillaMappings::getBlazeMap;
        }
        if (vanillaModel instanceof BeeEntityModel) {
            return VanillaMappings::getBeeMap;
        }
        if (vanillaModel instanceof BatEntityModel) {
            return VanillaMappings::getBatMap;
        }
        if (vanillaModel instanceof AxolotlEntityModel) {
            return VanillaMappings::getAxolotlMap;
        }
        if (vanillaModel instanceof SpiderEntityModel) {
            return VanillaMappings::getSpiderMap;
        }
        if (vanillaModel instanceof WitchEntityModel<?>) {
            return VanillaMappings::getWitchMap;
        }
        if (vanillaModel instanceof VillagerResemblingModel) {
            return VanillaMappings::getVillagerMap;
        }
        if (vanillaModel instanceof IronGolemEntityModel) {
            return VanillaMappings::getIronGolemMap;
        }
        if (vanillaModel instanceof ArmorStandEntityModel) {
            return VanillaMappings::getArmorStandMap;
        }
        if (vanillaModel instanceof PlayerEntityModel) {
            return VanillaMappings::getGenericPlayerMap;
        }
        if (vanillaModel instanceof BipedEntityModel) {
            return VanillaMappings::getGenericBipedMap;
        }
        if (vanillaModel instanceof QuadrupedEntityModel) {
            return VanillaMappings::getGenericQuadrapedMap;
        }
        if (vanillaModel instanceof AnimalModel<?>) {
            return VanillaMappings::getGenericAnimalMap;
        }

        //catches a lot automatically
        // list of correctly auto captured models, that I have confirmed are OptiFine name correct
        // Allay, Bat(partially)
        if (vanillaModel instanceof SinglePartEntityModel) {
            return VanillaMappings::getSinglePartModelMap;
        }
        return  VanillaMappings::getEmptyMap;


    }
    private static HashMap<String, ModelAndParent> getEmptyMap(EntityModel<?> vanillaModel){
        //todo mod entity integreation for part names
        System.out.println("empty model mapped for: "+ vanillaModel.getClass().toString());
        return new HashMap<>();
    }
    //this ones a bit special it automatically traverses a single part model capturing the vanilla part names
    // this automatically correctly captures allay model part and names for example
    private static HashMap<String, ModelAndParent> getSinglePartModelMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof SinglePartEntityModel model) {
            ModelPart root = model.getPart();
            Map<String,ModelPart> sourceChildren = ((ModelPartAccessor)root).getChildren();
            vanillaPartsList.putAll(iterateOverRootPartsChildren(sourceChildren,null));

        }
        return vanillaPartsList;
    }

    private static Map<String, ModelAndParent> getAllRootPartsChildren(ModelPart root){
        Map<String,ModelPart> sourceChildren = ((ModelPartAccessor)root).getChildren();
        return iterateOverRootPartsChildren(sourceChildren, null);
    }
    private static Map<String, ModelAndParent> iterateOverRootPartsChildren(Map<String, ModelPart> sourceChildren, String parentName){
        Map<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        for (Map.Entry<String, ModelPart> entry:
             sourceChildren.entrySet()) {
            vanillaPartsList.put(entry.getKey(), getEntry(entry.getValue(),parentName));
            Map<String,ModelPart> nextChildren = ((ModelPartAccessor)entry.getValue()).getChildren();
            vanillaPartsList.putAll(iterateOverRootPartsChildren(nextChildren,entry.getKey()));
        }
        return vanillaPartsList;
    }




//    private static HashMap<String, ModelPart> getAllayMap(EntityModel<?> vanillaModel){
//        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
//        if (vanillaModel instanceof AllayEntityModel model) {
//            ModelPart root = model.getPart();
//            vanillaPartsList.put("head",root.getChild("head"));
//            ModelPart body = root.getChild("body");
//            vanillaPartsList.put("body",body);
//            vanillaPartsList.put("right_arm",body.getChild("right_arm"));
//            vanillaPartsList.put("left_arm",body.getChild("left_arm"));
//            vanillaPartsList.put("right_wing",body.getChild("right_wing"));
//            vanillaPartsList.put("left_wing",body.getChild("left_wing"));
//        }
//        return vanillaPartsList;
//    }


    private static HashMap<String, ModelAndParent> getHoglinMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof HoglinEntityModel hoglin) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) hoglin).callGetHeadParts();
            if(hed.iterator().hasNext()) {
                ModelPart head = hed.iterator().next();
                vanillaPartsList.put("head", getEntry(head));
                if(head.hasChild("right_ear"))
                    vanillaPartsList.put("right_ear",getEntry(head.getChild("right_ear"),"head"));
                if(head.hasChild("left_ear"))
                    vanillaPartsList.put("left_ear",getEntry(head.getChild("left_ear"),"head"));

            }
            for (ModelPart part : ((AnimalModelAccessor) hoglin).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",getEntry(bodyParts.get(0)));
            vanillaPartsList.put("right_front_leg",getEntry(bodyParts.get(1)));
            vanillaPartsList.put("left_front_leg",getEntry(bodyParts.get(2)));
            vanillaPartsList.put("right_hind_leg",getEntry(bodyParts.get(3)));
            vanillaPartsList.put("left_hind_leg",getEntry(bodyParts.get(4)));
            if(bodyParts.get(0).hasChild("mane"))
                vanillaPartsList.put("mane",getEntry(bodyParts.get(0).getChild("mane"),"body"));


        }
        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getRavagerMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("jaw",vanillaPartsList.get("mouth"));
        vanillaPartsList.put("leg1",vanillaPartsList.get("right_hind_leg"));
        vanillaPartsList.put("leg2",vanillaPartsList.get("left_hind_leg"));
        vanillaPartsList.put("leg3",vanillaPartsList.get("right_front_leg"));
        vanillaPartsList.put("leg4",vanillaPartsList.get("left_front_leg"));

        return vanillaPartsList;
    }


    private static HashMap<String, ModelAndParent> getWolfMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof WolfEntityModel wolf) {
            //todo investigate the child head and tail models as they are possibly what is actually used by OptiFine

            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) wolf).callGetHeadParts();
            if(hed.iterator().hasNext()) {
                vanillaPartsList.put("head", getEntry(hed.iterator().next()));
            }
            for (ModelPart part : ((AnimalModelAccessor) wolf).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",getEntry(bodyParts.get(0)));
            vanillaPartsList.put("leg1",getEntry(bodyParts.get(1)));
            vanillaPartsList.put("leg2",getEntry(bodyParts.get(2)));
            vanillaPartsList.put("leg3",getEntry(bodyParts.get(3)));
            vanillaPartsList.put("leg4",getEntry(bodyParts.get(4)));

            vanillaPartsList.put("tail",getEntry(bodyParts.get(5)));

            vanillaPartsList.put("mane",getEntry(bodyParts.get(6)));


        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getSlimeMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("body",vanillaPartsList.get("cube"));
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getMagmaCubeMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("segment1",vanillaPartsList.get("cube0"));
        vanillaPartsList.put("segment2",vanillaPartsList.get("cube1"));
        vanillaPartsList.put("segment3",vanillaPartsList.get("cube2"));
        vanillaPartsList.put("segment4",vanillaPartsList.get("cube3"));
        vanillaPartsList.put("segment5",vanillaPartsList.get("cube4"));
        vanillaPartsList.put("segment6",vanillaPartsList.get("cube5"));
        vanillaPartsList.put("segment7",vanillaPartsList.get("cube6"));
        vanillaPartsList.put("segment8",vanillaPartsList.get("cube7"));

        vanillaPartsList.put("core",vanillaPartsList.get("inside_cube"));

        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getLlamaMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof LlamaEntityModel llama) {
            vanillaPartsList.put("head",getEntry(((LlamaEntityModelAccessor)llama).getHead()));
            vanillaPartsList.put("body",getEntry(((LlamaEntityModelAccessor)llama).getBody()));
            vanillaPartsList.put("leg1",getEntry(((LlamaEntityModelAccessor)llama).getRightHindLeg()));
            vanillaPartsList.put("leg2",getEntry(((LlamaEntityModelAccessor)llama).getLeftHindLeg()));
            vanillaPartsList.put("leg3",getEntry(((LlamaEntityModelAccessor)llama).getRightFrontLeg()));
            vanillaPartsList.put("leg4",getEntry(((LlamaEntityModelAccessor)llama).getLeftFrontLeg()));
            vanillaPartsList.put("chest_right",getEntry(((LlamaEntityModelAccessor)llama).getRightChest()));
            vanillaPartsList.put("chest_left",getEntry(((LlamaEntityModelAccessor)llama).getLeftChest()));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getFoxMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof FoxEntityModel fox) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) fox).callGetHeadParts();
            if(hed.iterator().hasNext()) {
                vanillaPartsList.put("head", getEntry(hed.iterator().next()));
            }
            for (ModelPart part : ((AnimalModelAccessor) fox).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",getEntry(bodyParts.get(0)));
            vanillaPartsList.put("leg1",getEntry(bodyParts.get(1)));
            vanillaPartsList.put("leg2",getEntry(bodyParts.get(2)));
            vanillaPartsList.put("leg3",getEntry(bodyParts.get(3)));
            vanillaPartsList.put("leg4",getEntry(bodyParts.get(4)));
            vanillaPartsList.put("tail",getEntry(((FoxEntityModelAccessor)fox).getTail()));


        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getGuardianMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("tail3",vanillaPartsList.get("tail2"));
        vanillaPartsList.put("tail2",vanillaPartsList.get("tail1"));
        vanillaPartsList.put("tail1",vanillaPartsList.get("tail0"));

        vanillaPartsList.put("body",vanillaPartsList.get("head"));

        vanillaPartsList.put("spine1",vanillaPartsList.get("spike0"));
        vanillaPartsList.put("spine2",vanillaPartsList.get("spike1"));
        vanillaPartsList.put("spine3",vanillaPartsList.get("spike2"));
        vanillaPartsList.put("spine4",vanillaPartsList.get("spike3"));
        vanillaPartsList.put("spine5",vanillaPartsList.get("spike4"));
        vanillaPartsList.put("spine6",vanillaPartsList.get("spike5"));
        vanillaPartsList.put("spine7",vanillaPartsList.get("spike6"));
        vanillaPartsList.put("spine8",vanillaPartsList.get("spike7"));
        vanillaPartsList.put("spine9",vanillaPartsList.get("spike8"));
        vanillaPartsList.put("spine10",vanillaPartsList.get("spike9"));
        vanillaPartsList.put("spine11",vanillaPartsList.get("spike10"));
        vanillaPartsList.put("spine12",vanillaPartsList.get("spike11"));
        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getHorseMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof AnimalModel horse) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) horse).callGetHeadParts();
            ModelPart neck = null;
            if(hed.iterator().hasNext()) {
                 neck =hed.iterator().next();
                    vanillaPartsList.put("neck", getEntry(neck));
            }
            if(neck!= null) {
               // Map<String, ModelPart> head_parts = ((ModelPartAccessor) neck).getChildren();

                if (neck.hasChild("head")) {
                    ModelPart head = neck.getChild("head");
                    vanillaPartsList.put("head", getEntry(head,"neck"));
                    Map<String, ModelPart> headSub_parts = ((ModelPartAccessor) head).getChildren();
                    if(headSub_parts.containsKey("left_ear"))
                        vanillaPartsList.put("left_ear", getEntry(headSub_parts.get("left_ear"),"head"));
                    if(headSub_parts.containsKey("right_ear"))
                        vanillaPartsList.put("right_ear", getEntry(headSub_parts.get("right_ear"), "head"));
                }
                if (neck.hasChild("mane"))
                    vanillaPartsList.put("mane",getEntry(neck.getChild("mane"),"neck"));
                if (neck.hasChild("mouth"))
                    vanillaPartsList.put("mouth",getEntry(neck.getChild("mouth"),"neck"));

                if (neck.hasChild("head_saddle"))
                    vanillaPartsList.put("headpiece",getEntry(neck.getChild("head_saddle"),"neck"));
                if (neck.hasChild("mouth_saddle_wrap"))
                    vanillaPartsList.put("nose_band",getEntry(neck.getChild("mouth_saddle_wrap"),"neck"));

                if (neck.hasChild("left_saddle_mouth"))
                    vanillaPartsList.put("left_bit",getEntry(neck.getChild("left_saddle_mouth"),"neck"));
                if (neck.hasChild("right_saddle_mouth"))
                    vanillaPartsList.put("right_bit",getEntry(neck.getChild("right_saddle_mouth"),"neck"));
                if (neck.hasChild("left_saddle_line"))
                    vanillaPartsList.put("left_rein",getEntry(neck.getChild("left_saddle_line"),"neck"));
                if (neck.hasChild("right_saddle_line"))
                    vanillaPartsList.put("right_rein",getEntry(neck.getChild("right_saddle_line"),"neck"));

                for (ModelPart part : ((AnimalModelAccessor) horse).callGetBodyParts()) {
                    bodyParts.add(part);
                }
                //Map<String, ModelPart> body_parts = ((ModelPartAccessor) bodyParts.get(0)).getChildren();
                if (bodyParts.get(0).hasChild("tail"))
                    vanillaPartsList.put("tail",getEntry(bodyParts.get(0).getChild("tail")));
                if (bodyParts.get(0).hasChild("saddle"))
                    vanillaPartsList.put("saddle",getEntry(bodyParts.get(0).getChild("saddle")));


                vanillaPartsList.put("body",            getEntry(bodyParts.get(0)));
                vanillaPartsList.put("back_right_leg",  getEntry(bodyParts.get(1)));
                vanillaPartsList.put("back_left_leg",   getEntry(bodyParts.get(2)));
                vanillaPartsList.put("front_right_leg", getEntry(bodyParts.get(3)));
                vanillaPartsList.put("front_left_leg",  getEntry(bodyParts.get(4)));

                vanillaPartsList.put("child_back_right_leg", getEntry(bodyParts.get(5)));
                vanillaPartsList.put("child_back_left_leg",  getEntry(bodyParts.get(6)));
                vanillaPartsList.put("child_front_right_leg",getEntry(bodyParts.get(7)));
                vanillaPartsList.put("child_front_left_leg", getEntry(bodyParts.get(8)));

            }
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getCreeperMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("leg1",vanillaPartsList.get("right_hind_leg"));
        vanillaPartsList.put("leg2",vanillaPartsList.get("left_hind_leg"));
        vanillaPartsList.put("leg3",vanillaPartsList.get("right_front_leg"));
        vanillaPartsList.put("leg4",vanillaPartsList.get("left_front_leg"));
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getBlazeMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("stick1",vanillaPartsList.get("part0"));
        vanillaPartsList.put("stick2",vanillaPartsList.get("part1"));
        vanillaPartsList.put("stick3",vanillaPartsList.get("part2"));
        vanillaPartsList.put("stick4",vanillaPartsList.get("part3"));
        vanillaPartsList.put("stick5",vanillaPartsList.get("part4"));
        vanillaPartsList.put("stick6",vanillaPartsList.get("part5"));
        vanillaPartsList.put("stick7",vanillaPartsList.get("part6"));
        vanillaPartsList.put("stick8",vanillaPartsList.get("part7"));
        vanillaPartsList.put("stick9",vanillaPartsList.get("part8"));
        vanillaPartsList.put("stick10",vanillaPartsList.get("part9"));
        vanillaPartsList.put("stick11",vanillaPartsList.get("part10"));
        vanillaPartsList.put("stick12",vanillaPartsList.get("part11"));
        return vanillaPartsList;
    }
// bee   body, torso, right_wing, left_wing, front_legs, middle_legs, back_legs, stinger, left_antenna, right_antenna
    private static HashMap<String, ModelAndParent> getBeeMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof BeeEntityModel bee) {
            ModelPart root = ((AnimalModelAccessor)bee).callGetBodyParts().iterator().next();
            vanillaPartsList.putAll(getAllRootPartsChildren(root));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getBatMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("outer_right_wing",vanillaPartsList.get("right_wing_tip"));
        vanillaPartsList.put("outer_left_wing",vanillaPartsList.get("left_wing_tip"));
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getWitchMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof WitchEntityModel witch) {
            ModelPart root = witch.getPart();
            vanillaPartsList.put("head",        getEntry(root.getChild("head")));
            vanillaPartsList.put("headwear",    getEntry(root.getChild("head").getChild("hat"),"head"));
            vanillaPartsList.put("headwear2",   getEntry(root.getChild("head").getChild("hat").getChild("hat_rim"),"hat"));
            vanillaPartsList.put("body",        getEntry(root.getChild("body")));
            vanillaPartsList.put("bodywear",    getEntry(root.getChild("body").getChild("jacket"),"body"));
            vanillaPartsList.put("arms",        getEntry(root.getChild("arms")));
            vanillaPartsList.put("left_leg",    getEntry(root.getChild("left_leg")));
            vanillaPartsList.put("right_leg",   getEntry(root.getChild("right_leg")));
            vanillaPartsList.put("nose",        getEntry(root.getChild("head").getChild("nose"),"head"));
            vanillaPartsList.put("mole",        getEntry(root.getChild("head").getChild("nose").getChild("mole"),"nose"));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getSpiderMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof SpiderEntityModel spider) {
            ModelPart root = spider.getPart();
            vanillaPartsList.put("head",getEntry(root.getChild("head")));
            vanillaPartsList.put("neck",getEntry(root.getChild("body0")));
            vanillaPartsList.put("body",getEntry(root.getChild("body1")));
            vanillaPartsList.put("leg1",getEntry(root.getChild("right_hind_leg")));
            vanillaPartsList.put("leg2",getEntry(root.getChild("left_hind_leg")));
            vanillaPartsList.put("leg3",getEntry(root.getChild("right_middle_hind_leg")));
            vanillaPartsList.put("leg4",getEntry(root.getChild("left_middle_hind_leg")));
            vanillaPartsList.put("leg5",getEntry(root.getChild("right_middle_front_leg")));
            vanillaPartsList.put("leg6",getEntry(root.getChild("left_middle_front_leg")));
            vanillaPartsList.put("leg7",getEntry(root.getChild("right_front_leg")));
            vanillaPartsList.put("leg8",getEntry(root.getChild("left_front_leg")));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getIronGolemMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof IronGolemEntityModel iron) {
            ModelPart root = iron.getPart();
            vanillaPartsList.put("head",        getEntry(root.getChild("head")));
            vanillaPartsList.put("body",        getEntry(root.getChild("body")));
            vanillaPartsList.put("right_arm",   getEntry(root.getChild("right_arm")));
            vanillaPartsList.put("left_arm",    getEntry(root.getChild("left_arm")));
            vanillaPartsList.put("right_leg",   getEntry(root.getChild("right_leg")));
            vanillaPartsList.put("left_leg",    getEntry(root.getChild("left_leg")));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getAxolotlMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof AxolotlEntityModel axol) {
            ModelPart body = ((AnimalModelAccessor)axol).callGetBodyParts().iterator().next();
            ModelPart head = body.getChild("head");
            vanillaPartsList.put("head",getEntry(head));
            vanillaPartsList.put("body",getEntry(body));
            vanillaPartsList.put("leg1",getEntry(body.getChild("right_hind_leg"),"body"));
            vanillaPartsList.put("leg2",getEntry(body.getChild("left_hind_leg"),"body"));
            vanillaPartsList.put("leg3",getEntry(body.getChild("right_front_leg"),"body"));
            vanillaPartsList.put("leg4",getEntry(body.getChild("left_front_leg"),"body"));
            vanillaPartsList.put("tail",getEntry(body.getChild("tail"),"body"));
            vanillaPartsList.put("top_gills",getEntry(head.getChild("top_gills"),"head"));
            vanillaPartsList.put("left_gills",getEntry(head.getChild("left_gills"),"head"));
            vanillaPartsList.put("right_gills",getEntry(head.getChild("right_gills"),"head"));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getGenericQuadrapedMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof QuadrupedEntityModel quadped) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((QuadrupedEntityModelAccessor) quadped).callGetHeadParts();
            if(hed.iterator().hasNext()) {
                vanillaPartsList.put("head", getEntry(hed.iterator().next()));
            }
            for (ModelPart part : ((QuadrupedEntityModelAccessor) quadped).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",getEntry(bodyParts.get(0)));
            vanillaPartsList.put("leg1",getEntry(bodyParts.get(1)));
            vanillaPartsList.put("leg2",getEntry(bodyParts.get(2)));
            vanillaPartsList.put("leg3",getEntry(bodyParts.get(3)));
            vanillaPartsList.put("leg4",getEntry(bodyParts.get(4)));


        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getGenericOcelotMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof OcelotEntityModel animal) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) animal).callGetHeadParts();
            if(hed.iterator().hasNext()) {
                vanillaPartsList.put("head", getEntry(hed.iterator().next()));
            }
            for (ModelPart part : ((AnimalModelAccessor) animal).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",            getEntry(bodyParts.get(0)));
            vanillaPartsList.put("back_left_leg",   getEntry(bodyParts.get(1)));
            vanillaPartsList.put("back_right_leg",  getEntry(bodyParts.get(2)));
            vanillaPartsList.put("front_left_leg",  getEntry(bodyParts.get(3)));
            vanillaPartsList.put("front_right_leg", getEntry(bodyParts.get(4)));
            vanillaPartsList.put("tail",            getEntry(bodyParts.get(5)));
            vanillaPartsList.put("tail2",           getEntry(bodyParts.get(6)));


        }
        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getChickenMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof ChickenEntityModel animal) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) animal).callGetHeadParts();
            //if(hed.iterator().hasNext()) {
            vanillaPartsList.put("head", getEntry(hed.iterator().next()));
            vanillaPartsList.put("bill", getEntry(hed.iterator().next()));
            vanillaPartsList.put("chin", getEntry(hed.iterator().next()));
            //}
            for (ModelPart part : ((AnimalModelAccessor) animal).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",        getEntry(bodyParts.get(0)));
            vanillaPartsList.put("right_leg",   getEntry(bodyParts.get(1)));
            vanillaPartsList.put("left_leg",    getEntry(bodyParts.get(2)));
            vanillaPartsList.put("right_wing",  getEntry(bodyParts.get(3)));
            vanillaPartsList.put("left_wing",   getEntry(bodyParts.get(4)));


        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getGenericAnimalMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof AnimalModel animal) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) animal).callGetHeadParts();
            if(hed.iterator().hasNext()) {
                vanillaPartsList.put("head", getEntry(hed.iterator().next()));
            }
            for (ModelPart part : ((AnimalModelAccessor) animal).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",getEntry(bodyParts.get(0)));
            vanillaPartsList.put("leg1",getEntry(bodyParts.get(1)));
            vanillaPartsList.put("leg2",getEntry(bodyParts.get(2)));
            vanillaPartsList.put("leg3",getEntry(bodyParts.get(3)));
            vanillaPartsList.put("leg4",getEntry(bodyParts.get(4)));


        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getArmorStandMap(EntityModel<?> vanillaModel){

        //get biped parts
        HashMap<String,ModelAndParent> vanillaPartsList = getGenericBipedMap(vanillaModel);

        if(vanillaModel instanceof ArmorStandEntityModel armor){
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            vanillaPartsList.put("head",getEntry(((ModelWithHead)armor).getHead()));
//            for (ModelPart part : ((BipedEntityModelAccessor) biped).callGetHeadParts()) {
//                vanillaPartsList.put("head",part);
//                break;
//            }
            //ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
            for (ModelPart part : ((BipedEntityModelAccessor) armor).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",        getEntry(bodyParts.get(0)));
            vanillaPartsList.put("right_arm",   getEntry(bodyParts.get(1)));
            vanillaPartsList.put("left_arm",    getEntry(bodyParts.get(2)));
            vanillaPartsList.put("right_leg",   getEntry(bodyParts.get(3)));
            vanillaPartsList.put("left_leg",    getEntry(bodyParts.get(4)));
            vanillaPartsList.put("headwear",    getEntry(bodyParts.get(5)));
            vanillaPartsList.put("right",       getEntry(bodyParts.get(6)));
            vanillaPartsList.put("left",        getEntry(bodyParts.get(7)));
            vanillaPartsList.put("waist",       getEntry(bodyParts.get(8)));//todo might be wrong part
            vanillaPartsList.put("base",        getEntry(bodyParts.get(9)));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getGenericPlayerMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if(vanillaModel instanceof PlayerEntityModel player){
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            vanillaPartsList.put("head",getEntry(((ModelWithHead)player).getHead()));
//            for (ModelPart part : ((BipedEntityModelAccessor) biped).callGetHeadParts()) {
//                vanillaPartsList.put("head",part);
//                break;
//            }
            //ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
            for (ModelPart part : ((BipedEntityModelAccessor) player).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",        getEntry(bodyParts.get(0)));
            vanillaPartsList.put("right_arm",   getEntry(bodyParts.get(1)));
            vanillaPartsList.put("left_arm",    getEntry(bodyParts.get(2)));
            vanillaPartsList.put("right_leg",   getEntry(bodyParts.get(3)));
            vanillaPartsList.put("left_leg",    getEntry(bodyParts.get(4)));
            vanillaPartsList.put("headwear",    getEntry(bodyParts.get(5)));
            vanillaPartsList.put("left_pants",  getEntry(bodyParts.get(6)));
            vanillaPartsList.put("right_pants", getEntry(bodyParts.get(7)));
            vanillaPartsList.put("left_sleeve", getEntry(bodyParts.get(8)));
            vanillaPartsList.put("right_sleeve",getEntry(bodyParts.get(9)));
            vanillaPartsList.put("jacket",      getEntry(bodyParts.get(10)));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getGenericBipedMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if(vanillaModel instanceof BipedEntityModel biped){
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            vanillaPartsList.put("head",getEntry(((ModelWithHead)biped).getHead()));
//            for (ModelPart part : ((BipedEntityModelAccessor) biped).callGetHeadParts()) {
//                vanillaPartsList.put("head",part);
//                break;
//            }
            //ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
            for (ModelPart part : ((BipedEntityModelAccessor) biped).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",        getEntry(bodyParts.get(0)));
            vanillaPartsList.put("right_arm",   getEntry(bodyParts.get(1)));
            vanillaPartsList.put("left_arm",    getEntry(bodyParts.get(2)));
            vanillaPartsList.put("right_leg",   getEntry(bodyParts.get(3)));
            vanillaPartsList.put("left_leg",    getEntry(bodyParts.get(4)));
            vanillaPartsList.put("headwear",    getEntry(bodyParts.get(5)));
        }
        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getVillagerMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = new HashMap<>();
        if(vanillaModel instanceof VillagerResemblingModel villager){
            ModelPart villagerRoot = villager.getPart();
            vanillaPartsList.put("head",        getEntry(villagerRoot.getChild("head")));
            vanillaPartsList.put("headwear",    getEntry(villagerRoot.getChild("head").getChild("hat"), "head"));
            vanillaPartsList.put("headwear2",   getEntry(villagerRoot.getChild("head").getChild("hat").getChild("hat_rim"), "hat"));
            vanillaPartsList.put("body",        getEntry(villagerRoot.getChild("body")));
            vanillaPartsList.put("bodywear",    getEntry(villagerRoot.getChild("body").getChild("jacket"), "body"));
            vanillaPartsList.put("arms",        getEntry(villagerRoot.getChild("arms")));
            vanillaPartsList.put("left_leg",    getEntry(villagerRoot.getChild("left_leg")));
            vanillaPartsList.put("right_leg",   getEntry(villagerRoot.getChild("right_leg")));
            vanillaPartsList.put("nose",        getEntry(villagerRoot.getChild("head").getChild("nose"), "head"));
        }
        return vanillaPartsList;
    }


    public interface VanillaMapper {

        HashMap<String, ModelAndParent> getVanillaModelPartsMapFromModel( EntityModel<?> vanillaModel);

    }

    private static ModelAndParent getEntry(@NotNull ModelPart part, @Nullable String parentName){
        return new ModelAndParent(part, parentName);
    }
    private static ModelAndParent getEntry(@NotNull ModelPart part){
        return new ModelAndParent(part, null);
    }

    public record ModelAndParent(@NotNull ModelPart part, @Nullable String parentName){//<part extends ModelPart,str extends String>() {

        @Override
        public String toString() {
            return "ModelAndParent{" +
                    "part=" + part +
                    ", parentName='" + parentName + '\'' +
                    '}';
        }
    }
}
