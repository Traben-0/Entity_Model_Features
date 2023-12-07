package traben.entity_model_features;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import org.apache.logging.log4j.LogManager;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_texture_features.ETFApi;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class EMFClient {

    public static final int EYES_FEATURE_LIGHT_VALUE = LightmapTextureManager.MAX_LIGHT_COORDINATE + 1;
    public static final String MOD_ID = "entity_model_features";
    private static final String[] quips = {
            "special thanks to Cody!",
            "your third cousin's, dog's, previous owner's, uncle's, old boss's, fifth favourite mod!",
            "Thanks for 200K plus downloads!!",
            "why does no one download Solid Mobs :(",
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
            ".jpms will work one day.",
            "yes EMF breaks your resource pack, on purpose >:). There are 300 lines of code dedicated just for detecting if it is you specifically and if your favourite resource pack is installed, then EMF breaks it >:)\n/s",
            "we get there when we get there.",
            "the ETA is a lie.",
            "allegedly compatible with the OptiFine format.",
            "now compatible with every mod, except all the ones that aren't compatible...",
            "100% of the time it works 90% of the time!",
            "now moving all models 0.00001 blocks to the left every 4 seconds.",
            "PI = " + ((float) Math.PI) + " and you can't convince me otherwise.",
            "90 =" + ((float) Math.toRadians(90)) + "!"
    };

    public static void init() {
        LogManager.getLogger().info("Loading Entity Model Features, " + randomQuip());
        //init data manager
        EMFManager.getInstance();

        //register new etf random property for emf to track variants
        ETFApi.registerCustomRandomPropertyFactory(MOD_ID, EntityVariantProperty::getPropertyOrNull);

        //register EMF physics mod hook
//        RagdollMapper.addHook(new EMFCustomRagDollHookTest());

    }

    private static String randomQuip() {
        int rand = new Random().nextInt(quips.length);
        return quips[rand];
    }

}
