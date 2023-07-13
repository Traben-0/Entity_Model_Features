package traben.entity_model_features.config;

import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.Objects;

public class EMFConfigScreenTools extends ETFConfigScreen {



    public EMFConfigScreenTools(EMFConfigScreenMain parent) {
        super(Text.translatable("entity_model_features.config.tools"),parent);
        emfParent = parent;
    }

    private final EMFConfigScreenMain emfParent;


    @Override
    protected void init() {
        super.init();



        this.addDrawableChild(getETFButton((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20,
                Text.translatable("dataPack.validation.reset"),
                (button) -> {
                    emfParent.tempConfig.renderCustomModelsGreen = false;
                    emfParent.tempConfig.printModelCreationInfoToLog = false;
                    emfParent.tempConfig.printAllMaths = false;
                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
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
                Text.of(Text.translatable("entity_model_features.config.vanilla_render").getString() +
                        ": " + (emfParent.tempConfig.vanillaModelRenderMode.asText()).getString()),
                (button) -> {
                    emfParent.tempConfig.vanillaModelRenderMode = emfParent.tempConfig.vanillaModelRenderMode.next();
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.vanilla_render").getString() +
                            ": " + (emfParent.tempConfig.vanillaModelRenderMode.asText()).getString()));
                },
                Text.translatable("entity_model_features.config.vanilla_render.tooltip")
        ));


        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.unknown_model_print_mode").getString() +
                        ": " + (emfParent.tempConfig.printUnknownModelsMode.asText()).getString()),
                (button) -> {
                    emfParent.tempConfig.printUnknownModelsMode = emfParent.tempConfig.printUnknownModelsMode.next();
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.unknown_model_print_mode").getString() +
                            ": " + (emfParent.tempConfig.printUnknownModelsMode.asText()).getString()));
                },
                Text.translatable("entity_model_features.config.unknown_model_print_mode.tooltip")
        ));

    }


}
