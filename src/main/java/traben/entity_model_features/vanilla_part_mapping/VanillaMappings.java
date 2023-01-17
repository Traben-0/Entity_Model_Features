package traben.entity_model_features.vanilla_part_mapping;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import traben.entity_model_features.mixin.accessor.*;

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
    private static HashMap<String, ModelPart> getEmptyMap(EntityModel<?> vanillaModel){
        //todo mod entity integreation for part names
        System.out.println("empty model mapped for: "+ vanillaModel.getClass().toString());
        return new HashMap<>();
    }
    //this ones a bit special it automatically traverses a single part model capturing the vanilla part names
    // this automatically correctly captures allay model part and names for example
    private static HashMap<String, ModelPart> getSinglePartModelMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof SinglePartEntityModel model) {
            ModelPart root = model.getPart();
            Map<String,ModelPart> sourceChildren = ((ModelPartAccessor)root).getChildren();
            vanillaPartsList.putAll(iterateOverRootPartsChildren(sourceChildren));

        }
        return vanillaPartsList;
    }

    private static Map<String, ModelPart> getAllRootPartsChildren(ModelPart root){
        Map<String,ModelPart> sourceChildren = ((ModelPartAccessor)root).getChildren();
        return iterateOverRootPartsChildren(sourceChildren);
    }
    private static Map<String, ModelPart> iterateOverRootPartsChildren(Map<String, ModelPart> sourceChildren){
        Map<String,ModelPart> vanillaPartsList = new HashMap<>();
        for (Map.Entry<String, ModelPart> entry:
             sourceChildren.entrySet()) {
            vanillaPartsList.put(entry.getKey(), entry.getValue());
            Map<String,ModelPart> nextChildren = ((ModelPartAccessor)entry.getValue()).getChildren();
            vanillaPartsList.putAll(iterateOverRootPartsChildren(nextChildren));
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









    private static HashMap<String, ModelPart> getMagmaCubeMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = getSinglePartModelMap(vanillaModel);
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

    private static HashMap<String, ModelPart> getLlamaMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof LlamaEntityModel llama) {
            vanillaPartsList.put("head",((LlamaEntityModelAccessor)llama).getHead());
            vanillaPartsList.put("body",((LlamaEntityModelAccessor)llama).getBody());
            vanillaPartsList.put("leg1",((LlamaEntityModelAccessor)llama).getRightHindLeg());
            vanillaPartsList.put("leg2",((LlamaEntityModelAccessor)llama).getLeftHindLeg());
            vanillaPartsList.put("leg3",((LlamaEntityModelAccessor)llama).getRightFrontLeg());
            vanillaPartsList.put("leg4",((LlamaEntityModelAccessor)llama).getLeftFrontLeg());
            vanillaPartsList.put("chest_right",((LlamaEntityModelAccessor)llama).getRightChest());
            vanillaPartsList.put("chest_left",((LlamaEntityModelAccessor)llama).getLeftChest());
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getFoxMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof FoxEntityModel fox) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) fox).callGetHeadParts();
            if(hed.iterator().hasNext()) {
                vanillaPartsList.put("head", hed.iterator().next());
            }
            for (ModelPart part : ((AnimalModelAccessor) fox).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",bodyParts.get(0));
            vanillaPartsList.put("leg1",bodyParts.get(1));
            vanillaPartsList.put("leg2",bodyParts.get(2));
            vanillaPartsList.put("leg3",bodyParts.get(3));
            vanillaPartsList.put("leg4",bodyParts.get(4));
            vanillaPartsList.put("tail",((FoxEntityModelAccessor)fox).getTail());


        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getGuardianMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = getSinglePartModelMap(vanillaModel);
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

    private static HashMap<String, ModelPart> getHorseMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof AnimalModel horse) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) horse).callGetHeadParts();
            ModelPart neck = null;
            if(hed.iterator().hasNext()) {
                 neck =hed.iterator().next();
                    vanillaPartsList.put("neck", neck);
            }
            if(neck!= null) {
               // Map<String, ModelPart> head_parts = ((ModelPartAccessor) neck).getChildren();

                if (neck.hasChild("head")) {
                    ModelPart head = neck.getChild("head");
                    vanillaPartsList.put("head", head);
                    Map<String, ModelPart> headSub_parts = ((ModelPartAccessor) head).getChildren();
                    if(headSub_parts.containsKey("left_ear"))
                        vanillaPartsList.put("left_ear", headSub_parts.get("left_ear"));
                    if(headSub_parts.containsKey("right_ear"))
                        vanillaPartsList.put("right_ear", headSub_parts.get("right_ear"));
                }
                if (neck.hasChild("mane"))
                    vanillaPartsList.put("mane",neck.getChild("mane"));
                if (neck.hasChild("mouth"))
                    vanillaPartsList.put("mouth",neck.getChild("mouth"));

                if (neck.hasChild("head_saddle"))
                    vanillaPartsList.put("headpiece",neck.getChild("head_saddle"));
                if (neck.hasChild("mouth_saddle_wrap"))
                    vanillaPartsList.put("nose_band",neck.getChild("mouth_saddle_wrap"));

                if (neck.hasChild("left_saddle_mouth"))
                    vanillaPartsList.put("left_bit",neck.getChild("left_saddle_mouth"));
                if (neck.hasChild("right_saddle_mouth"))
                    vanillaPartsList.put("right_bit",neck.getChild("right_saddle_mouth"));
                if (neck.hasChild("left_saddle_line"))
                    vanillaPartsList.put("left_rein",neck.getChild("left_saddle_line"));
                if (neck.hasChild("right_saddle_line"))
                    vanillaPartsList.put("right_rein",neck.getChild("right_saddle_line"));

                for (ModelPart part : ((AnimalModelAccessor) horse).callGetBodyParts()) {
                    bodyParts.add(part);
                }
                //Map<String, ModelPart> body_parts = ((ModelPartAccessor) bodyParts.get(0)).getChildren();
                if (bodyParts.get(0).hasChild("tail"))
                    vanillaPartsList.put("tail",bodyParts.get(0).getChild("tail"));
                if (bodyParts.get(0).hasChild("saddle"))
                    vanillaPartsList.put("saddle",bodyParts.get(0).getChild("saddle"));


                vanillaPartsList.put("body", bodyParts.get(0));
                vanillaPartsList.put("back_right_leg", bodyParts.get(1));
                vanillaPartsList.put("back_left_leg", bodyParts.get(2));
                vanillaPartsList.put("front_right_leg", bodyParts.get(3));
                vanillaPartsList.put("front_left_leg", bodyParts.get(4));

                vanillaPartsList.put("child_back_right_leg", bodyParts.get(5));
                vanillaPartsList.put("child_back_left_leg", bodyParts.get(6));
                vanillaPartsList.put("child_front_right_leg", bodyParts.get(7));
                vanillaPartsList.put("child_front_left_leg", bodyParts.get(8));

            }
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getCreeperMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("leg1",vanillaPartsList.get("right_hind_leg"));
        vanillaPartsList.put("leg2",vanillaPartsList.get("left_hind_leg"));
        vanillaPartsList.put("leg3",vanillaPartsList.get("right_front_leg"));
        vanillaPartsList.put("leg4",vanillaPartsList.get("left_front_leg"));
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getBlazeMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = getSinglePartModelMap(vanillaModel);
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
    private static HashMap<String, ModelPart> getBeeMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof BeeEntityModel bee) {
            ModelPart root = ((AnimalModelAccessor)bee).callGetBodyParts().iterator().next();
            vanillaPartsList.putAll(getAllRootPartsChildren(root));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getBatMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("outer_right_wing",vanillaPartsList.get("right_wing_tip"));
        vanillaPartsList.put("outer_left_wing",vanillaPartsList.get("left_wing_tip"));
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getWitchMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof WitchEntityModel witch) {
            ModelPart root = witch.getPart();
            vanillaPartsList.put("head",root.getChild("head"));
            vanillaPartsList.put("headwear",root.getChild("head").getChild("hat"));
            vanillaPartsList.put("headwear2",root.getChild("head").getChild("hat").getChild("hat_rim"));
            vanillaPartsList.put("body",root.getChild("body"));
            vanillaPartsList.put("bodywear",root.getChild("body").getChild("jacket"));
            vanillaPartsList.put("arms",root.getChild("arms"));
            vanillaPartsList.put("left_leg",root.getChild("left_leg"));
            vanillaPartsList.put("right_leg",root.getChild("right_leg"));
            vanillaPartsList.put("nose",root.getChild("head").getChild("nose"));
            vanillaPartsList.put("mole",root.getChild("head").getChild("nose").getChild("mole"));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getSpiderMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof SpiderEntityModel spider) {
            ModelPart root = spider.getPart();
            vanillaPartsList.put("head",root.getChild("head"));
            vanillaPartsList.put("neck",root.getChild("body0"));
            vanillaPartsList.put("body",root.getChild("body1"));
            vanillaPartsList.put("leg1",root.getChild("right_hind_leg"));
            vanillaPartsList.put("leg2",root.getChild("left_hind_leg"));
            vanillaPartsList.put("leg3",root.getChild("right_middle_hind_leg"));
            vanillaPartsList.put("leg4",root.getChild("left_middle_hind_leg"));
            vanillaPartsList.put("leg5",root.getChild("right_middle_front_leg"));
            vanillaPartsList.put("leg6",root.getChild("left_middle_front_leg"));
            vanillaPartsList.put("leg7",root.getChild("right_front_leg"));
            vanillaPartsList.put("leg8",root.getChild("left_front_leg"));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getIronGolemMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof IronGolemEntityModel iron) {
            ModelPart root = iron.getPart();
            vanillaPartsList.put("head",root.getChild("head"));
            vanillaPartsList.put("body",root.getChild("body"));
            vanillaPartsList.put("right_arm",root.getChild("right_arm"));
            vanillaPartsList.put("left_arm",root.getChild("left_arm"));
            vanillaPartsList.put("right_leg",root.getChild("right_leg"));
            vanillaPartsList.put("left_leg",root.getChild("left_leg"));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getAxolotlMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof AxolotlEntityModel axol) {
            ModelPart body = ((AnimalModelAccessor)axol).callGetBodyParts().iterator().next();
            ModelPart head = body.getChild("head");
            vanillaPartsList.put("head",head);
            vanillaPartsList.put("body",body);
            vanillaPartsList.put("leg1",body.getChild("right_hind_leg"));
            vanillaPartsList.put("leg2",body.getChild("left_hind_leg"));
            vanillaPartsList.put("leg3",body.getChild("right_front_leg"));
            vanillaPartsList.put("leg4",body.getChild("left_front_leg"));
            vanillaPartsList.put("tail",body.getChild("tail"));
            vanillaPartsList.put("top_gills",head.getChild("top_gills"));
            vanillaPartsList.put("left_gills",head.getChild("left_gills"));
            vanillaPartsList.put("right_gills",head.getChild("right_gills"));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getGenericQuadrapedMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof QuadrupedEntityModel quadped) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((QuadrupedEntityModelAccessor) quadped).callGetHeadParts();
            if(hed.iterator().hasNext()) {
                vanillaPartsList.put("head", hed.iterator().next());
            }
            for (ModelPart part : ((QuadrupedEntityModelAccessor) quadped).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",bodyParts.get(0));
            vanillaPartsList.put("leg1",bodyParts.get(1));
            vanillaPartsList.put("leg2",bodyParts.get(2));
            vanillaPartsList.put("leg3",bodyParts.get(3));
            vanillaPartsList.put("leg4",bodyParts.get(4));


        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getGenericOcelotMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof OcelotEntityModel animal) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) animal).callGetHeadParts();
            if(hed.iterator().hasNext()) {
                vanillaPartsList.put("head", hed.iterator().next());
            }
            for (ModelPart part : ((AnimalModelAccessor) animal).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",bodyParts.get(0));
            vanillaPartsList.put("back_left_leg",bodyParts.get(1));
            vanillaPartsList.put("back_right_leg",bodyParts.get(2));
            vanillaPartsList.put("front_left_leg",bodyParts.get(3));
            vanillaPartsList.put("front_right_leg",bodyParts.get(4));
            vanillaPartsList.put("tail",bodyParts.get(5));
            vanillaPartsList.put("tail2",bodyParts.get(6));


        }
        return vanillaPartsList;
    }

    private static HashMap<String, ModelPart> getChickenMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof ChickenEntityModel animal) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) animal).callGetHeadParts();
            //if(hed.iterator().hasNext()) {
            vanillaPartsList.put("head", hed.iterator().next());
            vanillaPartsList.put("bill", hed.iterator().next());
            vanillaPartsList.put("chin", hed.iterator().next());
            //}
            for (ModelPart part : ((AnimalModelAccessor) animal).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",bodyParts.get(0));
            vanillaPartsList.put("right_leg",bodyParts.get(1));
            vanillaPartsList.put("left_leg",bodyParts.get(2));
            vanillaPartsList.put("right_wing",bodyParts.get(3));
            vanillaPartsList.put("left_wing",bodyParts.get(4));


        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getGenericAnimalMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof AnimalModel animal) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) animal).callGetHeadParts();
            if(hed.iterator().hasNext()) {
                vanillaPartsList.put("head", hed.iterator().next());
            }
            for (ModelPart part : ((AnimalModelAccessor) animal).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",bodyParts.get(0));
            vanillaPartsList.put("leg1",bodyParts.get(1));
            vanillaPartsList.put("leg2",bodyParts.get(2));
            vanillaPartsList.put("leg3",bodyParts.get(3));
            vanillaPartsList.put("leg4",bodyParts.get(4));


        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getArmorStandMap(EntityModel<?> vanillaModel){

        //get biped parts
        HashMap<String,ModelPart> vanillaPartsList = getGenericBipedMap(vanillaModel);

        if(vanillaModel instanceof ArmorStandEntityModel armor){
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            vanillaPartsList.put("head",((ModelWithHead)armor).getHead());
//            for (ModelPart part : ((BipedEntityModelAccessor) biped).callGetHeadParts()) {
//                vanillaPartsList.put("head",part);
//                break;
//            }
            //ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
            for (ModelPart part : ((BipedEntityModelAccessor) armor).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",bodyParts.get(0));
            vanillaPartsList.put("right_arm",bodyParts.get(1));
            vanillaPartsList.put("left_arm",bodyParts.get(2));
            vanillaPartsList.put("right_leg",bodyParts.get(3));
            vanillaPartsList.put("left_leg",bodyParts.get(4));
            vanillaPartsList.put("headwear",bodyParts.get(5));
            vanillaPartsList.put("right",bodyParts.get(6));
            vanillaPartsList.put("left",bodyParts.get(7));
            vanillaPartsList.put("waist",bodyParts.get(8));//todo might be wrong part
            vanillaPartsList.put("base",bodyParts.get(9));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getGenericBipedMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if(vanillaModel instanceof BipedEntityModel biped){
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            vanillaPartsList.put("head",((ModelWithHead)biped).getHead());
//            for (ModelPart part : ((BipedEntityModelAccessor) biped).callGetHeadParts()) {
//                vanillaPartsList.put("head",part);
//                break;
//            }
            //ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
            for (ModelPart part : ((BipedEntityModelAccessor) biped).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",bodyParts.get(0));
            vanillaPartsList.put("right_arm",bodyParts.get(1));
            vanillaPartsList.put("left_arm",bodyParts.get(2));
            vanillaPartsList.put("right_leg",bodyParts.get(3));
            vanillaPartsList.put("left_leg",bodyParts.get(4));
            vanillaPartsList.put("headwear",bodyParts.get(5));
        }
        return vanillaPartsList;
    }
    private static HashMap<String, ModelPart> getVillagerMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();
        if(vanillaModel instanceof VillagerResemblingModel villager){
            ModelPart villagerRoot = villager.getPart();
            vanillaPartsList.put("head",villagerRoot.getChild("head"));
            vanillaPartsList.put("headwear",villagerRoot.getChild("head").getChild("hat"));
            vanillaPartsList.put("headwear2",villagerRoot.getChild("head").getChild("hat").getChild("hat_rim"));
            vanillaPartsList.put("body",villagerRoot.getChild("body"));
            vanillaPartsList.put("bodywear",villagerRoot.getChild("body").getChild("jacket"));
            vanillaPartsList.put("arms",villagerRoot.getChild("arms"));
            vanillaPartsList.put("left_leg",villagerRoot.getChild("left_leg"));
            vanillaPartsList.put("right_leg",villagerRoot.getChild("right_leg"));
            vanillaPartsList.put("nose",villagerRoot.getChild("head").getChild("nose"));
        }
        return vanillaPartsList;
    }


    public interface VanillaMapper {

        HashMap<String, ModelPart> getVanillaModelPartsMapFromModel( EntityModel<?> vanillaModel);

    }
}
