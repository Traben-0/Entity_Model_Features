package traben.entity_model_features.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.Objects;

public class EMFConfigMainScreen extends ETFConfigScreen {



    public EMFConfigMainScreen(Screen parent) {
        super(Text.translatable("entity_model_features.title"),parent);
       // this.parent = parent;
        tempConfig = EMFConfig.copyFrom(EMFConfig.getConfig());
    }


    EMFConfig tempConfig = null;

    @Override
    protected void init() {
        super.init();



        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.done"),
                (button) -> {
                    EMFConfig.setConfig(tempConfig);
                    EMFConfig.EMF_saveConfig();
                    if(EMFConfig.getConfig().reloadMode == EMFConfig.ModelDataRefreshMode.MANUAL) {
                        EMFManager.resetInstance();
                    }
                    MinecraftClient.getInstance().reloadResources();
                    Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.7), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("dataPack.validation.reset"),
                (button) -> {
                    tempConfig = new EMFConfig();
                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20).build());
        this.addDrawableChild(ButtonWidget.builder(
                ScreenTexts.CANCEL,
                (button) -> {
                    tempConfig = null;
                    Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());



        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.substitute_vanilla").getString() +
                        ": " + (tempConfig.attemptToCopyVanillaModelIntoMissingModelPart ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    tempConfig.attemptToCopyVanillaModelIntoMissingModelPart = !tempConfig.attemptToCopyVanillaModelIntoMissingModelPart;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.substitute_vanilla").getString() +
                            ": " + (tempConfig.attemptToCopyVanillaModelIntoMissingModelPart ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.substitute_vanilla.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.green_render").getString() +
                        ": " + (tempConfig.renderCustomModelsGreen ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    tempConfig.renderCustomModelsGreen = !tempConfig.renderCustomModelsGreen;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.green_render").getString() +
                            ": " + (tempConfig.renderCustomModelsGreen ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.green_render.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.4), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.log_models").getString() +
                        ": " + (tempConfig.printModelCreationInfoToLog ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    tempConfig.printModelCreationInfoToLog = !tempConfig.printModelCreationInfoToLog;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.log_models").getString() +
                            ": " + (tempConfig.printModelCreationInfoToLog ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.log_models.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.5), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.log_math").getString() +
                        ": " + (tempConfig.printAllMaths ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    tempConfig.printAllMaths = !tempConfig.printAllMaths;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.log_math").getString() +
                            ": " + (tempConfig.printAllMaths ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.log_math.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.6), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.vanilla_render").getString() +
                        ": " + (tempConfig.vanillaModelRenderMode.asText()).getString()),
                (button) -> {
                    tempConfig.vanillaModelRenderMode = tempConfig.vanillaModelRenderMode.next();
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.vanilla_render").getString() +
                            ": " + (tempConfig.vanillaModelRenderMode.asText()).getString()));
                },
                Text.translatable("entity_model_features.config.vanilla_render.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.7), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("data reset test mode").getString() +
                        ": " + tempConfig.reloadMode),
                (button) -> {
                    tempConfig.reloadMode = tempConfig.reloadMode.next();
                    button.setMessage(Text.of(Text.translatable("data reset test mode").getString() +
                            ": " + tempConfig.reloadMode));
                },
                Text.translatable("try the different data resetting mode options\nmanual will only reset EMF data upon leaving this config screen\noriginal means resetting data on every reload the same way its been done so far\n test tries resetting emf data at a different point in the resource reloading stage\nthere will likely be more to come")
        ));
    }


}
