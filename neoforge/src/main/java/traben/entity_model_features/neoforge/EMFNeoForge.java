package traben.entity_model_features.neoforge;


import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
#if MC >= MC_20_6
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
#else
import net.neoforged.neoforge.client.ConfigScreenHandler;
#endif
import traben.entity_model_features.EMF;
import traben.entity_model_features.utils.EMFUtils;

@Mod(EMF.MOD_ID)
public class EMFNeoForge {
    public EMFNeoForge() {
        // Submit our event bus to let architectury register our content on the right time
        //EventBuses.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        if (FMLEnvironment.dist.isClient()) {

            //not 100% sure what this actually does but it will trigger the catch if loading on the server side
            //ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));


            try {
                ModLoadingContext.get().registerExtensionPoint(
                          #if MC >= MC_20_6
                        IConfigScreenFactory.class,
                        ()-> EMF::getConfigScreen);
                        #else
                        ConfigScreenHandler.ConfigScreenFactory.class,
                        ()-> new ConfigScreenHandler.ConfigScreenFactory(EMF::getConfigScreen));
                        #endif

            } catch (NoClassDefFoundError e) {
                EMFUtils.logError("[Entity Model Features]: Mod config screen broken, download latest forge version");
            }

            EMF.init();
        } else {

            throw new UnsupportedOperationException("Attempting to load a clientside only mod [EMF] on the server, refusing");
        }
    }
}
