package traben.entity_model_features.fabric;

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
import traben.entity_model_features.config.EMFConfig;

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
                        .name(Text.of("force translucent rendering"))
                        .tooltip(Text.of("forces models to render using translucency support\n might break things idk")) // optional
                        .binding(
                                false, // default
                                () -> EMFData.getInstance().getConfig().forceTranslucentMobRendering, // getter
                                newValue -> EMFData.getInstance().getConfig().forceTranslucentMobRendering = newValue // setter
                        )
                        .controller(BooleanController::new)
                        .build())

                .option(Option.createBuilder(EMFConfig.VanillaModelRenderMode.class)
                        .name(Text.of("render vanilla model hologram mode"))
                        .tooltip(Text.of("render the vanilla model next to or on top of the EMF model")) // optional
                        .binding(
                                EMFConfig.VanillaModelRenderMode.Off, // default
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
                        .tooltip(Text.of("select a spawn animation type\nexpect some bugs\nnot all work yet")) // optional
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

//                .option(Option.createBuilder(EMFConfig.MathFunctionChoice.class)
//                        .name(Text.of("Math function type"))
//                        .tooltip(Text.of("hi")) // optional
//                        .binding(
//                                EMFConfig.MathFunctionChoice.JavaMath, // default
//                                () -> EMFData.getInstance().getConfig().mathFunctionChoice, // getter
//                                newValue -> EMFData.getInstance().getConfig().mathFunctionChoice = newValue // setter
//                        )
//                        .controller((val)->new EnumController<EMFConfig.MathFunctionChoice>(val , enumConstant -> Text.of(enumConstant.toString()) ))
//                        .build())


//                .option(Option.createBuilder(float.class)
//                        .name(Text.of("minimum animation drop off distance"))
//                        .tooltip(Text.of("animations will reduce their rate depending on distance from the player\n this will start happening from this distance from the player")) // optional
//                        .binding(
//                                8f, // default
//                                () -> EMFData.getInstance().getConfig().animationRateMinimumDistanceDropOff, // getter
//                                newValue -> EMFData.getInstance().getConfig().animationRateMinimumDistanceDropOff = newValue // setter
//                        )
//                        .controller((val)->new FloatSliderController(val,1,128,1f))
//                        .build())
//                .option(Option.createBuilder(float.class)
//                        .name(Text.of("min animation tps"))
//                        .tooltip(Text.of("sets the minimun tps for distant animations for if you want to crank up the drop off rate below")) // optional
//                        .binding(
//                                0.3f, // default
//                                () -> EMFData.getInstance().getConfig().minimumAnimationFPS, // getter
//                                newValue -> EMFData.getInstance().getConfig().minimumAnimationFPS = newValue // setter
//                        )
//                        .controller((val)->new FloatSliderController(val,0.1f,20,0.1f))
//                        .build())
//                .option(Option.createBuilder(float.class)
//                        .name(Text.of("animation quality drop off rate"))
//                        .tooltip(Text.of("this sets the rate at which distant mobs animation rate will reduce")) // optional
//                        .binding(
//                                10f, // default
//                                () -> EMFData.getInstance().getConfig().animationRateDistanceDropOffRate, // getter
//                                newValue -> EMFData.getInstance().getConfig().animationRateDistanceDropOffRate = newValue // setter
//                        )
//                        .controller((val)->new FloatSliderController(val,0,16,1f))
//                        .build())
                .build();
    }


    private static ConfigCategory getDebug(){
        return ConfigCategory.createBuilder()
                .name(Text.of("Debug"))
                .tooltip(Text.of("debug options, these are mostly just for the dev"))
                .option(Option.createBuilder(boolean.class)
                        .name(Text.of("patch for features"))
                        .tooltip(Text.of("temp patch while all mobs do not extend vanilla models")) // optional
                        .binding(
                                false, // default
                                () -> EMFData.getInstance().getConfig().patchFeatures, // getter
                                newValue -> EMFData.getInstance().getConfig().patchFeatures = newValue // setter
                        )
                        .controller(BooleanController::new)
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.of("print maths"))
                        .tooltip(Text.of("prints math debug data to log\nWARNING EXTREMELY LAG INDUCING!")) // optional
                        .binding(
                                false, // default
                                () -> EMFData.getInstance().getConfig().printAllMaths, // getter
                                newValue -> EMFData.getInstance().getConfig().printAllMaths = newValue // setter
                        )
                        .controller(BooleanController::new)
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.of("print model creation"))
                        .tooltip(Text.of("prints model creation debug data to log\nwill increase the stutter time the first time a model is rendered")) // optional
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
        EMFData.reset();
        MinecraftClient.getInstance().reloadResources();
        EMFData.reset();
    }
}
