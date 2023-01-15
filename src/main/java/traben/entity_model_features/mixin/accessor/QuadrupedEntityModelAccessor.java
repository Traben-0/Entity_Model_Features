package traben.entity_model_features.mixin.accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(QuadrupedEntityModel.class)
public interface QuadrupedEntityModelAccessor {

    @Invoker
    Iterable<ModelPart> callGetHeadParts();

    @Invoker
    Iterable<ModelPart> callGetBodyParts();

    @Accessor
    ModelPart getHead();

    @Mutable
    @Accessor
    void setHead(ModelPart head);

    @Accessor
    ModelPart getBody();

    @Mutable
    @Accessor
    void setBody(ModelPart body);

    @Accessor
    ModelPart getRightHindLeg();

    @Mutable
    @Accessor
    void setRightHindLeg(ModelPart rightHindLeg);

    @Accessor
    ModelPart getLeftHindLeg();

    @Mutable
    @Accessor
    void setLeftHindLeg(ModelPart leftHindLeg);

    @Accessor
    ModelPart getRightFrontLeg();

    @Mutable
    @Accessor
    void setRightFrontLeg(ModelPart rightFrontLeg);

    @Accessor
    ModelPart getLeftFrontLeg();

    @Mutable
    @Accessor
    void setLeftFrontLeg(ModelPart leftFrontLeg);
}
