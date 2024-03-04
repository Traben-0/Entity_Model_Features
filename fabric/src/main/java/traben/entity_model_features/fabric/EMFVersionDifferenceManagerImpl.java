package traben.entity_model_features.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class EMFVersionDifferenceManagerImpl {

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static boolean isThisModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }


    @SuppressWarnings("SameReturnValue")
    public static boolean isForge() {
        return false;
    }
}
