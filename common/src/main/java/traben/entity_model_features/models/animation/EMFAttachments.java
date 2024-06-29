package traben.entity_model_features.models.animation;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.function.Consumer;

public enum EMFAttachments {
    left_handheld_item,
    right_handheld_item;


    private PoseStack.Pose stackEntry = null;

    public static void closeBoth(){
        left_handheld_item.stackEntry = null;
        right_handheld_item.stackEntry = null;
    }

    public PoseStack.Pose getAndNullify() {
        PoseStack.Pose hold = stackEntry;
        stackEntry = null;
        return hold;
    }

    public void setAttachment(PoseStack entry, float x, float y, float z) {
        entry.pushPose();
        entry.translate(x / 16, y / 16, z / 16);
        var save = entry.last();
        this.stackEntry = new PoseStack.Pose(new Matrix4f(save.pose()), new Matrix3f(save.normal()));//save;
        entry.popPose();
    }

    public Consumer<PoseStack> getConsumerWithTranslates(float x, float y, float z) {
        return (entry) -> this.setAttachment(entry, x, y, z);
    }

}
