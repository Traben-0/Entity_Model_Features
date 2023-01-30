package traben.entity_model_features.mixin.accessor.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FoxEntityModel.class)
public interface FoxEntityModelAccessor {
    @Accessor
    ModelPart getTail();

    @Mutable
    @Accessor
    void setTail(ModelPart tail);

    @Mutable
    @Accessor
    void setHead(ModelPart head);
}
