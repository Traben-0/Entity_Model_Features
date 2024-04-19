package traben.entity_model_features.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import traben.entity_model_features.EMF;

public class EMFModMenuEntry implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        try {
            return EMF::getConfigScreen;
        } catch (Exception e) {
            return screen -> null;
        }
    }


}
