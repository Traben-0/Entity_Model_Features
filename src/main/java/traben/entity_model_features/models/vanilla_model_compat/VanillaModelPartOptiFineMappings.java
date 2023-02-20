package traben.entity_model_features.models.vanilla_model_compat;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;
import traben.entity_model_features.mixin.accessor.entity.model.*;
import traben.entity_model_features.utils.EMFUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/*
* this class provides static methods to acquire 'maps' of the vanilla model parts
* a "vanilla part map" refers to a Map<String, record<ModelPart,String>> with the key being the part name as specified
* in the optifine documentation, and the record containing the vanilla model part and the string name of it's vanilla parent part
*
* EMF handles this externally from the model itself to:
*  - allow future mod supplied mappings to quite easily slip in here
*  - to keep the EMF model object as generic as possible
*  - to cache already processed map suppliers for model variants
*
* This class should only ever process once per entity type per resource load
*  the result is cached for any future call needed for model variants / etc
 */
public class VanillaModelPartOptiFineMappings {


    VanillaModelPartOptiFineMappings() {
        //todo api for modded mappings that for some reason dont extend single part entity model
    }


    public static VanillaMapper getVanillaModelPartsMapSupplier(int entityTypeHash, EntityModel<?> vanillaModel) {
        if (!EMFData.getInstance().vanilla_mappings_map.containsKey(entityTypeHash)) {
            EMFData.getInstance().vanilla_mappings_map.put(entityTypeHash, discoverMappingToUse(vanillaModel));
        }
        return EMFData.getInstance().vanilla_mappings_map.get(entityTypeHash);
    }


    private static VanillaMapper discoverMappingToUse(EntityModel<?> vanillaModel) {


        if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
            EMFUtils.EMF_modMessage("getting vanilla model map...");
        //if an instance check starts with '✅' it means it is a single part entity that maps correctly to the optifine name format

        //todo needs to extend to all vanilla mobs and confirm OptiFine part name mappings

// ✅ allay                    head, body, left_arm, right_arm, left_wing, right_wing
// armor_stand              head, headwear, body, left_arm, right_arm, left_leg, right_leg, right, left, waist, base
        if (vanillaModel instanceof ArmorStandEntityModel) {
            return VanillaModelPartOptiFineMappings::getArmorStandMap;
        }
// axolotl                  head, body, leg1 ... leg4, tail, top_gills, left_gills, right_gills
        if (vanillaModel instanceof AxolotlEntityModel) {
            return VanillaModelPartOptiFineMappings::getAxolotlMap;
        }
//todo banner                   slate, stand, top

// bat                      head, body, right_wing, left_wing, outer_right_wing, outer_left_wing
        if (vanillaModel instanceof BatEntityModel) {
            return VanillaModelPartOptiFineMappings::getBatMap;
        }
// bee                      body, torso, right_wing, left_wing, front_legs, middle_legs, back_legs, stinger, left_antenna, right_antenna
        if (vanillaModel instanceof BeeEntityModel) {
            return VanillaModelPartOptiFineMappings::getBeeMap;
        }
//todo bed                      head, foot, leg1 ... leg4
// bell                     body

// blaze                    head, stick1 ... stick12
        if (vanillaModel instanceof BlazeEntityModel) {
            return VanillaModelPartOptiFineMappings::getBlazeMap;
        }
//todo boat                     bottom, back, front, right, left, paddle_left, paddle_right, bottom_no_water

        //camel unknown
        if (vanillaModel instanceof CamelEntityModel) {
            return VanillaModelPartOptiFineMappings::getSinglePartModelMap;
        }
// ✅ cat                      back_left_leg, back_right_leg, front_left_leg, front_right_leg, tail, tail2, head, body
// ✅ cat_collar               back_left_leg, back_right_leg, front_left_leg, front_right_leg, tail, tail2, head, body
// ✅ cave_spider              head, neck, body, leg1 ... leg8
//todo chest                    lid, base, knob
// chest_boat               bottom, back, front, right, left, paddle_left, paddle_right, bottom_no_water, chest_base, chest_lid, chest_knob
// chest_large              lid_left, base_left, knob_left, lid_right, base_right, knob_right
// chest_minecart           bottom, back, front, right, left

// chicken                  head, body, right_leg, left_leg, right_wing, left_wing, bill, chin
        if (vanillaModel instanceof ChickenEntityModel) {
            return VanillaModelPartOptiFineMappings::getChickenMap;
        }
// cod                      body, fin_back, head, nose, fin_right, fin_left, tail
        if (vanillaModel instanceof CodEntityModel) {
            return VanillaModelPartOptiFineMappings::getCodMap;
        }
//todo command_block_minecart   bottom, back, front, right, left
// conduit                  base, eye, cage, wind

// ✅  cow                      head, body, leg1 ... leg4
// ✅ creeper                  head, body, leg1 ... leg4
// creeper_charge           head, body, leg1 ... leg4
        if (vanillaModel instanceof CreeperEntityModel) {
            return VanillaModelPartOptiFineMappings::getCreeperMap;
        }
//todo dragon                   head, spine, jaw, body, left_wing, left_wing_tip, right_wing, right_wing_tip,
//                          front_left_leg, front_left_shin, front_left_foot, back_left_leg, back_left_shin, back_left_foot,
//                          front_right_leg, front_right_shin, front_right_foot, back_right_leg, back_right_shin, back_right_foot

// donkey                   <same as horse>, left_chest, right_chest
        if (vanillaModel instanceof DonkeyEntityModel) {
            return VanillaModelPartOptiFineMappings::getDonkeyMap;
        }
// ✅ dolphin                  body, back_fin, left_fin, right_fin, tail, tail_fin, head

// ✅ drowned                  head, headwear, body, left_arm, right_arm, left_leg, right_leg
// ✅ drowned_outer            head, headwear, body, left_arm, right_arm, left_leg, right_leg
// ✅ elder_guardian           body, eye, spine1 ... spine12, tail1 ... tail3

//todo enchanting_book          cover_right, cover_left, pages_right, pages_left, flipping_page_right, flipping_page_left, book_spine
// ender_chest              lid, base, knob
// end_crystal              cube, glass, base

// ✅ enderman                 head, headwear, body, left_arm, right_arm, left_leg, right_leg

// endermite                body1 ... body4
        if (vanillaModel instanceof EndermiteEntityModel) {
            return VanillaModelPartOptiFineMappings::getEndermiteMap;
        }
// ✅ evoker                   head, hat, body, arms, left_leg, right_leg, nose, left_arm, right_arm
// ✅ evoker_fangs             base, upper_jaw, lower_jaw
// fox                      head, body, leg1 ... leg4, tail
        if (vanillaModel instanceof FoxEntityModel) {
            return VanillaModelPartOptiFineMappings::getFoxMap;
        }
// ✅ frog                     head, body, eyes, tongue, left_arm, right_arm, left_leg, right_leg, croaking_body

//todo furnace_minecart         bottom, back, front, right, left

// ghast                    body, tentacle1 ... tentacle9
        if (vanillaModel instanceof GhastEntityModel) {
            return VanillaModelPartOptiFineMappings::getGhastMap;
        }
// ✅ giant                    head, headwear, body, left_arm, right_arm, left_leg, right_leg
// glow_squid               body, tentacle1 ... tentacle8
        if (vanillaModel instanceof SquidEntityModel) {
            return VanillaModelPartOptiFineMappings::getSquidMap;
        }
// goat                     head, body, leg1 ... leg4, left_horn, right_horn, nose
        if (vanillaModel instanceof GoatEntityModel) {
            return VanillaModelPartOptiFineMappings::getGoatMap;
        }
// guardian                 body, eye, spine1 ... spine12, tail1 ... tail3
        if (vanillaModel instanceof GuardianEntityModel) {
            return VanillaModelPartOptiFineMappings::getGuardianMap;
        }
//todo head_dragon              head, jaw
// head_player              head
// head_skeleton            head
// head_wither_skeleton     head
// head_zombie              head

// hoglin                   head, right_ear, left_ear, body, front_right_leg, front_left_leg, back_right_leg, back_left_leg, mane
        if (vanillaModel instanceof HoglinEntityModel) {
            return VanillaModelPartOptiFineMappings::getHoglinMap;
        }
//todo hopper_minecart          bottom, back, front, right, left

// ✅ horse                    body, neck, back_left_leg, back_right_leg, front_left_leg, front_right_leg, tail, saddle,
//                          head, mane, mouth, left_ear, right_ear, left_bit, right_bit, left_rein, right_rein, headpiece, noseband,
//                          child_back_left_leg, child_back_right_leg, child_front_left_leg, child_front_right_leg
// ✅ horse_armor              body, neck, back_left_leg, back_right_leg, front_left_leg, front_right_leg, tail, saddle,
//                          head, mane, mouth, left_ear, right_ear, left_bit, right_bit, left_rein, right_rein, headpiece, noseband,
//                          child_back_left_leg, child_back_right_leg, child_front_left_leg, child_front_right_leg
        if (vanillaModel instanceof HorseEntityModel) {
            return VanillaModelPartOptiFineMappings::getHorseMap;
        }
// ✅ husk                     head, headwear, body, left_arm, right_arm, left_leg, right_leg
// ✅ illusioner               head, hat, body, arms, left_leg, right_leg, nose, left_arm, right_arm

// iron_golem               head, body, left_arm, right_arm, left_leg, right_leg
        if (vanillaModel instanceof IronGolemEntityModel) {
            return VanillaModelPartOptiFineMappings::getIronGolemMap;
        }
//todo lead_knot                knot
// lectern_book             cover_right, cover_left, pages_right, pages_left, flipping_page_right, flipping_page_left, book_spine

// ✅ llama                    head, body, leg1 ... leg4, chest_right, chest_left
// ✅ llama_decor              head, body, leg1 ... leg4, chest_right, chest_left
        if (vanillaModel instanceof LlamaEntityModel) {
            return VanillaModelPartOptiFineMappings::getLlamaMap;
        }
// llama_spit               body
        if (vanillaModel instanceof LlamaSpitEntityModel) {
            return VanillaModelPartOptiFineMappings::getllamaSpitMap;
        }
// magma_cube               core, segment1 ... segment8
        if (vanillaModel instanceof MagmaCubeEntityModel) {
            return VanillaModelPartOptiFineMappings::getMagmaCubeMap;
        }
//todo minecart                 bottom, back, front, right, left

// ✅ mooshroom                head, body, leg1 ... leg4
// ✅ mule                     <same as horse>, left_chest, right_chest
// ocelot                   back_left_leg, back_right_leg, front_left_leg, front_right_leg, tail, tail2, head, body
        if (vanillaModel instanceof OcelotEntityModel) {
            return VanillaModelPartOptiFineMappings::getGenericOcelotMap;
        }
// ✅ panda                    head, body, leg1 ... leg4
// ✅ parrot                   head, body, tail, left_wing, right_wing, left_leg, right_leg
// phantom                  body, left_wing, left_wing_tip, right_wing, right_wing_tip, head, tail, tail2
        if (vanillaModel instanceof PhantomEntityModel) {
            return VanillaModelPartOptiFineMappings::getPhantomMap;
        }
// puffer_fish_big          body, fin_right, fin_left, spikes_front_top, spikes_middle_top, spikes_back_top, spikes_front_right, spikes_front_left,
//                          spikes_front_bottom, spikes_middle_bottom, spikes_back_bottom, spikes_back_right, spikes_back_left
        if (vanillaModel instanceof LargePufferfishEntityModel) {
            return VanillaModelPartOptiFineMappings::getPufferMapLarge;
        }
// puffer_fish_medium       body, fin_right, fin_left, spikes_front_top, spikes_back_top, spikes_front_right, spikes_back_right, spikes_back_left,
//                          spikes_front_left, spikes_back_bottom, spikes_front_bottom
        if (vanillaModel instanceof MediumPufferfishEntityModel) {
            return VanillaModelPartOptiFineMappings::getPufferMapMed;
        }
// puffer_fish_small        body, eye_right, eye_left, tail, fin_right, fin_left
        if (vanillaModel instanceof SmallPufferfishEntityModel) {
            return VanillaModelPartOptiFineMappings::getPufferMapSmall;
        }
// ✅ pig                      head, body, leg1 ... leg4
// ✅ pig_saddle               head, body, leg1 ... leg4
// ✅ piglin                   head, headwear, body, left_arm, right_arm, left_leg, right_leg, left_ear, right_ear, left_sleeve, right_sleeve, left_pants, right_pants, jacket
// piglin_brute             head, headwear, body, left_arm, right_arm, left_leg, right_leg, left_ear, right_ear, left_sleeve, right_sleeve, left_pants, right_pants, jacket
        if (vanillaModel instanceof PiglinEntityModel) {
            return VanillaModelPartOptiFineMappings::getPiglinMap;
        }
// ✅ pillager                 head, hat, body, arms, left_leg, right_leg, nose, left_arm, right_arm

// ✅ polar_bear               head, body, leg1 ... leg4
// rabbit                   left_foot, right_foot, left_thigh, right_thigh, body, left_arm, right_arm, head, right_ear, left_ear, tail, nose
        if (vanillaModel instanceof RabbitEntityModel) {
            return VanillaModelPartOptiFineMappings::getRabbitMap;
        }
// ravager                  head, jaw, body, leg1 ... leg4, neck
        if (vanillaModel instanceof RavagerEntityModel) {
            return VanillaModelPartOptiFineMappings::getRavagerMap;
        }
// salmon                   body_front, body_back, head, fin_back_1, fin_back_2, tail, fin_right, fin_left
        if (vanillaModel instanceof SalmonEntityModel) {
            return VanillaModelPartOptiFineMappings::getSalmonMap;
        }
// ✅ sheep                    head, body, leg1 ... leg4
// ✅ sheep_wool               head, body, leg1 ... leg4
// ✅ shulker                  head, base, lid

//todo shulker_box              base, lid
// shulker_bullet           bullet
// sign                     board, stick

// silverfish               body1 ... body7, wing1 ... wing3
        if (vanillaModel instanceof SilverfishEntityModel) {
            return VanillaModelPartOptiFineMappings::getSilverfishMap;
        }
// ✅ skeleton                 head, headwear, body, left_arm, right_arm, left_leg, right_leg
// ✅ skeleton_horse           <same as horse>

// ✅ slime                    body, left_eye, right_eye, mouth
// ✅ slime_outer              body, left_eye, right_eye, mouth
        if (vanillaModel instanceof SlimeEntityModel) {
            return VanillaModelPartOptiFineMappings::getSlimeMap;
        }

// ✅ snow_golem               body, body_bottom, head, left_hand, right_hand
        if (vanillaModel instanceof SnowGolemEntityModel) {
            return VanillaModelPartOptiFineMappings::getSnowGolemMap;
        }
//todo spawner_minecart         bottom, back, front, right, left

// spider                   head, neck, body, leg1, ... leg8
        if (vanillaModel instanceof SpiderEntityModel) {
            return VanillaModelPartOptiFineMappings::getSpiderMap;
        }
// ✅ squid                    body, tentacle1 ... tentacle8
// ✅ stray                    head, headwear, body, left_arm, right_arm, left_leg, right_leg
// ✅ stray_outer              head, headwear, body, left_arm, right_arm, left_leg, right_leg
// strider                  body, right_leg, left_leg, hair_right_top, hair_right_middle, hair_right_bottom, hair_left_top, hair_left_middle, hair_left_bottom
// strider_saddle           body, right_leg, left_leg, hair_right_top, hair_right_middle, hair_right_bottom, hair_left_top, hair_left_middle, hair_left_bottom
        if (vanillaModel instanceof StriderEntityModel) {
            return VanillaModelPartOptiFineMappings::getStriderMap;
        }

//todo tnt_minecart             bottom, back, front, right, left

// tadpole                  body, tail
        if (vanillaModel instanceof TadpoleEntityModel) {
            return VanillaModelPartOptiFineMappings::getTadpoleMap;
        }
// ✅ trader_llama             head, body, leg1 ... leg4, chest_right, chest_left
// ✅ trader_llama_decor       head, body, leg1 ... leg4, chest_right, chest_left

//todo trapped_chest            lid, base, knob
// trapped_chest_large      lid_left, base_left, knob_left, lid_right, base_right, knob_right
// trident                  body

// tropical_fish_a          body, tail, fin_right, fin_left, fin_top
        if (vanillaModel instanceof SmallTropicalFishEntityModel) {
            return VanillaModelPartOptiFineMappings::getTropicalSmallMap;
        }
// tropical_fish_b          body, tail, fin_right, fin_left, fin_top, fin_bottom
        if (vanillaModel instanceof LargeTropicalFishEntityModel) {
            return VanillaModelPartOptiFineMappings::getTropicalLargeMap;
        }
// ✅ tropical_fish_pattern_a  body, tail, fin_right, fin_left, fin_top
// ✅ tropical_fish_pattern_b  body, tail, fin_right, fin_left, fin_top, fin_bottom

// turtle                   head, body, leg1 ... leg4, body2
        if (vanillaModel instanceof TurtleEntityModel) {
            return VanillaModelPartOptiFineMappings::getTurtleMap;
        }
//todo vex                      head, headwear, body, left_arm, right_arm, left_leg, right_leg, left_wing, right_wing

// ✅ villager                 head, headwear, headwear2, body, bodywear, arms, left_leg, right_leg, nose

// ✅ vindicator               head, hat, body, arms, left_leg, right_leg, nose, left_arm, right_arm
// ✅ wandering_trader         head, headwear, headwear2, body, bodywear, arms, left_leg, right_leg, nose
// warden                   body, torso, head, left_leg, right_leg, left_arm, right_arm, left_tendril, right_tendril, left_ribcage, right_ribcage
        if (vanillaModel instanceof WardenEntityModel) {
            return VanillaModelPartOptiFineMappings::getWardenMap;
        }
// ✅ witch                    head, headwear, headwear2, body, bodywear, arms, left_leg, right_leg, nose, mole
        if (vanillaModel instanceof WitchEntityModel) {
            return VanillaModelPartOptiFineMappings::getWitchMap;
        }
//todo wither                   body1 ... body3, head1 ... head3
// wither_armor             body1 ... body3, head1 ... head3

// ✅ wither_skeleton          head, headwear, body, left_arm, right_arm, left_leg, right_leg
//todo wither_skull             head

// ✅ wolf                     head, body, leg1 ... leg4, tail, mane
// ✅ wolf_collar              head, body, leg1 ... leg4, tail, mane
        if (vanillaModel instanceof WolfEntityModel) {
            return VanillaModelPartOptiFineMappings::getWolfMap;
        }
// ✅ zoglin                   head, right_ear, left_ear, body, front_right_leg, front_left_leg, back_right_leg, back_left_leg, mane
// ✅ zombie                   head, headwear, body, left_arm, right_arm, left_leg, right_leg
// ✅ zombie_horse             <same as horse>
//@Deprecated zombie_pigman            head, headwear, body, left_arm, right_arm, left_leg, right_leg

// ✅ zombie_villager          head, headwear, body, left_arm, right_arm, left_leg, right_leg
// ✅ zombified_piglin         head, headwear, body, left_arm, right_arm, left_leg, right_leg, left_ear, left_sleeve, right_sleeve, left_pants, right_pants, jacket



        //player
        if (vanillaModel instanceof PlayerEntityModel) {
            return VanillaModelPartOptiFineMappings::getGenericPlayerMap;
        }

        //all villager types
        if (vanillaModel instanceof VillagerResemblingModel) {
            return VanillaModelPartOptiFineMappings::getVillagerMap;
        }

        // single part entity model can catch a lot automatically
        if (vanillaModel instanceof SinglePartEntityModel) {
            return VanillaModelPartOptiFineMappings::getSinglePartModelMap;
        }

        //generics, these are intended to try catch some modded entities
        if (vanillaModel instanceof BipedEntityModel) {
            return VanillaModelPartOptiFineMappings::getGenericBipedMap;
        }
        if (vanillaModel instanceof QuadrupedEntityModel) {
            return VanillaModelPartOptiFineMappings::getGenericQuadrapedMap;
        }
        if (vanillaModel instanceof AnimalModel<?>) {
            return VanillaModelPartOptiFineMappings::getGenericAnimalMap;
        }


        //else simply go with no map, the model will still work fine but cannot rely on vanilla movements/animations
        return VanillaModelPartOptiFineMappings::getEmptyMap;
    }

    private static HashMap<String, ModelAndParent> getEmptyMap(EntityModel<?> vanillaModel) {

        System.out.println("empty model mapped for: " + vanillaModel.getClass().toString());
        return new HashMap<>();
    }

    //this ones a bit special it automatically traverses a single part model capturing the vanilla part names
    // this automatically correctly captures allay model part and names for example
    private static HashMap<String, ModelAndParent> getSinglePartModelMap(EntityModel<?> vanillaModel) {
        HashMap<String, ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof SinglePartEntityModel model) {
            ModelPart root = model.getPart();
            Map<String, ModelPart> sourceChildren = ((ModelPartAccessor) root).getChildren();
            vanillaPartsList.putAll(iterateOverRootPartsChildren(sourceChildren, null));

        }
        return vanillaPartsList;
    }

    private static Map<String, ModelAndParent> getAllRootPartsChildren(ModelPart root) {
        Map<String, ModelPart> sourceChildren = ((ModelPartAccessor) root).getChildren();
        return iterateOverRootPartsChildren(sourceChildren, null);
    }

    private static Map<String, ModelAndParent> iterateOverRootPartsChildren(Map<String, ModelPart> sourceChildren, String parentName) {
        Map<String, ModelAndParent> vanillaPartsList = new HashMap<>();
        for (Map.Entry<String, ModelPart> entry :
                sourceChildren.entrySet()) {
            vanillaPartsList.put(entry.getKey(), getEntry(entry.getValue(), parentName));
            Map<String, ModelPart> nextChildren = ((ModelPartAccessor) entry.getValue()).getChildren();
            vanillaPartsList.putAll(iterateOverRootPartsChildren(nextChildren, entry.getKey()));
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


    private static HashMap<String, ModelAndParent> getTadpoleMap(EntityModel<?> vanillaModel) {
        HashMap<String, ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof AnimalModel animal) {
            Iterable<ModelPart> bodyParts = ((AnimalModelAccessor) animal).callGetHeadParts();
            Iterable<ModelPart> hed = ((AnimalModelAccessor) animal).callGetHeadParts();
            if (hed.iterator().hasNext()) {
                vanillaPartsList.put("body", getEntry(hed.iterator().next()));
            }
            if (bodyParts.iterator().hasNext()) {
                vanillaPartsList.put("tail", getEntry(bodyParts.iterator().next(), "body"));
            }
        }
        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getSilverfishMap(EntityModel<?> vanillaModel) {
        //# silverfish               body1 ... body7, wing1 ... wing3
        HashMap<String, ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("segment0", vanillaPartsList.get("body1"));
        vanillaPartsList.put("segment1", vanillaPartsList.get("body2"));
        vanillaPartsList.put("segment2", vanillaPartsList.get("body3"));
        vanillaPartsList.put("segment3", vanillaPartsList.get("body4"));
        vanillaPartsList.put("segment4", vanillaPartsList.get("body5"));
        vanillaPartsList.put("segment5", vanillaPartsList.get("body6"));
        vanillaPartsList.put("segment6", vanillaPartsList.get("body7"));

        vanillaPartsList.put("layer0", vanillaPartsList.get("wing1"));
        vanillaPartsList.put("layer1", vanillaPartsList.get("wing2"));
        vanillaPartsList.put("layer2", vanillaPartsList.get("wing3"));

        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getWardenMap(EntityModel<?> vanillaModel) {
        //# warden
        // body, torso, head, left_leg, right_leg, left_arm, right_arm, left_tendril, right_tendril, left_ribcage, right_ribcage
        HashMap<String, ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("body",            getEntry( vanillaPartsList.get("body").part,"torso"));
        vanillaPartsList.put("torso",           getEntry( vanillaPartsList.get("bone").part));
        vanillaPartsList.put("left_leg",        getEntry( vanillaPartsList.get("left_leg").part,"torso"));
        vanillaPartsList.put("right_leg",       getEntry( vanillaPartsList.get("right_leg").part,"torso"));


        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getTurtleMap(EntityModel<?> vanillaModel) {
        //# turtle                   head, body, leg1 ... leg4, body2
        HashMap<String, ModelAndParent> vanillaPartsList = new HashMap<>();
        if (vanillaModel instanceof QuadrupedEntityModel<?> quadped) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((QuadrupedEntityModelAccessor) quadped).callGetHeadParts();
            if (hed.iterator().hasNext()) {
                vanillaPartsList.put("head", getEntry(hed.iterator().next()));
            }
            for (ModelPart part : ((QuadrupedEntityModelAccessor) quadped).callGetBodyParts()) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body", getEntry(bodyParts.get(0)));
            vanillaPartsList.put("leg1", getEntry(bodyParts.get(1)));
            vanillaPartsList.put("leg2", getEntry(bodyParts.get(2)));
            vanillaPartsList.put("leg3", getEntry(bodyParts.get(3)));
            vanillaPartsList.put("leg4", getEntry(bodyParts.get(4)));
            vanillaPartsList.put("body2", getEntry(bodyParts.get(5)));


        }
        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getStriderMap(EntityModel<?> vanillaModel){
        //# strider
        // body, right_leg, left_leg, hair_right_top, hair_right_middle, hair_right_bottom, hair_left_top, hair_left_middle, hair_left_bottom
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("hair_right_top",      vanillaPartsList.get("right_top_bristle"));
        vanillaPartsList.put("hair_right_middle",   vanillaPartsList.get("right_middle_bristle"));
        vanillaPartsList.put("hair_right_bottom",   vanillaPartsList.get("right_bottom_bristle"));
        vanillaPartsList.put("hair_left_top",       vanillaPartsList.get("left_top_bristle"));
        vanillaPartsList.put("hair_left_middle",    vanillaPartsList.get("left_middle_bristle"));
        vanillaPartsList.put("hair_left_bottom",    vanillaPartsList.get("left_bottom_bristle"));

        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getSnowGolemMap(EntityModel<?> vanillaModel){
        //# snow_golem               body, body_bottom, head, left_hand, right_hand
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("left_hand",vanillaPartsList.get("left_arm"));
        vanillaPartsList.put("right_hand",vanillaPartsList.get("right_arm"));
        vanillaPartsList.put("body",vanillaPartsList.get("upper_body"));
        vanillaPartsList.put("body_bottom",vanillaPartsList.get("lower_body"));

        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getTropicalSmallMap(EntityModel<?> vanillaModel){
        //# body, tail, fin_right, fin_left, fin_top
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("fin_right",vanillaPartsList.get("right_fin"));
        vanillaPartsList.put("fin_left",vanillaPartsList.get("left_fin"));
        vanillaPartsList.put("fin_top",vanillaPartsList.get("top_fin"));

        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getTropicalLargeMap(EntityModel<?> vanillaModel){
        //# body, tail, fin_right, fin_left, fin_top, fin_bottom
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("fin_right",vanillaPartsList.get("right_fin"));
        vanillaPartsList.put("fin_left",vanillaPartsList.get("left_fin"));
        vanillaPartsList.put("fin_top",vanillaPartsList.get("top_fin"));
        vanillaPartsList.put("fin_bottom",vanillaPartsList.get("bottom_fin"));

        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getCodMap(EntityModel<?> vanillaModel){
        //# cod                      body, fin_back, head, nose, fin_right, fin_left, tail
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("tail",vanillaPartsList.get("tail_fin"));
        vanillaPartsList.put("fin_back",vanillaPartsList.get("top_fin"));
        vanillaPartsList.put("right_fin",vanillaPartsList.get("fin_right"));
        vanillaPartsList.put("left_fin",vanillaPartsList.get("fin_left"));

        return vanillaPartsList;
    }
    // goat                     head, body, leg1 ... leg4, left_horn, right_horn, nose
    private static HashMap<String, ModelAndParent> getGoatMap(EntityModel<?> vanillaModel) {
        // goat                     head, body, leg1 ... leg4, left_horn, right_horn, nose
        HashMap<String, ModelAndParent> vanillaPartsList = getGenericQuadrapedMap(vanillaModel);
        ModelPart head = vanillaPartsList.get("head").part();
        vanillaPartsList.put("left_horn", getEntry( head.getChild("left_horn"),"head"));
        vanillaPartsList.put("right_horn", getEntry( head.getChild("right_horn"),"head"));
        vanillaPartsList.put("nose", getEntry( head.getChild("nose"),"head"));
        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getRabbitMap(EntityModel<?> vanillaModel) {
        // rabbit                   left_foot, right_foot, left_thigh, right_thigh, body, left_arm, right_arm, head, right_ear, left_ear, tail, nose
        HashMap<String, ModelAndParent> vanillaPartsList = new HashMap<>();
        if( vanillaModel instanceof RabbitEntityModel rabbit) {
            vanillaPartsList.put("left_foot", getEntry(((RabbitEntityModelAccessor) rabbit).getLeftHindLeg()));
            vanillaPartsList.put("right_foot", getEntry(((RabbitEntityModelAccessor) rabbit).getRightHindLeg()));
            vanillaPartsList.put("left_thigh", getEntry(((RabbitEntityModelAccessor) rabbit).getLeftHaunch()));
            vanillaPartsList.put("right_thigh", getEntry(((RabbitEntityModelAccessor) rabbit).getRightHaunch()));
            vanillaPartsList.put("body", getEntry(((RabbitEntityModelAccessor) rabbit).getBody()));
            vanillaPartsList.put("left_arm", getEntry(((RabbitEntityModelAccessor) rabbit).getLeftFrontLeg()));
            vanillaPartsList.put("right_arm", getEntry(((RabbitEntityModelAccessor) rabbit).getRightFrontLeg()));
            vanillaPartsList.put("head", getEntry(((RabbitEntityModelAccessor) rabbit).getHead()));
            vanillaPartsList.put("right_ear", getEntry(((RabbitEntityModelAccessor) rabbit).getRightEar()));
            vanillaPartsList.put("left_ear", getEntry(((RabbitEntityModelAccessor) rabbit).getLeftEar()));
            vanillaPartsList.put("tail", getEntry(((RabbitEntityModelAccessor) rabbit).getTail()));
            vanillaPartsList.put("nose", getEntry(((RabbitEntityModelAccessor) rabbit).getNose()));
        }
        return vanillaPartsList;
    }


    private static HashMap<String, ModelAndParent> getPufferMapLarge(EntityModel<?> vanillaModel) {
// puffer_fish_big          body, fin_right, fin_left, spikes_front_top, spikes_middle_top, spikes_back_top, spikes_front_right, spikes_front_left,
//                          spikes_front_bottom, spikes_middle_bottom, spikes_back_bottom, spikes_back_right, spikes_back_left
        HashMap<String, ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("fin_right", vanillaPartsList.get("right_blue_fin"));
        vanillaPartsList.put("fin_left", vanillaPartsList.get("left_blue_fin"));
        vanillaPartsList.put("spikes_front_top", vanillaPartsList.get("top_front_fin"));
        vanillaPartsList.put("spikes_middle_top", vanillaPartsList.get("top_middle_fin"));
        vanillaPartsList.put("spikes_back_top", vanillaPartsList.get("top_back_fin"));
        vanillaPartsList.put("spikes_front_right", vanillaPartsList.get("right_front_fin"));
        vanillaPartsList.put("spikes_front_left", vanillaPartsList.get("left_front_fin"));
        vanillaPartsList.put("spikes_front_bottom", vanillaPartsList.get("bottom_front_fin"));
        vanillaPartsList.put("spikes_middle_bottom", vanillaPartsList.get("bottom_middle_fin"));
        vanillaPartsList.put("spikes_back_bottom", vanillaPartsList.get("bottom_back_fin"));
        vanillaPartsList.put("spikes_back_right", vanillaPartsList.get("right_back_fin"));
        vanillaPartsList.put("spikes_back_left", vanillaPartsList.get("left_back_fin"));
        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getPufferMapMed(EntityModel<?> vanillaModel) {
// puffer_fish_medium       body, fin_right, fin_left, spikes_front_top, spikes_back_top, spikes_front_right, spikes_back_right, spikes_back_left,
//                          spikes_front_left, spikes_back_bottom, spikes_front_bottom

        HashMap<String, ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("fin_right", vanillaPartsList.get("right_blue_fin"));
        vanillaPartsList.put("fin_left", vanillaPartsList.get("left_blue_fin"));
        vanillaPartsList.put("spikes_front_top", vanillaPartsList.get("top_front_fin"));
        vanillaPartsList.put("spikes_back_top", vanillaPartsList.get("top_back_fin"));
        vanillaPartsList.put("spikes_front_right", vanillaPartsList.get("right_front_fin"));
        vanillaPartsList.put("spikes_front_left", vanillaPartsList.get("left_front_fin"));
        vanillaPartsList.put("spikes_front_bottom", vanillaPartsList.get("bottom_front_fin"));
        vanillaPartsList.put("spikes_back_bottom", vanillaPartsList.get("bottom_back_fin"));
        vanillaPartsList.put("spikes_back_right", vanillaPartsList.get("right_back_fin"));
        vanillaPartsList.put("spikes_back_left", vanillaPartsList.get("left_back_fin"));
        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getPufferMapSmall(EntityModel<?> vanillaModel) {
// puffer_fish_small        body, eye_right, eye_left, tail, fin_right, fin_left
        HashMap<String, ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("eye_right", vanillaPartsList.get("right_eye"));
        vanillaPartsList.put("eye_left", vanillaPartsList.get("left_eye"));
        vanillaPartsList.put("tail", vanillaPartsList.get("back_fin"));
        vanillaPartsList.put("fin_right", vanillaPartsList.get("right_fin"));
        vanillaPartsList.put("fin_left", vanillaPartsList.get("left_fin"));
        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getPhantomMap(EntityModel<?> vanillaModel) {
        // phantom                  body, left_wing, left_wing_tip, right_wing, right_wing_tip, head, tail, tail2
        HashMap<String, ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("tail", vanillaPartsList.get("tail_base"));
        vanillaPartsList.put("tail2", vanillaPartsList.get("tail_tip"));
        vanillaPartsList.put("left_wing", vanillaPartsList.get("left_wing_base"));
        vanillaPartsList.put("right_wing", vanillaPartsList.get("right_wing_base"));
        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getllamaSpitMap(EntityModel<?> vanillaModel) {
        // llama_spit               body
        HashMap<String, ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("body", vanillaPartsList.get("main"));
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getSquidMap(EntityModel<?> vanillaModel) {
        // glow_squid               body, tentacle1 ... tentacle8
        HashMap<String, ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("tentacle1", vanillaPartsList.get("tentacle0"));
        vanillaPartsList.put("tentacle2", vanillaPartsList.get("tentacle1"));
        vanillaPartsList.put("tentacle3", vanillaPartsList.get("tentacle2"));
        vanillaPartsList.put("tentacle4", vanillaPartsList.get("tentacle3"));
        vanillaPartsList.put("tentacle5", vanillaPartsList.get("tentacle4"));
        vanillaPartsList.put("tentacle6", vanillaPartsList.get("tentacle5"));
        vanillaPartsList.put("tentacle7", vanillaPartsList.get("tentacle6"));
        vanillaPartsList.put("tentacle8", vanillaPartsList.get("tentacle7"));
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getGhastMap(EntityModel<?> vanillaModel) {
        // ghast                    body, tentacle1 ... tentacle9
        HashMap<String, ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("tentacle1", vanillaPartsList.get("tentacle0"));
        vanillaPartsList.put("tentacle2", vanillaPartsList.get("tentacle1"));
        vanillaPartsList.put("tentacle3", vanillaPartsList.get("tentacle2"));
        vanillaPartsList.put("tentacle4", vanillaPartsList.get("tentacle3"));
        vanillaPartsList.put("tentacle5", vanillaPartsList.get("tentacle4"));
        vanillaPartsList.put("tentacle6", vanillaPartsList.get("tentacle5"));
        vanillaPartsList.put("tentacle7", vanillaPartsList.get("tentacle6"));
        vanillaPartsList.put("tentacle8", vanillaPartsList.get("tentacle7"));
        vanillaPartsList.put("tentacle9", vanillaPartsList.get("tentacle8"));
        return vanillaPartsList;
    }
    private static HashMap<String, ModelAndParent> getEndermiteMap(EntityModel<?> vanillaModel){
        // endermite                body1 ... body4
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("body1",vanillaPartsList.get("segment0"));
        vanillaPartsList.put("body2",vanillaPartsList.get("segment1"));
        vanillaPartsList.put("body3",vanillaPartsList.get("segment2"));
        vanillaPartsList.put("body4",vanillaPartsList.get("segment3"));
        return vanillaPartsList;
    }

    private static HashMap<String, ModelAndParent> getSalmonMap(EntityModel<?> vanillaModel){
        //# salmon                   body_front, body_back, head, fin_back_1, fin_back_2, tail, fin_right, fin_left
        HashMap<String,ModelAndParent> vanillaPartsList = getSinglePartModelMap(vanillaModel);
        vanillaPartsList.put("fin_back_1",vanillaPartsList.get("top_front_fin"));
        vanillaPartsList.put("fin_back_2",vanillaPartsList.get("top_back_fin"));
        vanillaPartsList.put("fin_right",vanillaPartsList.get("right_fin"));
        vanillaPartsList.put("fin_left",vanillaPartsList.get("left_fin"));
        vanillaPartsList.put("tail",vanillaPartsList.get("back_fin"));

        return vanillaPartsList;
    }




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
            vanillaPartsList.put("tail",getEntry(((FoxEntityModelAccessor)fox).getTail(),"body"));


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
    private static HashMap<String, ModelAndParent> getDonkeyMap(EntityModel<?> vanillaModel){
        HashMap<String,ModelAndParent> vanillaPartsList = getHorseMap(vanillaModel);
        if (vanillaModel instanceof DonkeyEntityModel donkey) {
            vanillaPartsList.put("left_chest",getEntry(((DonkeyEntityModelAccessor)donkey).getLeftChest(),"body"));
            vanillaPartsList.put("right_chest",getEntry(((DonkeyEntityModelAccessor)donkey).getRightChest(),"body"));
        }
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
                if (neck.hasChild("upper_mouth"))
                    vanillaPartsList.put("mouth",getEntry(neck.getChild("upper_mouth"),"neck"));

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
                    vanillaPartsList.put("tail",getEntry(bodyParts.get(0).getChild("tail"),"body"));
                if (bodyParts.get(0).hasChild("saddle"))
                    vanillaPartsList.put("saddle",getEntry(bodyParts.get(0).getChild("saddle"),"body"));


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

            for (int i = 0; i < bodyParts.size(); i++) {
                switch (i){
                    case 0->vanillaPartsList.put("body",getEntry(bodyParts.get(0)));
                    case 1->vanillaPartsList.put("leg1",getEntry(bodyParts.get(1)));
                    case 2->vanillaPartsList.put("leg2",getEntry(bodyParts.get(2)));
                    case 3->vanillaPartsList.put("leg3",getEntry(bodyParts.get(3)));
                    case 4->vanillaPartsList.put("leg4",getEntry(bodyParts.get(4)));
                    default->{
                        EMFUtils.EMF_modWarn("unkown part found for "+ vanillaModel.getClass());
                        vanillaPartsList.put("unkown"+i,getEntry(bodyParts.get(i)));
                    }
                }
            }

//            vanillaPartsList.put("leg1",getEntry(bodyParts.get(1)));
//            vanillaPartsList.put("leg2",getEntry(bodyParts.get(2)));
//            vanillaPartsList.put("leg3",getEntry(bodyParts.get(3)));
//            vanillaPartsList.put("leg4",getEntry(bodyParts.get(4)));


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
    private static HashMap<String, ModelAndParent> getPiglinMap(EntityModel<?> vanillaModel) {
        HashMap<String, ModelAndParent> vanillaPartsList = getGenericPlayerMap(vanillaModel);
        // piglin_brute             head, headwear, body, left_arm, right_arm, left_leg, right_leg, left_ear, right_ear, left_sleeve, right_sleeve, left_pants, right_pants, jacket
        ModelPart head = vanillaPartsList.get("head").part();
        vanillaPartsList.put("left_ear",getEntry(head.getChild("left_ear"),"head"));
        vanillaPartsList.put("right_ear",getEntry(head.getChild("right_ear"),"head"));

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
