package traben.entity_model_features;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.LightmapTextureManager;
import org.apache.logging.log4j.LogManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.propeties.*;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.property_reading.properties.RandomProperties;
import traben.tconfig.TConfigHandler;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class EMF {

    public static final int EYES_FEATURE_LIGHT_VALUE = LightmapTextureManager.MAX_LIGHT_COORDINATE + 1;
    public static final String MOD_ID = "entity_model_features";
    private static final String[] quips = {
            "special thanks to Cody, top donator!",
            "your third cousin's, dog's, previous owner's, uncle's, old boss's, fifth favourite mod!",
            "breaking your resource packs since April 1st 2023.",
            "not fit for consumption in Portugal.",
            "one of the mods ever made!",
            ",serutaeF ledoM ytitnE gnidoaL",
            "did you know if you turn off the lights and whisper 'OptiFine' 3 times you will lose 20fps.",
            "now compatible with Minecraft Legends!",
            "now available for Terraria!",
            "OptiFine's weirder younger half-brother that runs around making train models.",
            "(:",
            "0% Opti, 70% Fine.",
            "yes EMF breaks your resource pack, on purpose >:). There are 300 lines of code dedicated just for detecting if it is you specifically and if your favourite resource pack is installed, then EMF breaks it >:)\n/s",
            "we get there when we get there.",
            "the ETA is a lie.",
            "allegedly compatible with the OptiFine format.",
            "now compatible with every mod, except all the ones that aren't...",
            "100% of the time it works 90% of the time!",
            "now moving all models 0.00001 blocks to the left every 4 seconds.",
            "PI = " + ((float) Math.PI) + " and you can't convince me otherwise.",
            "90 =" + ((float) Math.toRadians(90)) + "!",
            "making those animations fresh since 1862!"
    };
    public static boolean forgeHadLoadingError = false;
    public static boolean testedForge = !EMFVersionDifferenceManager.isForge();

    private static TConfigHandler<EMFConfig> configHandler = null;

    public static TConfigHandler<EMFConfig> config() {
        if (configHandler == null) {
            configHandler = new TConfigHandler<>(EMFConfig::new, MOD_ID, "EMF");
            ETF.registerConfigHandler(configHandler);
        }
        return configHandler;
    }

    public static void init() {
        LogManager.getLogger().info("Loading Entity Model Features, " + randomQuip());
        //init data manager
        EMFManager.getInstance();

        //register EMF random properties to ETF
        ETFApi.registerCustomRandomPropertyFactory(MOD_ID,
                RandomProperties.RandomPropertyFactory.of("modelRule",
                        "entity_model_features.rule_property",
                        ModelRuleIndexProperty::getPropertyOrNull),
                RandomProperties.RandomPropertyFactory.of("modelSuffix",
                        "entity_model_features.suffix_property",
                        ModelSuffixProperty::getPropertyOrNull),
                RandomProperties.RandomPropertyFactory.of("var",
                        "entity_model_features.var_property",
                        EntityVariableFloatProperty::getPropertyOrNull),
                RandomProperties.RandomPropertyFactory.of("varb",
                        "entity_model_features.varb_property",
                        EntityVariableBooleanProperty::getPropertyOrNull),
                RandomProperties.RandomPropertyFactory.of("global_var",
                        "entity_model_features.global_var_property",
                        GlobalVariableFloatProperty::getPropertyOrNull),
                RandomProperties.RandomPropertyFactory.of("global_varb",
                        "entity_model_features.global_varb_property",
                        GlobalVariableBooleanProperty::getPropertyOrNull));

        //register EMF physics mod hook
//todo        RagdollMapper.addHook(new EMFCustomRagDollHookTest());

    }

    //mod menu config screen factory
    public static Screen getConfigScreen(Screen parent) {
        return getConfigScreen(null, parent);
    }

    //forge config screen factory
    public static Screen getConfigScreen(MinecraftClient ignored, Screen parent) {
        return ETF.getConfigScreen(parent);
    }

    public static boolean testForForgeLoadingError() {
        if (!testedForge) {
            testedForge = true;
            // this is required for forge
            // if forge detects a missing dependency it decides that access wideners are dumb and won't load them...
            // this would be all fine and dandy usually as forge shows a warning about the missing mods right after it finishes loading the game
            // however EMF mixins to the game loading before this...
            // EMF relies on access widening...
            // EMF crashes...
            // users think its EMF's fault when really it's an entirely unrelated mod missing a dependency...
            // thanks Forge.
            // this method gets called to cancel out of every game loading stage mixin in the case of this forge issue
            // so that forge can at the very least survive long enough to tell users the real reason the game won't work
            try {
                EMFManager.getInstance();
            } catch (IncompatibleClassChangeError error) {
                if (error.getMessage().contains("cannot inherit from final class")) {
                    EMF.forgeHadLoadingError = true;
                    EMFUtils.logError(
                            "EMF has crashed due to a (probably) unrelated forge dependency error,\n EMF has been disabled so the true culprit will be sent to users after game load:\n"
                                    + error.getMessage());
                } else {
                    //throw the error if it's something we were not expecting
                    throw error;
                }
            }
        }

        return forgeHadLoadingError;
    }

    private static String randomQuip() {
        int rand = new Random().nextInt(quips.length);
        return quips[rand];
    }

}
