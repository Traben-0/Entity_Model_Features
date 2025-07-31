package traben.entity_model_features.config;

//#if FABRIC
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import traben.entity_texture_features.ETF;

@Environment(EnvType.CLIENT)
public class EMFModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ETF::getConfigScreen;
    }
}
//#else
//$$ public class EMFModMenu { }
//#endif