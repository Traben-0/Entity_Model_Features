package traben.entity_model_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.model.ModelPart;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
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
import java.util.function.Consumer;

public class EMFOptiFinePartNameMappings {

    public static final Map<String, Map<String, String>> UNKNOWN_MODEL_MAP_CACHE = new HashMap<>();
    public static final Map<String, Map<String, String>> OPTIFINE_MODEL_MAP_CACHE = new HashMap<>();

    public static final Map<String, String> genericNonPlayerBiped = Map.ofEntries(
            getOptifineMapEntry("head"),
            getOptifineMapEntry("headwear", "hat"),
            getOptifineMapEntry("body"),
            getOptifineMapEntry("left_arm"),
            getOptifineMapEntry("right_arm"),
            getOptifineMapEntry("left_leg"),
            getOptifineMapEntry("right_leg")
    );


    static {
        //# horse                    , back_left_leg, back_right_leg, front_left_leg, front_right_leg,
        //#                          child_back_left_leg, child_back_right_leg, child_front_left_leg, child_front_right_leg
        var genericHorse = Map.ofEntries(
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
        var genericPlayerBiped = Map.ofEntries(
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
        var genericPiglinBiped = Map.ofEntries(
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


        optifineModels("villager", "wandering_trader")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("headwear", "hat"),
                        getOptifineMapEntry("headwear2", "hat_rim"),
                        getOptifineMapEntry("bodywear", "jacket"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("arms"),
                        getOptifineMapEntry("right_leg"),
                        getOptifineMapEntry("left_leg"),
                        getOptifineMapEntry("nose"),
                        getOptifineMapEntry("root")));
        optifineModels("iron_golem")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("left_arm"),
                        getOptifineMapEntry("right_arm"),
                        getOptifineMapEntry("left_leg"),
                        getOptifineMapEntry("right_leg"),
                        getOptifineMapEntry("root")));

        optifineModels("spider", "cave_spider")
                .accept(Map.ofEntries(
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
                        getOptifineMapEntry("leg8", "left_front_leg"),
                        getOptifineMapEntry("root")));

        var genericQuadraped = Map.ofEntries(
                getOptifineMapEntry("head"),
                getOptifineMapEntry("body"),
                getOptifineMapEntry("leg1", "right_hind_leg"),
                getOptifineMapEntry("leg2", "left_hind_leg"),
                getOptifineMapEntry("leg3", "right_front_leg"),
                getOptifineMapEntry("leg4", "left_front_leg"));
        var genericLlama = new HashMap<>(genericQuadraped) {{
            putAll(Map.ofEntries(
                    getOptifineMapEntry("chest_left", "left_chest"),
                    getOptifineMapEntry("chest_right", "right_chest")
            ));

        }};

        optifineModels("sheep", "cow", "mooshroom", "panda", "pig", "pig_saddle", "polar_bear", "sheep_wool")
                .accept(genericQuadraped);

        optifineModels("creeper", "creeper_charge").accept(
                new HashMap<>(genericQuadraped) {{
                    put("root", "root");
                }}
        );


        optifineModels("zombie", "husk", "drowned", "drowned_outer", "enderman", "giant", "skeleton", "stray", "stray_outer", "wither_skeleton", "zombie_pigman")
                .accept(
                        genericNonPlayerBiped);


        optifineModels("piglin", "piglin_brute", "zombified_piglin")
                .accept(genericPiglinBiped);

        //what

        optifineModels("allay", "vex")
                .accept(
                        Map.ofEntries(//# allay                    head, body, left_arm, right_arm, left_wing, right_wing
                                getOptifineMapEntry("head"),
                                getOptifineMapEntry("body"),
                                getOptifineMapEntry("left_arm"),
                                getOptifineMapEntry("right_arm"),
                                getOptifineMapEntry("left_wing"),
                                getOptifineMapEntry("right_wing"),
                                getOptifineMapEntry("root")
                        ));


        optifineModels("squid", "glow_squid")
                .accept(Map.ofEntries(//body, tentacle1 ... tentacle8
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("tentacle1", "tentacle0"),
                        getOptifineMapEntry("tentacle2", "tentacle1"),
                        getOptifineMapEntry("tentacle3", "tentacle2"),
                        getOptifineMapEntry("tentacle4", "tentacle3"),
                        getOptifineMapEntry("tentacle5", "tentacle4"),
                        getOptifineMapEntry("tentacle6", "tentacle5"),
                        getOptifineMapEntry("tentacle7", "tentacle6"),
                        getOptifineMapEntry("tentacle8", "tentacle7"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("ghast")
                .accept(Map.ofEntries(//body, tentacle1 ... tentacle9
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("tentacle1", "tentacle0"),
                        getOptifineMapEntry("tentacle2", "tentacle1"),
                        getOptifineMapEntry("tentacle3", "tentacle2"),
                        getOptifineMapEntry("tentacle4", "tentacle3"),
                        getOptifineMapEntry("tentacle5", "tentacle4"),
                        getOptifineMapEntry("tentacle6", "tentacle5"),
                        getOptifineMapEntry("tentacle7", "tentacle6"),
                        getOptifineMapEntry("tentacle8", "tentacle7"),
                        getOptifineMapEntry("tentacle9", "tentacle8"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("wolf", "wolf_collar", "wolf_armor")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("tail"),
                        getOptifineMapEntry("mane", "upper_body"),
                        getOptifineMapEntry("leg1", "right_hind_leg"),
                        getOptifineMapEntry("leg2", "left_hind_leg"),
                        getOptifineMapEntry("leg3", "right_front_leg"),
                        getOptifineMapEntry("leg4", "left_front_leg")
                ));


        optifineModels("shulker_bullet")
                .accept(Map.ofEntries(getOptifineMapEntry("bullet", "main"),
                        getOptifineMapEntry("root")));


        optifineModels("llama_spit")
                .accept(Map.ofEntries(getOptifineMapEntry("body", "main"),
                        getOptifineMapEntry("root")));


        optifineModels("wither_skull", "head_zombie", "head_wither_skeleton", "head_skeleton", "head_player", "head_creeper")
                .accept(
                        Map.ofEntries(
                                getOptifineMapEntry("head"),
                                getOptifineMapEntry("root")));


        optifineModels("head_piglin")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("left_ear"),
                        getOptifineMapEntry("right_ear"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("head_dragon")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("jaw"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("camel")
                .accept(Map.ofEntries(
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
                        getOptifineMapEntry("bridle"),
                        getOptifineMapEntry("root")

                ));


        optifineModels("sniffer")
                .accept(Map.ofEntries(
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
                ));


        optifineModels("chest", "ender_chest", "trapped_chest")
                .accept(Map.ofEntries(//lid, base, knob
                        getOptifineMapEntry("lid"),
                        getOptifineMapEntry("base", "bottom"),
                        getOptifineMapEntry("knob", "lock")
                ));


//optifineModels("chest_large")
//                .accept( {
//                EMFUtils.logError("CHEST_LARGE SHOULDN'T HAVE RUN");
//                 Map.of();
//            );


        optifineModels("double_chest_right", "trapped_double_chest_right", "ender_double_chest_right")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("lid_left", "lid"),
                        getOptifineMapEntry("base_left", "bottom"),
                        getOptifineMapEntry("knob_left", "lock")
                ));


        optifineModels("double_chest_left", "trapped_double_chest_left", "ender_double_chest_left")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("lid_right", "lid"),
                        getOptifineMapEntry("base_right", "bottom"),
                        getOptifineMapEntry("knob_right", "lock")
                ));


        optifineModels("horse", "horse_armor", "skeleton_horse", "zombie_horse")
                .accept(genericHorse);


        optifineModels("donkey", "mule")
                .accept(new HashMap<>(genericHorse) {{
                    putAll(Map.ofEntries(
                            getOptifineMapEntry("right_chest"),
                            getOptifineMapEntry("left_chest"),
                            getOptifineMapEntry("body")
                    ));
                }});


        optifineModels("zombie_villager")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("headwear", "hat"),
                        //getOptifineMapEntry("hat_rim"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("left_arm"),
                        getOptifineMapEntry("right_arm"),
                        getOptifineMapEntry("left_leg"),
                        getOptifineMapEntry("right_leg")
                ));


        optifineModels("evoker", "illusioner", "pillager", "vindicator")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head", "head"),
                        getOptifineMapEntry("headwear", "hat"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("nose"),
                        getOptifineMapEntry("arms"),
                        getOptifineMapEntry("left_arm"),
                        getOptifineMapEntry("right_arm"),
                        getOptifineMapEntry("left_leg"),
                        getOptifineMapEntry("right_leg"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("llama", "llama_decor", "trader_llama", "trader_llama_decor")
                .accept(genericLlama);


        optifineModels("armor_stand")
                .accept(new HashMap<>(genericNonPlayerBiped) {{
                    putAll(Map.ofEntries(
                            getOptifineMapEntry("right", "right_body_stick"),
                            getOptifineMapEntry("left", "left_body_stick"),
                            getOptifineMapEntry("waist", "shoulder_stick"),//todo probably swap with body
                            getOptifineMapEntry("base", "base_plate")
                    ));
                }});

        //# axolotl                  head, body, leg1 ... leg4, tail, top_gills, left_gills, right_gills

        optifineModels("axolotl")
                .accept(Map.ofEntries(
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
                ));


        optifineModels("bat")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("right_wing"),
                        getOptifineMapEntry("left_wing"),
                        getOptifineMapEntry("outer_right_wing", "right_wing_tip"),
                        getOptifineMapEntry("outer_left_wing", "left_wing_tip"),
                        //feet added to new bat
                        getOptifineMapEntry("feet"),
                        getOptifineMapEntry("root")

                ));


        optifineModels("bee")
                .accept(Map.ofEntries(
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
                ));


        optifineModels("blaze")
                .accept(Map.ofEntries(
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
                        getOptifineMapEntry("stick12", "part11"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("cat", "cat_collar", "ocelot")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("tail", "tail1"),
                        getOptifineMapEntry("tail2"),
                        getOptifineMapEntry("back_left_leg", "left_hind_leg"),
                        getOptifineMapEntry("back_right_leg", "right_hind_leg"),
                        getOptifineMapEntry("front_left_leg", "left_front_leg"),
                        getOptifineMapEntry("front_right_leg", "right_front_leg")
                ));


        optifineModels("chicken")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("right_leg"),
                        getOptifineMapEntry("left_leg"),
                        getOptifineMapEntry("right_wing"),
                        getOptifineMapEntry("left_wing"),
                        getOptifineMapEntry("bill", "beak"),
                        getOptifineMapEntry("chin", "red_thing")
                ));


        optifineModels("cod")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("nose"),
                        getOptifineMapEntry("tail", "tail_fin"),
                        getOptifineMapEntry("fin_right", "right_fin"),
                        getOptifineMapEntry("fin_left", "left_fin"),
                        getOptifineMapEntry("fin_back", "top_fin"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("dolphin")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("tail"),
                        getOptifineMapEntry("tail_fin"),
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("right_fin"),
                        getOptifineMapEntry("left_fin"),
                        getOptifineMapEntry("back_fin"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("elder_guardian", "guardian")
                .accept(Map.ofEntries(
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
                        getOptifineMapEntry("spine12", "spike11"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("endermite")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body1", "segment0"),
                        getOptifineMapEntry("body2", "segment1"),
                        getOptifineMapEntry("body3", "segment2"),
                        getOptifineMapEntry("body4", "segment3"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("evoker_fangs")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("base"),
                        getOptifineMapEntry("upper_jaw"),
                        getOptifineMapEntry("lower_jaw"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("fox")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("leg1", "right_hind_leg"),
                        getOptifineMapEntry("leg2", "left_hind_leg"),
                        getOptifineMapEntry("leg3", "right_front_leg"),
                        getOptifineMapEntry("leg4", "left_front_leg"),
                        getOptifineMapEntry("tail")
                ));


        optifineModels("frog")
                .accept(Map.ofEntries(//TODO INVESTIGATE IF CORRECT ABOUT OPTIFINE MAPPING
                        //getOptifineMapEntry("root","root",List.of("body","left_leg","right_leg")),//,"!left_leg","!right_leg")),
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("body"),//,"left_leg","right_leg")),
                        getOptifineMapEntry("left_leg"),
                        getOptifineMapEntry("right_leg"),
                        getOptifineMapEntry("croaking_body"),
                        getOptifineMapEntry("left_arm"),
                        getOptifineMapEntry("right_arm"),
                        getOptifineMapEntry("tongue"),
                        getOptifineMapEntry("eyes"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("goat")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("leg1", "right_hind_leg"),
                        getOptifineMapEntry("leg2", "left_hind_leg"),
                        getOptifineMapEntry("leg3", "right_front_leg"),
                        getOptifineMapEntry("leg4", "left_front_leg"),
                        getOptifineMapEntry("left_horn"),
                        getOptifineMapEntry("right_horn"),
                        getOptifineMapEntry("nose")
                ));


        optifineModels("hoglin", "zoglin")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("back_right_leg", "right_hind_leg"),
                        getOptifineMapEntry("back_left_leg", "left_hind_leg"),
                        getOptifineMapEntry("front_right_leg", "right_front_leg"),
                        getOptifineMapEntry("front_left_leg", "left_front_leg"),
                        getOptifineMapEntry("mane"),
                        getOptifineMapEntry("left_ear"),
                        getOptifineMapEntry("right_ear")
                ));


        optifineModels("magma_cube")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("core", "inside_cube"),
                        getOptifineMapEntry("segment1", "cube0"),
                        getOptifineMapEntry("segment2", "cube1"),
                        getOptifineMapEntry("segment3", "cube2"),
                        getOptifineMapEntry("segment4", "cube3"),
                        getOptifineMapEntry("segment5", "cube4"),
                        getOptifineMapEntry("segment6", "cube5"),
                        getOptifineMapEntry("segment7", "cube6"),
                        getOptifineMapEntry("segment8", "cube7"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("phantom")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("tail", "tail_base"),
                        getOptifineMapEntry("tail2", "tail_tip"),
                        getOptifineMapEntry("left_wing", "left_wing_base"),
                        getOptifineMapEntry("right_wing", "right_wing_base"),
                        getOptifineMapEntry("left_wing_tip"),
                        getOptifineMapEntry("right_wing_tip"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("parrot")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("tail"),
                        getOptifineMapEntry("left_wing"),
                        getOptifineMapEntry("right_wing"),
                        getOptifineMapEntry("left_leg"),
                        getOptifineMapEntry("right_leg"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("puffer_fish_big")
                .accept(Map.ofEntries(
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
                        getOptifineMapEntry("spikes_back_left", "left_back_fin"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("puffer_fish_medium")
                .accept(Map.ofEntries(
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
                        getOptifineMapEntry("spikes_back_left", "left_back_fin"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("puffer_fish_small")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("fin_right", "right_fin"),
                        getOptifineMapEntry("fin_left", "left_fin"),
                        getOptifineMapEntry("eye_right", "right_eye"),
                        getOptifineMapEntry("eye_left", "left_eye"),
                        getOptifineMapEntry("tail", "back_fin"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("rabbit")
                .accept(Map.ofEntries(
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
                ));


        optifineModels("ravager")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("leg1", "right_hind_leg"),
                        getOptifineMapEntry("leg2", "left_hind_leg"),
                        getOptifineMapEntry("leg3", "right_front_leg"),
                        getOptifineMapEntry("leg4", "left_front_leg"),
                        getOptifineMapEntry("jaw", "mouth"),
                        getOptifineMapEntry("neck"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("salmon")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body_front"),
                        getOptifineMapEntry("body_back"),
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("fin_back_1", "top_front_fin"),
                        getOptifineMapEntry("fin_back_2", "top_back_fin"),
                        getOptifineMapEntry("tail", "back_fin"),
                        getOptifineMapEntry("fin_right", "right_fin"),
                        getOptifineMapEntry("fin_left", "left_fin"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("shulker")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("lid"),
                        getOptifineMapEntry("base"),
                        getOptifineMapEntry("head")
                ));


        optifineModels("shulker_box")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("lid"),
                        getOptifineMapEntry("base")
                ));


        optifineModels("silverfish")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body1", "segment0"),
                        getOptifineMapEntry("body2", "segment1"),
                        getOptifineMapEntry("body3", "segment2"),
                        getOptifineMapEntry("body4", "segment3"),
                        getOptifineMapEntry("body5", "segment4"),
                        getOptifineMapEntry("body6", "segment5"),
                        getOptifineMapEntry("body7", "segment6"),
                        getOptifineMapEntry("wing1", "layer0"),
                        getOptifineMapEntry("wing2", "layer1"),
                        getOptifineMapEntry("wing3", "layer2"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("slime")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body", "cube"),
                        getOptifineMapEntry("left_eye"),
                        getOptifineMapEntry("right_eye"),
                        getOptifineMapEntry("mouth"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("slime_outer")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body", "cube")
                ));


        optifineModels("snow_golem")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body", "upper_body"),
                        getOptifineMapEntry("body_bottom", "lower_body"),
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("left_hand", "left_arm"),
                        getOptifineMapEntry("right_hand", "right_arm"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("strider", "strider_saddle")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("right_leg"),
                        getOptifineMapEntry("left_leg"),
                        getOptifineMapEntry("hair_right_top", "right_top_bristle"),
                        getOptifineMapEntry("hair_right_middle", "right_middle_bristle"),
                        getOptifineMapEntry("hair_right_bottom", "right_bottom_bristle"),
                        getOptifineMapEntry("hair_left_top", "left_top_bristle"),
                        getOptifineMapEntry("hair_left_middle", "left_middle_bristle"),
                        getOptifineMapEntry("hair_left_bottom", "left_bottom_bristle"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("tadpole")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body", "root"),//body = root for some reason because we don't need things to make sense when it comes to optifine
                        getOptifineMapEntry("EMPTY", "body"),//EMPTY is important
                        getOptifineMapEntry("tail")
                ));


        optifineModels("tropical_fish_a", "tropical_fish_pattern_a")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("tail"),
                        getOptifineMapEntry("fin_right", "right_fin"),
                        getOptifineMapEntry("fin_left", "left_fin"),
                        getOptifineMapEntry("fin_top", "top_fin"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("tropical_fish_b", "tropical_fish_pattern_b")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("tail"),
                        getOptifineMapEntry("fin_right", "right_fin"),
                        getOptifineMapEntry("fin_left", "left_fin"),
                        getOptifineMapEntry("fin_top", "top_fin"),
                        getOptifineMapEntry("fin_bottom", "bottom_fin"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("turtle")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("body2", "egg_belly"),
                        getOptifineMapEntry("leg1", "right_hind_leg"),
                        getOptifineMapEntry("leg2", "left_hind_leg"),
                        getOptifineMapEntry("leg3", "right_front_leg"),
                        getOptifineMapEntry("leg4", "left_front_leg")
                ));


        optifineModels("warden")
                .accept(Map.ofEntries(
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
                        getOptifineMapEntry("right_ribcage"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("witch")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("headwear", "hat"),
                        getOptifineMapEntry("headwear2", "hat_rim"),
                        getOptifineMapEntry("bodywear", "jacket"),
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("arms"),
                        getOptifineMapEntry("right_leg"),
                        getOptifineMapEntry("left_leg"),
                        getOptifineMapEntry("nose"),
                        getOptifineMapEntry("mole"),
                        getOptifineMapEntry("root")
                ));

        //# wither                   body1 ... body3, head1 ... head3

        optifineModels("wither", "wither_armor")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body1", "shoulders"),
                        getOptifineMapEntry("body2", "ribcage"),
                        getOptifineMapEntry("body3", "tail"),
                        getOptifineMapEntry("head1", "center_head"),
                        getOptifineMapEntry("head2", "right_head"),
                        getOptifineMapEntry("head3", "left_head"),
                        getOptifineMapEntry("root")
                ));


        optifineModels("dragon")
                .accept(Map.ofEntries(
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
                ));


        optifineModels("player", "player_slim")
                .accept(genericPlayerBiped);
        optifineModels("player_cape")
                .accept(Map.ofEntries(getOptifineMapEntry("cloak")));


//# boat                     bottom, back, front, right, left, paddle_left, paddle_right, bottom_no_water

        optifineModels("boat")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("bottom"),//todo check
                        getOptifineMapEntry("back"),
                        getOptifineMapEntry("front"),
                        getOptifineMapEntry("right"),
                        getOptifineMapEntry("left"),
                        getOptifineMapEntry("paddle_left", "left_paddle"),
                        getOptifineMapEntry("paddle_right", "right_paddle"),
                        getOptifineMapEntry("bottom_no_water", "water_patch")//todo check
                ));

//# banner                   slate, stand, top

        optifineModels("banner")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("slate", "flag"),
                        getOptifineMapEntry("stand", "pole"),
                        getOptifineMapEntry("top", "bar")
                ));

//# bed                      head, foot, leg1 ... leg4

        optifineModels("bed_head")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("head", "main"),
                        getOptifineMapEntry("leg1", "left_leg"),//todo check
                        getOptifineMapEntry("leg2", "right_leg")//todo check
                ));


        optifineModels("bed_foot")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("foot", "main"),
                        getOptifineMapEntry("leg3", "left_leg"),//todo check
                        getOptifineMapEntry("leg4", "right_leg")//todo check
                ));

//# bell                     body

        optifineModels("bell")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body", "bell_body")
                ));

//# chest_boat               bottom, back, front, right, left, paddle_left, paddle_right, bottom_no_water, chest_base, chest_lid, chest_knob

        optifineModels("chest_boat")
                .accept(Map.ofEntries(
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
                ));

//# raft                     bottom, paddle_left, paddle_right

        optifineModels("raft")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("bottom"),
                        getOptifineMapEntry("paddle_left", "left_paddle"),
                        getOptifineMapEntry("paddle_right", "right_paddle")
                ));

//# chest_raft               bottom, paddle_left, paddle_right, chest_base, chest_lid, chest_knob

        optifineModels("chest_raft")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("bottom"),
                        getOptifineMapEntry("paddle_left", "left_paddle"),
                        getOptifineMapEntry("paddle_right", "right_paddle"),

                        getOptifineMapEntry("chest_base", "chest_bottom"),
                        getOptifineMapEntry("chest_lid"),
                        getOptifineMapEntry("chest_knob", "chest_lock")
                ));

//# chest_minecart           bottom, back, front, right, left
//# command_block_minecart   bottom, back, front, right, left
//# spawner_minecart         bottom, back, front, right, left
//# tnt_minecart             bottom, back, front, right, left
//# furnace_minecart         bottom, back, front, right, left
//# hopper_minecart          bottom, back, front, right, left
//# minecart                 bottom, back, front, right, left

        optifineModels("minecart", "chest_minecart", "command_block_minecart", "spawner_minecart", "tnt_minecart", "furnace_minecart", "hopper_minecart")
                .accept(
                        Map.ofEntries(
                                getOptifineMapEntry("bottom"),
                                getOptifineMapEntry("back"),
                                getOptifineMapEntry("front"),
                                getOptifineMapEntry("right"),
                                getOptifineMapEntry("left"),
                                getOptifineMapEntry("root")
                        ));

//# conduit                  base, eye, cage, wind

        optifineModels("conduit_cage")
                .accept(Map.ofEntries(getOptifineMapEntry("cage", "shell")));


        optifineModels("conduit_eye")
                .accept(Map.ofEntries(getOptifineMapEntry("eye")));


        optifineModels("conduit_shell")
                .accept(Map.ofEntries(getOptifineMapEntry("base", "shell")));


        optifineModels("conduit_wind")
                .accept(Map.ofEntries(getOptifineMapEntry("wind")));

//# decorated_pot            neck, front, back, left, right, top, bottom

        optifineModels("decorated_pot_base")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("neck"),
                        getOptifineMapEntry("top"),
                        getOptifineMapEntry("bottom")
                ));


        optifineModels("decorated_pot_sides")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("front"),
                        getOptifineMapEntry("back"),
                        getOptifineMapEntry("left"),
                        getOptifineMapEntry("right")
                ));

//# enchanting_book          cover_right, cover_left, pages_right, pages_left, flipping_page_right, flipping_page_left, book_spine
//# lectern_book             cover_right, cover_left, pages_right, pages_left, flipping_page_right, flipping_page_left, book_spine

        optifineModels("book")
                .accept(Map.ofEntries(
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
                        getOptifineMapEntry("book_spine", "seam"),
                        getOptifineMapEntry("root")
                ));

//# end_crystal              cube, glass, base

        optifineModels("end_crystal")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("cube"),
                        getOptifineMapEntry("glass"),
                        getOptifineMapEntry("base")
                ));


//# hanging_sign             board, plank, chains, chain_left1, chain_left2, chain_right1, chain_right2, chains_v

        optifineModels("hanging_sign")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("board"),
                        getOptifineMapEntry("plank"),
                        getOptifineMapEntry("chains", "normalChains"),
                        getOptifineMapEntry("chain_left1", "chainL1"),
                        getOptifineMapEntry("chain_left2", "chainL2"),
                        getOptifineMapEntry("chain_right1", "chainR1"),
                        getOptifineMapEntry("chain_right2", "chainR2"),
                        getOptifineMapEntry("chains_v", "vChains")
                ));

//# lead_knot                knot

        optifineModels("lead_knot")
                .accept(Map.ofEntries(getOptifineMapEntry("knot"),
                        getOptifineMapEntry("root")));

//# sign                     board, stick

        optifineModels("sign")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("board", "root"),//todo check
                        //getOptifineMapEntry("EMPTY","sign"),//todo check
                        getOptifineMapEntry("stick")
                ));

//# trident                  body

        optifineModels("trident")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body", "pole"),
                        getOptifineMapEntry("root"),
                        getOptifineMapEntry("base"),
                        getOptifineMapEntry("left_spike"),
                        getOptifineMapEntry("middle_spike"),
                        getOptifineMapEntry("right_spike")));

        optifineModels("spectral_arrow", "arrow")
                .accept(Map.ofEntries(getOptifineMapEntry("body", "root")));

        optifineModels("shield")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("plate"),
                        getOptifineMapEntry("handle"),
                        getOptifineMapEntry("root")));
//todo check

//# breeze                   body, rods, head, wind_body, wind_middle, wind_bottom, wind_top
//# breeze_eyes              body, rods, head, wind_body, wind_middle, wind_bottom, wind_top
//# breeze_wind              body, rods, head, wind_body, wind_middle, wind_bottom, wind_top

        optifineModels("breeze", "breeze_eyes", "breeze_wind")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("body"),
                        getOptifineMapEntry("rods"),
                        getOptifineMapEntry("head"),
                        getOptifineMapEntry("wind_body"),
                        getOptifineMapEntry("wind_middle", "wind_mid"),
                        getOptifineMapEntry("wind_bottom"),
                        getOptifineMapEntry("wind_top"),
                        getOptifineMapEntry("root")
                ));

//# wind_charge              core, wind, cube1, cube2, charge

        optifineModels("wind_charge")
                .accept(Map.ofEntries(
                        getOptifineMapEntry("core", "projectile"),//maybe "bone"???
                        getOptifineMapEntry("wind"),
                        getOptifineMapEntry("cube1", "cube_r1"),
                        getOptifineMapEntry("cube2", "cube_r2"),
                        getOptifineMapEntry("charge", "wind_charge"),
                        getOptifineMapEntry("root")
                ));


    }

    public static Map.Entry<String, String> getOptifineMapEntry(String optifineName) {
        return new MutablePair<>(optifineName, optifineName);
    }

    public static Map.Entry<String, String> getOptifineMapEntry(String optifineName, String vanillaName) {
        return new MutablePair<>(optifineName, vanillaName);
    }

    public static Map<String, String> getMapOf(String mobName, @Nullable ModelPart root) {
        return getMapOf(mobName, root, true);
    }

    public static Map<String, String> getMapOf(String mobName, @Nullable ModelPart root, boolean exportOnlyFirstTime) {


        Map<String, String> knownMap;
        if (mobName.endsWith("_inner_armor") || mobName.endsWith("_outer_armor")) {
            knownMap = genericNonPlayerBiped;
        } else {
            knownMap = getKnownMap(mobName);
        }
        if (knownMap == null) {
            return root == null ? Map.of() : exploreProvidedEntityModelAndExportIfNeeded(root, mobName, null, exportOnlyFirstTime);
        }
        //trigger the export of the known model if we are exporting all
        if (EMF.config().getConfig().modelExportMode.doesAll()) {
            exportKnown(mobName, root, knownMap, exportOnlyFirstTime);
        }

        return knownMap;
    }

    private static void exportKnown(final String mobName, final @Nullable ModelPart root, final Map<String, String> knownMap, boolean exportOnlyFirstTime) {
        EMFUtils.log("Exporting/logging  model for " + mobName + " that has known OptiFine part names:");
        exploreProvidedEntityModelAndExportIfNeeded(root, mobName, knownMap, exportOnlyFirstTime);
        //also print out the model with its actual values in case a mod has added something
        EMFUtils.log("Additionally exporting/logging model for " + mobName + " again as though it did not have known OptiFine part names:\nThis might highlight some vanilla, or mod added, parts that are not usually exposed by OptiFine");

        var old = EMF.config().getConfig().modelExportMode;
        EMF.config().getConfig().modelExportMode = EMFConfig.ModelPrintMode.ALL_LOG_ONLY;
        try {
            exploreProvidedEntityModelAndExportIfNeeded(root, mobName, null, exportOnlyFirstTime);
        } catch (Exception e) {
            EMFUtils.logError("Error while exporting model for " + mobName + " again as though it did not have known OptiFine part names:");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }

        EMF.config().getConfig().modelExportMode = old;
    }

    private static @Nullable Map<String, String> getKnownMap(String mobName) {
        return OPTIFINE_MODEL_MAP_CACHE.get(mobName);
    }

    private static Consumer<Map<String, String>> optifineModels(String... modelNames) {
        return map -> {
            for (String key : modelNames) {
                if (OPTIFINE_MODEL_MAP_CACHE.containsKey(key)) {
                    EMFUtils.logError("OptiFine model map for " + key + " already exists, overwriting");
                }
                OPTIFINE_MODEL_MAP_CACHE.put(key, map);
            }
        };
    }

    //
    //this would make a usable mapping of the given model but with no part name changing as it would not be optifine customized
    public static Map<String, String> exploreProvidedEntityModelAndExportIfNeeded(ModelPart originalModel, String mobName, @Nullable Map<String, String> mobMap, boolean exportOnlyFirstTime) {
        if (UNKNOWN_MODEL_MAP_CACHE.containsKey(mobName) && exportOnlyFirstTime)
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
        if (EMF.config().getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE) {
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

            if (EMF.config().getConfig().modelExportMode.doesJems()) {
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
                            //these are inherited
//                            partPrinter.rotate = new float[]{
//                                    (float) Math.toDegrees(vanillaModelPart.pitch),
//                                    (float) Math.toDegrees(vanillaModelPart.yaw),
//                                    -(float) Math.toDegrees(vanillaModelPart.roll)};
                            partPrinter.scale = vanillaModelPart.xScale;
                            //get part size incase empty, though cuboids often have better ideas about this
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
                                //can be different from part
                                partPrinter.textureSize = ((EMFCuboidDataSupplier) cube).emf$getTextureXY();
                                boxPrinter.textureOffset = ((EMFCuboidDataSupplier) cube).emf$getTextureUV();
                                var adds = ((EMFCuboidDataSupplier) cube).emf$getSizeAdd();
                                if (adds != null) {
                                    if (adds[0] == adds[1] && adds[0] == adds[2]) {
                                        boxPrinter.sizeAdd = adds[0];
                                    } else {
                                        boxPrinter.sizeAddX = adds[0];
                                        boxPrinter.sizeAddY = adds[1];
                                        boxPrinter.sizeAddZ = adds[2];
                                    }
                                }

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
        if (EMF.config().getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE) {
            detailsMap.put(partName,
                    " | | |-pivots=" + originalModel.pivotX + ", " + originalModel.pivotY + ", " + originalModel.pivotZ +
                            "\n | | |-rotations=" + Math.toDegrees(originalModel.pitch) + ", " + Math.toDegrees(originalModel.yaw) + ", " + Math.toDegrees(originalModel.roll) +
                            "\n | | |-scales=" + originalModel.xScale + ", " + originalModel.yScale + ", " + originalModel.zScale +
                            "\n | |  \\visibles=" + originalModel.visible + ", " + originalModel.hidden + "\n"
            );
        }
    }


}
