package traben.entity_model_features.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EMFConfigMainScreen extends ETFConfigScreen {

    //TODO should be in main config class but forgot to make it public... do that
    final Screen parent;

    public EMFConfigMainScreen(Screen parent) {
        super(Text.translatable("entity_model_features.title"),parent);
        this.parent = parent;
        tempConfig = EMFConfig.copyFrom(EMFConfig.getConfig());
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
                    tempConfig = new EMFConfig();
                    this.clearAndInit();
                }));
        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.CANCEL,
                (button) -> {
                    tempConfig = null;
                    Objects.requireNonNull(client).setScreen(parent);
                }));



        this.addDrawableChild(getEMFButton((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.substitute_vanilla").getString() +
                        ": " + (tempConfig.attemptToCopyVanillaModelIntoMissingModelPart ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    tempConfig.attemptToCopyVanillaModelIntoMissingModelPart = !tempConfig.attemptToCopyVanillaModelIntoMissingModelPart;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.substitute_vanilla").getString() +
                            ": " + (tempConfig.attemptToCopyVanillaModelIntoMissingModelPart ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.substitute_vanilla.tooltip")
        ));

        this.addDrawableChild(getEMFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.green_render").getString() +
                        ": " + (tempConfig.renderCustomModelsGreen ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    tempConfig.renderCustomModelsGreen = !tempConfig.renderCustomModelsGreen;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.green_render").getString() +
                            ": " + (tempConfig.renderCustomModelsGreen ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.green_render.tooltip")
        ));

        this.addDrawableChild(getEMFButton((int) (this.width * 0.2), (int) (this.height * 0.4), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.log_models").getString() +
                        ": " + (tempConfig.printModelCreationInfoToLog ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    tempConfig.printModelCreationInfoToLog = !tempConfig.printModelCreationInfoToLog;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.log_models").getString() +
                            ": " + (tempConfig.printModelCreationInfoToLog ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.log_models.tooltip")
        ));

        this.addDrawableChild(getEMFButton((int) (this.width * 0.2), (int) (this.height * 0.5), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.log_math").getString() +
                        ": " + (tempConfig.printAllMaths ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    tempConfig.printAllMaths = !tempConfig.printAllMaths;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.log_math").getString() +
                            ": " + (tempConfig.printAllMaths ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.log_math.tooltip")
        ));

        this.addDrawableChild(getEMFButton((int) (this.width * 0.2), (int) (this.height * 0.6), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.vanilla_render").getString() +
                        ": " + (tempConfig.vanillaModelRenderMode.asText()).getString()),
                (button) -> {
                    tempConfig.vanillaModelRenderMode = tempConfig.vanillaModelRenderMode.next();
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.vanilla_render").getString() +
                            ": " + (tempConfig.vanillaModelRenderMode.asText()).getString()));
                },
                Text.translatable("entity_model_features.config.vanilla_render.tooltip")
        ));
    }



    //TODO copy of etf button cause i forgot to make it public... do that
    ButtonWidget getEMFButton(int x, int y, int width, int height, Text buttonText, ButtonWidget.PressAction onPress, Text toolTipText) {
        int nudgeLeftEdge;
        if (width > 384) {
            nudgeLeftEdge = (width - 384) / 2;
            width = 384;
        } else {
            nudgeLeftEdge = 0;
        }
//        if (width > 800)
//            height=80;
//        if (width > 1600)
//            height=16;
        boolean tooltipIsEmpty = toolTipText.getString().isBlank();
        String[] strings = toolTipText.getString().split("\n");
        List<Text> lines = new ArrayList<>();
        for (String str :
                strings) {
            lines.add(Text.of(str.strip()));
        }

        return new ButtonWidget(x + nudgeLeftEdge, y, width, height,
                buttonText,
                onPress,
                (buttonWidget, matrices, mouseX, mouseY) -> {
                    if (buttonWidget.isHovered() && !tooltipIsEmpty) {
                        this.renderTooltip(matrices, lines, mouseX, mouseY);
                    }
                });
    }
}
