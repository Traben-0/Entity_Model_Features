package traben.entity_model_features.mixin.accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BipedEntityModel.class)
public interface BipedEntityModelAccessor{
    @Invoker
    Iterable<ModelPart> callGetBodyParts();

    @Invoker
    Iterable<ModelPart> callGetHeadParts();

    @Mutable
    @Accessor
    void setBody(ModelPart body);

    @Mutable
    @Accessor
    void setRightArm(ModelPart rightArm);

    @Mutable
    @Accessor
    void setHead(ModelPart head);

    @Mutable
    @Accessor
    void setHat(ModelPart hat);

    @Mutable
    @Accessor
    void setLeftArm(ModelPart leftArm);

    @Mutable
    @Accessor
    void setRightLeg(ModelPart rightLeg);

    @Mutable
    @Accessor
    void setLeftLeg(ModelPart leftLeg);
}
