package traben.entity_model_features.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.config.EMFConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.geom.ModelPart;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.IEMFCuboidDataSupplier;
import traben.entity_model_features.utils.IEMFTextureSizeSupplier;
import traben.entity_model_features.utils.IEMFUnmodifiedLayerRootGetter;
import traben.entity_texture_features.ETF;

public class EMFModelMappings {

    public static final Map<String, Map<String, String>> UNKNOWN_MODEL_MAP_CACHE = new HashMap<>();
    public static final Map<String, Map<String, String>> OPTIFINE_MODEL_MAP_CACHE = new HashMap<>();
//    public static Map<String, String> DEFAULT_TEXTURE_MAPPINGS;

    public static final Map<String, String> genericNonPlayerBiped = Map.ofEntries(
            partMapping("head"),
            partMapping("headwear", "hat"),
            partMapping("body"),
            partMapping("left_arm"),
            partMapping("right_arm"),
            partMapping("left_leg"),
            partMapping("right_leg")
    );

    static {
        initOptifineMappings();
//        initDefaultTextureMappings();
    }

//    private static String texture(String name){
//        return "minecraft:textures/entity/"+ name +".png";
//    }
//    private static String texture(String folder,String name){
//        return "minecraft:textures/entity/" + folder + "/" + name +".png";
//    }
//
//    private static Map.Entry<String,String> entry(String key, String value){
//        return new MutablePair<>(key,value);
//    }
//
//    private static void initDefaultTextureMappings() {
//        DEFAULT_TEXTURE_MAPPINGS = Map.ofEntries(
//                entry("allay", texture("allay","allay")),
//                entry("armor_stand", texture("armor_stand","wood")),
//                entry("armor_stand_small", texture("armor_stand","wood")),
//                entry("bat", texture("bat")),
//                entry("bell", texture("bell","bell_body")),
//                entry("blaze", texture("blaze")),
//                entry("breeze", texture("breeze","breeze")),
//                entry("breeze_eyes", texture("breeze","breeze_eyes")),
//                entry("breeze_wind", texture("breeze","breeze_wind")),
//                entry("camel", texture("camel","camel")),
//                entry("cat_collar", texture("cat","cat_collar")),
//                entry("cave_spider", texture("spider","cave_spider")),
//                entry("cod", texture("fish","cod")),
//                entry("creeper", texture("creeper","creeper")),
//                entry("creeper_charge", texture("creeper", "creeper_armor")),
//                entry("donkey", texture("horse","donkey")),
//                entry("dolphin", texture("dolphin")),
//                entry("drowned", texture("zombie", "drowned")),
//                entry("drowned_outer", texture("zombie", "drowned_outer_layer")),
//                entry("elder_guardian", texture("guardian_elder")),
//                entry("enchanting_book", texture("enchanting_table_book")),
//                entry("ender_chest", texture("chest","ender")),
//                entry("end_crystal", texture("end_crystal","end_crystal")),
//                entry("enderman", texture("enderman","enderman")),
//                entry("endermite", texture("endermite")),
//                entry("evoker", texture("illager", "evoker")),
//                entry("evoker_fangs", texture("illager", "evoker_fangs")),
//                entry("giant", texture("zombie", "zombie")),
//                entry("glow_squid", texture("squid", "glow_squid")),
//                entry("goat", texture("goat","goat")),
//                entry("guardian", texture("guardian")),
//                entry("head_creeper", texture("creeper","creeper")),
//                entry("head_piglin", texture("piglin", "piglin")),
//                entry("head_skeleton", texture("skeleton", "skeleton")),
//                entry("head_wither_skeleton", texture("skeleton", "wither_skeleton")),
//                entry("head_zombie", texture("zombie", "zombie")),
//                entry("hoglin", texture("hoglin","hoglin")),
//                entry("husk", texture("zombie", "husk")),
//                entry("illusioner", texture("illager", "illusioner")),
//                entry("lead_knot", texture("lead_knot")),
//                entry("lectern_book", texture("enchanting_table_book")),
//                entry("llama_spit", texture("llama","spit")),
//                entry("magma_cube", texture("slime","magmacube")),
//                entry("minecart", texture("minecart")),
//                entry("mule", texture("hosre","mule")),
//                entry("ocelot", texture("cat", "ocelot")),
//                entry("phantom", texture("phantom")),
//                entry("puffer_fish_big", texture("fish", "pufferfish")),
//                entry("puffer_fish_medium", texture("fish", "pufferfish")),
//                entry("puffer_fish_small", texture("fish", "pufferfish")),
//                entry("pig_saddle", texture("pig","pig_saddle")),
//                entry("piglin", texture("piglin", "piglin")),
//                entry("piglin_brute", texture("piglin", "piglin_brute")),
//                entry("pillager", texture("illager", "pillager")),
//                entry("polar_bear", texture("bear","polarbear")),
//                entry("ravager", texture("illager", "ravager")),
//                entry("salmon", texture("fish","salmon")),
//                entry("sheep", texture("sheep","sheep")),
//                entry("sheep_wool", texture("sheep","sheep_fur")),
//                entry("shulker_bullet", texture("shulker", "spark")),
//                entry("silverfish", texture("silverfish")),
//                entry("skeleton", texture("skeleton", "skeleton")),
//                entry("skeleton_horse", texture("horse","horse_skeleton")),
//                entry("slime", texture("slime","slime")),
//                entry("slime_outer", texture("slime","slime")),
//                entry("sniffer", texture("sniffer","sniffer")),
//                entry("snow_golem", texture("snow_golem")),
//                entry("spider", texture("spider","spider")),
//                entry("squid", texture("squid","squid")),
//                entry("stray", texture("skeleton", "stray")),
//                entry("stray_outer", texture("skeleton", "stray")),
//                entry("strider_saddle", texture("strider", "strider_saddle")),
//                entry("tadpole", texture("tadpole", "tadpole")),
//                entry("trader_llama_decor", texture("llama/decor","trader_llama")),
//                entry("trident", texture("trident")),
//                entry("turtle", texture("turtle","big_sea_turtle")),
//                entry("villager", texture("villager","villager")),
//                entry("vindicator", texture("illager","vindicator")),
//                entry("wandering_trader", texture("wandering_trader")),
//                entry("warden", texture("warden", "warden")),
//                entry("wind_charge", texture("projectiles", "wind_charge")),
//                entry("witch", texture("witch")),
//                entry("wither_skeleton", texture("skeleton", "wither_skeleton")),
//                entry("wolf_collar", texture("wolf", "wolf_collar")),
//                entry("zoglin", texture("hoglin", "zoglin")),
//                entry("zombie", texture("zombie","zombie")),
//                entry("zombie_horse", texture(" horse","horse_zombie")),
////                "zombie_pigman", texture(),
//                entry("zombie_villager", texture("zombie_villager", "zombie_villager")),
//                entry("zombified_piglin", texture("piglin", "zombified_piglin")),
//
//                //#if MC >= 12105
//                entry("cow", texture("cow", "temperate_cow")),
//                entry("warm_cow", texture("cow", "warm_cow")),
//                entry("cold_cow", texture("cow", "cold_cow")),
//                entry("pig", texture("pig","temperate_pig")),
//                entry("warm_pig", texture("pig","warm_pig")),
//                entry("cold_pig", texture("pig","cold_pig")),
//                entry("chicken", texture("chicken", "temperate_chicken")),
//                entry("warm_chicken", texture("chicken", "warm_chicken")),
//                entry("cold_chicken", texture("chicken", "cold_chicken"))
//                //#else
//                //$$ entry("cow", texture("cow", "cow")),
//                //$$ entry("pig", texture("pig","pig")),
//                //$$ entry("chicken", texture("chicken"))
//                //#endif
//        );
//    }

    private static void initOptifineMappings() {


        var genericHorse = Map.ofEntries(
                  partMapping("body", "body"),
                  partMapping("head", "head"),
                  partMapping("tail"),
                  partMapping("mane"),
                  partMapping("mouth", "upper_mouth"),
                  partMapping("left_ear"),
                  partMapping("right_ear"),
                  partMapping("neck", "head_parts"),
                  partMapping("back_left_leg", "left_hind_leg"),
                  partMapping("back_right_leg", "right_hind_leg"),
                  partMapping("front_left_leg", "left_front_leg"),
                  partMapping("front_right_leg", "right_front_leg")
                    //#if MC < 12105
                    //$$ , partMapping("headpiece", "head_saddle"),
                    //$$ partMapping("saddle"),
                    //$$ partMapping("noseband", "mouth_saddle_wrap"),
                    //$$ partMapping("right_rein", "right_saddle_line"),
                    //$$ partMapping("left_rein", "left_saddle_line"),
                    //$$ partMapping("right_bit", "right_saddle_mouth"),
                    //$$ partMapping("left_bit", "left_saddle_mouth")
                    //#endif

                    //#if MC < 12102
                    //$$ , partMapping("child_back_left_leg", "left_hind_baby_leg"),
                    //$$ partMapping("child_back_right_leg", "right_hind_baby_leg"),
                    //$$ partMapping("child_front_left_leg", "left_front_baby_leg"),
                    //$$ partMapping("child_front_right_leg", "right_front_baby_leg")
                    //#endif
          );
        var genericHorseSaddle = Map.ofEntries(
                partMapping("body", "body"),
                partMapping("head", "head"),
                partMapping("tail"),
                partMapping("saddle"),
                partMapping("mane"),
                partMapping("mouth", "upper_mouth"),
                partMapping("left_ear"),
                partMapping("right_ear"),
                partMapping("neck", "head_parts"),
                partMapping("noseband", "mouth_saddle_wrap"),
                partMapping("headpiece", "head_saddle"),
                partMapping("right_rein", "right_saddle_line"),
                partMapping("left_rein", "left_saddle_line"),
                partMapping("right_bit", "right_saddle_mouth"),
                partMapping("left_bit", "left_saddle_mouth"),
                partMapping("back_left_leg", "left_hind_leg"),
                partMapping("back_right_leg", "right_hind_leg"),
                partMapping("front_left_leg", "left_front_leg"),
                partMapping("front_right_leg", "right_front_leg")
        );
        var genericPlayerBiped = Map.ofEntries(
                partMapping("head"),
                partMapping("headwear", "hat"),
                partMapping("body"),
                partMapping("left_arm"),
                partMapping("right_arm"),
                partMapping("left_leg"),
                partMapping("right_leg"),
                partMapping("ear"),
                partMapping("left_sleeve"),
                partMapping("right_sleeve"),
                partMapping("left_pants"),
                partMapping("right_pants"),
                partMapping("jacket"),
                partMapping("cloak")
        );
        var genericPiglinBiped = Map.ofEntries(
                partMapping("head", "head"),
                partMapping("headwear", "hat"),
                partMapping("body"),
                partMapping("left_arm"),
                partMapping("right_arm"),
                partMapping("left_leg"),
                partMapping("ear"),
                partMapping("right_leg"),
                partMapping("left_ear"),
                partMapping("right_ear"),
                partMapping("left_sleeve"),
                partMapping("right_sleeve"),
                partMapping("left_pants"),
                partMapping("right_pants"),
                partMapping("jacket"),
                partMapping("cloak")
        );
        var genericQuadraped = Map.ofEntries(
                partMapping("head"),
                partMapping("body"),
                partMapping("leg1", "right_hind_leg"),
                partMapping("leg2", "left_hind_leg"),
                partMapping("leg3", "right_front_leg"),
                partMapping("leg4", "left_front_leg"));
        var genericLlama = new HashMap<>(genericQuadraped) {{
            putAll(Map.ofEntries(
                    partMapping("chest_left", "left_chest"),
                    partMapping("chest_right", "right_chest")
            ));
        }};

        OptifineMapper.models("villager", "wandering_trader")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("headwear", "hat"),
                        partMapping("headwear2", "hat_rim"),
                        partMapping("bodywear", "jacket"),
                        partMapping("body"),
                        partMapping("arms"),
                        partMapping("right_leg"),
                        partMapping("left_leg"),
                        partMapping("nose")));
        OptifineMapper.models("iron_golem")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("body"),
                        partMapping("left_arm"),
                        partMapping("right_arm"),
                        partMapping("left_leg"),
                        partMapping("right_leg")));
        OptifineMapper.models("spider", "cave_spider")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("neck", "body0"),
                        partMapping("body", "body1"),
                        partMapping("leg1", "right_hind_leg"),
                        partMapping("leg2", "left_hind_leg"),
                        partMapping("leg3", "right_middle_hind_leg"),
                        partMapping("leg4", "left_middle_hind_leg"),
                        partMapping("leg5", "right_middle_front_leg"),
                        partMapping("leg6", "left_middle_front_leg"),
                        partMapping("leg7", "right_front_leg"),
                        partMapping("leg8", "left_front_leg")));
        OptifineMapper.models("sheep", "cow", "warm_cow", "mooshroom", "panda", "pig", "cold_pig", "warm_pig", "pig_saddle", "polar_bear", "sheep_wool", "sheep_wool_undercoat", "sheep_baby_wool_undercoat")
                .parts(genericQuadraped);


        OptifineMapper.models("cold_cow")
                .parts( new HashMap<>(genericQuadraped) {{
                    putAll(Map.ofEntries(
                            partMapping("right_horn"),
                            partMapping("left_horn")));
                }});

        OptifineMapper.models("creeper", "creeper_charge")
                .parts(genericQuadraped);
        OptifineMapper.models("inner_armor","outer_armor","zombie", "husk", "drowned", "drowned_outer",
                        "enderman", "giant", "skeleton", "stray", "stray_outer", "wither_skeleton", "zombie_pigman", "bogged_outer",
                        "helmet", "helmet_baby", "chestplate", "chestplate_baby", "leggings", "leggings_baby", "boots", "boots_baby"
                        //#if MC >= 12109
                        , "helmet", "chestplate", "leggings", "boots"
                        //#endif
                )
                .parts(genericNonPlayerBiped);
        OptifineMapper.models("piglin", "piglin_brute", "zombified_piglin")
                .parts(genericPiglinBiped);
        OptifineMapper.models("allay", "vex")
                .parts(Map.ofEntries(
                                partMapping("head"),
                                partMapping("body"),
                                partMapping("left_arm"),
                                partMapping("right_arm"),
                                partMapping("left_wing"),
                                partMapping("right_wing")
                        ));
        OptifineMapper.models("squid", "glow_squid")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("tentacle1", "tentacle0"),
                        partMapping("tentacle2", "tentacle1"),
                        partMapping("tentacle3", "tentacle2"),
                        partMapping("tentacle4", "tentacle3"),
                        partMapping("tentacle5", "tentacle4"),
                        partMapping("tentacle6", "tentacle5"),
                        partMapping("tentacle7", "tentacle6"),
                        partMapping("tentacle8", "tentacle7")
                ));
        OptifineMapper.models("ghast")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("tentacle1", "tentacle0"),
                        partMapping("tentacle2", "tentacle1"),
                        partMapping("tentacle3", "tentacle2"),
                        partMapping("tentacle4", "tentacle3"),
                        partMapping("tentacle5", "tentacle4"),
                        partMapping("tentacle6", "tentacle5"),
                        partMapping("tentacle7", "tentacle6"),
                        partMapping("tentacle8", "tentacle7"),
                        partMapping("tentacle9", "tentacle8")
                ));
        OptifineMapper.models("happy_ghast", "happy_ghast_ropes")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("tentacle1", "tentacle0"),
                        partMapping("tentacle2", "tentacle1"),
                        partMapping("tentacle3", "tentacle2"),
                        partMapping("tentacle4", "tentacle3"),
                        partMapping("tentacle5", "tentacle4"),
                        partMapping("tentacle6", "tentacle5"),
                        partMapping("tentacle7", "tentacle6"),
                        partMapping("tentacle8", "tentacle7"),
                        partMapping("tentacle9", "tentacle8")
                ));
        OptifineMapper.models("happy_ghast_baby", "happy_ghast_baby_ropes")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("inner_body"),
                        partMapping("tentacle1", "tentacle0"),
                        partMapping("tentacle2", "tentacle1"),
                        partMapping("tentacle3", "tentacle2"),
                        partMapping("tentacle4", "tentacle3"),
                        partMapping("tentacle5", "tentacle4"),
                        partMapping("tentacle6", "tentacle5"),
                        partMapping("tentacle7", "tentacle6"),
                        partMapping("tentacle8", "tentacle7"),
                        partMapping("tentacle9", "tentacle8")
                ));
        OptifineMapper.models("happy_ghast_harness", "happy_ghast_baby_harness")
                .parts(Map.ofEntries(
                        partMapping("harness"),
                        partMapping("goggles")
                ));
        OptifineMapper.models("wolf", "wolf_collar", "wolf_armor")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("head"),
                        partMapping("tail"),
                        partMapping("mane", "upper_body"),
                        partMapping("leg1", "right_hind_leg"),
                        partMapping("leg2", "left_hind_leg"),
                        partMapping("leg3", "right_front_leg"),
                        partMapping("leg4", "left_front_leg")
                ));
        OptifineMapper.models("shulker_bullet")
                .parts(Map.ofEntries(
                        partMapping("bullet", "main")
                ));
        OptifineMapper.models("llama_spit")
                .parts(Map.ofEntries(
                        partMapping("body", "main")
                ));
        OptifineMapper.models("wither_skull", "head_zombie", "head_wither_skeleton", "head_skeleton", "head_player", "head_creeper")
                .parts(Map.ofEntries(
                                partMapping("head")
                ));
        OptifineMapper.models("head_piglin")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("left_ear"),
                        partMapping("right_ear")
                ));
        OptifineMapper.models("head_dragon")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("jaw")
                ));
        OptifineMapper.models("camel")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("hump"),
                        partMapping("tail"),
                        partMapping("head"),
                        partMapping("left_ear"),
                        partMapping("right_ear"),
                        partMapping("back_left_leg", "left_hind_leg"),
                        partMapping("back_right_leg", "right_hind_leg"),
                        partMapping("front_left_leg", "left_front_leg"),
                        partMapping("front_right_leg", "right_front_leg"),
                        partMapping("saddle"),
                        partMapping("reins"),
                        partMapping("bridle")
                ));
        OptifineMapper.models("sniffer")
                .parts(Map.ofEntries(
                        partMapping("bone"),
                        partMapping("body"),
                        partMapping("front_right_leg", "right_front_leg"),
                        partMapping("middle_right_leg", "right_mid_leg"),
                        partMapping("back_right_leg", "right_hind_leg"),
                        partMapping("front_left_leg", "left_front_leg"),
                        partMapping("middle_left_leg", "left_mid_leg"),
                        partMapping("back_left_leg", "left_hind_leg"),
                        partMapping("head"),
                        partMapping("left_ear"),
                        partMapping("right_ear"),
                        partMapping("nose"),
                        partMapping("lower_beak")
                ));
        OptifineMapper.models("chest", "ender_chest", "trapped_chest", "chest_left", "trapped_chest_left", "chest_right", "trapped_chest_right")
                .parts(Map.ofEntries(
                        partMapping("lid"),
                        partMapping("base", "bottom"),
                        partMapping("knob", "lock")
                ));
        OptifineMapper.models("double_chest_right", "trapped_double_chest_right", "ender_double_chest_right")
                .parts(Map.ofEntries(
                        //#if MC>=12102
                        partMapping("lid"),
                        partMapping("base", "bottom"),
                        partMapping("knob", "lock")
                        //#else
                        //$$ partMapping("lid_left", "lid"),
                        //$$ partMapping("base_left", "bottom"),
                        //$$ partMapping("knob_left", "lock")
                        //#endif
                ));
        OptifineMapper.models("double_chest_left", "trapped_double_chest_left", "ender_double_chest_left")
                .parts(Map.ofEntries(
                        //#if MC>=12102
                        partMapping("lid"),
                        partMapping("base", "bottom"),
                        partMapping("knob", "lock")
                        //#else
                        //$$ partMapping("lid_right", "lid"),
                        //$$ partMapping("base_right", "bottom"),
                        //$$ partMapping("knob_right", "lock")
                        //#endif
                ));

        OptifineMapper.models("horse", "horse_armor", "skeleton_horse", "zombie_horse")
                .parts(genericHorse);
        OptifineMapper.models("donkey", "mule")
                .parts(new HashMap<>(genericHorse) {{
                    putAll(Map.ofEntries(
                            partMapping("right_chest"),
                            partMapping("left_chest"),
                            partMapping("body")
                    ));
                }});
        OptifineMapper.models("horse_saddle", "mule_saddle", "donkey_saddle", "skeleton_horse_saddle", "zombie_horse_saddle")
                .parts(new HashMap<>(genericHorseSaddle));
        OptifineMapper.models("zombie_villager")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("headwear", "hat"),
                        partMapping("body"),
                        partMapping("left_arm"),
                        partMapping("right_arm"),
                        partMapping("left_leg"),
                        partMapping("right_leg")
                ));
        OptifineMapper.models("evoker", "illusioner", "pillager", "vindicator")
                .parts(Map.ofEntries(
                        partMapping("head", "head"),
                        partMapping("headwear", "hat"),
                        partMapping("body"),
                        partMapping("nose"),
                        partMapping("arms"),
                        partMapping("left_arm"),
                        partMapping("right_arm"),
                        partMapping("left_leg"),
                        partMapping("right_leg")
                ));
        OptifineMapper.models("llama", "llama_decor", "trader_llama", "trader_llama_decor")
                .parts(genericLlama);
        OptifineMapper.models("armor_stand", "armor_stand_small")
                .parts(new HashMap<>(genericNonPlayerBiped) {{
                    putAll(Map.ofEntries(
                            partMapping("right", "right_body_stick"),
                            partMapping("left", "left_body_stick"),
                            partMapping("waist", "shoulder_stick"),//todo probably swap with body
                            partMapping("base", "base_plate")
                    ));
                }});
        OptifineMapper.models("axolotl")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("body"),
                        partMapping("leg1", "right_hind_leg"),
                        partMapping("leg2", "left_hind_leg"),
                        partMapping("leg3", "right_front_leg"),
                        partMapping("leg4", "left_front_leg"),
                        partMapping("right_gills"),
                        partMapping("top_gills"),
                        partMapping("left_gills"),
                        partMapping("tail")
                ));
        OptifineMapper.models("bat")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("head"),
                        partMapping("right_wing"),
                        partMapping("left_wing"),
                        partMapping("outer_right_wing", "right_wing_tip"),
                        partMapping("outer_left_wing", "left_wing_tip"),
                        //feet added to new bat
                        partMapping("feet")
                ));
        OptifineMapper.models("bee")
                .parts(Map.ofEntries(
                        partMapping("body", "bone"),
                        partMapping("torso", "body"),
                        partMapping("right_wing"),
                        partMapping("left_wing"),
                        partMapping("front_legs"),
                        partMapping("middle_legs"),
                        partMapping("back_legs"),
                        partMapping("stinger"),
                        partMapping("left_antenna"),
                        partMapping("right_antenna")
                ));
        OptifineMapper.models("blaze")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("stick1", "part0"),
                        partMapping("stick2", "part1"),
                        partMapping("stick3", "part2"),
                        partMapping("stick4", "part3"),
                        partMapping("stick5", "part4"),
                        partMapping("stick6", "part5"),
                        partMapping("stick7", "part6"),
                        partMapping("stick8", "part7"),
                        partMapping("stick9", "part8"),
                        partMapping("stick10", "part9"),
                        partMapping("stick11", "part10"),
                        partMapping("stick12", "part11")
                ));
        OptifineMapper.models("cat", "cat_collar", "ocelot")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("body"),
                        partMapping("tail", "tail1"),
                        partMapping("tail2"),
                        partMapping("back_left_leg", "left_hind_leg"),
                        partMapping("back_right_leg", "right_hind_leg"),
                        partMapping("front_left_leg", "left_front_leg"),
                        partMapping("front_right_leg", "right_front_leg")
                ));
        OptifineMapper.models("chicken", "warm_chicken", "cold_chicken")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("body"),
                        partMapping("right_leg"),
                        partMapping("left_leg"),
                        partMapping("right_wing"),
                        partMapping("left_wing"),
                        partMapping("bill", "beak"),
                        partMapping("chin", "red_thing")
                ));
        OptifineMapper.models("cod")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("head"),
                        partMapping("nose"),
                        partMapping("tail", "tail_fin"),
                        partMapping("fin_right", "right_fin"),
                        partMapping("fin_left", "left_fin"),
                        partMapping("fin_back", "top_fin")
                ));
        OptifineMapper.models("dolphin")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("tail"),
                        partMapping("tail_fin"),
                        partMapping("head"),
                        partMapping("right_fin"),
                        partMapping("left_fin"),
                        partMapping("back_fin")
                ));
        OptifineMapper.models("elder_guardian", "guardian")
                .parts(Map.ofEntries(
                        // guardian                 body, eye, spine1 ... spine12, tail1 ... tail3
                        partMapping("tail1", "tail0"),
                        partMapping("tail2", "tail1"),
                        partMapping("tail3", "tail2"),
                        partMapping("body", "head"),
                        partMapping("eye"),

                        partMapping("spine1", "spike0"),
                        partMapping("spine2", "spike1"),
                        partMapping("spine3", "spike2"),
                        partMapping("spine4", "spike3"),
                        partMapping("spine5", "spike4"),
                        partMapping("spine6", "spike5"),
                        partMapping("spine7", "spike6"),
                        partMapping("spine8", "spike7"),
                        partMapping("spine9", "spike8"),
                        partMapping("spine10", "spike9"),
                        partMapping("spine11", "spike10"),
                        partMapping("spine12", "spike11")
                ));
        OptifineMapper.models("endermite")
                .parts(Map.ofEntries(
                        partMapping("body1", "segment0"),
                        partMapping("body2", "segment1"),
                        partMapping("body3", "segment2"),
                        partMapping("body4", "segment3")
                ));
        OptifineMapper.models("evoker_fangs")
                .parts(Map.ofEntries(
                        partMapping("base"),
                        partMapping("upper_jaw"),
                        partMapping("lower_jaw")
                ));
        OptifineMapper.models("fox")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("body"),
                        partMapping("leg1", "right_hind_leg"),
                        partMapping("leg2", "left_hind_leg"),
                        partMapping("leg3", "right_front_leg"),
                        partMapping("leg4", "left_front_leg"),
                        partMapping("tail")
                ));
        OptifineMapper.models("frog")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("body"),
                        partMapping("left_leg"),
                        partMapping("right_leg"),
                        partMapping("croaking_body"),
                        partMapping("left_arm"),
                        partMapping("right_arm"),
                        partMapping("tongue"),
                        partMapping("eyes")
                ));
        OptifineMapper.models("goat")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("body"),
                        partMapping("leg1", "right_hind_leg"),
                        partMapping("leg2", "left_hind_leg"),
                        partMapping("leg3", "right_front_leg"),
                        partMapping("leg4", "left_front_leg"),
                        partMapping("left_horn"),
                        partMapping("right_horn"),
                        partMapping("nose")
                ));
        OptifineMapper.models("hoglin", "zoglin")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("body"),
                        partMapping("back_right_leg", "right_hind_leg"),
                        partMapping("back_left_leg", "left_hind_leg"),
                        partMapping("front_right_leg", "right_front_leg"),
                        partMapping("front_left_leg", "left_front_leg"),
                        partMapping("mane"),
                        partMapping("left_ear"),
                        partMapping("right_ear")
                ));
        OptifineMapper.models("magma_cube")
                .parts(Map.ofEntries(
                        partMapping("core", "inside_cube"),
                        partMapping("segment1", "cube0"),
                        partMapping("segment2", "cube1"),
                        partMapping("segment3", "cube2"),
                        partMapping("segment4", "cube3"),
                        partMapping("segment5", "cube4"),
                        partMapping("segment6", "cube5"),
                        partMapping("segment7", "cube6"),
                        partMapping("segment8", "cube7")
                ));
        OptifineMapper.models("phantom")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("head"),
                        partMapping("tail", "tail_base"),
                        partMapping("tail2", "tail_tip"),
                        partMapping("left_wing", "left_wing_base"),
                        partMapping("right_wing", "right_wing_base"),
                        partMapping("left_wing_tip"),
                        partMapping("right_wing_tip")
                ));
        OptifineMapper.models("parrot")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("body"),
                        partMapping("tail"),
                        partMapping("left_wing"),
                        partMapping("right_wing"),
                        partMapping("left_leg"),
                        partMapping("right_leg")
                ));
        OptifineMapper.models("puffer_fish_big")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("fin_right", "right_blue_fin"),
                        partMapping("fin_left", "left_blue_fin"),
                        partMapping("spikes_front_top", "top_front_fin"),
                        partMapping("spikes_middle_top", "top_middle_fin"),
                        partMapping("spikes_back_top", "top_back_fin"),
                        partMapping("spikes_front_right", "right_front_fin"),
                        partMapping("spikes_front_left", "left_front_fin"),
                        partMapping("spikes_front_bottom", "bottom_front_fin"),
                        partMapping("spikes_middle_bottom", "bottom_middle_fin"),
                        partMapping("spikes_back_bottom", "bottom_back_fin"),
                        partMapping("spikes_back_right", "right_back_fin"),
                        partMapping("spikes_back_left", "left_back_fin")
                ));
        OptifineMapper.models("puffer_fish_medium")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("fin_right", "right_blue_fin"),
                        partMapping("fin_left", "left_blue_fin"),
                        partMapping("spikes_front_top", "top_front_fin"),
                        partMapping("spikes_back_top", "top_back_fin"),
                        partMapping("spikes_front_right", "right_front_fin"),
                        partMapping("spikes_front_left", "left_front_fin"),
                        partMapping("spikes_front_bottom", "bottom_front_fin"),
                        partMapping("spikes_back_bottom", "bottom_back_fin"),
                        partMapping("spikes_back_right", "right_back_fin"),
                        partMapping("spikes_back_left", "left_back_fin")
                ));
        OptifineMapper.models("puffer_fish_small")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("fin_right", "right_fin"),
                        partMapping("fin_left", "left_fin"),
                        partMapping("eye_right", "right_eye"),
                        partMapping("eye_left", "left_eye"),
                        partMapping("tail", "back_fin")
                ));
        OptifineMapper.models("rabbit")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("left_foot", "left_hind_foot"),
                        partMapping("right_foot", "right_hind_foot"),
                        partMapping("left_thigh", "left_haunch"),
                        partMapping("right_thigh", "right_haunch"),
                        partMapping("left_arm", "left_front_leg"),
                        partMapping("right_arm", "right_front_leg"),
                        partMapping("head"),
                        partMapping("right_ear"),
                        partMapping("left_ear"),
                        partMapping("tail"),
                        partMapping("nose")
                ));
        OptifineMapper.models("ravager")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("body"),
                        partMapping("leg1", "right_hind_leg"),
                        partMapping("leg2", "left_hind_leg"),
                        partMapping("leg3", "right_front_leg"),
                        partMapping("leg4", "left_front_leg"),
                        partMapping("jaw", "mouth"),
                        partMapping("neck")
                ));
        OptifineMapper.models("salmon", "salmon_large", "salmon_small")
                .parts(Map.ofEntries(
                        partMapping("body_front"),
                        partMapping("body_back"),
                        partMapping("head"),
                        partMapping("fin_back_1", "top_front_fin"),
                        partMapping("fin_back_2", "top_back_fin"),
                        partMapping("tail", "back_fin"),
                        partMapping("fin_right", "right_fin"),
                        partMapping("fin_left", "left_fin")
                ));
        OptifineMapper.models("shulker")
                .parts(Map.ofEntries(
                        partMapping("lid"),
                        partMapping("base"),
                        partMapping("head")
                ));
        OptifineMapper.models("shulker_box")
                .parts(Map.ofEntries(
                        partMapping("lid"),
                        partMapping("base")
                ));
        OptifineMapper.models("silverfish")
                .parts(Map.ofEntries(
                        partMapping("body1", "segment0"),
                        partMapping("body2", "segment1"),
                        partMapping("body3", "segment2"),
                        partMapping("body4", "segment3"),
                        partMapping("body5", "segment4"),
                        partMapping("body6", "segment5"),
                        partMapping("body7", "segment6"),
                        partMapping("wing1", "layer0"),
                        partMapping("wing2", "layer1"),
                        partMapping("wing3", "layer2")
                ));
        OptifineMapper.models("slime")
                .parts(Map.ofEntries(
                        partMapping("body", "cube"),
                        partMapping("left_eye"),
                        partMapping("right_eye"),
                        partMapping("mouth")
                ));
        OptifineMapper.models("slime_outer")
                .parts(Map.ofEntries(
                        partMapping("body", "cube")
                ));
        OptifineMapper.models("snow_golem")
                .parts(Map.ofEntries(
                        partMapping("body", "upper_body"),
                        partMapping("body_bottom", "lower_body"),
                        partMapping("head"),
                        partMapping("left_hand", "left_arm"),
                        partMapping("right_hand", "right_arm")
                ));
        OptifineMapper.models("strider", "strider_saddle")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("right_leg"),
                        partMapping("left_leg"),
                        partMapping("hair_right_top", "right_top_bristle"),
                        partMapping("hair_right_middle", "right_middle_bristle"),
                        partMapping("hair_right_bottom", "right_bottom_bristle"),
                        partMapping("hair_left_top", "left_top_bristle"),
                        partMapping("hair_left_middle", "left_middle_bristle"),
                        partMapping("hair_left_bottom", "left_bottom_bristle")
                ));
        OptifineMapper.models("tadpole")
                .parts(Map.ofEntries(
                        partMapping("body", "root"),//body = root for some reason because we don't need things to make sense when it comes to optifine
                        partMapping("EMPTY", "body"),//EMPTY is important
                        partMapping("tail")
                ));
        OptifineMapper.models("tropical_fish_a", "tropical_fish_pattern_a")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("tail"),
                        partMapping("fin_right", "right_fin"),
                        partMapping("fin_left", "left_fin"),
                        partMapping("fin_top", "top_fin")
                ));
        OptifineMapper.models("tropical_fish_b", "tropical_fish_pattern_b")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("tail"),
                        partMapping("fin_right", "right_fin"),
                        partMapping("fin_left", "left_fin"),
                        partMapping("fin_top", "top_fin"),
                        partMapping("fin_bottom", "bottom_fin")
                ));
        OptifineMapper.models("turtle")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("body"),
                        partMapping("body2", "egg_belly"),
                        partMapping("leg1", "right_hind_leg"),
                        partMapping("leg2", "left_hind_leg"),
                        partMapping("leg3", "right_front_leg"),
                        partMapping("leg4", "left_front_leg")
                ));
        OptifineMapper.models("warden")
                .parts(Map.ofEntries(
                        partMapping("body", "bone"),
                        partMapping("torso", "body"),
                        partMapping("head"),
                        partMapping("left_leg"),
                        partMapping("right_leg"),
                        partMapping("left_arm"),
                        partMapping("right_arm"),
                        partMapping("left_tendril"),
                        partMapping("right_tendril"),
                        partMapping("left_ribcage"),
                        partMapping("right_ribcage")
                ));
        OptifineMapper.models("witch")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("headwear", "hat"),
                        partMapping("headwear2", "hat_rim"),
                        partMapping("bodywear", "jacket"),
                        partMapping("body"),
                        partMapping("arms"),
                        partMapping("right_leg"),
                        partMapping("left_leg"),
                        partMapping("nose"),
                        partMapping("mole")
                ));
        OptifineMapper.models("wither", "wither_armor")
                .parts(Map.ofEntries(
                        partMapping("body1", "shoulders"),
                        partMapping("body2", "ribcage"),
                        partMapping("body3", "tail"),
                        partMapping("head1", "center_head"),
                        partMapping("head2", "right_head"),
                        partMapping("head3", "left_head")
                ));

        OptifineMapper.models("player", "player_slim")
                .parts(genericPlayerBiped);

        OptifineMapper.models("boat")
                .parts(Map.ofEntries(
                        partMapping("bottom"),//todo check
                        partMapping("back"),
                        partMapping("front"),
                        partMapping("right"),
                        partMapping("left"),
                        partMapping("paddle_left", "left_paddle"),
                        partMapping("paddle_right", "right_paddle")
                        //#if MC < 12102
                        //$$ , partMapping("bottom_no_water", "water_patch")//todo check
                        //#endif
                ));
        OptifineMapper.models("banner")
                .parts(Map.ofEntries(
                        partMapping("slate", "flag"),
                        partMapping("stand", "pole"),
                        partMapping("top", "bar")
                ));
        OptifineMapper.models("bed_head")
                .parts(Map.ofEntries(
                        partMapping("head", "main"),
                        partMapping("leg1", "left_leg"),//todo check
                        partMapping("leg2", "right_leg")//todo check
                ));
        OptifineMapper.models("bed_foot")
                .parts(Map.ofEntries(
                        partMapping("foot", "main"),
                        partMapping("leg3", "left_leg"),//todo check
                        partMapping("leg4", "right_leg")//todo check
                ));
        OptifineMapper.models("bell")
                .parts(Map.ofEntries(
                        partMapping("body", "bell_body")
                ));
        OptifineMapper.models("chest_boat")
                .parts(Map.ofEntries(
                        partMapping("bottom"),//todo check
                        partMapping("back"),
                        partMapping("front"),
                        partMapping("right"),
                        partMapping("left"),
                        partMapping("paddle_left", "left_paddle"),
                        partMapping("paddle_right", "right_paddle"),
                        //#if MC < 12102
                        //$$ partMapping("bottom_no_water", "water_patch"),//todo check
                        //#endif

                        partMapping("chest_base", "chest_bottom"),
                        partMapping("chest_lid"),
                        partMapping("chest_knob", "chest_lock")
                ));
        OptifineMapper.models("raft")
                .parts(Map.ofEntries(
                        partMapping("bottom"),
                        partMapping("paddle_left", "left_paddle"),
                        partMapping("paddle_right", "right_paddle")
                ));
        OptifineMapper.models("chest_raft")
                .parts(Map.ofEntries(
                        partMapping("bottom"),
                        partMapping("paddle_left", "left_paddle"),
                        partMapping("paddle_right", "right_paddle"),

                        partMapping("chest_base", "chest_bottom"),
                        partMapping("chest_lid"),
                        partMapping("chest_knob", "chest_lock")
                ));
        OptifineMapper.models("minecart", "chest_minecart", "command_block_minecart", "spawner_minecart", "tnt_minecart", "furnace_minecart", "hopper_minecart")
                .parts(
                        Map.ofEntries(
                                partMapping("bottom"),
                                partMapping("back"),
                                partMapping("front"),
                                partMapping("right"),
                                partMapping("left")
                        ));
        OptifineMapper.models("conduit_cage")
                .parts(Map.ofEntries(partMapping("cage", "shell")));
        OptifineMapper.models("conduit_eye")
                .parts(Map.ofEntries(partMapping("eye")));
        OptifineMapper.models("conduit_shell")
                .parts(Map.ofEntries(partMapping("base", "shell")));
        OptifineMapper.models("conduit_wind")
                .parts(Map.ofEntries(partMapping("wind")));
        OptifineMapper.models("decorated_pot_base")
                .parts(Map.ofEntries(
                        partMapping("neck"),
                        partMapping("top"),
                        partMapping("bottom")
                ));
        OptifineMapper.models("decorated_pot_sides")
                .parts(Map.ofEntries(
                        partMapping("front"),
                        partMapping("back"),
                        partMapping("left"),
                        partMapping("right")
                ));
        OptifineMapper.models("book")
                .parts(Map.ofEntries(//yes the left right swap is correct to optifine :/
                        partMapping("cover_left", "right_lid"),
                        partMapping("cover_right", "left_lid"),
                        partMapping("pages_left", "right_pages"),
                        partMapping("pages_right", "left_pages"),
                        partMapping("flipping_page_right", "flip_page1"),
                        partMapping("flipping_page_left", "flip_page2"),
                        partMapping("book_spine", "seam")
                ));

        OptifineMapper.models("hanging_sign")
                .parts(Map.ofEntries(
                        partMapping("board"),
                        partMapping("plank"),
                        partMapping("chains", "normalChains"),
                        partMapping("chain_left1", "chainL1"),
                        partMapping("chain_left2", "chainL2"),
                        partMapping("chain_right1", "chainR1"),
                        partMapping("chain_right2", "chainR2"),
                        partMapping("chains_v", "vChains")
                ));
        OptifineMapper.models("lead_knot")
                .parts(Map.ofEntries(
                        partMapping("knot")
                ));
        OptifineMapper.models("spin_attack")
                .parts(Map.ofEntries(
                        partMapping("box1", "box0"),
                        partMapping("box2", "box1")
                ));
        OptifineMapper.models("sign")
                .parts(Map.ofEntries(
                        partMapping("board", "root"),
                        partMapping("stick")
                ));
        OptifineMapper.models("wall_sign")
                .parts(Map.ofEntries(
                        partMapping("board", "root")
                ));
        OptifineMapper.models("trident")
                .parts(Map.ofEntries(
                        partMapping("body", "pole")
                        //#if MC >= 1.20.3
                        , partMapping("base"),
                        partMapping("left_spike"),
                        partMapping("middle_spike"),
                        partMapping("right_spike")
                        //#endif
                ));

        OptifineMapper.models("shield")
                .parts(Map.ofEntries(
                        partMapping("plate"),
                        partMapping("handle")
                ));
        OptifineMapper.models("breeze", "breeze_eyes", "breeze_wind")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("rods"),
                        partMapping("head"),
                        partMapping("wind_body"),
                        partMapping("wind_middle", "wind_mid"),
                        partMapping("wind_bottom"),
                        partMapping("wind_top")
                ));

        OptifineMapper.models("wind_charge","breeze_wind_charge")
                .parts(Map.ofEntries(
//                        partMapping("core", "projectile"),//maybe "bone"???
                        partMapping("wind"),
//                        partMapping("cube1", "cube_r1"),
//                        partMapping("cube2", "cube_r2"),
                        partMapping("charge", "wind_charge")
                ));
        OptifineMapper.models("bogged").parts(
                new HashMap<>(genericNonPlayerBiped) {{
                    put("mushrooms","mushrooms");
                }});

        //#if MC >= 12102
        OptifineMapper.models("player_cape")
                .parts(Map.ofEntries(partMapping("cape")));
        OptifineMapper.models("player_ears")
                .parts(Map.ofEntries(partMapping("left_ear"), partMapping("right_ear")));
        OptifineMapper.models("dragon")
                .parts(Map.ofEntries(
                        partMapping("head"),
                        partMapping("jaw", "jaw"),
                        partMapping("body", "body"),

                        partMapping("neck1", "neck0"),
                        partMapping("neck2", "neck1"),
                        partMapping("neck3", "neck2"),
                        partMapping("neck4", "neck3"),
                        partMapping("neck5", "neck4"),

                        partMapping("tail1", "tail0"),
                        partMapping("tail2", "tail1"),
                        partMapping("tail3", "tail2"),
                        partMapping("tail4", "tail3"),
                        partMapping("tail5", "tail4"),
                        partMapping("tail6", "tail5"),
                        partMapping("tail7", "tail6"),
                        partMapping("tail8", "tail7"),
                        partMapping("tail9", "tail8"),
                        partMapping("tail10", "tail9"),
                        partMapping("tail11", "tail10"),
                        partMapping("tail12", "tail11"),

                        partMapping("left_wing"),
                        partMapping("left_wing_tip", "left_wing_tip"),
                        partMapping("right_wing"),
                        partMapping("right_wing_tip", "right_wing_tip"),

                        partMapping("front_left_leg", "left_front_leg"),
                        partMapping("front_left_shin", "left_front_leg_tip"),
                        partMapping("front_left_foot", "left_front_foot"),

                        partMapping("back_left_leg", "left_hind_leg"),
                        partMapping("back_left_shin", "left_hind_leg_tip"),
                        partMapping("back_left_foot", "left_hind_foot"),

                        partMapping("front_right_leg", "right_front_leg"),
                        partMapping("front_right_shin", "right_front_leg_tip"),
                        partMapping("front_right_foot", "right_front_foot"),

                        partMapping("back_right_leg", "right_hind_leg"),
                        partMapping("back_right_shin", "right_hind_leg_tip"),
                        partMapping("back_right_foot", "right_hind_foot")
                ));
        OptifineMapper.models("end_crystal")
                .parts(Map.ofEntries(
                        partMapping("cube"),
                        partMapping("inner_glass"),
                        partMapping("outer_glass"),
                        partMapping("base")
                ));
        OptifineMapper.models("armadillo")
                .parts(Map.ofEntries(
                        partMapping("body"),
                        partMapping("head"),
                        partMapping("left_ear"),
                        partMapping("right_ear"),
                        partMapping("left_ear_cube"),
                        partMapping("right_ear_cube"),
                        partMapping("back_left_leg","left_hind_leg"),
                        partMapping("back_right_leg","right_hind_leg"),
                        partMapping("front_left_leg","left_front_leg"),
                        partMapping("front_right_leg","right_front_leg"),
                        partMapping("cube"),
                        partMapping("tail")
                ));

        OptifineMapper.models("spectral_arrow", "arrow")
                .parts(Map.ofEntries(
                        partMapping("back"),
                        partMapping("cross_1"),
                        partMapping("cross_2")
                ));
        OptifineMapper.models( "bee_stinger")
                .parts(Map.ofEntries(
                        partMapping("cross_1"),
                        partMapping("cross_2")
                ));

        OptifineMapper.models("boat_water_patch")
                .parts(Map.ofEntries(partMapping("water_patch", "water_patch")));//todo check

        //#else
        //$$ OptifineMapper.models("player_cape")
        //$$         .parts(Map.ofEntries(partMapping("cloak")));
        //$$ OptifineMapper.models("spectral_arrow", "arrow")
        //$$         .parts(Map.ofEntries(partMapping("body", "root")));
        //$$ OptifineMapper.models("dragon")
        //$$         .parts(Map.ofEntries(
        //$$                 partMapping("head"),
        //$$                partMapping("jaw", "jaw"),
        //$$                 partMapping("spine", "neck"),
        //$$                 partMapping("body", "body"),
        //$$
        //$$                 partMapping("left_wing"),
        //$$                 partMapping("left_wing_tip", "left_wing_tip"),
        //$$                partMapping("right_wing"),
        //$$                 partMapping("right_wing_tip", "right_wing_tip"),
        //$$
        //$$                 partMapping("front_left_leg", "left_front_leg"),
        //$$                 partMapping("front_left_shin", "left_front_leg_tip"),
        //$$                 partMapping("front_left_foot", "left_front_foot"),
        //$$
        //$$                 partMapping("back_left_leg", "left_hind_leg"),
        //$$                 partMapping("back_left_shin", "left_hind_leg_tip"),
        //$$                 partMapping("back_left_foot", "left_hind_foot"),
        //$$
        //$$                 partMapping("front_right_leg", "right_front_leg"),
        //$$                 partMapping("front_right_shin", "right_front_leg_tip"),
        //$$                 partMapping("front_right_foot", "right_front_foot"),
        //$$
        //$$                 partMapping("back_right_leg", "right_hind_leg"),
        //$$                 partMapping("back_right_shin", "right_hind_leg_tip"),
        //$$                 partMapping("back_right_foot", "right_hind_foot")
        //$$         ));
        //$$ OptifineMapper.models("end_crystal")
        //$$         .parts(Map.ofEntries(
        //$$                 partMapping("cube"),
        //$$                 partMapping("glass"),
        //$$                 partMapping("base")
        //$$         ));
        //#endif
    }

    @Contract(value = "_ -> new", pure = true)
    public static Map.@NotNull Entry<String, String> partMapping(String optifineName) {
        return new MutablePair<>(optifineName, optifineName);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static Map.@NotNull Entry<String, String> partMapping(String optifineName, String vanillaName) {
        return new MutablePair<>(optifineName, vanillaName);
    }

    public static Map<String, String> getMapOf(EMFModel_ID mobId, @Nullable ModelPart root) {
        return getMapOf(mobId, root, true);
    }

    public static Map<String, String> getMapOf(@NotNull EMFModel_ID mobId, @Nullable ModelPart root, boolean exportOnlyFirstTime) {

        String mobName = mobId.getMapId();
        if (mobName.matches(".*_baby($|_.*)") && !mobName.contains(":")){
            mobName = mobName.replaceFirst("_baby","");
        }

        Map<String, String> knownMap;
        if (mobName.endsWith("_inner_armor") || mobName.endsWith("_outer_armor")) {
            knownMap = genericNonPlayerBiped;
        } else {
            knownMap = getKnownMap(mobName);
        }
        if (knownMap == null) {
            return root == null ? Map.of() : exploreProvidedEntityModelAndExportIfNeeded(root, mobId, null, exportOnlyFirstTime);
        }
        //trigger the export of the known model if we are exporting all
        if (EMF.config().getConfig().modelExportMode.doesAll()) {
            exportKnown(mobId, root, knownMap, exportOnlyFirstTime);
        }

        return knownMap;
    }

    private static void exportKnown(final EMFModel_ID mobId, final @Nullable ModelPart root, final Map<String, String> knownMap, boolean exportOnlyFirstTime) {
        String mobName = mobId.getfileName();
        EMFUtils.log("Exporting/logging  model for " + mobName + " that has known OptiFine part names:");
        exploreProvidedEntityModelAndExportIfNeeded(root, mobId, knownMap, exportOnlyFirstTime);
        //also print out the model with its actual values in case a mod has added something
        EMFUtils.log("Additionally exporting/logging model for " + mobName + " again as though it did not have known OptiFine part names:\nThis might highlight some vanilla, or mod added, parts that are not usually exposed by OptiFine");

        var old = EMF.config().getConfig().modelExportMode;
        EMF.config().getConfig().modelExportMode = EMFConfig.ModelPrintMode.ALL_LOG_ONLY;
        try {
            exploreProvidedEntityModelAndExportIfNeeded(root, mobId, null, exportOnlyFirstTime);
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



    //this would make a usable mapping of the given model but with no part name changing as it would not be optifine customized
    public static Map<String, String> exploreProvidedEntityModelAndExportIfNeeded(ModelPart originalModel, EMFModel_ID mobId, @Nullable Map<String, String> mobMap, boolean exportOnlyFirstTime) {
        String id = mobId.getDisplayFileName();
        if (UNKNOWN_MODEL_MAP_CACHE.containsKey(id) && exportOnlyFirstTime)
            return UNKNOWN_MODEL_MAP_CACHE.get(id);

        //find unmodified model
        var layer = EMFManager.getInstance().cache_LayersByModelName.get(mobId);
        if(layer != null){
            var unModifiedModel = ((IEMFUnmodifiedLayerRootGetter) Minecraft.getInstance().getEntityModels())
                    .emf$getUnmodifiedRoots().get(layer);
            if (unModifiedModel != null) {
                originalModel = unModifiedModel.bakeRoot();
            }
        }

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
        UNKNOWN_MODEL_MAP_CACHE.put(id, mobMap);

        if (EMF.config().getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE) {
            StringBuilder mapString = new StringBuilder();

            String namespace = mobId.namespace;
            String fileName = mobId.getfileName();

            mapString.append(" |-[assets/").append(namespace).append("/optifine/cem/").append(fileName).append(".jem]\n");
            mobMap.forEach((key, entry) -> {
                mapString.append(" | |-[").append("root".equals(key) ? "(optional) " : "").append("part=").append(key).append("]\n");
                mapString.append(detailsMap.get(key));
            });
            mapString.append("  \\-\\{{end of model}}");

            if (known) {
                EMFUtils.log("Known model detected, Mapping now...\n" + mapString);
            } else {
                EMFUtils.log("Unknown (possibly modded) model detected, Mapping now...\n" + mapString);
            }

            if (EMF.config().getConfig().modelExportMode.doesJems()) {
                EMFUtils.log("creating example .jem file for " + fileName);
                var jemPrinter = new JsonObject();
                int[] textureSize = null;
                var models = new JsonArray();
                for (Map.Entry<String, String> entry :
                        mobMap.entrySet()) {

                    JsonObject partPrinter = new JsonObject();
                    partPrinter.addProperty("invertAxis", "xy");
                    partPrinter.addProperty("part", entry.getKey());
                    partPrinter.addProperty("id", entry.getKey());
                    PartAndOffsets searchPart = getChildByName(entry.getValue(), originalModel,0,0,0);
                    // allow nested child parts named root to be found first otherwise apply the root part as the root
                    PartAndOffsets vanillaModelPart = searchPart == null && "root".equals(entry.getKey()) ? new PartAndOffsets(originalModel, 0,0,0) : searchPart;

                    textureSize = initPartPrinterAndCaptureTextureSizeIfNeeded(vanillaModelPart, partPrinter, textureSize);
                    models.add(partPrinter);

                }
                jemPrinter.add("models", models);
                if (textureSize == null) {
                    textureSize = new int[]{64, 32};
                }

                var textureSize2 = new JsonArray();
                textureSize2.add(textureSize[0]);
                textureSize2.add(textureSize[1]);
                jemPrinter.add("textureSize", textureSize2);

                printModel(namespace, fileName, jemPrinter);
                mobId.forEachFallback((fallback) -> printModel(fallback.namespace, fallback.getfileName(), jemPrinter));
            }
        }
        return mobMap;
    }

    private static void printModel(final String namespace, final String fileName, final JsonObject jemPrinter) {
        String path = ETF.getConfigDirectory().toFile().getParent()
                + "/emf/export/" + "assets/" + namespace + "/optifine/cem/" + fileName + ".jem";
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

    private record PartAndOffsets(ModelPart part, float x, float y, float z) {
    }

    private static void addArrayProperty(JsonObject obj, String name, Number... values) {
        var arr = new JsonArray(values.length);
        for (Number value : values) {
            arr.add(value);
        }
        obj.add(name, arr);
    }

    private static void addArrayProperty(JsonObject obj, String name, JsonObject... values) {
        var arr = new JsonArray(values.length);
        for (JsonObject value : values) {
            arr.add(value);
        }
        obj.add(name, arr);
    }


    private static int[] initPartPrinterAndCaptureTextureSizeIfNeeded(PartAndOffsets partAndOffsets, JsonObject partPrinter, int[] textureSize) {
        if (partAndOffsets != null && partAndOffsets.part != null) {

            var vanillaModelPart = partAndOffsets.part;

            // invert x and y's
            var x = -partAndOffsets.x + vanillaModelPart.x;
            var y = -24 -partAndOffsets.y + vanillaModelPart.y;
            var z = partAndOffsets.z -vanillaModelPart.z;
            if (x != 0 && y != 0 && z != 0) addArrayProperty(partPrinter,"translate", x, y, z);

            if (vanillaModelPart.xScale != 0) partPrinter.addProperty("scale", vanillaModelPart.xScale);
            // get part size incase empty, though cuboids often have better ideas about this
            //partPrinter.textureSize = ((IEMFTextureSizeSupplier) vanillaModelPart).emf$getTextureSize();
            textureSize = ((IEMFTextureSizeSupplier) vanillaModelPart).emf$getTextureSize();

            var list = new ArrayList<JsonObject>(vanillaModelPart.cubes.size());

            for (ModelPart.Cube cube :
                    vanillaModelPart.cubes) {
                JsonObject boxPrinter = new JsonObject();
                var coordinates = new float[]{
                        cube.minX,
                        cube.minY,
                        cube.minZ,
                        cube.maxX - cube.minX,
                        cube.maxY - cube.minY,
                        cube.maxZ - cube.minZ};
                // can be different from part
                var xy = ((IEMFCuboidDataSupplier) cube).emf$getTextureXY();
                addArrayProperty(boxPrinter,"textureSize", xy[0], xy[1]);

                var uv = ((IEMFCuboidDataSupplier) cube).emf$getTextureUV();
                addArrayProperty(boxPrinter,"textureOffset", uv[0], uv[1]);

                var adds = ((IEMFCuboidDataSupplier) cube).emf$getSizeAdd();
                if (adds != null) {
                    if (adds[0] == adds[1] && adds[0] == adds[2]) {
                        boxPrinter.addProperty("sizeAdd", adds[0]);
                    } else {
                        boxPrinter.addProperty("sizeAddX", adds[0]);
                        boxPrinter.addProperty("sizeAddY", adds[1]);
                        boxPrinter.addProperty("sizeAddZ", adds[2]);
                    }
                }

                // invert x and y
                coordinates[0] = -coordinates[0] - coordinates[3] - x;
                coordinates[1] = -coordinates[1] - coordinates[4] - y;

                coordinates[2] = coordinates[2] - z;

                list.add(boxPrinter);
            }
            addArrayProperty(partPrinter, "boxes", list.toArray(new JsonObject[0]));
        }
        return textureSize;
    }

    private static @Nullable PartAndOffsets getChildByName(String name, @NotNull ModelPart part, float x, float y, float z) {
        if (part.hasChild(name)) return new PartAndOffsets(part.getChild(name),
                 x - part.x,
                 y - part.y,
                 z - part.z
        );
        for (ModelPart childPart :
                part.children.values()) {
            PartAndOffsets possibleReturn = getChildByName(name, childPart,
                     x - part.x,
                     y - part.y,
                     z - part.z
            );
            if (possibleReturn != null) return possibleReturn;
        }
        return null;
    }

    private static void mapThisAndChildren(String partName, @NotNull ModelPart originalModel, Map<String, String> newMap, Map<String, String> detailsMap) {
        // iterate over children while collecting their names in a list
        for (Map.Entry<String, ModelPart> entry :
                originalModel.children.entrySet()) {
            mapThisAndChildren(entry.getKey(), entry.getValue(), newMap, detailsMap);
        }

        // add this part and its children names
        newMap.put(partName, partName);
        if (EMF.config().getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE) {
            StringBuilder cubes = new StringBuilder( originalModel.cubes.isEmpty() ? "[No boxes]" : "[boxes]");
            int i = 0;
            for (ModelPart.Cube cube :
                    originalModel.cubes) {
                    i++;
                    var coordinates = new float[]{
                            cube.minX,
                            cube.minY,
                            cube.minZ,
                            cube.maxX - cube.minX,
                            cube.maxY - cube.minY,
                            cube.maxZ - cube.minZ};
                    var textureOffset = ((IEMFCuboidDataSupplier) cube).emf$getTextureUV();
                    var adds = ((IEMFCuboidDataSupplier) cube).emf$getSizeAdd();
                    float sizeAdd = Float.NaN;
                    float sizeAddX = Float.NaN;
                    float sizeAddY = Float.NaN;
                    float sizeAddZ = Float.NaN;
                    if (adds != null) {
                        if (adds[0] == adds[1] && adds[0] == adds[2]) {
                            sizeAdd = adds[0];
                        } else {
                            sizeAddX = adds[0];
                            sizeAddY = adds[1];
                            sizeAddZ = adds[2];
                        }
                    }

                    // invert x and y
                    coordinates[0] = -coordinates[0] - coordinates[3] - originalModel.x;
                    coordinates[1] = -coordinates[1] - coordinates[4] - originalModel.y;

                    coordinates[2] = coordinates[2] - originalModel.z;

                    // now print final values
                    cubes.   append("\n | |   |-[#").append(i).append("]")
                            .append("\n | |   |-coordinates=").append(Arrays.toString(coordinates))
                            .append("\n | |   |-textureOffset=").append(Arrays.toString(textureOffset));
                    if(!Float.isNaN(sizeAdd)) {
                        cubes.append("\n | |    \\-sizeAdd=").append(sizeAdd);
                    }else{
                        cubes.   append("\n | |   |-sizeAddX=").append(sizeAddX)
                                .append("\n | |   |-sizeAddY=").append(sizeAddY)
                                .append("\n | |    \\-sizeAddZ=").append(sizeAddZ);
                    }
            }


            detailsMap.put(partName,
                    " | | |-pivots=" + originalModel.x + ", " + originalModel.y + ", " + originalModel.z +
                            "\n | | |-rotations=" + (originalModel.xRot* Mth.RAD_TO_DEG) + ", " + (originalModel.yRot* Mth.RAD_TO_DEG) + ", " + (originalModel.zRot* Mth.RAD_TO_DEG) +
                            "\n | | |-scales=" + originalModel.xScale + ", " + originalModel.yScale + ", " + originalModel.zScale +
                            "\n | | |visible=" + originalModel.visible + ", _boxes=" + !originalModel.skipDraw +
                            "\n | |  \\" + cubes +"\n"

            );
        }
    }

    private static class OptifineMapper {

        String[] modelNames;

        OptifineMapper(String... modelNames){
            this.modelNames = modelNames;
        }

        @Contract(value = "_ -> new", pure = true)
        static @NotNull OptifineMapper models(String... modelNames) {
            return new OptifineMapper(modelNames);
        }


        void parts(final Map<String, String> stringStringMap) {
            for (String key : modelNames) {
                if (OPTIFINE_MODEL_MAP_CACHE.put(key, stringStringMap) != null) {
                    EMFUtils.logError("OptiFine model map for " + key + " already exists, overwriting");
                }
            }
        }
    }
}
