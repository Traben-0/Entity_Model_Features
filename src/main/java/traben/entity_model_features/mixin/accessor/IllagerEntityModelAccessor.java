package traben.entity_model_features.mixin.accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IllagerEntityModel.class)
public interface IllagerEntityModelAccessor {
    @Mutable
    @Accessor
    void setHead(ModelPart head);

    @Mutable
    @Accessor
    void setRightArm(ModelPart rightArm);

    @Mutable
    @Accessor
    void setLeftArm(ModelPart leftArm);

    @Accessor
    ModelPart getRightArm();

    @Accessor
    ModelPart getLeftArm();
}
