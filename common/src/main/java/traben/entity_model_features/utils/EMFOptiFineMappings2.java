package traben.entity_model_features.utils;

import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EMFOptiFineMappings2 {

    public static  Map.Entry<String,PartAndChildName> getEntryOptifineSameAsVanilla(String name){
        return new MutablePair<>(name,_getPartAndChild(name));
    }

    public static  Map.Entry<String,PartAndChildName> getEntryOptifineDifferent(String name, String vanillaName){
        return new MutablePair<>(name,_getPartAndChild(vanillaName));
    }

    public static  Map.Entry<String,PartAndChildName> getEntryOptifineWithChild(String name, String vanillaName, String childName){
        return new MutablePair<>(name,_getPartAndChild( vanillaName,childName));
    }

    public static  Map.Entry<String,PartAndChildName> getEntryOptifineWithChildList(String name, String vanillaName, List<String> childNames){
        return new MutablePair<>(name,_getPartAndChild( vanillaName,childNames));
    }

    public static PartAndChildName _getPartAndChild(String partName, String childName){
        return new PartAndChildName(partName, Collections.singletonList(childName));
    }

    public static PartAndChildName _getPartAndChild(String partName){
        return new PartAndChildName(partName, new ArrayList<>());
    }

    public static PartAndChildName _getPartAndChild(String partName, List<String> childNamesToExpect){
        return new PartAndChildName(partName, childNamesToExpect);
    }

    public static Map<String, PartAndChildName> getMapOf(String mobName) {

        return switch (mobName){
            case "villager" -> Map.ofEntries(
                    getEntryOptifineWithChildList("head", "head", List.of("hat", "nose")),
                    getEntryOptifineWithChild("headwear", "hat", "hat_rim"),
                    getEntryOptifineDifferent("headwear2", "hat_rim"),
                    getEntryOptifineDifferent("bodywear", "jacket"),
                    getEntryOptifineWithChild("body", "body", "jacket"),
                    getEntryOptifineSameAsVanilla("arms"),
                    getEntryOptifineSameAsVanilla("right_leg"),
                    getEntryOptifineSameAsVanilla("left_leg"),
                    getEntryOptifineSameAsVanilla("nose"));

            case "iron_golem" -> Map.ofEntries(
                    getEntryOptifineSameAsVanilla("head"),
                    getEntryOptifineSameAsVanilla("body"),
                    getEntryOptifineSameAsVanilla("left_arm"),
                    getEntryOptifineSameAsVanilla("right_arm"),
                    getEntryOptifineSameAsVanilla("left_leg"),
                    getEntryOptifineSameAsVanilla("right_leg")
            );
            case "spider","cave_spider" -> Map.ofEntries(
                    getEntryOptifineSameAsVanilla("head"),
                    getEntryOptifineDifferent("neck", "body0"),
                    getEntryOptifineDifferent("body", "body1"),
                    getEntryOptifineDifferent("leg1", "right_hind_leg"),
                    getEntryOptifineDifferent("leg2", "left_hind_leg"),
                    getEntryOptifineDifferent("leg3", "right_middle_hind_leg"),
                    getEntryOptifineDifferent("leg4", "left_middle_hind_leg"),
                    getEntryOptifineDifferent("leg5", "right_middle_front_leg"),
                    getEntryOptifineDifferent("leg6", "left_middle_front_leg"),
                    getEntryOptifineDifferent("leg7", "right_front_leg"),
                    getEntryOptifineDifferent("leg8", "left_front_leg")
            );
            case "sheep","cow","creeper","creeper_charge","mooshroom","panda","pig","pig_saddle","polar_bear","sheep_wool"
                    -> genericQuadraped;
            case "zombie","husk","drowned","drowned_outer","enderman","giant","skeleton","stray","stray_outer","wither_skeleton","zombie_villager","zombie_pigman"
                    -> genericNonPlayerBiped;
            case "player"-> genericPlayerBiped;
            case "piglin","piglin_brute","zombified_piglin" -> genericPiglinBiped;
            case "allay","vex" ->Map.ofEntries(//# allay                    head, body, left_arm, right_arm, left_wing, right_wing
                    getEntryOptifineSameAsVanilla("head"),
                    getEntryOptifineWithChildList("body","body",List.of("left_arm","right_arm","left_wing","right_wing")),
                    getEntryOptifineSameAsVanilla("left_arm"),
                    getEntryOptifineSameAsVanilla("right_arm"),
                    getEntryOptifineSameAsVanilla("left_wing"),
                    getEntryOptifineSameAsVanilla("right_wing")
            );
            case "squid","glow_squid" ->Map.ofEntries(//body, tentacle1 ... tentacle8
                    getEntryOptifineSameAsVanilla("body"),
                    getEntryOptifineDifferent("tentacle1","tentacle0"),
                    getEntryOptifineDifferent("tentacle2","tentacle1"),
                    getEntryOptifineDifferent("tentacle3","tentacle2"),
                    getEntryOptifineDifferent("tentacle4","tentacle3"),
                    getEntryOptifineDifferent("tentacle5","tentacle4"),
                    getEntryOptifineDifferent("tentacle6","tentacle5"),
                    getEntryOptifineDifferent("tentacle7","tentacle6"),
                    getEntryOptifineDifferent("tentacle8","tentacle7")
            );
            case "ghast" ->Map.ofEntries(//body, tentacle1 ... tentacle9
                    getEntryOptifineSameAsVanilla("body"),
                    getEntryOptifineDifferent("tentacle1","tentacle0"),
                    getEntryOptifineDifferent("tentacle2","tentacle1"),
                    getEntryOptifineDifferent("tentacle3","tentacle2"),
                    getEntryOptifineDifferent("tentacle4","tentacle3"),
                    getEntryOptifineDifferent("tentacle5","tentacle4"),
                    getEntryOptifineDifferent("tentacle6","tentacle5"),
                    getEntryOptifineDifferent("tentacle7","tentacle6"),
                    getEntryOptifineDifferent("tentacle8","tentacle7"),
                    getEntryOptifineDifferent("tentacle9","tentacle8")
            );
            case "wolf","wolf_collar" ->Map.ofEntries(
                    getEntryOptifineSameAsVanilla("body"),
                    getEntryOptifineWithChild("head","head","real_head"),//todo
                    getEntryOptifineWithChild("tail","tail","real_tail"),
                    getEntryOptifineDifferent("mane","upper_body"),
                    getEntryOptifineDifferent("leg1","right_hind_leg"),
                    getEntryOptifineDifferent("leg2","left_hind_leg"),
                    getEntryOptifineDifferent("leg3","right_front_leg"),
                    getEntryOptifineDifferent("leg4","left_front_leg")
            );
            case "wither_skull","head_zombie","head_wither_skeleton","head_skeleton","head_player"
                    -> Map.ofEntries(getEntryOptifineSameAsVanilla("head"));

            case "horse","horse_armor","skeleton_horse","zombie_horse"
                -> genericHorse;
            case "donkey","mule" -> new HashMap<String, PartAndChildName>(genericHorse){{
                putAll(Map.ofEntries(
                        getEntryOptifineSameAsVanilla("right_chest"),
                        getEntryOptifineSameAsVanilla("left_chest"),
                        getEntryOptifineWithChildList("body","body",List.of("tail","saddle","left_chest","right_chest"))
                ));
            }};

//# armor_stand              head, headwear, body, left_arm, right_arm, left_leg, right_leg, right, left, waist, base
//# axolotl                  head, body, leg1 ... leg4, tail, top_gills, left_gills, right_gills
//# banner                   slate, stand, top
//# bat                      head, body, right_wing, left_wing, outer_right_wing, outer_left_wing
//# bee                      body, torso, right_wing, left_wing, front_legs, middle_legs, back_legs, stinger, left_antenna, right_antenna
//# bed                      head, foot, leg1 ... leg4
//# bell                     body
//# blaze                    head, stick1 ... stick12
//# boat                     bottom, back, front, right, left, paddle_left, paddle_right, bottom_no_water
//# cat                      back_left_leg, back_right_leg, front_left_leg, front_right_leg, tail, tail2, head, body
//# cat_collar               back_left_leg, back_right_leg, front_left_leg, front_right_leg, tail, tail2, head, body
//# chest                    lid, base, knob
//# chest_boat               bottom, back, front, right, left, paddle_left, paddle_right, bottom_no_water, chest_base, chest_lid, chest_knob
//# chest_large              lid_left, base_left, knob_left, lid_right, base_right, knob_right
//# chest_minecart           bottom, back, front, right, left
//# chicken                  head, body, right_leg, left_leg, right_wing, left_wing, bill, chin
//# cod                      body, fin_back, head, nose, fin_right, fin_left, tail
//# command_block_minecart   bottom, back, front, right, left
//# conduit                  base, eye, cage, wind
//# dragon                   head, spine, jaw, body, left_wing, left_wing_tip, right_wing, right_wing_tip,
//#                          front_left_leg, front_left_shin, front_left_foot, back_left_leg, back_left_shin, back_left_foot,
//#                          front_right_leg, front_right_shin, front_right_foot, back_right_leg, back_right_shin, back_right_foot
//# dolphin                  body, back_fin, left_fin, right_fin, tail, tail_fin, head
//# elder_guardian           body, eye, spine1 ... spine12, tail1 ... tail3
//# enchanting_book          cover_right, cover_left, pages_right, pages_left, flipping_page_right, flipping_page_left, book_spine
//# ender_chest              lid, base, knob
//# end_crystal              cube, glass, base
//# endermite                body1 ... body4
//# evoker                   head, hat, body, arms, left_leg, right_leg, nose, left_arm, right_arm
//# evoker_fangs             base, upper_jaw, lower_jaw
//# fox                      head, body, leg1 ... leg4, tail
//# frog                     head, body, eyes, tongue, left_arm, right_arm, left_leg, right_leg, croaking_body
//# furnace_minecart         bottom, back, front, right, left
//# goat                     head, body, leg1 ... leg4, left_horn, right_horn, nose
//# guardian                 body, eye, spine1 ... spine12, tail1 ... tail3
//# head_dragon              head, jaw
//# hoglin                   head, right_ear, left_ear, body, front_right_leg, front_left_leg, back_right_leg, back_left_leg, mane
//# hopper_minecart          bottom, back, front, right, left
//# illusioner               head, hat, body, arms, left_leg, right_leg, nose, left_arm, right_arm
//# iron_golem               head, body, left_arm, right_arm, left_leg, right_leg
//# lead_knot                knot
//# lectern_book             cover_right, cover_left, pages_right, pages_left, flipping_page_right, flipping_page_left, book_spine
//# llama                    head, body, leg1 ... leg4, chest_right, chest_left
//# llama_decor              head, body, leg1 ... leg4, chest_right, chest_left
//# llama_spit               body
//# magma_cube               core, segment1 ... segment8
//# minecart                 bottom, back, front, right, left
//# ocelot                   back_left_leg, back_right_leg, front_left_leg, front_right_leg, tail, tail2, head, body
//# parrot                   head, body, tail, left_wing, right_wing, left_leg, right_leg
//# phantom                  body, left_wing, left_wing_tip, right_wing, right_wing_tip, head, tail, tail2
//# puffer_fish_big          body, fin_right, fin_left, spikes_front_top, spikes_middle_top, spikes_back_top, spikes_front_right, spikes_front_left,
//#                          spikes_front_bottom, spikes_middle_bottom, spikes_back_bottom, spikes_back_right, spikes_back_left
//# puffer_fish_medium       body, fin_right, fin_left, spikes_front_top, spikes_back_top, spikes_front_right, spikes_back_right, spikes_back_left,
//#                          spikes_front_left, spikes_back_bottom, spikes_front_bottom
//# puffer_fish_small        body, eye_right, eye_left, tail, fin_right, fin_left
//# pillager                 head, hat, body, arms, left_leg, right_leg, nose, left_arm, right_arm
//# rabbit                   left_foot, right_foot, left_thigh, right_thigh, body, left_arm, right_arm, head, right_ear, left_ear, tail, nose
//# ravager                  head, jaw, body, leg1 ... leg4, neck
//# salmon                   body_front, body_back, head, fin_back_1, fin_back_2, tail, fin_right, fin_left
//# shulker                  head, base, lid
//# shulker_box              base, lid
//# shulker_bullet           bullet
//# sign                     board, stick
//# silverfish               body1 ... body7, wing1 ... wing3
//# slime                    body, left_eye, right_eye, mouth
//# slime_outer              body
//# snow_golem               body, body_bottom, head, left_hand, right_hand
//# spawner_minecart         bottom, back, front, right, left
//# spider                   head, neck, body, leg1, ... leg8
//# strider                  body, right_leg, left_leg, hair_right_top, hair_right_middle, hair_right_bottom, hair_left_top, hair_left_middle, hair_left_bottom
//# strider_saddle           body, right_leg, left_leg, hair_right_top, hair_right_middle, hair_right_bottom, hair_left_top, hair_left_middle, hair_left_bottom
//# tnt_minecart             bottom, back, front, right, left
//# tadpole                  body, tail
//# trader_llama             head, body, leg1 ... leg4, chest_right, chest_left
//# trader_llama_decor       head, body, leg1 ... leg4, chest_right, chest_left
//# trapped_chest            lid, base, knob
//# trapped_chest_large      lid_left, base_left, knob_left, lid_right, base_right, knob_right
//# trident                  body
//# tropical_fish_a          body, tail, fin_right, fin_left, fin_top
//# tropical_fish_b          body, tail, fin_right, fin_left, fin_top, fin_bottom
//# tropical_fish_pattern_a  body, tail, fin_right, fin_left, fin_top
//# tropical_fish_pattern_b  body, tail, fin_right, fin_left, fin_top, fin_bottom
//# turtle                   head, body, leg1 ... leg4, body2
//# villager                 head, headwear, headwear2, body, bodywear, arms, left_leg, right_leg, nose
//# vindicator               head, hat, body, arms, left_leg, right_leg, nose, left_arm, right_arm
//# wandering_trader         head, headwear, headwear2, body, bodywear, arms, left_leg, right_leg, nose
//# warden                   body, torso, head, left_leg, right_leg, left_arm, right_arm, left_tendril, right_tendril, left_ribcage, right_ribcage
//# witch                    head, headwear, headwear2, body, bodywear, arms, left_leg, right_leg, nose, mole
//# wither                   body1 ... body3, head1 ... head3
//# wither_armor             body1 ... body3, head1 ... head3
//# zoglin                   head, right_ear, left_ear, body, front_right_leg, front_left_leg, back_right_leg, back_left_leg, mane
            default -> null; //throw new RuntimeException("EMF doesn't map: "+mobName); //todo
        };


    }



    private static final Map<String, PartAndChildName> genericNonPlayerBiped = Map.ofEntries(
            getEntryOptifineSameAsVanilla("head"),
            getEntryOptifineDifferent("headwear", "hat"),
            getEntryOptifineSameAsVanilla("body"),
            getEntryOptifineSameAsVanilla("left_arm"),
            getEntryOptifineSameAsVanilla("right_arm"),
            getEntryOptifineSameAsVanilla("left_leg"),
            getEntryOptifineSameAsVanilla("right_leg")
    );
    //# horse                    , back_left_leg, back_right_leg, front_left_leg, front_right_leg,
//#                          child_back_left_leg, child_back_right_leg, child_front_left_leg, child_front_right_leg
    private static final Map<String, PartAndChildName> genericHorse = Map.ofEntries(
            getEntryOptifineWithChildList("body","body",List.of("tail","saddle")),
            getEntryOptifineWithChildList("head","head",List.of("left_ear","right_ear")),
            getEntryOptifineSameAsVanilla("tail"),
            getEntryOptifineSameAsVanilla("saddle"),
            getEntryOptifineSameAsVanilla("mane"),
            getEntryOptifineDifferent("mouth","upper_mouth"),
            getEntryOptifineSameAsVanilla("left_ear"),
            getEntryOptifineSameAsVanilla("right_ear"),
            getEntryOptifineWithChildList("neck", "head_parts",
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
            getEntryOptifineDifferent("noseband", "mouth_saddle_wrap"),
            getEntryOptifineDifferent("headpiece", "head_saddle"),
            getEntryOptifineDifferent("right_rein", "right_saddle_line"),
                    getEntryOptifineDifferent("left_rein", "left_saddle_line"),
                    getEntryOptifineDifferent("right_bit", "right_saddle_mouth"),
                    getEntryOptifineDifferent("left_bit", "left_saddle_mouth"),

                    getEntryOptifineDifferent("back_left_leg", "left_hind_leg"),
                    getEntryOptifineDifferent("back_right_leg", "right_hind_leg"),
                    getEntryOptifineDifferent("front_left_leg", "left_front_leg"),
                    getEntryOptifineDifferent("front_right_leg", "right_front_leg"),
                    getEntryOptifineDifferent("child_back_left_leg", "left_hind_baby_leg"),
                    getEntryOptifineDifferent("child_back_right_leg", "right_hind_baby_leg"),
                    getEntryOptifineDifferent("child_front_left_leg", "left_front_baby_leg"),
                    getEntryOptifineDifferent("child_front_right_leg", "right_front_baby_leg")
    );

    private static final Map<String, PartAndChildName> genericPlayerBiped = Map.ofEntries(
            getEntryOptifineSameAsVanilla("head"),
            getEntryOptifineDifferent("headwear", "hat"),
            getEntryOptifineSameAsVanilla("body"),
            getEntryOptifineSameAsVanilla("left_arm"),
            getEntryOptifineSameAsVanilla("right_arm"),
            getEntryOptifineSameAsVanilla("left_leg"),
            getEntryOptifineSameAsVanilla("right_leg"),
            getEntryOptifineSameAsVanilla("ear"),
            getEntryOptifineSameAsVanilla("left_sleeve"),
            getEntryOptifineSameAsVanilla("right_sleeve"),
            getEntryOptifineSameAsVanilla("left_pants"),
            getEntryOptifineSameAsVanilla("right_pants"),
            getEntryOptifineSameAsVanilla("jacket"),
            getEntryOptifineSameAsVanilla("cloak")
    );
    private static final Map<String, PartAndChildName> genericPiglinBiped = Map.ofEntries(
            getEntryOptifineSameAsVanilla("head"),
            getEntryOptifineDifferent("headwear", "hat"),
            getEntryOptifineSameAsVanilla("body"),
            getEntryOptifineSameAsVanilla("left_arm"),
            getEntryOptifineSameAsVanilla("right_arm"),
            getEntryOptifineSameAsVanilla("left_leg"),
            getEntryOptifineSameAsVanilla("ear"),
            getEntryOptifineSameAsVanilla("right_leg"),
            getEntryOptifineSameAsVanilla("left_ear"),
            getEntryOptifineSameAsVanilla("right_ear"),
            getEntryOptifineSameAsVanilla("left_sleeve"),
            getEntryOptifineSameAsVanilla("right_sleeve"),
            getEntryOptifineSameAsVanilla("left_pants"),
            getEntryOptifineSameAsVanilla("right_pants"),
            getEntryOptifineSameAsVanilla("jacket"),
            getEntryOptifineSameAsVanilla("cloak")
    );
    private static final Map<String, PartAndChildName> genericQuadraped = Map.ofEntries(
            getEntryOptifineSameAsVanilla("head"),
            getEntryOptifineSameAsVanilla("body"),
            getEntryOptifineDifferent("leg1", "right_hind_leg"),
            getEntryOptifineDifferent("leg2", "left_hind_leg"),
            getEntryOptifineDifferent("leg3", "right_front_leg"),
            getEntryOptifineDifferent("leg4", "left_front_leg"));


    public static record PartAndChildName(@NotNull String partName, @NotNull List<String> childNamesToExpect){

    }
}
