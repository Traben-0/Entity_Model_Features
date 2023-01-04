package traben.entity_model_features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.EMF_CustomModel;
import traben.entity_model_features.utils.EMFUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class Entity_model_featuresClient implements ClientModInitializer {

    public static EMFConfig EMFConfigData;

    public static Int2ObjectOpenHashMap<EMF_CustomModel<LivingEntity>> JEMPATH_CustomModel = new Int2ObjectOpenHashMap<>();

    //public static HashMap<String, EntityModel> ENTITYNAME_VanillaModel = new HashMap<>();

    @Override
    public void onInitializeClient() {
        //testing
        LogManager.getLogger().info("[Entity Model Features]: Loading! 1.18.x");
        loadConfig();
    }

    // config code based on bedrockify & actually unbreaking fabric config code
    // https://github.com/juancarloscp52/BedrockIfy/blob/1.17.x/src/main/java/me/juancarloscp52/bedrockify/Bedrockify.java
    // https://github.com/wutdahack/ActuallyUnbreakingFabric/blob/1.18.1/src/main/java/wutdahack/actuallyunbreaking/ActuallyUnbreaking.java
    public void loadConfig() {
        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (config.exists()) {
            try {
                FileReader fileReader = new FileReader(config);
                EMFConfigData = gson.fromJson(fileReader, EMFConfig.class);
                fileReader.close();
                EMFUtils.EMF_saveConfig();
            } catch (IOException e) {
                EMFUtils.EMF_modMessage("Config could not be loaded, using defaults", false);
            }
        } else {
            EMFConfigData = new EMFConfig();
            EMFUtils.EMF_saveConfig();
        }
    }
}
