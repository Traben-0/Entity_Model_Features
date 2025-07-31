package traben.entity_model_features.mod_compat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import traben.entity_model_features.utils.EMFUtils;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * see EBE api docs <a href="https://github.com/FoundationGames/EnhancedBlockEntities">...</a>
 */
public class EBEConfigModifier implements BiConsumer<Properties, Map<String, Component>>, Consumer<Runnable> {

    public static boolean chestsDisabled = false;
    public static boolean shulkerBoxesDisabled = false;
    public static boolean bellsDisabled = false;
    public static boolean bedsDisabled = false;
    public static boolean signsDisabled = false;
    public static boolean decoratedPotsDisabled = false;

    private static Runnable ebeReloader = () -> {};

    /**
     * Modifies the Enhanced Block Entities config to disable rendering of blocks that have custom entity models
     *
     * @param ebeAffectingJemsFound a set of block names that have custom entity models
     */
    @SuppressWarnings("RedundantThrows")
    public static void modifyEBEConfig(Set<String> ebeAffectingJemsFound) {

        chestsDisabled = ebeAffectingJemsFound.contains("chest");
        shulkerBoxesDisabled = ebeAffectingJemsFound.contains("shulker_box");
        bellsDisabled = ebeAffectingJemsFound.contains("bell");
        bedsDisabled = ebeAffectingJemsFound.contains("bed");
        signsDisabled = ebeAffectingJemsFound.contains("sign");
        decoratedPotsDisabled = ebeAffectingJemsFound.contains("decorated_pot");

        ebeReloader.run();
        EMFUtils.log("EBE config modified by EMF, the following blocks have been disabled in EBE because they have custom entity models (this can be disabled in EMF's settings): " + ebeAffectingJemsFound);
    }

    @Override
    public void accept(final Properties overrideConfigValues, final Map<String, Component> overrideReasons) {
        if (chestsDisabled) {
            overrideConfigValues.setProperty("render_enhanced_chests", "false");
            overrideReasons.put("render_enhanced_chests", Component.literal("EBE Enhanced Chests are disabled by a loaded EMF model!").withStyle(ChatFormatting.YELLOW));
        }
        if (shulkerBoxesDisabled) {
            overrideConfigValues.setProperty("render_enhanced_shulker_boxes", "false");
            overrideReasons.put("render_enhanced_shulker_boxes", Component.literal("EBE Enhanced Shulker Boxes are disabled by a loaded EMF model!").withStyle(ChatFormatting.YELLOW));
        }
        if (bellsDisabled) {
            overrideConfigValues.setProperty("render_enhanced_bells", "false");
            overrideReasons.put("render_enhanced_bells", Component.literal("EBE Enhanced Bells are disabled by a loaded EMF model!").withStyle(ChatFormatting.YELLOW));
        }
        if (bedsDisabled) {
            overrideConfigValues.setProperty("render_enhanced_beds", "false");
            overrideReasons.put("render_enhanced_beds", Component.literal("EBE Enhanced Beds are disabled by a loaded EMF model!").withStyle(ChatFormatting.YELLOW));
        }
        if (signsDisabled) {
            overrideConfigValues.setProperty("render_enhanced_signs", "false");
            overrideReasons.put("render_enhanced_signs", Component.literal("EBE Enhanced Signs are disabled by a loaded EMF model!").withStyle(ChatFormatting.YELLOW));
        }
        if (decoratedPotsDisabled) {
            overrideConfigValues.setProperty("render_enhanced_decorated_pots", "false");
            overrideReasons.put("render_enhanced_decorated_pots", Component.literal("EBE Enhanced Decorated Pots are disabled by a loaded EMF model!").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public void accept(final Runnable ebeConfigReloader) {
        ebeReloader = ebeConfigReloader;
    }
}
