package traben.entity_model_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.model.ModelPart;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFVersionDifferenceManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.jem_objects.EMFBoxData;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EMFOptiFinePartNameMappings {


    public static final Map<String, Map<String, String>> UNKNOWN_MODEL_MAP_CACHE = new HashMap<>();
    //# minecart                 bottom, back, front, right, left
    private static final Map<String, String> genericMinecart = Map.ofEntries(
            getOptifineMapEntry("bottom"),
            getOptifineMapEntry("back"),
            getOptifineMapEntry("front"),
            getOptifineMapEntry("right"),
            getOptifineMapEntry("left")
    );
    private static final Map<String, String> genericNonPlayerBiped = Map.ofEntries(
            getOptifineMapEntry("head"),
            getOptifineMapEntry("headwear", "hat"),
            getOptifineMapEntry("body"),
            getOptifineMapEntry("left_arm"),
            getOptifineMapEntry("right_arm"),
            getOptifineMapEntry("left_leg"),
            getOptifineMapEntry("right_leg")
    );

    private static final Map<String, String> genericIllager = Map.ofEntries(
            getOptifineMapEntry("head", "head"),
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
    private static final Map<String, String> genericHorse = Map.ofEntries(
            getOptifineMapEntry("body", "body"),
            getOptifineMapEntry("head", "head"),
            getOptifineMapEntry("tail"),
            getOptifineMapEntry("saddle"),
            getOptifineMapEntry("mane"),
            getOptifineMapEntry("mouth", "upper_mouth"),
            getOptifineMapEntry("left_ear"),
            getOptifineMapEntry("right_ear"),
            getOptifineMapEntry("neck", "head_parts"),
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
    private static final Map<String, String> genericPlayerBiped = Map.ofEntries(
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
    private static final Map<String, String> genericPiglinBiped = Map.ofEntries(
            getOptifineMapEntry("head", "head"),
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
    private static final Map<String, String> genericQuadraped = Map.ofEntries(
            getOptifineMapEntry("head"),
            getOptifineMapEntry("body"),
            getOptifineMapEntry("leg1", "right_hind_leg"),
            getOptifineMapEntry("leg2", "left_hind_leg"),
            getOptifineMapEntry("leg3", "right_front_leg"),
            getOptifineMapEntry("leg4", "left_front_leg"));
    private static final Map<String, String> genericLlama = new HashMap<>(genericQuadraped) {{
        putAll(Map.ofEntries(
                getOptifineMapEntry("chest_left", "left_chest"),
                getOptifineMapEntry("chest_right", "right_chest")
        ));

    }};

    public static Map.Entry<String, String> getOptifineMapEntry(String optifineName) {
        return new MutablePair<>(optifineName, optifineName);
    }

    public static Map.Entry<String, String> getOptifineMapEntry(String optifineName, String vanillaName) {
        return new MutablePair<>(optifineName, vanillaName);
    }

    public static Map<String, String> getMapOf(String mobName, @Nullable ModelPart root) {
        if (mobName.endsWith("_inner_armor") || mobName.endsWith("_outer_armor"))
            return genericNonPlayerBiped;

        var knownMap = getKnownMap(mobName);
        if (knownMap == null) {
            return root == null ? Map.of() : exploreProvidedEntityModelAndExportIfNeeded(root, mobName, null);
        }
        //trigger the export of the known model if we are exporting all
        if (EMFConfig.getConfig().modelExportMode.doesAll())
            exploreProvidedEntityModelAndExportIfNeeded(root, mobName, knownMap);

        return knownMap;
    }

    private static Map<String, String> getKnownMap(String mobName) {

        return switch (mobName) {
            case "villager", "wandering_trader" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("headwear", "hat"),
                    getOptifineMapEntry("headwear2", "hat_rim"),
                    getOptifineMapEntry("bodywear", "jacket"),
                    getOptifineMapEntry("body"),
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
            case "allay", "vex" ->
                    Map.ofEntries(//# allay                    head, body, left_arm, right_arm, left_wing, right_wing
                            getOptifineMapEntry("head"),
                            getOptifineMapEntry("body"),
                            getOptifineMapEntry("left_arm"),
                            getOptifineMapEntry("right_arm"),
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
            case "wolf", "wolf_collar", "wolf_armor" -> Map.ofEntries(
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("tail"),
                    getOptifineMapEntry("mane", "upper_body"),
                    getOptifineMapEntry("leg1", "right_hind_leg"),
                    getOptifineMapEntry("leg2", "left_hind_leg"),
                    getOptifineMapEntry("leg3", "right_front_leg"),
                    getOptifineMapEntry("leg4", "left_front_leg")
            );
            case "shulker_bullet" -> Map.ofEntries(getOptifineMapEntry("bullet", "main"));
            case "llama_spit" -> Map.ofEntries(getOptifineMapEntry("body", "main"));
            case "wither_skull", "head_zombie", "head_wither_skeleton", "head_skeleton", "head_player", "head_creeper" ->
                    Map.ofEntries(getOptifineMapEntry("head"));
            case "head_piglin" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("left_ear"),
                    getOptifineMapEntry("right_ear")
            );
            case "head_dragon" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("jaw")
            );
            case "camel" -> Map.ofEntries(
                    //# camel                    body, hump, tail, head, left_ear, right_ear, back_left_leg, back_right_leg, front_left_leg, front_right_leg,
                    //#                          saddle, reins, bridle
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("hump"),
                    getOptifineMapEntry("tail"),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("left_ear"),
                    getOptifineMapEntry("right_ear"),
                    getOptifineMapEntry("back_left_leg", "left_hind_leg"),
                    getOptifineMapEntry("back_right_leg", "right_hind_leg"),
                    getOptifineMapEntry("front_left_leg", "left_front_leg"),
                    getOptifineMapEntry("front_right_leg", "right_front_leg"),
                    getOptifineMapEntry("saddle"),
                    getOptifineMapEntry("reins"),
                    getOptifineMapEntry("bridle")

            );
            case "sniffer" -> Map.ofEntries(
                    //# sniffer                  body, back_left_leg, back_right_leg, middle_left_leg, middle_right_leg, front_left_leg, front_right_leg,
                    //#                          head, left_ear, right_ear, nose, lower_beak
                    getOptifineMapEntry("root"),
                    getOptifineMapEntry("bone"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("front_right_leg", "right_front_leg"),
                    getOptifineMapEntry("middle_right_leg", "right_mid_leg"),
                    getOptifineMapEntry("back_right_leg", "right_hind_leg"),
                    getOptifineMapEntry("front_left_leg", "left_front_leg"),
                    getOptifineMapEntry("middle_left_leg", "left_mid_leg"),
                    getOptifineMapEntry("back_left_leg", "left_hind_leg"),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("left_ear"),
                    getOptifineMapEntry("right_ear"),
                    getOptifineMapEntry("nose"),
                    getOptifineMapEntry("lower_beak")
            );
            case "chest", "ender_chest", "trapped_chest" -> Map.ofEntries(//lid, base, knob
                    getOptifineMapEntry("lid"),
                    getOptifineMapEntry("base", "bottom"),
                    getOptifineMapEntry("knob", "lock")
            );

            case "chest_large" -> {
                EMFUtils.logError("CHEST_LARGE SHOULDN'T HAVE RUN");
                yield Map.of();
            }

            case "double_chest_right", "trapped_double_chest_right", "ender_double_chest_right" -> Map.ofEntries(
                    getOptifineMapEntry("lid_left", "lid"),
                    getOptifineMapEntry("base_left", "bottom"),
                    getOptifineMapEntry("knob_left", "lock")
            );
            case "double_chest_left", "trapped_double_chest_left", "ender_double_chest_left" -> Map.ofEntries(
                    getOptifineMapEntry("lid_right", "lid"),
                    getOptifineMapEntry("base_right", "bottom"),
                    getOptifineMapEntry("knob_right", "lock")
            );

            case "horse", "horse_armor", "skeleton_horse", "zombie_horse" -> genericHorse;
            case "donkey", "mule" -> new HashMap<>(genericHorse) {{
                putAll(Map.ofEntries(
                        getOptifineMapEntry("right_chest"),
                        getOptifineMapEntry("left_chest"),
                        getOptifineMapEntry("body")
                ));
            }};
            case "zombie_villager" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("headwear", "hat"),
                    //getOptifineMapEntry("hat_rim"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("left_arm"),
                    getOptifineMapEntry("right_arm"),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("right_leg")
            );

            case "evoker", "illusioner", "pillager", "vindicator" -> genericIllager;
            case "llama", "llama_decor", "trader_llama", "trader_llama_decor" -> genericLlama;
            case "armor_stand" -> new HashMap<>(genericNonPlayerBiped) {{
                putAll(Map.ofEntries(
                        getOptifineMapEntry("right", "right_body_stick"),
                        getOptifineMapEntry("left", "left_body_stick"),
                        getOptifineMapEntry("waist", "shoulder_stick"),//todo probably swap with body
                        getOptifineMapEntry("base", "base_plate")
                ));
            }};
            //# axolotl                  head, body, leg1 ... leg4, tail, top_gills, left_gills, right_gills
            case "axolotl" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("leg1", "right_hind_leg"),
                    getOptifineMapEntry("leg2", "left_hind_leg"),
                    getOptifineMapEntry("leg3", "right_front_leg"),
                    getOptifineMapEntry("leg4", "left_front_leg"),
                    getOptifineMapEntry("right_gills"),
                    getOptifineMapEntry("top_gills"),
                    getOptifineMapEntry("left_gills"),
                    getOptifineMapEntry("tail")
            );
            case "bat" -> Map.ofEntries(
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("right_wing"),
                    getOptifineMapEntry("left_wing"),
                    getOptifineMapEntry("outer_right_wing", "right_wing_tip"),
                    getOptifineMapEntry("outer_left_wing", "left_wing_tip"),
                    //feet added to new bat
                    getOptifineMapEntry("feet")

            );
            case "bee" -> Map.ofEntries(
                    getOptifineMapEntry("body", "bone"),
                    getOptifineMapEntry("torso", "body"),
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
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("tail"),
                    getOptifineMapEntry("tail_fin"),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("right_fin"),
                    getOptifineMapEntry("left_fin"),
                    getOptifineMapEntry("back_fin")
            );
            case "elder_guardian", "guardian" -> Map.ofEntries(
                    //# guardian                 body, eye, spine1 ... spine12, tail1 ... tail3
                    getOptifineMapEntry("tail1", "tail0"),
                    getOptifineMapEntry("tail2", "tail1"),
                    getOptifineMapEntry("tail3", "tail2"),
                    getOptifineMapEntry("body", "head"),
                    getOptifineMapEntry("eye"),

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
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("leg1", "right_hind_leg"),
                    getOptifineMapEntry("leg2", "left_hind_leg"),
                    getOptifineMapEntry("leg3", "right_front_leg"),
                    getOptifineMapEntry("leg4", "left_front_leg"),
                    getOptifineMapEntry("tail")
            );
            case "frog" -> Map.ofEntries(//TODO INVESTIGATE IF CORRECT ABOUT OPTIFINE MAPPING
                    //getOptifineMapEntry("root","root",List.of("body","left_leg","right_leg")),//,"!left_leg","!right_leg")),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("body"),//,"left_leg","right_leg")),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("right_leg"),
                    getOptifineMapEntry("croaking_body"),
                    getOptifineMapEntry("left_arm"),
                    getOptifineMapEntry("right_arm"),
                    getOptifineMapEntry("tongue"),
                    getOptifineMapEntry("eyes")
            );
            case "goat" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
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
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("body"),
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
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("tail", "tail_base"),
                    getOptifineMapEntry("tail2", "tail_tip"),
                    getOptifineMapEntry("left_wing", "left_wing_base"),
                    getOptifineMapEntry("right_wing", "right_wing_base"),
                    getOptifineMapEntry("left_wing_tip"),
                    getOptifineMapEntry("right_wing_tip")
            );
            case "parrot" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("tail"),
                    getOptifineMapEntry("left_wing"),
                    getOptifineMapEntry("right_wing"),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("right_leg")
            );
            case "puffer_fish_big" -> Map.ofEntries(
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
            case "puffer_fish_medium" -> Map.ofEntries(
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
            case "puffer_fish_small" -> Map.ofEntries(
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
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("leg1", "right_hind_leg"),
                    getOptifineMapEntry("leg2", "left_hind_leg"),
                    getOptifineMapEntry("leg3", "right_front_leg"),
                    getOptifineMapEntry("leg4", "left_front_leg"),
                    getOptifineMapEntry("jaw", "mouth"),
                    getOptifineMapEntry("neck")
            );
            case "salmon" -> Map.ofEntries(
                    getOptifineMapEntry("body_front"),
                    getOptifineMapEntry("body_back"),
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
            case "shulker_box" -> Map.ofEntries(
                    getOptifineMapEntry("lid"),
                    getOptifineMapEntry("base")
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
                    getOptifineMapEntry("body"),
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
                    getOptifineMapEntry("body", "EMF_root"),//body = root for some reason because we don't need things to make sense when it comes to optifine
                    getOptifineMapEntry("EMPTY", "body"),//EMPTY is important
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
                    getOptifineMapEntry("body", "bone"),
                    getOptifineMapEntry("torso", "body"),
                    getOptifineMapEntry("head"),
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
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("headwear", "hat"),
                    getOptifineMapEntry("headwear2", "hat_rim"),
                    getOptifineMapEntry("bodywear", "jacket"),
                    getOptifineMapEntry("body"),
                    getOptifineMapEntry("arms"),
                    getOptifineMapEntry("right_leg"),
                    getOptifineMapEntry("left_leg"),
                    getOptifineMapEntry("nose"),
                    getOptifineMapEntry("mole")
            );
            //# wither                   body1 ... body3, head1 ... head3
            case "wither", "wither_armor" -> Map.ofEntries(
                    getOptifineMapEntry("body1", "shoulders"),
                    getOptifineMapEntry("body2", "ribcage"),
                    getOptifineMapEntry("body3", "tail"),
                    getOptifineMapEntry("head1", "center_head"),
                    getOptifineMapEntry("head2", "right_head"),
                    getOptifineMapEntry("head3", "left_head")
            );

            case "dragon" -> Map.ofEntries(
                    getOptifineMapEntry("head"),
                    getOptifineMapEntry("jaw", "jaw"),
                    getOptifineMapEntry("spine", "neck"),
                    getOptifineMapEntry("body", "body"),

                    getOptifineMapEntry("left_wing"),
                    getOptifineMapEntry("left_wing_tip", "left_wing_tip"),
                    getOptifineMapEntry("right_wing"),
                    getOptifineMapEntry("right_wing_tip", "right_wing_tip"),

                    getOptifineMapEntry("front_left_leg", "left_front_leg"),
                    getOptifineMapEntry("front_left_shin", "left_front_leg_tip"),
                    getOptifineMapEntry("front_left_foot", "left_front_foot"),

                    getOptifineMapEntry("back_left_leg", "left_hind_leg"),
                    getOptifineMapEntry("back_left_shin", "left_hind_leg_tip"),
                    getOptifineMapEntry("back_left_foot", "left_hind_foot"),

                    getOptifineMapEntry("front_right_leg", "right_front_leg"),
                    getOptifineMapEntry("front_right_shin", "right_front_leg_tip"),
                    getOptifineMapEntry("front_right_foot", "right_front_foot"),

                    getOptifineMapEntry("back_right_leg", "right_hind_leg"),
                    getOptifineMapEntry("back_right_shin", "right_hind_leg_tip"),
                    getOptifineMapEntry("back_right_foot", "right_hind_foot")
            );
            case "player", "player_slim" -> genericPlayerBiped;

//# boat                     bottom, back, front, right, left, paddle_left, paddle_right, bottom_no_water
            case "boat" -> Map.ofEntries(
                    getOptifineMapEntry("bottom"),//todo check
                    getOptifineMapEntry("back"),
                    getOptifineMapEntry("front"),
                    getOptifineMapEntry("right"),
                    getOptifineMapEntry("left"),
                    getOptifineMapEntry("paddle_left", "left_paddle"),
                    getOptifineMapEntry("paddle_right", "right_paddle"),
                    getOptifineMapEntry("bottom_no_water", "water_patch")//todo check
            );
//# banner                   slate, stand, top
            case "banner" -> Map.ofEntries(
                    getOptifineMapEntry("slate", "flag"),
                    getOptifineMapEntry("stand", "pole"),
                    getOptifineMapEntry("top", "bar")
            );
//# bed                      head, foot, leg1 ... leg4
            case "bed_head" -> Map.ofEntries(
                    getOptifineMapEntry("head", "main"),
                    getOptifineMapEntry("leg1", "left_leg"),//todo check
                    getOptifineMapEntry("leg2", "right_leg")//todo check
            );
            case "bed_foot" -> Map.ofEntries(
                    getOptifineMapEntry("foot", "main"),
                    getOptifineMapEntry("leg3", "left_leg"),//todo check
                    getOptifineMapEntry("leg4", "right_leg")//todo check
            );
//# bell                     body
            case "bell" -> Map.ofEntries(
                    getOptifineMapEntry("body", "bell_body")
            );
//# chest_boat               bottom, back, front, right, left, paddle_left, paddle_right, bottom_no_water, chest_base, chest_lid, chest_knob
            case "chest_boat" -> Map.ofEntries(
                    getOptifineMapEntry("bottom"),//todo check
                    getOptifineMapEntry("back"),
                    getOptifineMapEntry("front"),
                    getOptifineMapEntry("right"),
                    getOptifineMapEntry("left"),
                    getOptifineMapEntry("paddle_left", "left_paddle"),
                    getOptifineMapEntry("paddle_right", "right_paddle"),
                    getOptifineMapEntry("bottom_no_water", "water_patch"),//todo check

                    getOptifineMapEntry("chest_base", "chest_bottom"),
                    getOptifineMapEntry("chest_lid"),
                    getOptifineMapEntry("chest_knob", "chest_lock")
            );
//# raft                     bottom, paddle_left, paddle_right
            case "raft" -> Map.ofEntries(
                    getOptifineMapEntry("bottom"),
                    getOptifineMapEntry("paddle_left", "left_paddle"),
                    getOptifineMapEntry("paddle_right", "right_paddle")
            );
//# chest_raft               bottom, paddle_left, paddle_right, chest_base, chest_lid, chest_knob
            case "chest_raft" -> Map.ofEntries(
                    getOptifineMapEntry("bottom"),
                    getOptifineMapEntry("paddle_left", "left_paddle"),
                    getOptifineMapEntry("paddle_right", "right_paddle"),

                    getOptifineMapEntry("chest_base", "chest_bottom"),
                    getOptifineMapEntry("chest_lid"),
                    getOptifineMapEntry("chest_knob", "chest_lock")
            );
//# chest_minecart           bottom, back, front, right, left
//# command_block_minecart   bottom, back, front, right, left
//# spawner_minecart         bottom, back, front, right, left
//# tnt_minecart             bottom, back, front, right, left
//# furnace_minecart         bottom, back, front, right, left
//# hopper_minecart          bottom, back, front, right, left
//# minecart                 bottom, back, front, right, left
            case "minecart", "chest_minecart", "command_block_minecart", "spawner_minecart", "tnt_minecart", "furnace_minecart", "hopper_minecart" ->
                    genericMinecart;
//# conduit                  base, eye, cage, wind
            case "conduit_cage" -> Map.ofEntries(getOptifineMapEntry("cage", "shell"));
            case "conduit_eye" -> Map.ofEntries(getOptifineMapEntry("eye"));
            case "conduit_shell" -> Map.ofEntries(getOptifineMapEntry("base", "shell"));
            case "conduit_wind" -> Map.ofEntries(getOptifineMapEntry("wind"));
//# decorated_pot            neck, front, back, left, right, top, bottom
            case "decorated_pot_base" -> Map.ofEntries(
                    getOptifineMapEntry("neck"),
                    getOptifineMapEntry("top"),
                    getOptifineMapEntry("bottom")
            );
            case "decorated_pot_sides" -> Map.ofEntries(
                    getOptifineMapEntry("front"),
                    getOptifineMapEntry("back"),
                    getOptifineMapEntry("left"),
                    getOptifineMapEntry("right")
            );
//# enchanting_book          cover_right, cover_left, pages_right, pages_left, flipping_page_right, flipping_page_left, book_spine
//# lectern_book             cover_right, cover_left, pages_right, pages_left, flipping_page_right, flipping_page_left, book_spine
            case "book" -> Map.ofEntries(
//                    getOptifineMapEntry("cover_right", "right_lid"),
//                    getOptifineMapEntry("cover_left", "left_lid"),
//                    getOptifineMapEntry("pages_right", "right_pages"),
//                    getOptifineMapEntry("pages_left", "left_pages"),
//                    getOptifineMapEntry("flipping_page_left", "flip_page2"),
//                    getOptifineMapEntry("flipping_page_right", "flip_page1"),
//                    getOptifineMapEntry("book_spine", "seam")
                    getOptifineMapEntry("cover_left", "right_lid"),
                    getOptifineMapEntry("cover_right", "left_lid"),
                    getOptifineMapEntry("pages_left", "right_pages"),
                    getOptifineMapEntry("pages_right", "left_pages"),
                    getOptifineMapEntry("flipping_page_right", "flip_page1"),
                    getOptifineMapEntry("flipping_page_left", "flip_page2"),
                    getOptifineMapEntry("book_spine", "seam")
            );
//# end_crystal              cube, glass, base
            case "end_crystal" -> Map.ofEntries(
                    getOptifineMapEntry("cube"),
                    getOptifineMapEntry("glass"),
                    getOptifineMapEntry("base")
            );

//# hanging_sign             board, plank, chains, chain_left1, chain_left2, chain_right1, chain_right2, chains_v
            case "hanging_sign" -> Map.ofEntries(
                    getOptifineMapEntry("board"),
                    getOptifineMapEntry("plank"),
                    getOptifineMapEntry("chains", "normalChains"),
                    getOptifineMapEntry("chain_left1", "chainL1"),
                    getOptifineMapEntry("chain_left2", "chainL2"),
                    getOptifineMapEntry("chain_right1", "chainR1"),
                    getOptifineMapEntry("chain_right2", "chainR2"),
                    getOptifineMapEntry("chains_v", "vChains")
            );
//# lead_knot                knot
            case "lead_knot" -> Map.ofEntries(getOptifineMapEntry("knot"));//todo possibly needs root assigned);
//# sign                     board, stick
            case "sign" -> Map.ofEntries(
                    getOptifineMapEntry("board", "EMF_root"),//todo check
                    //getOptifineMapEntry("EMPTY","sign"),//todo check
                    getOptifineMapEntry("stick")
            );
//# trident                  body
            case "trident" -> Map.ofEntries(getOptifineMapEntry("body", "EMF_root"));//todo check

            default -> null;
        };


    }

    //
    //this would make a usable mapping of the given model but with no part name changing as it would not be optifine customized
    public static Map<String, String> exploreProvidedEntityModelAndExportIfNeeded(ModelPart originalModel, String mobName, @Nullable Map<String, String> mobMap) {

        if (UNKNOWN_MODEL_MAP_CACHE.containsKey(mobName))
            return UNKNOWN_MODEL_MAP_CACHE.get(mobName);

        if (originalModel == null) {
            EMFUtils.logError("model part was null and not already mapped in exploreProvidedEntityModel() EMF");
            return Map.of();
        }


        Map<String, String> detailsMap = new HashMap<>();
        boolean known = mobMap != null;
        if (!known) {
            mobMap = new HashMap<>();
            mapThisAndChildren("root", originalModel, mobMap, detailsMap);
        }
        //cache result;
        UNKNOWN_MODEL_MAP_CACHE.put(mobName, mobMap);
        if (EMFConfig.getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE) {
            StringBuilder mapString = new StringBuilder();
            mapString.append(" |-[optifine/cem/").append(mobName).append(".jem]\n");
            mobMap.forEach((key, entry) -> {
                mapString.append(" | |-[").append("root".equals(key) ? "(optional) " : "").append("part=").append(key).append("]\n");
                mapString.append(detailsMap.get(key));
            });
            mapString.append("  \\-\\{{end of model}}");

            if (known) {
                EMFUtils.log("OptiFine specified model detected, Mapping now...\n" + mapString);
            } else {
                EMFUtils.log("Unknown possibly modded model detected, Mapping now...\n" + mapString);
            }

            if (EMFConfig.getConfig().modelExportMode.doesJems()) {
                EMFUtils.log("creating example .jem file for " + mobName);
                EMFJemData.EMFJemPrinter jemPrinter = new EMFJemData.EMFJemPrinter();
                int[] textureSize = null;
                for (Map.Entry<String, String> entry :
                        mobMap.entrySet()) {
                    if (!"root".equals(entry.getKey())) {
                        EMFPartData.EMFPartPrinter partPrinter = new EMFPartData.EMFPartPrinter();
                        partPrinter.part = entry.getKey();
                        partPrinter.id = entry.getKey();
                        ModelPart vanillaModelPart = getChildByName(entry.getValue(), originalModel);
                        if (vanillaModelPart != null) {
                            //invert x and y's
                            partPrinter.translate = new float[]{
                                    vanillaModelPart.pivotX,
                                    -24 + vanillaModelPart.pivotY,
                                    -vanillaModelPart.pivotZ};
//                            partPrinter.rotate = new float[]{
//                                    (float) Math.toDegrees(vanillaModelPart.pitch),
//                                    (float) Math.toDegrees(vanillaModelPart.yaw),
//                                    -(float) Math.toDegrees(vanillaModelPart.roll)};
                            partPrinter.scale = vanillaModelPart.xScale;
                            partPrinter.textureSize = ((EMFTextureSizeSupplier) vanillaModelPart).emf$getTextureSize();
                            textureSize = partPrinter.textureSize;
                            //List<ModelPart.Cuboid> cuboids = vanillaModelPart.cuboids;
                            for (ModelPart.Cuboid cube :
                                    vanillaModelPart.cuboids) {
                                EMFBoxData.EMFBoxPrinter boxPrinter = new EMFBoxData.EMFBoxPrinter();
                                boxPrinter.coordinates = new float[]{
                                        cube.minX,
                                        cube.minY,
                                        cube.minZ,
                                        cube.maxX - cube.minX,
                                        cube.maxY - cube.minY,
                                        cube.maxZ - cube.minZ};

                                boxPrinter.textureOffset = ((EMFTextureUVSupplier) cube).emf$getTextureUV();

                                //invert x and y
                                boxPrinter.coordinates[0] = -boxPrinter.coordinates[0] - boxPrinter.coordinates[3] - partPrinter.translate[0];
                                boxPrinter.coordinates[1] = -boxPrinter.coordinates[1] - boxPrinter.coordinates[4] - partPrinter.translate[1];

                                boxPrinter.coordinates[2] = boxPrinter.coordinates[2] - partPrinter.translate[2];

                                //add to array
                                partPrinter.boxes = Arrays.copyOf(partPrinter.boxes, partPrinter.boxes.length + 1);
                                partPrinter.boxes[partPrinter.boxes.length - 1] = boxPrinter;
                            }

                        }
                        jemPrinter.models.add(partPrinter);
                    }
                }
                if (textureSize == null) {
                    textureSize = new int[]{64, 32};
                }
                jemPrinter.textureSize = textureSize;

                String path = EMFVersionDifferenceManager.getConfigDirectory().toFile().getParent() + "/emf/export/" + mobName + ".jem";
                File outFile = new File(path);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                if (!outFile.getParentFile().exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    outFile.getParentFile().mkdirs();
                }
                try {
                    FileWriter fileWriter = new FileWriter(outFile);
                    fileWriter.write(gson.toJson(jemPrinter));
                    fileWriter.close();

                    EMFUtils.log(".jem file creation succeeded for [" + path + "]");
                } catch (IOException e) {
                    EMFUtils.log(".jem file creation failed for [" + path + "]");
                }
            }
        }
        return mobMap;
    }


    private static ModelPart getChildByName(String name, ModelPart part) {
        if (part.hasChild(name)) return part.getChild(name);
        for (ModelPart childPart :
                part.children.values()) {
            ModelPart possibleReturn = getChildByName(name, childPart);
            if (possibleReturn != null) return possibleReturn;
        }
        return null;
    }

    private static void mapThisAndChildren(String partName, ModelPart originalModel, Map<String, String> newMap, Map<String, String> detailsMap) {
        //iterate over children while collecting their names in a list
        for (Map.Entry<String, ModelPart> entry :
                originalModel.children.entrySet()) {
            mapThisAndChildren(entry.getKey(), entry.getValue(), newMap, detailsMap);
        }
        //add this part and its children names
        newMap.put(partName, partName);
        if (EMFConfig.getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE) {
            detailsMap.put(partName,
                    " | | |-pivots=" + originalModel.pivotX + ", " + originalModel.pivotY + ", " + originalModel.pivotZ +
                            "\n | | |-rotations=" + Math.toDegrees(originalModel.pitch) + ", " + Math.toDegrees(originalModel.yaw) + ", " + Math.toDegrees(originalModel.roll) +
                            "\n | | |-scales=" + originalModel.xScale + ", " + originalModel.yScale + ", " + originalModel.zScale +
                            "\n | |  \\visibles=" + originalModel.visible + ", " + originalModel.hidden + "\n"
            );
        }
    }


}
