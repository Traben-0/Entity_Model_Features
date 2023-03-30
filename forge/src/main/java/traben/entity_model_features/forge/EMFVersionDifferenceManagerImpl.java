package traben.entity_model_features.forge;

import net.minecraft.util.Identifier;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import traben.entity_model_features.utils.EMFManager;

import java.nio.file.Path;

public class EMFVersionDifferenceManagerImpl {

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }



    public static boolean isETFValidAPI(){
        return ETFCheck.isETFValidAPI();
    }
    public static boolean isThisModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
    public static EMFManager.EMFPropertyTester getAllValidPropertyObjects(Identifier propsID){
        return ETFPropertyReader.getAllValidPropertyObjects(propsID);
    }
}
