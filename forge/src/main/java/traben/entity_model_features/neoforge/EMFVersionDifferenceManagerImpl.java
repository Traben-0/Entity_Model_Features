package traben.entity_model_features.neoforge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class EMFVersionDifferenceManagerImpl {

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }




    public static boolean isThisModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }


    @SuppressWarnings("SameReturnValue")
    public static boolean isForge() {
        return true;
    }
}
