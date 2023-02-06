package traben.entity_model_features.mixin.accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DonkeyEntityModel.class)
public interface DonkeyEntityModelAccessor {
    @Accessor
    ModelPart getLeftChest();

    @Accessor
    ModelPart getRightChest();
}
