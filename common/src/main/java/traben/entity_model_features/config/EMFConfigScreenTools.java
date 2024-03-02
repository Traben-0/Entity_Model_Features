package traben.entity_model_features.config;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.Objects;

public class EMFConfigScreenTools extends ETFConfigScreen {


    private final EMFConfigScreenMain emfParent;

    public EMFConfigScreenTools(EMFConfigScreenMain parent) {
        super(Text.translatable("entity_model_features.config.tools"), parent);
        emfParent = parent;
    }

    @Override
    protected void init() {
        super.init();


        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("dataPack.validation.reset"),
                (button) -> {
                    emfParent.tempConfig.vanillaModelHologramRenderMode_2 = EMFConfig.VanillaModelRenderMode.OFF;
                    emfParent.tempConfig.modelExportMode = EMFConfig.ModelPrintMode.NONE;
                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20).build());
        this.addDrawableChild(ButtonWidget.builder(
                ScreenTexts.BACK,
                (button) -> {
                    //tempConfig = null;
                    Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());

        this.addDrawableChild(new EnumSliderWidget<>((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
                Text.translatable("entity_model_features.config.vanilla_render"),
                EMFConfig.VanillaModelRenderMode.OFF,
                (value) -> emfParent.tempConfig.vanillaModelHologramRenderMode_2 = value,
                Text.translatable("entity_model_features.config.vanilla_render.tooltip")
        ));

        this.addDrawableChild(new EnumSliderWidget<>((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.translatable("entity_model_features.config.print_mode"),
                EMFConfig.ModelPrintMode.NONE,
                (value) -> emfParent.tempConfig.modelExportMode = value,
                Text.translatable("entity_model_features.config.print_mode.tooltip")
        ));


    }


}
