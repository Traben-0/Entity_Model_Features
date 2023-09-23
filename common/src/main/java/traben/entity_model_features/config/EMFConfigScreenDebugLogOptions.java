package traben.entity_model_features.config;


import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.Objects;

public class EMFConfigScreenDebugLogOptions extends ETFConfigScreen {


    private final EMFConfigScreenMain emfParent;

    public EMFConfigScreenDebugLogOptions(EMFConfigScreenMain parent) {
        super(new TranslatableText("entity_model_features.config.debug"), parent);
        emfParent = parent;
    }

    @Override
    protected void init() {
        super.init();




        this.addDrawableChild(getETFButton((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20,
                new TranslatableText("dataPack.validation.reset"),
                (button) -> {
                    emfParent.tempConfig.renderModeChoice = EMFConfig.RenderModeChoice.NORMAL;
                    emfParent.tempConfig.logModelCreationData = false;
                    //this.();
                    Objects.requireNonNull(client).setScreen(new EMFConfigScreenDebugLogOptions(emfParent));
                }
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.BACK,
                (button) -> {
                    //tempConfig = null;
                    Objects.requireNonNull(client).setScreen(parent);
                }
        ));


        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
                Text.of(new TranslatableText("entity_model_features.config.render").getString() +
                        ": " + emfParent.tempConfig.renderModeChoice.asText().getString()),
                (button) -> {
                    emfParent.tempConfig.renderModeChoice = emfParent.tempConfig.renderModeChoice.next();
                    button.setMessage(Text.of(new TranslatableText("entity_model_features.config.render").getString() +
                            ": " + emfParent.tempConfig.renderModeChoice.asText().getString()));
                },
                new TranslatableText("entity_model_features.config.render.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(new TranslatableText("entity_model_features.config.log_models").getString() +
                        ": " + (emfParent.tempConfig.logModelCreationData ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.logModelCreationData = !emfParent.tempConfig.logModelCreationData;
                    button.setMessage(Text.of(new TranslatableText("entity_model_features.config.log_models").getString() +
                            ": " + (emfParent.tempConfig.logModelCreationData ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                new TranslatableText("entity_model_features.config.log_models.tooltip")
        ));


    }


}
