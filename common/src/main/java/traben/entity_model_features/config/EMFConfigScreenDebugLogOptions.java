package traben.entity_model_features.config;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.Objects;

public class EMFConfigScreenDebugLogOptions extends ETFConfigScreen {


    private final EMFConfigScreenMain emfParent;

    public EMFConfigScreenDebugLogOptions(EMFConfigScreenMain parent) {
        super(Text.translatable("entity_model_features.config.debug"), parent);
        emfParent = parent;
    }

    @Override
    protected void init() {
        super.init();


        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("dataPack.validation.reset"),
                (button) -> {
                    emfParent.tempConfig.renderModeChoice = EMFConfig.RenderModeChoice.NORMAL;
                    emfParent.tempConfig.logModelCreationData = false;
                    emfParent.tempConfig.logMathInRuntime = false;
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
                Text.of(Text.translatable("entity_model_features.config.render").getString() +
                        ": " + emfParent.tempConfig.renderModeChoice.asText().getString()),
                (button) -> {
                    emfParent.tempConfig.renderModeChoice = emfParent.tempConfig.renderModeChoice.next();
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.render").getString() +
                            ": " + emfParent.tempConfig.renderModeChoice.asText().getString()));
                },
                Text.translatable("entity_model_features.config.render.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.log_models").getString() +
                        ": " + (emfParent.tempConfig.logModelCreationData ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.logModelCreationData = !emfParent.tempConfig.logModelCreationData;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.log_models").getString() +
                            ": " + (emfParent.tempConfig.logModelCreationData ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.log_models.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.4), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.log_math").getString() +
                        ": " + (emfParent.tempConfig.logMathInRuntime ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.logMathInRuntime = !emfParent.tempConfig.logMathInRuntime;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.log_math").getString() +
                            ": " + (emfParent.tempConfig.logMathInRuntime ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.log_math.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.5), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("math mode rc3 test").getString() +
                        ": " + emfParent.tempConfig.mathFunctionChoice),
                (button) -> {
                    emfParent.tempConfig.mathFunctionChoice = emfParent.tempConfig.mathFunctionChoice.next();
                    button.setMessage(Text.of(Text.translatable("math mode rc3 test").getString() +
                            ": " + emfParent.tempConfig.mathFunctionChoice));
                },
                Text.translatable("tooltip")
        ));

    }


}
