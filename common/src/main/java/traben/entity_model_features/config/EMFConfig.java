package traben.entity_model_features.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_model_features.EMFVersionDifferenceManager;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.config.ETFConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static traben.entity_model_features.EMFClient.MOD_ID;

public class EMFConfig {

    private static EMFConfig INSTANCE;
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

    public static EMFConfig getConfig() {
        if (INSTANCE == null) {
            loadConfig();
        }
        return INSTANCE;
    }

    public static void setConfig(EMFConfig newConfig) {
        if (newConfig != null)
            INSTANCE = newConfig;
    }

    public static void EMF_saveConfig() {
        File config = new File(EMFVersionDifferenceManager.getConfigDirectory().toFile(), MOD_ID + ".json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!config.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(gson.toJson(INSTANCE));
            fileWriter.close();
        } catch (IOException e) {
            EMFUtils.log("Config could not be saved", false);
        }
    }

    public static void loadConfig() {
        try {
            File config = new File(EMFVersionDifferenceManager.getConfigDirectory().toFile(), MOD_ID + ".json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (config.exists()) {
                try {
                    FileReader fileReader = new FileReader(config);
                    INSTANCE = gson.fromJson(fileReader, EMFConfig.class);
                    fileReader.close();
                    EMF_saveConfig();
                } catch (IOException e) {
                    EMFUtils.log("Config could not be loaded, using defaults", false);
                }
            } else {
                INSTANCE = new EMFConfig();
                EMF_saveConfig();
            }
            if (INSTANCE == null) {
                INSTANCE = new EMFConfig();
                EMF_saveConfig();
            }
        } catch (Exception e) {
            INSTANCE = new EMFConfig();
        }

    }

    public static EMFConfig copyFrom(EMFConfig source) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.fromJson(gson.toJson(source), EMFConfig.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EMFConfig emfConfig = (EMFConfig) o;
        return allowEBEModConfigModify == emfConfig.allowEBEModConfigModify
                && debugOnRightClick == emfConfig.debugOnRightClick
                && modelUpdateFrequency == emfConfig.modelUpdateFrequency
                && logModelCreationData == emfConfig.logModelCreationData
                && attemptRevertingEntityModelsAlteredByAnotherMod == emfConfig.attemptRevertingEntityModelsAlteredByAnotherMod
                && renderModeChoice == emfConfig.renderModeChoice
                && vanillaModelHologramRenderMode_2 == emfConfig.vanillaModelHologramRenderMode_2
                && modelExportMode == emfConfig.modelExportMode
                && attemptPhysicsModPatch_2 == emfConfig.attemptPhysicsModPatch_2
                && animationLODDistance == emfConfig.animationLODDistance
                && retainDetailOnLowFps == emfConfig.retainDetailOnLowFps;
    }

    @Override
    public int hashCode() {
        return Objects.hash(retainDetailOnLowFps, animationLODDistance, allowEBEModConfigModify, debugOnRightClick, modelUpdateFrequency, logModelCreationData,
                renderModeChoice, vanillaModelHologramRenderMode_2, attemptRevertingEntityModelsAlteredByAnotherMod,
                modelExportMode, attemptPhysicsModPatch_2);
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

        public Text asText() {
            return text;
        }

        public PhysicsModCompatChoice next() {
            return switch (this) {
                case OFF -> CUSTOM;
                case CUSTOM -> VANILLA;
                default -> OFF;
            };
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
