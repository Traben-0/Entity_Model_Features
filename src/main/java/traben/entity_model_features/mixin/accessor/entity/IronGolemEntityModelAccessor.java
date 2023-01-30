package traben.entity_model_features.mixin.accessor.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IronGolemEntityModel.class)
public interface IronGolemEntityModelAccessor {
    @Mutable
    @Accessor
    void setRightArm(ModelPart rightArm);
}
