package traben.entity_model_features.models.animation;

import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public enum EMFAttachments {
    left_handheld_item,
    right_handheld_item;


    private MatrixStack.Entry stackEntry = null;

    public MatrixStack.Entry getAndNullify() {
        MatrixStack.Entry hold = stackEntry;
        stackEntry = null;
        return hold;
    }

    public void setAttachment(MatrixStack entry, float x, float y, float z) {
        entry.push();
        entry.translate(x / 16, y / 16, z / 16);
        var save = entry.peek();
        this.stackEntry = new MatrixStack.Entry(new Matrix4f(save.getPositionMatrix()), new Matrix3f(save.getNormalMatrix()));//save;
        entry.pop();
    }

    public Consumer<MatrixStack> getConsumerWithTranslates(float x, float y, float z) {
        return (entry) -> this.setAttachment(entry, x, y, z);
    }

}
