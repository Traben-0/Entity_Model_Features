package traben.entity_model_features.config;


import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.Objects;

public class EMFConfigScreenTools extends ETFConfigScreen {


    private final EMFConfigScreenMain emfParent;

    public EMFConfigScreenTools(EMFConfigScreenMain parent) {
        super(new TranslatableText("entity_model_features.config.tools"), parent);
        emfParent = parent;
    }

    @Override
    protected void init() {
        super.init();



        this.addDrawableChild(getETFButton((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20,
                new TranslatableText("dataPack.validation.reset"),
                (button) -> {
                    emfParent.tempConfig.vanillaModelHologramRenderMode = EMFConfig.VanillaModelRenderMode.Off;
                    emfParent.tempConfig.logUnknownOrModdedEntityModels = EMFConfig.UnknownModelPrintMode.NONE;

                    //this.clearAndInit();
                    Objects.requireNonNull(client).setScreen(new EMFConfigScreenTools(emfParent));
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
                Text.of(new TranslatableText("entity_model_features.config.vanilla_render").getString() +
                        ": " + (emfParent.tempConfig.vanillaModelHologramRenderMode.asText()).getString()),
                (button) -> {
                    emfParent.tempConfig.vanillaModelHologramRenderMode = emfParent.tempConfig.vanillaModelHologramRenderMode.next();
                    button.setMessage(Text.of(new TranslatableText("entity_model_features.config.vanilla_render").getString() +
                            ": " + (emfParent.tempConfig.vanillaModelHologramRenderMode.asText()).getString()));
                },
                new TranslatableText("entity_model_features.config.vanilla_render.tooltip")
        ));


        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(new TranslatableText("entity_model_features.config.unknown_model_print_mode").getString() +
                        ": " + (emfParent.tempConfig.logUnknownOrModdedEntityModels.asText()).getString()),
                (button) -> {
                    emfParent.tempConfig.logUnknownOrModdedEntityModels = emfParent.tempConfig.logUnknownOrModdedEntityModels.next();
                    button.setMessage(Text.of(new TranslatableText("entity_model_features.config.unknown_model_print_mode").getString() +
                            ": " + (emfParent.tempConfig.logUnknownOrModdedEntityModels.asText()).getString()));
                },
                new TranslatableText("entity_model_features.config.unknown_model_print_mode.tooltip")
        ));

    }


}
