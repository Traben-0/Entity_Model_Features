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
    public final PosePart root;

    public EMFBipedPose(HumanoidModel<?> model) {
        this.head = new PosePart(model.head);
        this.body = new PosePart(model.body);
        this.leftArm = new PosePart(model.leftArm);
        this.rightArm = new PosePart(model.rightArm);
        this.leftLeg = new PosePart(model.leftLeg);
        this.rightLeg = new PosePart(model.rightLeg);
        this.root = new PosePart(model.root());
    }

    public void applyTo(HumanoidModel<?> model) {
        head.applyTo(model.head);
        body.applyTo(model.body);
        leftArm.applyTo(model.leftArm);
        rightArm.applyTo(model.rightArm);
        leftLeg.applyTo(model.leftLeg);
        rightLeg.applyTo(model.rightLeg);
        root.applyTo(model.root());
    }

    private static class PosePart {
        private final float xRot;
        private final float yRot;
        private final float zRot;

        private final float x;
        private final float y;
        private final float z;

        private final float xScale;
        private final float yScale;
        private final float zScale;

        PosePart(ModelPart modelPart) {
            this.xRot = modelPart.xRot;
            this.yRot = modelPart.yRot;
            this.zRot = modelPart.zRot;

            this.x = modelPart.x;
            this.y = modelPart.y;
            this.z = modelPart.z;

            this.xScale = modelPart.xScale;
            this.yScale = modelPart.yScale;
            this.zScale = modelPart.zScale;
        }

        private void applyTo(ModelPart modelPart) {
            modelPart.xRot = this.xRot;
            modelPart.yRot = this.yRot;
            modelPart.zRot = this.zRot;

            modelPart.x = this.x;
            modelPart.y = this.y;
            modelPart.z = this.z;

            modelPart.xScale = this.xScale;
            modelPart.yScale = this.yScale;
            modelPart.zScale = this.zScale;
        }
    }
}
