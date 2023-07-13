package traben.entity_model_features.mixin.accessor;

import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public interface ModelPartAccessor {
    @Accessor
    Map<String, ModelPart> getChildren();

//    @Mutable
//    @Accessor
//    void setChildren(Map<String, ModelPart> children);

    @Accessor
    List<ModelPart.Cuboid> getCuboids();

    @Mutable
    @Accessor
    void setCuboids(List<ModelPart.Cuboid> cuboids);
}
