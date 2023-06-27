package traben.entity_model_features.config;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.Objects;

public class EMFConfigDebugLogOptionsScreen extends ETFConfigScreen {



    public EMFConfigDebugLogOptionsScreen(EMFConfigMainScreen parent) {
        super(Text.translatable("entity_model_features.debug"),parent);
        emfParent = parent;
    }

    private final EMFConfigMainScreen emfParent;


    @Override
    protected void init() {
        super.init();





        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("dataPack.validation.reset"),
                (button) -> {
                    emfParent.tempConfig.renderCustomModelsGreen = false;
                    emfParent.tempConfig.printModelCreationInfoToLog = false;
                    emfParent.tempConfig.printAllMaths = false;
                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20).build());
        this.addDrawableChild(ButtonWidget.builder(
                ScreenTexts.BACK,
                (button) -> {
                    //tempConfig = null;
                    Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());


        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.green_render").getString() +
                        ": " + (emfParent.tempConfig.renderCustomModelsGreen ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.renderCustomModelsGreen = !emfParent.tempConfig.renderCustomModelsGreen;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.green_render").getString() +
                            ": " + (emfParent.tempConfig.renderCustomModelsGreen ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.green_render.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.log_models").getString() +
                        ": " + (emfParent.tempConfig.printModelCreationInfoToLog ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.printModelCreationInfoToLog = !emfParent.tempConfig.printModelCreationInfoToLog;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.log_models").getString() +
                            ": " + (emfParent.tempConfig.printModelCreationInfoToLog ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.log_models.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.4), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.log_math").getString() +
                        ": " + (emfParent.tempConfig.printAllMaths ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.printAllMaths = !emfParent.tempConfig.printAllMaths;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.log_math").getString() +
                            ": " + (emfParent.tempConfig.printAllMaths ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.log_math.tooltip")
        ));




    }


}
