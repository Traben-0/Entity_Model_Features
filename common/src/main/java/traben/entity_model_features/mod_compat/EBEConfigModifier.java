package traben.entity_model_features.mod_compat;

import foundationgames.enhancedblockentities.EnhancedBlockEntities;
import traben.entity_model_features.utils.EMFUtils;

import java.util.Set;

public abstract class EBEConfigModifier {

    /**
     * Modifies the Enhanced Block Entities config to disable rendering of blocks that have custom entity models
     *
     * @param ebeAffectingJemsFound a set of block names that have custom entity models
     * @throws Exception if the config file cannot be modified
     * @throws Error     if the config file cannot be modified
     */
    public static void modifyEBEConfig(Set<String> ebeAffectingJemsFound) throws Exception, Error {
        final var ebeConfig = EnhancedBlockEntities.CONFIG;

        if (ebeAffectingJemsFound.contains("chest")) {
            ebeConfig.renderEnhancedChests = false;
        }
        if (ebeAffectingJemsFound.contains("shulker_box")) {
            ebeConfig.renderEnhancedShulkerBoxes = false;
        }
        if (ebeAffectingJemsFound.contains("bell")) {
            ebeConfig.renderEnhancedBells = false;
        }
        if (ebeAffectingJemsFound.contains("bed")) {
            ebeConfig.renderEnhancedBeds = false;
        }
        if (ebeAffectingJemsFound.contains("sign")) {
            ebeConfig.renderEnhancedSigns = false;
        }
        if (ebeAffectingJemsFound.contains("decorated_pot")) {
            ebeConfig.renderEnhancedDecoratedPots = false;
        }

        ebeConfig.save();
        EnhancedBlockEntities.load();
        EMFUtils.log("EBE config modified by EMF, the following blocks have been disabled in EBE because they have custom entity models (this can be disabled in EMF's settings): " + ebeAffectingJemsFound);
    }

}
