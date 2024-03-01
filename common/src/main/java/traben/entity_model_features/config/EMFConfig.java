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
    public VanillaModelRenderMode vanillaModelHologramRenderMode = VanillaModelRenderMode.Off;
    public boolean attemptRevertingEntityModelsAlteredByAnotherMod = true;
    public UnknownModelPrintMode logUnknownOrModdedEntityModels = UnknownModelPrintMode.NONE;
    public PhysicsModCompatChoice attemptPhysicsModPatch_2 = PhysicsModCompatChoice.CUSTOM;
    public ETFConfig.UpdateFrequency modelUpdateFrequency = ETFConfig.UpdateFrequency.Average;

    public boolean allowEBEModConfigModify = true;

    public LodScale lodScale = LodScale.LOD16;

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
        File config = new File(EMFVersionDifferenceManager.getConfigDirectory().toFile(), MOD_ID+".json");
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
            File config = new File(EMFVersionDifferenceManager.getConfigDirectory().toFile(), MOD_ID+".json");
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
                && vanillaModelHologramRenderMode == emfConfig.vanillaModelHologramRenderMode
                && logUnknownOrModdedEntityModels == emfConfig.logUnknownOrModdedEntityModels
                && attemptPhysicsModPatch_2 == emfConfig.attemptPhysicsModPatch_2
                && lodScale == emfConfig.lodScale;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lodScale,allowEBEModConfigModify, debugOnRightClick, modelUpdateFrequency, logModelCreationData,
                renderModeChoice, vanillaModelHologramRenderMode, attemptRevertingEntityModelsAlteredByAnotherMod,
                logUnknownOrModdedEntityModels, attemptPhysicsModPatch_2);
    }


    public enum UnknownModelPrintMode {
        NONE(ScreenTexts.OFF),
        LOG_ONLY(Text.translatable("entity_model_features.config.unknown_model_print_mode.log")),
        LOG_AND_JEM(Text.translatable("entity_model_features.config.unknown_model_print_mode.log_jem"));

        private final Text text;

        UnknownModelPrintMode(Text text) {
            this.text = text;
        }

        public Text asText() {
            return text;
        }

        public UnknownModelPrintMode next() {
            return switch (this) {
                case NONE -> LOG_ONLY;
                case LOG_ONLY -> LOG_AND_JEM;
                default -> NONE;
            };
        }
    }

    public enum VanillaModelRenderMode {
        Off(ScreenTexts.OFF),
        Position_normal(Text.translatable("entity_model_features.config.vanilla_render.normal")),
        Positon_offset(Text.translatable("entity_model_features.config.vanilla_render.offset"));

        private final Text text;

        VanillaModelRenderMode(Text text) {
            this.text = text;
        }

        public Text asText() {
            return text;
        }

        public VanillaModelRenderMode next() {
            return switch (this) {
                case Off -> Position_normal;
                case Position_normal -> Positon_offset;
                default -> Off;
            };
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
        LINES(Text.translatable("entity_model_features.config.render.lines")),
        LINES_AND_TEXTURE(Text.translatable("entity_model_features.config.render.lines_texture")),
        LINES_AND_TEXTURE_FLASH(Text.translatable("entity_model_features.config.render.lines_texture_flash")),
        NONE(Text.translatable("entity_model_features.config.render.none"));
        //TRANSPARENT(Text.translatable("entity_model_features.config.render.transparent"));

        private final Text text;

        RenderModeChoice(Text text) {
            this.text = text;
        }

        public Text asText() {
            return text;
        }

        public RenderModeChoice next() {
            return switch (this) {
                case NORMAL -> GREEN;
                case GREEN -> LINES_AND_TEXTURE_FLASH;
                case LINES_AND_TEXTURE_FLASH -> LINES_AND_TEXTURE;
                case LINES_AND_TEXTURE -> LINES;
                case LINES -> NONE;
                default -> NORMAL;
            };
        }
    }

    public enum LodScale {
        LOD64(64),
        LOD48(48),
        LOD32(32),
        LOD28(28),
        LOD24(24),
        LOD20(20),
        LOD16(16),
        LOD12(12),
        LOD10(10),
        LOD8(8),
        LOD4(4),
        LOD3(3),
        LOD2(2),
        LOD1(1),

        NONE(Text.translatable("entity_model_features.config.lod.none"),0);

        private final String text;
        private final int num;

        LodScale(Text text, int num) {
            this.text = text.getString();
            this.num = num;
        }

        LodScale( int num) {
            this.text = String.valueOf(num);
            this.num = num;
        }

        @Override
        public String toString() {
            return text;
        }

        public int getNum() {
            return num;
        }

        public LodScale next() {
            var all = values();
            for (int i = 0; i < all.length; i++) {
                if (all[i] == this) {
                    return all[(i + 1) % all.length];
                }
            }
            return NONE;
        }
    }

}
