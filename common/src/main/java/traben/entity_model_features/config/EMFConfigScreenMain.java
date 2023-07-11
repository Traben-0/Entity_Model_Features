package traben.entity_model_features.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.Objects;

public class EMFConfigScreenMain extends ETFConfigScreen {



    public EMFConfigScreenMain(Screen parent) {
        super(Text.translatable("entity_model_features.title"),parent);
       // this.parent = parent;
        tempConfig = EMFConfig.copyFrom(EMFConfig.getConfig());
    }


    public EMFConfig tempConfig = null;

    @Override
    protected void init() {
        super.init();



        this.addDrawableChild(getETFButton((int) (this.width * 0.7), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                Text.translatable("gui.done"),
                (button) -> {
                    EMFConfig.setConfig(tempConfig);
                    EMFConfig.EMF_saveConfig();
//                    if(EMFConfig.getConfig().reloadMode == EMFConfig.ModelDataRefreshMode.MANUAL) {
//                        EMFManager.resetInstance();
//                    }
                    MinecraftClient.getInstance().reloadResources();
                    Objects.requireNonNull(client).setScreen(parent);
                }
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20,
                Text.translatable("dataPack.validation.reset"),
                (button) -> {
                    tempConfig = new EMFConfig();
                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.CANCEL,
                (button) -> {
                    tempConfig = null;
                    Objects.requireNonNull(client).setScreen(parent);
                }
                ));




        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
                Text.translatable("entity_model_features.config.options"),
                (button) -> {
                    Objects.requireNonNull(client).setScreen(new EMFConfigScreenOptions(this));
                },
                Text.translatable("entity_model_features.config.options.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.translatable("entity_model_features.config.tools"),
                (button) -> {
                    Objects.requireNonNull(client).setScreen(new EMFConfigScreenTools(this));
                },
                Text.translatable("entity_model_features.config.tools.tooltip")
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.4), (int) (this.width * 0.6), 20,
                Text.translatable("entity_model_features.config.debug"),
                (button) -> {
                    Objects.requireNonNull(client).setScreen(new EMFConfigScreenDebugLogOptions(this));
                },
                Text.translatable("entity_model_features.config.debug.tooltip")
        ));

    }


}
