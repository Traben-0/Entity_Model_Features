package traben.entity_model_features.mixin.accessor.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AnimalModel.class)
public interface AnimalModelAccessor {
    @Invoker
    Iterable<ModelPart> callGetHeadParts();

    @Invoker
    Iterable<ModelPart> callGetBodyParts();

    @Accessor
    float getInvertedChildBodyScale();

    @Accessor
    float getChildBodyYOffset();
}
