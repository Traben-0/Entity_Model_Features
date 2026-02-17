package traben.entity_model_features.models.animation.state;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public class EMFBipedPose {

    private final PosePart head;
    private final PosePart body;
    private final PosePart leftArm;
    private final PosePart rightArm;
    private final PosePart leftLeg;
    private final PosePart rightLeg;

    public EMFBipedPose(HumanoidModel<?> model) {
        this.head = new PosePart(model.head);
        this.body = new PosePart(model.body);
        this.leftArm = new PosePart(model.leftArm);
        this.rightArm = new PosePart(model.rightArm);
        this.leftLeg = new PosePart(model.leftLeg);
        this.rightLeg = new PosePart(model.rightLeg);
    }

    public void applyTo(HumanoidModel<?> model) {
        head.applyTo(model.head);
        body.applyTo(model.body);
        leftArm.applyTo(model.leftArm);
        rightArm.applyTo(model.rightArm);
        leftLeg.applyTo(model.leftLeg);
        rightLeg.applyTo(model.rightLeg);
    }

    private static class PosePart {
        private final float xRot;
        private final float yRot;
        private final float zRot;

        private final float xOffset;
        private final float yOffset;
        private final float zOffset;

        private final float xScale;
        private final float yScale;
        private final float zScale;

        PosePart(ModelPart modelPart) {
            this.xRot = modelPart.xRot;
            this.yRot = modelPart.yRot;
            this.zRot = modelPart.zRot;

            this.xOffset = modelPart.x;
            this.yOffset = modelPart.y;
            this.zOffset = modelPart.z;

            this.xScale = modelPart.xScale;
            this.yScale = modelPart.yScale;
            this.zScale = modelPart.zScale;
        }

        private void applyTo(ModelPart modelPart) {
            modelPart.xRot = this.xRot;
            modelPart.yRot = this.yRot;
            modelPart.zRot = this.zRot;

            modelPart.x = this.xOffset;
            modelPart.y = this.yOffset;
            modelPart.z = this.zOffset;

            modelPart.xScale = this.xScale;
            modelPart.yScale = this.yScale;
            modelPart.zScale = this.zScale;
        }
    }
}
