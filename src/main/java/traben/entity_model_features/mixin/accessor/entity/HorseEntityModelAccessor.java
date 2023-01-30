package traben.entity_model_features.mixin.accessor.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HorseEntityModel.class)
public interface HorseEntityModelAccessor {
    @Mutable
    @Accessor
    void setBody(ModelPart body);

    @Mutable
    @Accessor
    void setHead(ModelPart head);

    @Mutable
    @Accessor
    void setRightHindLeg(ModelPart rightHindLeg);

    @Mutable
    @Accessor
    void setLeftHindLeg(ModelPart leftHindLeg);

    @Mutable
    @Accessor
    void setRightFrontLeg(ModelPart rightFrontLeg);

    @Mutable
    @Accessor
    void setLeftFrontLeg(ModelPart leftFrontLeg);

    @Mutable
    @Accessor
    void setRightHindBabyLeg(ModelPart rightHindBabyLeg);

    @Mutable
    @Accessor
    void setLeftHindBabyLeg(ModelPart leftHindBabyLeg);

    @Mutable
    @Accessor
    void setRightFrontBabyLeg(ModelPart rightFrontBabyLeg);

    @Mutable
    @Accessor
    void setLeftFrontBabyLeg(ModelPart leftFrontBabyLeg);

    @Mutable
    @Accessor
    void setTail(ModelPart tail);

    @Mutable
    @Accessor
    void setSaddle(ModelPart[] saddle);

    @Mutable
    @Accessor
    void setStraps(ModelPart[] straps);

    @Accessor
    ModelPart[] getSaddle();

    @Accessor
    ModelPart[] getStraps();
}
