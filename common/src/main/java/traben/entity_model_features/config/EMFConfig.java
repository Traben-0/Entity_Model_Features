package traben.entity_model_features.config;

import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import traben.entity_features.config.EFConfig;
import traben.entity_features.config.gui.options.EFOptionBoolean;
import traben.entity_features.config.gui.options.EFOptionCategory;
import traben.entity_features.config.gui.options.EFOptionEnum;
import traben.entity_features.config.gui.options.EFOptionInt;
import traben.entity_texture_features.config.ETFConfig;

public class EMFConfig extends EFConfig {

    public boolean logModelCreationData = false;
    public boolean debugOnRightClick = false;
    public RenderModeChoice renderModeChoice = RenderModeChoice.NORMAL;
    public VanillaModelRenderMode vanillaModelHologramRenderMode_2 = VanillaModelRenderMode.OFF;
    public boolean attemptRevertingEntityModelsAlteredByAnotherMod = true;
    public ModelPrintMode modelExportMode = ModelPrintMode.NONE;
    public PhysicsModCompatChoice attemptPhysicsModPatch_2 = PhysicsModCompatChoice.CUSTOM;
    public ETFConfig.UpdateFrequency modelUpdateFrequency = ETFConfig.UpdateFrequency.Average;

    public boolean allowEBEModConfigModify = true;

    public int animationLODDistance = 20;

    public boolean retainDetailOnLowFps = true;

    @Override
    public EFOptionCategory getGUIOptions() {
        return new EFOptionCategory.Empty().add(
                new EFOptionCategory("config.entity_features.models_main").add(
                        new EFOptionCategory("entity_model_features.config.options", "entity_model_features.config.options.tooltip").add(
                                new EFOptionBoolean("entity_model_features.config.force_models", "entity_model_features.config.force_models.tooltip",
                                        () -> attemptRevertingEntityModelsAlteredByAnotherMod, value -> attemptRevertingEntityModelsAlteredByAnotherMod = value, true),
                                new EFOptionEnum<>("entity_model_features.config.physics", "entity_model_features.config.physics.tooltip",
                                        () -> attemptPhysicsModPatch_2, value -> attemptPhysicsModPatch_2 = value, PhysicsModCompatChoice.CUSTOM),
                                new EFOptionEnum<>("entity_model_features.config.update", "entity_model_features.config.update.tooltip",
                                        () -> modelUpdateFrequency, value -> modelUpdateFrequency = value, ETFConfig.UpdateFrequency.Average),
                                new EFOptionBoolean("entity_model_features.config.ebe_config_modify", "entity_model_features.config.ebe_config_modify.tooltip",
                                        () -> allowEBEModConfigModify, value -> allowEBEModConfigModify = value, true),
                                new EFOptionInt("entity_model_features.config.lod", "entity_model_features.config.lod.tooltip",
                                        () -> animationLODDistance, value -> animationLODDistance = value, 20, 0, 65, true, true),
                                new EFOptionBoolean("entity_model_features.config.low_fps_lod", "entity_model_features.config.low_fps_lod",
                                        () -> retainDetailOnLowFps, value -> retainDetailOnLowFps = value, true)
                        ),
                        new EFOptionCategory("entity_model_features.config.tools", "entity_model_features.config.tools.tooltip").add(
                                new EFOptionEnum<>("entity_model_features.config.vanilla_render", "entity_model_features.config.vanilla_render.tooltip",
                                        () -> vanillaModelHologramRenderMode_2, value -> vanillaModelHologramRenderMode_2 = value, VanillaModelRenderMode.OFF),
                                new EFOptionEnum<>("entity_model_features.config.print_mode", "entity_model_features.config.print_mode.tooltip",
                                        () -> modelExportMode, value -> modelExportMode = value, ModelPrintMode.NONE)
                        ),
                        new EFOptionCategory("entity_model_features.config.debug", "entity_model_features.config.debug.tooltip").add(
                                new EFOptionEnum<>("entity_model_features.config.render", "entity_model_features.config.render.tooltip",
                                        () -> renderModeChoice, value -> renderModeChoice = value, RenderModeChoice.NORMAL),
                                new EFOptionBoolean("entity_model_features.config.log_models", "entity_model_features.config.log_models.tooltip",
                                        () -> logModelCreationData, value -> logModelCreationData = value, false),
                                new EFOptionBoolean("entity_model_features.config.debug_right_click", "entity_model_features.config.debug_right_click.tooltip",
                                        () -> debugOnRightClick, value -> debugOnRightClick = value, false)
                        )
                )//, new EFOptionCategory("config.entity_features.general_settings.title")
        );
    }

    @Override
    public Identifier getModIcon() {
        return new Identifier("entity_model_features", "textures/gui/icon.png");
    }


    public enum ModelPrintMode {
        NONE(ScreenTexts.OFF),
        @SuppressWarnings("unused")
        LOG_ONLY(Text.translatable("entity_model_features.config.print_mode.log")),
        LOG_AND_JEM(Text.translatable("entity_model_features.config.print_mode.log_jem")),
        @SuppressWarnings("unused")
        ALL_LOG_ONLY(Text.translatable("entity_model_features.config.print_mode.all_log")),
        ALL_LOG_AND_JEM(Text.translatable("entity_model_features.config.print_mode.all_log_jem"));

        private final Text text;

        ModelPrintMode(Text text) {
            this.text = text;
        }

        public boolean doesJems() {
            return this == LOG_AND_JEM || this == ALL_LOG_AND_JEM;
        }

        public boolean doesAll() {
            return this == ALL_LOG_ONLY || this == ALL_LOG_AND_JEM;
        }

        public boolean doesLog() {
            return this != NONE;
        }

        @Override
        public String toString() {
            return text.getString();
        }
    }

    public enum VanillaModelRenderMode {
        OFF(ScreenTexts.OFF),
        @SuppressWarnings("unused")
        NORMAL(Text.translatable("entity_model_features.config.vanilla_render.normal")),
        OFFSET(Text.translatable("entity_model_features.config.vanilla_render.offset"));

        private final Text text;

        VanillaModelRenderMode(Text text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text.getString();
        }
    }

    public enum PhysicsModCompatChoice {
        OFF(ScreenTexts.OFF),
        VANILLA(Text.translatable("entity_model_features.config.physics.1")),
        CUSTOM(Text.translatable("entity_model_features.config.physics.2"));

        private final Text text;

        PhysicsModCompatChoice(Text text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text.getString();
        }
    }

    public enum RenderModeChoice {
        NORMAL(Text.translatable("entity_model_features.config.render.normal")),
        GREEN(Text.translatable("entity_model_features.config.render.green")),
        LINES_AND_TEXTURE(Text.translatable("entity_model_features.config.render.lines_texture")),
        LINES_AND_TEXTURE_FLASH(Text.translatable("entity_model_features.config.render.lines_texture_flash")),
        LINES(Text.translatable("entity_model_features.config.render.lines")),
        NONE(Text.translatable("entity_model_features.config.render.none"));

        private final String text;


        RenderModeChoice(Text text) {
            this.text = text.getString();
        }

        @Override
        public String toString() {
            return text;
        }
    }

}
