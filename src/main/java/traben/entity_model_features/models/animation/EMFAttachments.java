package traben.entity_model_features.models.animation;

import com.mojang.blaze3d.vertex.PoseStack;
//#if MC<12105
//$$ import org.joml.Matrix3f;
//$$ import org.joml.Matrix4f;
//#endif

public class EMFAttachments {

    public PoseStack.Pose pose = null;
    private final float x;
    private final float y;
    private final float z;
    public final boolean right;

    public EMFAttachments(float x, float y, float z, boolean right) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.right = right;
    }

    public void translate(PoseStack entry) {
        entry.translate(x / 16, y / 16, z / 16);
    }
}
