package traben.entity_model_features.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.Objects;

public class EMFConfigMainScreen extends ETFConfigScreen {



    public EMFConfigMainScreen(Screen parent) {
        super(new TranslatableText("entity_model_features.title"),parent);
       // this.parent = parent;
        tempConfig = EMFConfig.copyFrom(EMFConfig.getConfig());
    }

    public EMFConfigMainScreen(Screen parent,EMFConfig newTemp) {
        super(new TranslatableText("entity_model_features.title"),parent);
        tempConfig = newTemp;
    }


    EMFConfig tempConfig = null;

    @Override
    protected void init() {
        super.init();


        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.7), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("gui.done"),
                (button) -> {
                    EMFConfig.setConfig(tempConfig);
                    EMFConfig.EMF_saveConfig();
                    //EMFManager.resetInstance(); done in next line
                    MinecraftClient.getInstance().reloadResources();
                    Objects.requireNonNull(client).setScreen(parent);
                }));
        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("dataPack.validation.reset"),
                (button) -> {
                    Objects.requireNonNull(client).setScreen(new EMFConfigMainScreen(parent,new EMFConfig()));
                    this.close();
                }));
        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.CANCEL,
                (button) -> {
                    tempConfig = null;
                    Objects.requireNonNull(client).setScreen(parent);
                }));



        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
                Text.of(new TranslatableText("entity_model_features.config.substitute_vanilla").getString() +
                        ": " + (tempConfig.attemptToCopyVanillaModelIntoMissingModelPart ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    tempConfig.attemptToCopyVanillaModelIntoMissingModelPart = !tempConfig.attemptToCopyVanillaModelIntoMissingModelPart;
                    button.setMessage(Text.of(new TranslatableText("entity_model_features.config.substitute_vanilla").getString() +
                            ": " + (tempConfig.attemptToCopyVanillaModelIntoMissingModelPart ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                new TranslatableText("entity_model_features.config.substitute_vanilla.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(new TranslatableText("entity_model_features.config.green_render").getString() +
                        ": " + (tempConfig.renderCustomModelsGreen ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    tempConfig.renderCustomModelsGreen = !tempConfig.renderCustomModelsGreen;
                    button.setMessage(Text.of(new TranslatableText("entity_model_features.config.green_render").getString() +
                            ": " + (tempConfig.renderCustomModelsGreen ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                new TranslatableText("entity_model_features.config.green_render.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.4), (int) (this.width * 0.6), 20,
                Text.of(new TranslatableText("entity_model_features.config.log_models").getString() +
                        ": " + (tempConfig.printModelCreationInfoToLog ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    tempConfig.printModelCreationInfoToLog = !tempConfig.printModelCreationInfoToLog;
                    button.setMessage(Text.of(new TranslatableText("entity_model_features.config.log_models").getString() +
                            ": " + (tempConfig.printModelCreationInfoToLog ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                new TranslatableText("entity_model_features.config.log_models.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.5), (int) (this.width * 0.6), 20,
                Text.of(new TranslatableText("entity_model_features.config.log_math").getString() +
                        ": " + (tempConfig.printAllMaths ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    tempConfig.printAllMaths = !tempConfig.printAllMaths;
                    button.setMessage(Text.of(new TranslatableText("entity_model_features.config.log_math").getString() +
                            ": " + (tempConfig.printAllMaths ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                new TranslatableText("entity_model_features.config.log_math.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.6), (int) (this.width * 0.6), 20,
                Text.of(new TranslatableText("entity_model_features.config.vanilla_render").getString() +
                        ": " + (tempConfig.vanillaModelRenderMode.asText()).getString()),
                (button) -> {
                    tempConfig.vanillaModelRenderMode = tempConfig.vanillaModelRenderMode.next();
                    button.setMessage(Text.of(new TranslatableText("entity_model_features.config.vanilla_render").getString() +
                            ": " + (tempConfig.vanillaModelRenderMode.asText()).getString()));
                },
                new TranslatableText("entity_model_features.config.vanilla_render.tooltip")
        ));
    }




}
