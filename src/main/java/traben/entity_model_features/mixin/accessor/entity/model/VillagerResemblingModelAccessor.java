package traben.entity_model_features.mixin.accessor.entity.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerResemblingModel.class)
public interface VillagerResemblingModelAccessor {
    @Mutable
    @Accessor
    void setHat(ModelPart hat);

    @Mutable
    @Accessor
    void setNose(ModelPart nose);
}
