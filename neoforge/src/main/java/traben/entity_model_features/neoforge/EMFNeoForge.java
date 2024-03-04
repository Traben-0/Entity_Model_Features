package traben.entity_model_features.neoforge;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import traben.entity_model_features.EMFClient;
import traben.entity_model_features.config.EMFConfigScreenMain;
import traben.entity_model_features.utils.EMFUtils;

@Mod(EMFClient.MOD_ID)
public class EMFNeoForge {
    public EMFNeoForge() {
        // Submit our event bus to let architectury register our content on the right time
        //EventBuses.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        if (FMLEnvironment.dist == Dist.CLIENT) {

            //not 100% sure what this actually does but it will trigger the catch if loading on the server side
            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));


            try {
                ModLoadingContext.get().registerExtensionPoint(
                        ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory((minecraftClient, screen) -> new EMFConfigScreenMain(screen)));
            } catch (NoClassDefFoundError e) {
                EMFUtils.logError("[Entity Model Features]: Mod config screen broken, download latest forge version");
            }

            EMFClient.init();
        } else {

            throw new UnsupportedOperationException("Attempting to load a clientside only mod [EMF] on the server, refusing");
        }
    }
}
