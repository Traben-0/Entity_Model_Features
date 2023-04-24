package traben.entity_model_features.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import traben.entity_model_features.EMFVersionDifferenceManager;
import traben.entity_model_features.utils.EMFUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EMFConfig {

    private static EMFConfig EMFConfigData;
    //public  VanillaModelRenderMode displayVanillaModelHologram = VanillaModelRenderMode.Off;
    public boolean printModelCreationInfoToLog = false;
    public boolean printAllMaths = false;
//    public boolean forceTranslucentMobRendering = false;


//    private boolean dontReduceFps = false;
//    public float getAnimationRateFromFPS(float interpolationModifier){
//        //if(animationFPS > 144) animationFPS = 144;
//        //if(animationFPS < 20) animationFPS = 20;
//        if(dontReduceFps) return 20f / animationRate.get();
//        // fps of 20 = 1.0
//        // fps of 60 = 0.3333
//        // fps of 144 = 0.138888   etc.
//        float calculatedValue = 20f / animationRate.get() + (interpolationModifier > 0 ? interpolationModifier : 0);
//        //System.out.println("fps"+Math.min(calculatedValue, 20f / minimumAnimationFPS));
//        return  Math.min(calculatedValue, 20f / minimumAnimationFPS);
//    }
//
//
//    public float getInterpolationModifiedByDistance(float distanceFromPlayer){
//        //was value / animationRateDistanceDropOffRate;
//        dontReduceFps = animationRateDistanceDropOffRate == 0;
//        if(dontReduceFps) return 0;
//        float vanillaModifer = MinecraftClient.getInstance().player == null ? 1 :  MinecraftClient.getInstance().player.getFovMultiplier();
//        float fov = MinecraftClient.getInstance().options.getFov().getValue() * vanillaModifer;
//
//        float fovModified;
//        if (fov >= 70 || fov <= 0) {
//            fovModified = (16 - animationRateDistanceDropOffRate);
//        } else {
//            fovModified = (16 - animationRateDistanceDropOffRate) * (70 / fov);//increase value the lower the fov is
//        }
//
//        return distanceFromPlayer / fovModified;//lower result = higher quality anim
//      //  return distanceFromPlayer / animationRateDistanceDropOffRate
//    }
//
//    public AnimationRatePerSecondMode animationRate = AnimationRatePerSecondMode.Sixty_tps;
//
//   // public int animationFPS = 30;
//    public float minimumAnimationFPS = 0.3F;
    public boolean renderCustomModelsGreen = false;
//    public float animationRateMinimumDistanceDropOff = 8;
//    public float animationRateDistanceDropOffRate = 10;
    public final MathFunctionChoice mathFunctionChoice = MathFunctionChoice.JavaMath;

    // public boolean useCustomPlayerHandInFPS = false;

    public boolean attemptToCopyVanillaModelIntoMissingModelPart = false;
    public static EMFConfig getConfig() {
        if (EMFConfigData == null) {
            loadConfig();
        }
        return EMFConfigData;
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

    // public SpawnAnimation spawnAnim = SpawnAnimation.None;
//    public float spawnAnimTime = 4;

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

    //    public enum SpawnAnimation{
//        None,
//        Rise,
//        InflateGround,
//        InflateCenter,
//        Fall,
//        Fade,
//        Dark,
//        Bright,
//        Pitch,
//        Yaw
//    }
//    public enum VanillaModelRenderMode{
//        Off,
//        Position_normal,
//        Positon_offset
//    }
    public enum MathFunctionChoice {
        JavaMath,
        MinecraftMath//bugged for some reason
        //FastMath
    }
//    public enum AnimationRatePerSecondMode{
//        Twenty_tps(()->Math.min(20,MinecraftClient.getInstance().getCurrentFps())),
//        Forty_tps(()->Math.min(40,MinecraftClient.getInstance().getCurrentFps())),
//        Sixty_tps(()->Math.min(60,MinecraftClient.getInstance().getCurrentFps())),
//        Every_four_frames(()->MinecraftClient.getInstance().getCurrentFps()/4),
//        Every_other_frame(()->MinecraftClient.getInstance().getCurrentFps()/2),
//        Every_frame(()->MinecraftClient.getInstance().getCurrentFps());
//
//        final Supplier<Integer> intGet;
//
//
//        AnimationRatePerSecondMode(Supplier<Integer> intGet){
//            this.intGet = intGet;
//        }
//
//        public int get(){
//            return intGet.get();
//        }
//    }


    // public boolean patchFeatures = false;
}
