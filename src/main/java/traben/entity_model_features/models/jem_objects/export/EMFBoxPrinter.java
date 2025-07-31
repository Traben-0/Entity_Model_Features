package traben.entity_model_features.models.jem_objects.export;

@SuppressWarnings("unused")
public class EMFBoxPrinter {
    public int[] textureOffset = {};
    public float[] uvDown = {0, 0, 0, 0};
    public float[] uvUp = {0, 0, 0, 0};

    public float[] uvNorth, uvFront = {0, 0, 0, 0};
    public float[] uvSouth, uvBack = {0, 0, 0, 0};
    public float[] uvWest, uvLeft = {0, 0, 0, 0};
    public float[] uvEast, uvRight = {0, 0, 0, 0};

    public float[] coordinates = {0, 0, 0, 0, 0, 0};
    public float sizeAdd = 0.0f;

    public float sizeAddX = 0.0f;
    public float sizeAddY = 0.0f;
    public float sizeAddZ = 0.0f;
}
