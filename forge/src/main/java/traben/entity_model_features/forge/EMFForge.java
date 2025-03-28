package traben.entity_model_features.forge;


import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import traben.entity_model_features.EMF;
import traben.entity_model_features.utils.EMFUtils;

import java.util.function.Function;

@Mod(EMF.MOD_ID)
public class EMFForge {
    public EMFForge() {
        // Submit our event bus to let architectury register our content on the right time
        //EventBuses.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        if (FMLEnvironment.dist == Dist.CLIENT) {

            //not 100% sure what this actually does but it will trigger the catch if loading on the server side
            ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));


            try {
                ModLoadingContext.get().registerExtensionPoint(
                        ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory(#if MC >= MC_20_4 (Function<Screen, Screen>) #endif EMF::getConfigScreen));
            } catch (NoClassDefFoundError e) {
                EMFUtils.logError("[Entity Model Features]: Mod config screen broken, download latest forge version");
            }

            EMF.init();
        }
    }
}
