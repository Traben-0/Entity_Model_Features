package traben.entity_model_features.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import traben.entity_model_features.config.EMFConfigMainScreen;

public class EMFModMenuEntry implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        try {
//            if (FabricLoader.getInstance().isModLoaded("yet-another-config-lib")) {
//                return EMFYACL::createGui;
//            }
            return EMFConfigMainScreen::new;
        }catch (Exception e){
            return screen -> null;
        }
    }



}
