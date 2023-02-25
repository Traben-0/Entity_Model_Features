package traben.entity_model_features.models.features;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FoxEntity;

@Environment(EnvType.CLIENT)
public class EMFoxHeldItemFeatureRenderer extends FoxHeldItemFeatureRenderer {
    private final HeldItemRenderer heldItemRenderer;
    public EMFoxHeldItemFeatureRenderer(FeatureRendererContext<FoxEntity, FoxEntityModel<FoxEntity>> context, HeldItemRenderer heldItemRenderer) {
        super(context, heldItemRenderer);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, FoxEntity foxEntity, float f, float g, float h, float j, float k, float l) {
        //todo this is not right but none of this makes any sense,
        // like the FA fox held item renderer can't possibly be reading the values of the foxes head because it is ALWAYS 0 if it's touching the ground
        // need to test comparisons with optifine
        super.render(matrixStack, vertexConsumerProvider, i, foxEntity, f, g, h, j, (float) -Math.toDegrees(this.getContextModel().head.roll), l);

//        boolean bl = foxEntity.isSleeping();
//        boolean bl2 = foxEntity.isBaby();
//        matrixStack.push();
//        float m;
//        if (bl2) {
//            m = 0.75F;
//            matrixStack.scale(0.75F, 0.75F, 0.75F);
//            matrixStack.translate(0.0F, 0.5F, 0.209375F);
//        }
//
//        FoxEntityModel<?> model = (FoxEntityModel)this.getContextModel();
//        matrixStack.translate(model.head.pivotX / 16.0F, model.head.pivotY / 16.0F, model.head.pivotZ / 16.0F);
//        m = foxEntity.getHeadRoll(h);
//   //     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation(m));
// //       matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k));
////        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(l));
// //       matrixStack.multiply(new Quaternionf().rotationZYX(m,k,-model.head.pitch));
//        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation(m));
//        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(-model.head.roll));
  //      matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(l));
//        if (foxEntity.isBaby()) {
//            if (bl) {
//                matrixStack.translate(0.4F, 0.26F, 0.15F);
//            } else {
//                matrixStack.translate(0.06F, 0.26F, -0.5F);
//            }
//        } else if (bl) {
//            matrixStack.translate(0.46F, 0.26F, 0.22F);
//        } else {
//            matrixStack.translate(0.06F, 0.27F, -0.5F);
//        }
//
//        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
//        if (bl) {
//            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
//        }
//
//        ItemStack itemStack = foxEntity.getEquippedStack(EquipmentSlot.MAINHAND);
//        this.heldItemRenderer.renderItem(foxEntity, itemStack, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumerProvider, i);
//        matrixStack.pop();
    }
}
