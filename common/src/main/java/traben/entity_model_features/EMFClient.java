package traben.entity_model_features;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import traben.entity_model_features.utils.EMFManager;

@Environment(EnvType.CLIENT)
public class EMFClient {

    public static final String MOD_ID = "entity_model_features";

    public static final long START_TIME = System.currentTimeMillis();

    //@Override
    public static void init() {
        LogManager.getLogger().info("[Entity Model Features]: Loading! 1.19.3");
        //init data manager
        EMFManager.getInstance();

//        if(FabricLoader.getInstance().isModLoaded("physicsmod")){
//            EMFRagdollHook.addRagdollHook();
//        }
    }

}
