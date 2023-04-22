package traben.entity_model_features.utils;

import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.NotNull;
import traben.entity_model_features.config.EMFConfig;

import java.util.*;

public class EMFOptiFineMappings2 {

    private static final Map<String, PartAndChildName> genericNonPlayerBiped = Map.ofEntries(
            getOptifineMapEntry("head"),
            getOptifineMapEntry("headwear", "hat"),
            getOptifineMapEntry("body"),
            getOptifineMapEntry("left_arm"),
            getOptifineMapEntry("right_arm"),
            getOptifineMapEntry("left_leg"),
            getOptifineMapEntry("right_leg")
    );
    private static final Map<String, PartAndChildName> genericIllager = Map.ofEntries(
            getOptifineMapEntry("head", "head", List.of("hat", "nose")),
            getOptifineMapEntry("headwear", "hat"),
            getOptifineMapEntry("body"),
            getOptifineMapEntry("nose"),
            getOptifineMapEntry("arms"),
            getOptifineMapEntry("left_arm"),
            getOptifineMapEntry("right_arm"),
            getOptifineMapEntry("left_leg"),
            getOptifineMapEntry("right_leg")
    );
    //# horse                    , back_left_leg, back_right_leg, front_left_leg, front_right_leg,
//#                          child_back_left_leg, child_back_right_leg, child_front_left_leg, child_front_right_leg
    private static final Map<String, PartAndChildName> genericHorse = Map.ofEntries(
            getOptifineMapEntry("body", "body", List.of("tail", "saddle")),
            getOptifineMapEntry("head", "head", List.of("left_ear", "right_ear")),
            getOptifineMapEntry("tail"),
            getOptifineMapEntry("saddle"),
            getOptifineMapEntry("mane"),
            getOptifineMapEntry("mouth", "upper_mouth"),
            getOptifineMapEntry("left_ear"),
            getOptifineMapEntry("right_ear"),
            getOptifineMapEntry("neck", "head_parts",
                    List.of("head",
                            "left_saddle_mouth",
                            "right_saddle_mouth",
                            "left_saddle_line",
                            "right_saddle_line",
                            "head_saddle",
                            "mouth_saddle_wrap",
                            "mane",
                            "upper_mouth"
                    )),
            getOptifineMapEntry("noseband", "mouth_saddle_wrap"),
            getOptifineMapEntry("headpiece", "head_saddle"),
            getOptifineMapEntry("right_rein", "right_saddle_line"),
            getOptifineMapEntry("left_rein", "left_saddle_line"),
            getOptifineMapEntry("right_bit", "right_saddle_mouth"),
            getOptifineMapEntry("left_bit", "left_saddle_mouth"),

            getOptifineMapEntry("back_left_leg", "left_hind_leg"),
            getOptifineMapEntry("back_right_leg", "right_hind_leg"),
            getOptifineMapEntry("front_left_leg", "left_front_leg"),
            getOptifineMapEntry("front_right_leg", "right_front_leg"),
            getOptifineMapEntry("child_back_left_leg", "left_hind_baby_leg"),
            getOptifineMapEntry("child_back_right_leg", "right_hind_baby_leg"),
            getOptifineMapEntry("child_front_left_leg", "left_front_baby_leg"),
            getOptifineMapEntry("child_front_right_leg", "right_front_baby_leg")
    );
    private static final Map<String, PartAndChildName> genericPlayerBiped = Map.ofEntries(
            getOptifineMapEntry("head"),
            getOptifineMapEntry("headwear", "hat"),
            getOptifineMapEntry("body"),
            getOptifineMapEntry("left_arm"),
            getOptifineMapEntry("right_arm"),
            getOptifineMapEntry("left_leg"),
            getOptifineMapEntry("right_leg"),
            getOptifineMapEntry("ear"),
            getOptifineMapEntry("left_sleeve"),
            getOptifineMapEntry("right_sleeve"),
            getOptifineMapEntry("left_pants"),
            getOptifineMapEntry("right_pants"),
            getOptifineMapEntry("jacket"),
            getOptifineMapEntry("cloak")
    );
    private static final Map<String, PartAndChildName> genericPiglinBiped = Map.ofEntries(
            getOptifineMapEntry("head", "head", List.of("right_ear", "left_ear")),
            getOptifineMapEntry("headwear", "hat"),
            getOptifineMapEntry("body"),
            getOptifineMapEntry("left_arm"),
            getOptifineMapEntry("right_arm"),
            getOptifineMapEntry("left_leg"),
            getOptifineMapEntry("ear"),
            getOptifineMapEntry("right_leg"),
            getOptifineMapEntry("left_ear"),
            getOptifineMapEntry("right_ear"),
            getOptifineMapEntry("left_sleeve"),
            getOptifineMapEntry("right_sleeve"),
            getOptifineMapEntry("left_pants"),
            getOptifineMapEntry("right_pants"),
            getOptifineMapEntry("jacket"),
            getOptifineMapEntry("cloak")
    );
    private static final Map<String, PartAndChildName> genericQuadraped = Map.ofEntries(
            getOptifineMapEntry("head"),
            getOptifineMapEntry("body"),
            getOptifineMapEntry("leg1", "right_hind_leg"),
            getOptifineMapEntry("leg2", "left_hind_leg"),
            getOptifineMapEntry("leg3", "right_front_leg"),
            getOptifineMapEntry("leg4", "left_front_leg"));
    private static final Map<String, PartAndChildName> genericLlama = new HashMap<>(genericQuadraped) {{
        putAll(Map.ofEntries(
                getOptifineMapEntry("chest_left", "left_chest"),
                getOptifineMapEntry("chest_right", "right_chest")
        ));

    }};

    public static Map.Entry<String, PartAndChildName> getOptifineMapEntry(String optifineName) {
        return new MutablePair<>(optifineName, _getPartAndChild(optifineName));
    }

    public static Map.Entry<String, PartAndChildName> getOptifineMapEntry(String optifineName, String vanillaName) {
        return new MutablePair<>(optifineName, _getPartAndChild(vanillaName));
    }

    public static Map.Entry<String, PartAndChildName> getOptifineMapEntry(String optifineName, String vanillaName, String childName) {
        return new MutablePair<>(optifineName, _getPartAndChild(vanillaName, childName));
    }

    public static Map.Entry<String, PartAndChildName> getOptifineMapEntry(String optifineName, String vanillaName, List<String> childNames) {
        return new MutablePair<>(optifineName, _getPartAndChild(vanillaName, childNames));
    }

    public static PartAndChildName _getPartAndChild(String partName, String childName) {
        return new PartAndChildName(partName, Collections.singletonList(childName));
    }

    public static PartAndChildName _getPartAndChild(String partName) {
        return new PartAndChildName(partName, new ArrayList<>());
    }

    public static PartAndChildName _getPartAndChild(String partName, List<String> childNamesToExpect) {
        return new PartAndChildName(partName, childNamesToExpect);
    }

    public static Map<String, PartAndChildName> getMapOf(String mobName) {
        if (mobName.contains("_inner_armor")) mobName = mobName.replace("_inner_armor", "");
        if (mobName.contains("_outer_armor")) mobName = mobName.replace("_outer_armor", "");

        //todo extract all maps once done to make them all static final for faster reloads
        return switch (mobName) {
            case "villager", "wandering_trader" -> Map.ofEntries(
                    getOptifineMapEntry("head", "head", List.of("hat", "nose")),
                    getOptifineMapEntry("headwear", "hat", "hat_rim"),
                    getOptifineMapEntry("headwear2", "hat_rim"),
                    getOptifineMapEntry("bodywear", "jacket"),
                    getOptifineMapEntry("body", "body", "jacket"),
                    getOptifineMapEntry("arms"),
                    getOptifineMapEntry("right_leg"),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("nose"));

            case "iron_golem" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("left_arm"),
                    getOptifineMapEntry("right_arm"),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("right_leg")
            );
            case "spider", "cave_spider" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("neck", "body0"),
                    getOptifineMapEntry("body", "body1"),
                    getOptifineMapEntry("leg1", "right_hind_leg"),
                    getOptifineMapEntry("leg2", "left_hind_leg"),
                    getOptifineMapEntry("leg3", "right_middle_hind_leg"),
                    getOptifineMapEntry("leg4", "left_middle_hind_leg"),
                    getOptifineMapEntry("leg5", "right_middle_front_leg"),
                    getOptifineMapEntry("leg6", "left_middle_front_leg"),
                    getOptifineMapEntry("leg7", "right_front_leg"),
                    getOptifineMapEntry("leg8", "left_front_leg")
            );
            case "sheep", "cow", "creeper", "creeper_charge", "mooshroom", "panda", "pig", "pig_saddle", "polar_bear", "sheep_wool" ->
                    genericQuadraped;
            case "zombie", "husk", "drowned", "drowned_outer", "enderman", "giant", "skeleton", "stray", "stray_outer", "wither_skeleton", "zombie_pigman" ->
                    genericNonPlayerBiped;
            case "piglin", "piglin_brute", "zombified_piglin" -> genericPiglinBiped;
            case "allay" ->
                    Map.ofEntries(//# allay                    head, body, left_arm, right_arm, left_wing, right_wing
                            getOptifineMapEntry("head"),
                            getOptifineMapEntry("body", "body", List.of("left_arm", "right_arm", "left_wing", "right_wing")),
                            getOptifineMapEntry("left_arm"),
                            getOptifineMapEntry("right_arm"),
                            getOptifineMapEntry("left_wing"),
                            getOptifineMapEntry("right_wing")
                    );
            case "vex" ->
                Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("headwear", "hat"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("left_arm"),
                    getOptifineMapEntry("right_arm"),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("right_leg"),
                    getOptifineMapEntry("left_wing"),
                    getOptifineMapEntry("right_wing")
                );
            case "squid", "glow_squid" -> Map.ofEntries(//body, tentacle1 ... tentacle8
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("tentacle1", "tentacle0"),
                    getOptifineMapEntry("tentacle2", "tentacle1"),
                    getOptifineMapEntry("tentacle3", "tentacle2"),
                    getOptifineMapEntry("tentacle4", "tentacle3"),
                    getOptifineMapEntry("tentacle5", "tentacle4"),
                    getOptifineMapEntry("tentacle6", "tentacle5"),
                    getOptifineMapEntry("tentacle7", "tentacle6"),
                    getOptifineMapEntry("tentacle8", "tentacle7")
            );
            case "ghast" -> Map.ofEntries(//body, tentacle1 ... tentacle9
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("tentacle1", "tentacle0"),
                    getOptifineMapEntry("tentacle2", "tentacle1"),
                    getOptifineMapEntry("tentacle3", "tentacle2"),
                    getOptifineMapEntry("tentacle4", "tentacle3"),
                    getOptifineMapEntry("tentacle5", "tentacle4"),
                    getOptifineMapEntry("tentacle6", "tentacle5"),
                    getOptifineMapEntry("tentacle7", "tentacle6"),
                    getOptifineMapEntry("tentacle8", "tentacle7"),
                    getOptifineMapEntry("tentacle9", "tentacle8")
            );
            case "wolf", "wolf_collar" -> Map.ofEntries(
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("head", "head", "real_head"),//todo
                    getOptifineMapEntry("tail", "tail", "real_tail"),
                    getOptifineMapEntry("mane", "upper_body"),
                    getOptifineMapEntry("leg1", "right_hind_leg"),
                    getOptifineMapEntry("leg2", "left_hind_leg"),
                    getOptifineMapEntry("leg3", "right_front_leg"),
                    getOptifineMapEntry("leg4", "left_front_leg")
            );
            case "wither_skull", "head_zombie", "head_wither_skeleton", "head_skeleton", "head_player" ->
                    Map.ofEntries(getOptifineMapEntry("head"));

            case "horse", "horse_armor", "skeleton_horse", "zombie_horse" -> genericHorse;
            case "donkey", "mule" -> new HashMap<String, PartAndChildName>(genericHorse) {{
                putAll(Map.ofEntries(
                        getOptifineMapEntry("right_chest"),
                        getOptifineMapEntry("left_chest"),
                        getOptifineMapEntry("body", "body", List.of("tail", "saddle", "left_chest", "right_chest"))
                ));
            }};
            case "zombie_villager" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("headwear", "hat", "hat_rim"),
                    getOptifineMapEntry("hat_rim"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("left_arm"),
                    getOptifineMapEntry("right_arm"),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("right_leg")
            );

            case "evoker", "illusioner", "pillager", "vindicator" -> genericIllager;
            case "llama", "llama_decor", "trader_llama", "trader_llama_decor" -> genericLlama;
            case "armor_stand" -> new HashMap<String, PartAndChildName>(genericNonPlayerBiped) {{
                putAll(Map.ofEntries(
                        getOptifineMapEntry("right", "right_body_stick"),
                        getOptifineMapEntry("left", "left_body_stick"),
                        getOptifineMapEntry("waist", "shoulder_stick"),//todo probably swap with body
                        getOptifineMapEntry("base", "base_plate")
                ));
            }};
            case "axolotl" -> Map.ofEntries(
                    getOptifineMapEntry("body", "body", List.of("head", "tail", "right_hind_leg", "left_hind_leg", "right_front_leg", "left_front_leg")),
                    getOptifineMapEntry("head", "head", List.of("top_gills", "left_gills", "right_gills")),
                    getOptifineMapEntry("leg1", "right_hind_leg"),
                    getOptifineMapEntry("leg2", "left_hind_leg"),
                    getOptifineMapEntry("leg3", "right_front_leg"),
                    getOptifineMapEntry("leg4", "left_front_leg"),
                    getOptifineMapEntry("right_gills"),
                    getOptifineMapEntry("top_gills"),
                    getOptifineMapEntry("left_gills")
            );
            case "bat" -> Map.ofEntries(
                    getOptifineMapEntry("body", "body", List.of("right_wing", "left_wing")),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("right_wing", "right_wing", "right_wing_tip"),
                    getOptifineMapEntry("left_wing", "left_wing", "left_wing_tip"),
                    getOptifineMapEntry("outer_right_wing", "right_wing_tip"),
                    getOptifineMapEntry("outer_left_wing", "left_wing_tip")
            );
            case "bee" -> Map.ofEntries(
                    getOptifineMapEntry("body", "bone", List.of("body", "right_wing", "left_wing", "front_legs", "middle_legs", "back_legs")),
                    getOptifineMapEntry("torso", "body", List.of("stinger", "left_antenna", "right_antenna")),
                    getOptifineMapEntry("right_wing"),
                    getOptifineMapEntry("left_wing"),
                    getOptifineMapEntry("front_legs"),
                    getOptifineMapEntry("middle_legs"),
                    getOptifineMapEntry("back_legs"),
                    getOptifineMapEntry("stinger"),
                    getOptifineMapEntry("left_antenna"),
                    getOptifineMapEntry("right_antenna")
            );
            case "blaze" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("stick1", "part0"),
                    getOptifineMapEntry("stick2", "part1"),
                    getOptifineMapEntry("stick3", "part2"),
                    getOptifineMapEntry("stick4", "part3"),
                    getOptifineMapEntry("stick5", "part4"),
                    getOptifineMapEntry("stick6", "part5"),
                    getOptifineMapEntry("stick7", "part6"),
                    getOptifineMapEntry("stick8", "part7"),
                    getOptifineMapEntry("stick9", "part8"),
                    getOptifineMapEntry("stick10", "part9"),
                    getOptifineMapEntry("stick11", "part10"),
                    getOptifineMapEntry("stick12", "part11")
            );
            case "cat", "cat_collar", "ocelot" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("tail", "tail1"),
                    getOptifineMapEntry("tail2"),
                    getOptifineMapEntry("back_left_leg", "left_hind_leg"),
                    getOptifineMapEntry("back_right_leg", "right_hind_leg"),
                    getOptifineMapEntry("front_left_leg", "left_front_leg"),
                    getOptifineMapEntry("front_right_leg", "right_front_leg")
            );
            case "chicken" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("right_leg"),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("right_wing"),
                    getOptifineMapEntry("left_wing"),
                    getOptifineMapEntry("bill", "beak"),
                    getOptifineMapEntry("chin", "red_thing")
            );
            case "cod" -> Map.ofEntries(
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("nose"),
                    getOptifineMapEntry("tail", "tail_fin"),
                    getOptifineMapEntry("fin_right", "right_fin"),
                    getOptifineMapEntry("fin_left", "left_fin"),
                    getOptifineMapEntry("fin_back", "top_fin")
            );
            case "dolphin" -> Map.ofEntries(
                    getOptifineMapEntry("body", "body", List.of("head", "tail", "right_fin", "left_fin", "back_fin")),
                    getOptifineMapEntry("tail", "tail", "tail_fin"),
                    getOptifineMapEntry("tail_fin"),
                    getOptifineMapEntry("head", "head", "nose"),
                    getOptifineMapEntry("right_fin"),
                    getOptifineMapEntry("left_fin"),
                    getOptifineMapEntry("back_fin")
            );
            case "elder_guardian", "guardian" -> Map.ofEntries(
                    getOptifineMapEntry("tail1", "tail0", "tail1"),
                    getOptifineMapEntry("tail2", "tail1", "tail2"),
                    getOptifineMapEntry("tail3", "tail2"),
                    getOptifineMapEntry("head", "head",
                            List.of("eye",
                                    "spike0",
                                    "spike1",
                                    "spike2",
                                    "spike3",
                                    "spike4",
                                    "spike5",
                                    "spike6",
                                    "spike7",
                                    "spike8",
                                    "spike9",
                                    "spike10",
                                    "spike11",
                                    "tail0"
                            )),
                    getOptifineMapEntry("right_fin", "right_fin"),
                    getOptifineMapEntry("left_fin", "left_fin"),
                    getOptifineMapEntry("back_fin", "back_fin"),

                    getOptifineMapEntry("spine1", "spike0"),
                    getOptifineMapEntry("spine2", "spike1"),
                    getOptifineMapEntry("spine3", "spike2"),
                    getOptifineMapEntry("spine4", "spike3"),
                    getOptifineMapEntry("spine5", "spike4"),
                    getOptifineMapEntry("spine6", "spike5"),
                    getOptifineMapEntry("spine7", "spike6"),
                    getOptifineMapEntry("spine8", "spike7"),
                    getOptifineMapEntry("spine9", "spike8"),
                    getOptifineMapEntry("spine10", "spike9"),
                    getOptifineMapEntry("spine11", "spike10"),
                    getOptifineMapEntry("spine12", "spike11")
            );
            case "endermite" -> Map.ofEntries(
                    getOptifineMapEntry("body1", "segment0"),
                    getOptifineMapEntry("body2", "segment1"),
                    getOptifineMapEntry("body3", "segment2"),
                    getOptifineMapEntry("body4", "segment3")
            );
            case "evoker_fangs" -> Map.ofEntries(
                    getOptifineMapEntry("base"),
                    getOptifineMapEntry("upper_jaw"),
                    getOptifineMapEntry("lower_jaw")
            );
            case "fox" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("body", "body", "tail"),
                    getOptifineMapEntry("leg1", "right_hind_leg"),
                    getOptifineMapEntry("leg2", "left_hind_leg"),
                    getOptifineMapEntry("leg3", "right_front_leg"),
                    getOptifineMapEntry("leg4", "left_front_leg"),
                    getOptifineMapEntry("tail")
            );
            case "frog" -> Map.ofEntries(//TODO INVESTIGATE IF CORRECT ABOUT OPTIFINE MAPPING
                    //getOptifineMapEntry("root","root",List.of("body","left_leg","right_leg")),//,"!left_leg","!right_leg")),
                    getOptifineMapEntry("head", "head", List.of("eyes")),
                    getOptifineMapEntry("body", "body", List.of("head", "tongue", "left_arm", "right_arm", "croaking_body")),//,"left_leg","right_leg")),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("right_leg"),
                    getOptifineMapEntry("croaking_body"),
                    getOptifineMapEntry("left_arm"),
                    getOptifineMapEntry("right_arm"),
                    getOptifineMapEntry("tongue"),
                    getOptifineMapEntry("eyes")
            );
            case "goat" -> Map.ofEntries(
                    getOptifineMapEntry("head", "head", List.of("left_horn", "right_horn", "nose")),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("leg1", "right_hind_leg"),
                    getOptifineMapEntry("leg2", "left_hind_leg"),
                    getOptifineMapEntry("leg3", "right_front_leg"),
                    getOptifineMapEntry("leg4", "left_front_leg"),
                    getOptifineMapEntry("left_horn"),
                    getOptifineMapEntry("right_horn"),
                    getOptifineMapEntry("nose")
            );
            case "hoglin", "zoglin" -> Map.ofEntries(
                    getOptifineMapEntry("head", "head", List.of("left_ear", "right_ear")),
                    getOptifineMapEntry("body", "body", "mane"),
                    getOptifineMapEntry("back_right_leg", "right_hind_leg"),
                    getOptifineMapEntry("back_left_leg", "left_hind_leg"),
                    getOptifineMapEntry("front_right_leg", "right_front_leg"),
                    getOptifineMapEntry("front_left_leg", "left_front_leg"),
                    getOptifineMapEntry("mane"),
                    getOptifineMapEntry("left_ear"),
                    getOptifineMapEntry("right_ear")
            );
            case "magma_cube" -> Map.ofEntries(
                    getOptifineMapEntry("core", "inside_cube"),
                    getOptifineMapEntry("segment1", "cube0"),
                    getOptifineMapEntry("segment2", "cube1"),
                    getOptifineMapEntry("segment3", "cube2"),
                    getOptifineMapEntry("segment4", "cube3"),
                    getOptifineMapEntry("segment5", "cube4"),
                    getOptifineMapEntry("segment6", "cube5"),
                    getOptifineMapEntry("segment7", "cube6"),
                    getOptifineMapEntry("segment8", "cube7")
            );
            case "phantom" -> Map.ofEntries(
                    getOptifineMapEntry("body", "body", List.of("tail_base", "left_wing_base", "right_wing_base")),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("tail", "tail_base", "tail_tip"),
                    getOptifineMapEntry("tail2", "tail_tip"),
                    getOptifineMapEntry("left_wing", "left_wing_base", "left_wing_tip"),
                    getOptifineMapEntry("right_wing", "right_wing_base", "right_wing_tip"),
                    getOptifineMapEntry("left_wing_tip"),
                    getOptifineMapEntry("right_wing_tip")
            );
            case "parrot" -> Map.ofEntries(
                    getOptifineMapEntry("head", "head", "feather"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("tail"),
                    getOptifineMapEntry("left_wing"),
                    getOptifineMapEntry("right_wing"),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("right_leg")
            );
            case "puffer_fish_big" -> Map.ofEntries( //todo optifine has puffer_fish with a _
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("fin_right", "right_blue_fin"),
                    getOptifineMapEntry("fin_left", "left_blue_fin"),
                    getOptifineMapEntry("spikes_front_top", "top_front_fin"),
                    getOptifineMapEntry("spikes_middle_top", "top_middle_fin"),
                    getOptifineMapEntry("spikes_back_top", "top_back_fin"),
                    getOptifineMapEntry("spikes_front_right", "right_front_fin"),
                    getOptifineMapEntry("spikes_front_left", "left_front_fin"),
                    getOptifineMapEntry("spikes_front_bottom", "bottom_front_fin"),
                    getOptifineMapEntry("spikes_middle_bottom", "bottom_middle_fin"),
                    getOptifineMapEntry("spikes_back_bottom", "bottom_back_fin"),
                    getOptifineMapEntry("spikes_back_right", "right_back_fin"),
                    getOptifineMapEntry("spikes_back_left", "left_back_fin")
            );
            case "puffer_fish_medium" -> Map.ofEntries( //todo optifine has puffer_fish with a _
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("fin_right", "right_blue_fin"),
                    getOptifineMapEntry("fin_left", "left_blue_fin"),
                    getOptifineMapEntry("spikes_front_top", "top_front_fin"),
                    getOptifineMapEntry("spikes_back_top", "top_back_fin"),
                    getOptifineMapEntry("spikes_front_right", "right_front_fin"),
                    getOptifineMapEntry("spikes_front_left", "left_front_fin"),
                    getOptifineMapEntry("spikes_front_bottom", "bottom_front_fin"),
                    getOptifineMapEntry("spikes_back_bottom", "bottom_back_fin"),
                    getOptifineMapEntry("spikes_back_right", "right_back_fin"),
                    getOptifineMapEntry("spikes_back_left", "left_back_fin")
            );
            case "puffer_fish_small" -> Map.ofEntries( //todo optifine has puffer_fish with a _
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("fin_right", "right_fin"),
                    getOptifineMapEntry("fin_left", "left_fin"),
                    getOptifineMapEntry("eye_right", "right_eye"),
                    getOptifineMapEntry("eye_left", "left_eye"),
                    getOptifineMapEntry("tail", "back_fin")
            );
            case "rabbit" -> Map.ofEntries(
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("left_foot", "left_hind_foot"),
                    getOptifineMapEntry("right_foot", "right_hind_foot"),
                    getOptifineMapEntry("left_thigh", "left_haunch"),
                    getOptifineMapEntry("right_thigh", "right_haunch"),
                    getOptifineMapEntry("left_arm", "left_front_leg"),
                    getOptifineMapEntry("right_arm", "right_front_leg"),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("right_ear"),
                    getOptifineMapEntry("left_ear"),
                    getOptifineMapEntry("tail"),
                    getOptifineMapEntry("nose")
            );
            case "ravager" -> Map.ofEntries(
                    getOptifineMapEntry("head", "head", "mouth"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("leg1", "right_hind_leg"),
                    getOptifineMapEntry("leg2", "left_hind_leg"),
                    getOptifineMapEntry("leg3", "right_front_leg"),
                    getOptifineMapEntry("leg4", "left_front_leg"),
                    getOptifineMapEntry("jaw"),
                    getOptifineMapEntry("neck", "neck", "head")
            );
            case "salmon" -> Map.ofEntries(
                    getOptifineMapEntry("body_front", "body_front", "top_front_fin"),
                    getOptifineMapEntry("body_back", "body_back", List.of("back_fin", "top_back_fin")),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("fin_back_1", "top_front_fin"),
                    getOptifineMapEntry("fin_back_2", "top_back_fin"),
                    getOptifineMapEntry("tail", "back_fin"),
                    getOptifineMapEntry("fin_right", "right_fin"),
                    getOptifineMapEntry("fin_left", "left_fin")
            );
            case "shulker" -> Map.ofEntries(
                    getOptifineMapEntry("lid"),
                    getOptifineMapEntry("base"),
                    getOptifineMapEntry("head")
            );
            case "silverfish" -> Map.ofEntries(
                    getOptifineMapEntry("body1", "segment0"),
                    getOptifineMapEntry("body2", "segment1"),
                    getOptifineMapEntry("body3", "segment2"),
                    getOptifineMapEntry("body4", "segment3"),
                    getOptifineMapEntry("body5", "segment4"),
                    getOptifineMapEntry("body6", "segment5"),
                    getOptifineMapEntry("body7", "segment6"),
                    getOptifineMapEntry("wing1", "layer0"),
                    getOptifineMapEntry("wing2", "layer1"),
                    getOptifineMapEntry("wing3", "layer2")
            );
            case "slime" -> Map.ofEntries(
                    getOptifineMapEntry("body", "cube"),
                    getOptifineMapEntry("left_eye"),
                    getOptifineMapEntry("right_eye"),
                    getOptifineMapEntry("mouth")
            );
            case "slime_outer" -> Map.ofEntries(
                    getOptifineMapEntry("body", "cube")
            );
            case "snow_golem" -> Map.ofEntries(
                    getOptifineMapEntry("body", "upper_body"),
                    getOptifineMapEntry("body_bottom", "lower_body"),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("left_hand", "left_arm"),
                    getOptifineMapEntry("right_hand", "right_arm")
            );
            case "strider", "strider_saddle" -> Map.ofEntries(
                    getOptifineMapEntry("body", "body", List.of(
                            "right_top_bristle",
                            "right_middle_bristle",
                            "right_bottom_bristle",
                            "left_top_bristle",
                            "left_middle_bristle",
                            "left_bottom_bristle"
                    )),
                    getOptifineMapEntry("right_leg"),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("hair_right_top", "right_top_bristle"),
                    getOptifineMapEntry("hair_right_middle", "right_middle_bristle"),
                    getOptifineMapEntry("hair_right_bottom", "right_bottom_bristle"),
                    getOptifineMapEntry("hair_left_top", "left_top_bristle"),
                    getOptifineMapEntry("hair_left_middle", "left_middle_bristle"),
                    getOptifineMapEntry("hair_left_bottom", "left_bottom_bristle")
            );
            case "tadpole" -> Map.ofEntries(
                    getOptifineMapEntry("body","root","tail"),
                    getOptifineMapEntry("tail")
            );

            case "tropical_fish_a", "tropical_fish_pattern_a" -> Map.ofEntries(
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("tail"),
                    getOptifineMapEntry("fin_right", "right_fin"),
                    getOptifineMapEntry("fin_left", "left_fin"),
                    getOptifineMapEntry("fin_top", "top_fin")
            );
            case "tropical_fish_b", "tropical_fish_pattern_b" -> Map.ofEntries(
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("tail"),
                    getOptifineMapEntry("fin_right", "right_fin"),
                    getOptifineMapEntry("fin_left", "left_fin"),
                    getOptifineMapEntry("fin_top", "top_fin"),
                    getOptifineMapEntry("fin_bottom", "bottom_fin")
            );
            case "turtle" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("body2", "egg_belly"),
                    getOptifineMapEntry("leg1", "right_hind_leg"),
                    getOptifineMapEntry("leg2", "left_hind_leg"),
                    getOptifineMapEntry("leg3", "right_front_leg"),
                    getOptifineMapEntry("leg4", "left_front_leg")
            );
            case "warden" -> Map.ofEntries(
                    getOptifineMapEntry("body", "bone", List.of("body", "right_leg", "left_leg")),
                    getOptifineMapEntry("torso", "body", List.of("head", "right_arm", "left_arm", "right_ribcage", "left_ribcage")),
                    getOptifineMapEntry("head", "head", List.of("right_tendril", "left_tendril")),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("right_leg"),
                    getOptifineMapEntry("left_arm"),
                    getOptifineMapEntry("right_arm"),
                    getOptifineMapEntry("left_tendril"),
                    getOptifineMapEntry("right_tendril"),
                    getOptifineMapEntry("left_ribcage"),
                    getOptifineMapEntry("right_ribcage")
            );

            case "witch" -> Map.ofEntries(
                    getOptifineMapEntry("head", "head", List.of("hat", "nose")),
                    getOptifineMapEntry("headwear", "hat", "hat_rim"),
                    getOptifineMapEntry("headwear2", "hat_rim"),
                    getOptifineMapEntry("bodywear", "jacket"),
                    getOptifineMapEntry("body", "body", "jacket"),
                    getOptifineMapEntry("arms"),
                    getOptifineMapEntry("right_leg"),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("nose", "nose", "mole"),
                    getOptifineMapEntry("mole")
            );
            //# wither                   body1 ... body3, head1 ... head3
            case "wither" -> Map.ofEntries(
                    getOptifineMapEntry("body1", "shoulders"),
                    getOptifineMapEntry("body2", "ribcage"),
                    getOptifineMapEntry("body3", "tail"),
                    getOptifineMapEntry("head1", "center_head"),
                    getOptifineMapEntry("head2", "right_head"),
                    getOptifineMapEntry("head3", "left_head")
            );

            case "dragon" -> Map.ofEntries(
                    getOptifineMapEntry("head", "head","jaw"),
                    getOptifineMapEntry("jaw", "jaw"),
                    getOptifineMapEntry("spine", "neck"),
                    getOptifineMapEntry("body", "body"),

                    getOptifineMapEntry("left_wing", "left_wing","left_wing_tip"),
                    getOptifineMapEntry("left_wing_tip", "left_wing_tip"),
                    getOptifineMapEntry("right_wing", "right_wing","right_wing_tip"),
                    getOptifineMapEntry("right_wing_tip", "right_wing_tip"),

                    getOptifineMapEntry("front_left_leg", "left_front_leg","left_front_leg_tip"),
                    getOptifineMapEntry("front_left_shin", "left_front_leg_tip","left_front_foot"),
                    getOptifineMapEntry("front_left_foot", "left_front_foot"),

                    getOptifineMapEntry("back_left_leg", "left_hind_leg","left_hind_leg_tip"),
                    getOptifineMapEntry("back_left_shin", "left_hind_leg_tip","left_hind_foot"),
                    getOptifineMapEntry("back_left_foot", "left_hind_foot"),

                    getOptifineMapEntry("front_right_leg", "right_front_leg","right_front_leg_tip"),
                    getOptifineMapEntry("front_right_shin", "right_front_leg_tip","right_front_foot"),
                    getOptifineMapEntry("front_right_foot", "right_front_foot"),

                    getOptifineMapEntry("back_right_leg", "right_hind_leg","right_hind_leg_tip"),
                    getOptifineMapEntry("back_right_shin", "right_hind_leg_tip","right_hind_foot"),
                    getOptifineMapEntry("back_right_foot", "right_hind_foot")
            );
            case "player", "player_slim" -> genericPlayerBiped;

            default -> {
                //throw new RuntimeException("EMF doesn't map: "+mobName);
                if ( EMFConfig.getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMFModError("no model part mapping found for: " + mobName);
                //todo custom mappings
                yield Map.of();
            }

        };


    }

    public static record PartAndChildName(@NotNull String partName, @NotNull List<String> childNamesToExpect) {

    }
}
