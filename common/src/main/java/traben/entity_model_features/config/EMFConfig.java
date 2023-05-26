package traben.entity_model_features.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import traben.entity_model_features.EMFVersionDifferenceManager;
import traben.entity_model_features.utils.EMFUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EMFConfig {

    private static EMFConfig EMFConfigData;
    public boolean printModelCreationInfoToLog = false;
    public boolean printAllMaths = false;

    public boolean renderCustomModelsGreen = false;

    public VanillaModelRenderMode vanillaModelRenderMode =  VanillaModelRenderMode.Off;
    public final MathFunctionChoice mathFunctionChoice = MathFunctionChoice.JavaMath;


    public boolean attemptToCopyVanillaModelIntoMissingModelPart = false;
    public static EMFConfig getConfig() {
        if (EMFConfigData == null) {
            loadConfig();
        }
        return EMFConfigData;
    }

    public static void setConfig(EMFConfig newConfig) {
        if(newConfig != null)
            EMFConfigData = newConfig;
    }


    public static void EMF_saveConfig() {
        File config = new File(EMFVersionDifferenceManager.getConfigDirectory().toFile(), "entity_model_features.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!config.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(gson.toJson(EMFConfigData));
            fileWriter.close();
        } catch (IOException e) {
            EMFUtils.EMFModMessage("Config could not be saved", false);
        }
    }


    public static void loadConfig() {
        try {
            File config = new File(EMFVersionDifferenceManager.getConfigDirectory().toFile(), "entity_model_features.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (config.exists()) {
                try {
                    FileReader fileReader = new FileReader(config);
                    EMFConfigData = gson.fromJson(fileReader, EMFConfig.class);
                    fileReader.close();
                    EMF_saveConfig();
                } catch (IOException e) {
                    EMFUtils.EMFModMessage("Config could not be loaded, using defaults", false);
                }
            } else {
                EMFConfigData = new EMFConfig();
                EMF_saveConfig();
            }
            if (EMFConfigData == null) {
                EMFConfigData = new EMFConfig();
                EMF_saveConfig();
            }
        } catch (Exception e) {
            EMFConfigData = new EMFConfig();
        }
    }

    public static EMFConfig copyFrom(EMFConfig source) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.fromJson(gson.toJson(source), EMFConfig.class);
    }


    public enum VanillaModelRenderMode{
        Off(ScreenTexts.OFF),
        Position_normal(new TranslatableText("entity_model_features.config.vanilla_render.normal")),
        Positon_offset(new TranslatableText("entity_model_features.config.vanilla_render.offset"));

        private final Text text;
        VanillaModelRenderMode(Text text){
            this.text = text;
        }

        public Text asText(){
            return text;
        }

        public VanillaModelRenderMode next(){
            return switch (this){
                case Off -> Position_normal;
                case Position_normal -> Positon_offset;
                default -> Off;
            };
        }

    }
    public enum MathFunctionChoice {
        JavaMath,
        MinecraftMath//bugged for some reason
        //FastMath
    }

}
