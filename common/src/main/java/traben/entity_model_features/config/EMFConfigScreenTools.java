package traben.entity_model_features.config;


import net.minecraft.client.gui.screen.ScreenTexts;
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
                    emfParent.tempConfig.vanillaModelHologramRenderMode_2 = EMFConfig.VanillaModelRenderMode.OFF;
                    emfParent.tempConfig.modelExportMode = EMFConfig.ModelPrintMode.NONE;

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






        this.addDrawableChild(new EnumSliderWidget<>((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
                new TranslatableText("entity_model_features.config.vanilla_render"),
                EMFConfig.VanillaModelRenderMode.OFF,
                (value) -> emfParent.tempConfig.vanillaModelHologramRenderMode_2 = value,
                new TranslatableText("entity_model_features.config.vanilla_render.tooltip"),this
        ));

        this.addDrawableChild(new EnumSliderWidget<>((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                new TranslatableText("entity_model_features.config.print_mode"),
                EMFConfig.ModelPrintMode.NONE,
                (value) -> emfParent.tempConfig.modelExportMode = value,
                new TranslatableText("entity_model_features.config.print_mode.tooltip"),this
        ));

    }


}
