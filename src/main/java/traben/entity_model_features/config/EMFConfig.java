package traben.entity_model_features.config;

import net.minecraft.client.MinecraftClient;

public class EMFConfig {
    public boolean enableCustomEntityModels = true;
    //public float minimunAnimationCalculationRate = 1;

    public float getMinAnimationRateFromFPS(){
        //if(animationFPS > 144) animationFPS = 144;
        if(animationFPS < 20) animationFPS = 20;


        // fps of 20 = 1.0
        // fps of 60 = 0.3333
        // fps of 144 = 0.138888   etc.
        return 20f / Math.min(animationFPS, MinecraftClient.getInstance().getCurrentFps());
    }

    public int animationFPS = 30;

    public  boolean displayVanillaModelHologram = false;
    public boolean printModelCreationInfoToLog = false;
    public float animationRateMinimumDistanceDropOff = 8;
    public float animationRateDistanceDropOffRate = 6;


    public boolean printAllMaths = false;

    public boolean useCustomPlayerHandInFPS = false;

    public boolean forceTranslucentMobRendering = false;

    public SpawnAnimation spawnAnim = SpawnAnimation.None;
    public float spawnAnimTime = 4;


    public enum SpawnAnimation{
        None,
        Rise,
        InflateGround,
        InflateCenter,
        Fall,
        Fade,
        Dark,
        Bright,
        Pitch,
        Yaw;
    }
    public enum VanillaModelRenderMode{
        No,
        Offset,
        InflateGround
    }

}
