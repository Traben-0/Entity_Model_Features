package traben.entity_model_features.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.awt.*;
import java.util.Objects;

public class EMFConfigScreenMain extends ETFConfigScreen {



    public EMFConfig tempConfig;

    public EMFConfigScreenMain(Screen parent) {
        super(new TranslatableText("entity_model_features.title"), parent);
        // this.parent = parent;
        tempConfig = EMFConfig.copyFrom(EMFConfig.getConfig());
    }

    @Override
    protected void init() {
        super.init();



        this.addDrawableChild(getETFButton((int) (this.width * 0.7), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                new TranslatableText("gui.done"),
                (button) -> {
                    if(!tempConfig.equals(EMFConfig.getConfig())) {
                        EMFConfig.setConfig(tempConfig);
                        EMFConfig.EMF_saveConfig();
                        MinecraftClient.getInstance().reloadResources();
                    }
                    Objects.requireNonNull(client).setScreen(parent);
                }
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20,
                new TranslatableText("dataPack.validation.reset"),
                (button) -> {
                    tempConfig = new EMFConfig();
                    //this.clearAndInit();
                    Objects.requireNonNull(client).setScreen(new EMFConfigScreenMain(parent));
                }
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.CANCEL,
                (button) -> {
                    tempConfig = null;
                    Objects.requireNonNull(client).setScreen(parent);
                }
                ));



        this.addDrawableChild(getETFButton((int) (this.width * 0.6), (int) (this.height * 0.2), (int) (this.width * 0.3), 20,
                new TranslatableText("entity_model_features.config.options"),
                (button) -> Objects.requireNonNull(client).setScreen(new EMFConfigScreenOptions(this)),
                new TranslatableText("entity_model_features.config.options.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.6), (int) (this.height * 0.3), (int) (this.width * 0.3), 20,
                new TranslatableText("entity_model_features.config.tools"),
                (button) -> Objects.requireNonNull(client).setScreen(new EMFConfigScreenTools(this)),
                new TranslatableText("entity_model_features.config.tools.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.6), (int) (this.height * 0.4), (int) (this.width * 0.3), 20,
                new TranslatableText("entity_model_features.config.debug"),
                (button) -> Objects.requireNonNull(client).setScreen(new EMFConfigScreenDebugLogOptions(this)),
                new TranslatableText("entity_model_features.config.debug.tooltip")
        ));

    }



    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        drawCenteredTextWithShadow(matrices,this.textRenderer, Text.of("In 1.19.4+ entities will appear here.").asOrderedText(), this.width / 3, this.height / 2, Color.GRAY.getRGB());

    }
}
