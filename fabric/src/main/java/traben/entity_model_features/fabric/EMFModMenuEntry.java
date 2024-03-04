package traben.entity_model_features.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import traben.entity_model_features.config.EMFConfigScreenMain;

public class EMFModMenuEntry implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        try {
            return EMFConfigScreenMain::new;
        } catch (Exception e) {
            return screen -> null;
        }
    }


}
