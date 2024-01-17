package traben.entity_model_features.neoforge;


import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
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
        }catch (Exception e){
            return false;
        }
    }

    private static boolean checkInitialModList(@NotNull LoadingModList list, String modId){
        try {
            for (ModInfo mod : list.getMods()) {
                if(mod.getModId().equals(modId)) return true;
            }
        }catch (Exception e){
        }
        return false;
    }


    @SuppressWarnings("SameReturnValue")
    public static boolean isForge() {
        return true;
    }
}
