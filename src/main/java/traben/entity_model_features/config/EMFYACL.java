package traben.entity_model_features.config;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.BooleanController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import traben.entity_model_features.EMFData;

public class EMFYACL{

    public static Screen createGui(Screen parent) {




        return YetAnotherConfigLib.createBuilder()
                .title(Text.of("Entity Model features"))
                .category(getGeneral())
                .category(getOptimizations())
                .category(getDebug())
                .save(EMFYACL::saveAndReset)
                .build()
                .generateScreen(parent);
    }

    private static ConfigCategory getGeneral(){
        return ConfigCategory.createBuilder()
                .name(Text.of("General"))
                .tooltip(Text.of("General settings"))
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
//                .option(Option.createBuilder(boolean.class)
//                        .name(Text.of("use custom player model arms in first person"))
//                        .tooltip(Text.of("")) // optional
//                        .binding(
//                                false, // default
//                                () -> EMFData.getInstance().getConfig().useCustomPlayerHandInFPS, // getter
//                                newValue -> EMFData.getInstance().getConfig().useCustomPlayerHandInFPS = newValue // setter
//                        )
//                        .controller(BooleanController::new)
//                        .build())

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
                .build();
    }

    private static ConfigCategory getOptimizations(){
        return ConfigCategory.createBuilder()
                .name(Text.of("Optimization"))
                .tooltip(Text.of("Settings related to animation optimization"))

                .option(Option.createBuilder(EMFConfig.AnimationRatePerSecondMode.class)
                        .name(Text.of("animation rate: "))
                        .tooltip(Text.of("actual value is always limited by fps")) // optional
                        .binding(
                                EMFConfig.AnimationRatePerSecondMode.Sixty_tps, // default
                                () -> EMFData.getInstance().getConfig().animationRate, // getter
                                newValue -> EMFData.getInstance().getConfig().animationRate = newValue // setter

                        )
                        .controller((val)->new EnumController<EMFConfig.AnimationRatePerSecondMode>(val , enumConstant -> Text.of(enumConstant.toString()) ))
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
                                0.3f, // default
                                () -> EMFData.getInstance().getConfig().minimumAnimationFPS, // getter
                                newValue -> EMFData.getInstance().getConfig().minimumAnimationFPS = newValue // setter
                        )
                        .controller((val)->new FloatSliderController(val,0.1f,20,0.1f))
                        .build())
                .option(Option.createBuilder(float.class)
                        .name(Text.of("animation quality drop off rate"))
                        .tooltip(Text.of("")) // optional
                        .binding(
                                10f, // default
                                () -> EMFData.getInstance().getConfig().animationRateDistanceDropOffRate, // getter
                                newValue -> EMFData.getInstance().getConfig().animationRateDistanceDropOffRate = newValue // setter
                        )
                        .controller((val)->new FloatSliderController(val,0,16,1f))
                        .build())
                .build();
    }


    private static ConfigCategory getDebug(){
        return ConfigCategory.createBuilder()
                .name(Text.of("Debug"))
                .tooltip(Text.of("debug options, these are mostly just for the dev"))

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
                .build();
    }



    static void saveAndReset(){
        EMFData.getInstance().EMF_saveConfig();
        MinecraftClient.getInstance().reloadResources();
        EMFData.reset();
    }
}
