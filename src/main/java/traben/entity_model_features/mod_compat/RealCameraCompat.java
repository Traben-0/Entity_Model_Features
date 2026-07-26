package traben.entity_model_features.mod_compat;

public class RealCameraCompat {
    public static boolean isComputeCameraRendering = false;

    public static boolean isShouldRenderEntity() {
        return !isComputeCameraRendering;
    }
}