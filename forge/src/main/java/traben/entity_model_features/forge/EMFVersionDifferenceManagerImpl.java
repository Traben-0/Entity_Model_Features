package traben.entity_model_features.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class EMFVersionDifferenceManagerImpl {

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }


    public static boolean isThisModLoaded(String modId) {
        try {
            ModList list = ModList.get();
            if (list == null) {
                LoadingModList list2 = FMLLoader.getLoadingModList();
                return list2 != null && checkInitialModList(list2, modId);
            }
            return list.isLoaded(modId);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkInitialModList(@NotNull LoadingModList list, String modId) {
        try {
            for (ModInfo mod : list.getMods()) {
                if (mod.getModId().equals(modId)) return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }


    @SuppressWarnings("SameReturnValue")
    public static boolean isForge() {
        return true;
    }
}
