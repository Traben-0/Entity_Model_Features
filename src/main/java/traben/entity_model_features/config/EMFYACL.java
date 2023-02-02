package traben.entity_model_features.config;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.BooleanController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import traben.entity_model_features.EMFData;

public class EMFYACL{

    public static Screen createGui(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.of("Entity Model features"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Main"))
                        .tooltip(Text.of("This displays when you hover over a category button")) // optional
//                        .option(Option.createBuilder(boolean.class)
//                                .name(Text.of("Use mXParser"))
//                                .tooltip(Text.of("more accurate but slower")) // optional
//                                .binding(
//                                        false, // default
//                                        () -> EMFData.getInstance().getConfig().useMXParser, // getter
//                                        newValue -> EMFData.getInstance().getConfig().useMXParser = newValue // setter
//                                )
//                                .controller(BooleanController::new)
//                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(Text.of("print maths"))
                                .tooltip(Text.of("")) // optional
                                .binding(
                                        false, // default
                                        () -> EMFData.getInstance().getConfig().printAllMaths, // getter
                                        newValue -> EMFData.getInstance().getConfig().printAllMaths = newValue // setter
                                )
                                .controller(BooleanController::new)
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(Text.of("print model creation"))
                                .tooltip(Text.of("")) // optional
                                .binding(
                                        false, // default
                                        () -> EMFData.getInstance().getConfig().printModelCreationInfoToLog, // getter
                                        newValue -> EMFData.getInstance().getConfig().printModelCreationInfoToLog = newValue // setter
                                )
                                .controller(BooleanController::new)
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(Text.of("force transparent rendering"))
                                .tooltip(Text.of("")) // optional
                                .binding(
                                        false, // default
                                        () -> EMFData.getInstance().getConfig().forceTranslucentMobRendering, // getter
                                        newValue -> EMFData.getInstance().getConfig().forceTranslucentMobRendering = newValue // setter
                                )
                                .controller(BooleanController::new)
                                .build())

                        .option(Option.createBuilder(EMFConfig.VanillaModelRenderMode.class)
                                .name(Text.of("render vanilla model hologram mode"))
                                .tooltip(Text.of("")) // optional
                                .binding(
                                        EMFConfig.VanillaModelRenderMode.No, // default
                                        () -> EMFData.getInstance().getConfig().displayVanillaModelHologram, // getter
                                        newValue -> EMFData.getInstance().getConfig().displayVanillaModelHologram = newValue // setter
                                )
                                .controller((val)->new EnumController<EMFConfig.VanillaModelRenderMode>(val , enumConstant -> Text.of(enumConstant.toString()) ))
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(Text.of("use custom player model arms in first person"))
                                .tooltip(Text.of("")) // optional
                                .binding(
                                        false, // default
                                        () -> EMFData.getInstance().getConfig().useCustomPlayerHandInFPS, // getter
                                        newValue -> EMFData.getInstance().getConfig().useCustomPlayerHandInFPS = newValue // setter
                                )
                                .controller(BooleanController::new)
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(Text.of("animation rate per second"))
                                .tooltip(Text.of("")) // optional
                                .binding(
                                        30, // default
                                        () -> EMFData.getInstance().getConfig().animationFPS, // getter
                                        newValue -> EMFData.getInstance().getConfig().animationFPS = newValue // setter

                                )
                                .controller((val)->new IntegerSliderController(val,20,144,1))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.of("minimum animation drop off distance"))
                                .tooltip(Text.of("")) // optional
                                .binding(
                                        8f, // default
                                        () -> EMFData.getInstance().getConfig().animationRateMinimumDistanceDropOff, // getter
                                        newValue -> EMFData.getInstance().getConfig().animationRateMinimumDistanceDropOff = newValue // setter
                                )
                                .controller((val)->new FloatSliderController(val,1,128,1f))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.of("min animation fps"))
                                .tooltip(Text.of("")) // optional
                                .binding(
                                        1f, // default
                                        () -> EMFData.getInstance().getConfig().minimumAnimationFPS, // getter
                                        newValue -> EMFData.getInstance().getConfig().minimumAnimationFPS = newValue // setter
                                )
                                .controller((val)->new FloatSliderController(val,0,20,0.1f))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.of("animation quality drop off rate"))
                                .tooltip(Text.of("")) // optional
                                .binding(
                                        94f, // default
                                        () -> EMFData.getInstance().getConfig().animationRateDistanceDropOffRate, // getter
                                        newValue -> EMFData.getInstance().getConfig().animationRateDistanceDropOffRate = newValue // setter
                                )
                                .controller((val)->new FloatSliderController(val,1,100,1f))
                                .build())
                        .option(Option.createBuilder(EMFConfig.SpawnAnimation.class)
                                .name(Text.of("SPAWN ANIMATION"))
                                .tooltip(Text.of("")) // optional
                                .binding(
                                        EMFConfig.SpawnAnimation.None, // default
                                        () -> EMFData.getInstance().getConfig().spawnAnim, // getter
                                        newValue -> EMFData.getInstance().getConfig().spawnAnim = newValue // setter
                                )
                                .controller((val)->new EnumController<EMFConfig.SpawnAnimation>(val , enumConstant -> Text.of(enumConstant.toString()) ))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.of("spawn animation speed"))
                                .tooltip(Text.of("")) // optional
                                .binding(
                                        4f, // default
                                        () -> EMFData.getInstance().getConfig().spawnAnimTime, // getter
                                        newValue -> EMFData.getInstance().getConfig().spawnAnimTime = newValue // setter
                                )
                                .controller((val)->new FloatSliderController(val,1,50,1f))
                                .build())
                        .build())
                .save(EMFYACL::saveAndReset)
                .build()
                .generateScreen(parent);
    }

    static void saveAndReset(){
        EMFData.getInstance().EMF_saveConfig();
        MinecraftClient.getInstance().reloadResources();
        EMFData.reset();
    }
}
