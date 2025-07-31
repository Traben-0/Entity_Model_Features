package traben.entity_model_features.models.jem_objects.export;

import java.util.LinkedList;

@SuppressWarnings("unused")
public class EMFJemPrinter {//todo use and assign values
    public String texture = "";
    public int[] textureSize = {16, 16};
    public double shadow_size = 1.0;
    public LinkedList<EMFPartPrinter> models = new LinkedList<>();
}
