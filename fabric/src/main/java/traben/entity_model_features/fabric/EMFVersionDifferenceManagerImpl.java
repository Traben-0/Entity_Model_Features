package traben.entity_model_features.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import traben.entity_model_features.utils.EMFManager;

import java.nio.file.Path;

public class EMFVersionDifferenceManagerImpl {

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static boolean isThisModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
    public static boolean isETFValidAPI(){
        return ETFCheck.isETFValidAPI();
    }

    public static EMFManager.EMFPropertyTester getAllValidPropertyObjects(Identifier propsID){
        return ETFPropertyReader.getAllValidPropertyObjects(propsID);
    }
}
