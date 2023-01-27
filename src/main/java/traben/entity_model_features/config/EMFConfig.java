package traben.entity_model_features.config;

import net.minecraft.client.gui.screen.Screen;

public class EMFConfig {
    public boolean enableCustomEntityModels = true;
    public float minimunAnimationCalculationRate = 1;

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
}
