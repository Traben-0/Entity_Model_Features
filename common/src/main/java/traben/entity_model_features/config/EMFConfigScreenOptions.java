package traben.entity_model_features.config;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EMFConfigScreenOptions extends ETFConfigScreen {


    private final EMFConfigScreenMain emfParent;

    public EMFConfigScreenOptions(EMFConfigScreenMain parent) {
        super(Text.translatable("entity_model_features.config.options"), parent);
        emfParent = parent;
    }

    @Override
    protected void init() {
        super.init();




        this.addDrawableChild(getETFButton((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20,
                Text.translatable("dataPack.validation.reset"),
                (button) -> {
                    emfParent.tempConfig.attemptRevertingEntityModelsAlteredByAnotherMod = true;
                    emfParent.tempConfig.attemptPhysicsModPatch_2 = EMFConfig.PhysicsModCompatChoice.CUSTOM;
                    emfParent.tempConfig.modelUpdateFrequency = ETFConfig.UpdateFrequency.Average;
                    emfParent.tempConfig.allowEBEModConfigModify = true;
                    emfParent.tempConfig.animationLODDistance = 20;
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
                Text.of(Text.translatable("entity_model_features.config.force_models").getString() +
                        ": " + (emfParent.tempConfig.attemptRevertingEntityModelsAlteredByAnotherMod ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.attemptRevertingEntityModelsAlteredByAnotherMod = !emfParent.tempConfig.attemptRevertingEntityModelsAlteredByAnotherMod;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.force_models").getString() +
                            ": " + (emfParent.tempConfig.attemptRevertingEntityModelsAlteredByAnotherMod ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.force_models.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.physics").getString() +
                        ": " + (emfParent.tempConfig.attemptPhysicsModPatch_2.asText()).getString()),
                (button) -> {
                    emfParent.tempConfig.attemptPhysicsModPatch_2 = emfParent.tempConfig.attemptPhysicsModPatch_2.next();
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.physics").getString() +
                            ": " + (emfParent.tempConfig.attemptPhysicsModPatch_2.asText()).getString()));
                },
                Text.translatable("entity_model_features.config.physics.tooltip")
        ));


        this.addDrawableChild(new EnumSliderWidget<>((int) (this.width * 0.2), (int) (this.height * 0.4), (int) (this.width * 0.6), 20,
                Text.translatable("entity_model_features.config.update"),
                ETFConfig.UpdateFrequency.Average,
                (value) -> emfParent.tempConfig.modelUpdateFrequency = value,
                Text.translatable("entity_model_features.config.update.tooltip"),this
        ));


        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.5), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.ebe_config_modify").getString() +
                        ": " + (emfParent.tempConfig.allowEBEModConfigModify ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.allowEBEModConfigModify = !emfParent.tempConfig.allowEBEModConfigModify;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.ebe_config_modify").getString() +
                            ": " + (emfParent.tempConfig.allowEBEModConfigModify ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.ebe_config_modify.tooltip")
        ));

        this.addDrawableChild(getLodSlider());

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.7), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.low_fps_lod").getString() +
                        ": " + (emfParent.tempConfig.retainDetailOnLowFps ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.retainDetailOnLowFps = !emfParent.tempConfig.retainDetailOnLowFps;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.low_fps_lod").getString() +
                            ": " + (emfParent.tempConfig.retainDetailOnLowFps ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.low_fps_lod.tooltip")
        ));
    }

    @NotNull
    private SliderWidget getLodSlider() {
        var toolTipText = Text.translatable("entity_model_features.config.lod.tooltip");
        boolean tooltipIsEmpty = toolTipText.getString().isBlank();
        String[] strings = toolTipText.getString().split("\n");
        List<Text> lines = new ArrayList();
        String[] var12 = strings;
        int var13 = strings.length;

        for(int var14 = 0; var14 < var13; ++var14) {
            String str = var12[var14];
            lines.add(Text.of(str.strip()));
        }

        TriConsumer<MatrixStack,Integer,Integer> tooltip = tooltipIsEmpty ?
                (a,b,c)->{} :
                (matrices, mouseX, mouseY) -> renderTooltip(matrices, lines, Optional.empty(), mouseX, mouseY);
        var lodSlider = new SliderWidget((int) (this.width * 0.2), (int) (this.height * 0.6), (int) (this.width * 0.6), 20,
                Text.translatable("entity_model_features.config.lod"), emfParent.tempConfig.animationLODDistance / 65d
        ) {
            private static final String title = Text.translatable("entity_model_features.config.lod").getString();

            @Override
            protected void updateMessage() {
                int val = getIntWrappedValue();
                setMessage(Text.of(title + ": " + (val == 0 ? ScreenTexts.OFF.getString() : val)));
            }

            private int getIntWrappedValue() {
                //allow the start and end of the slider to both mean none
                int val = (int) (value * 65);
                value = val / 65d;
                return val > 64 ? 0 : val;
            }

            @Override
            protected void applyValue() {

                emfParent.tempConfig.animationLODDistance = getIntWrappedValue();
            }

            @Override
            public void renderTooltip(final MatrixStack matrices, final int mouseX, final int mouseY) {
                if (isHovered()) tooltip.accept(matrices, mouseX, mouseY);
            }
        };
        lodSlider.updateMessage();
        return lodSlider;
    }


}
