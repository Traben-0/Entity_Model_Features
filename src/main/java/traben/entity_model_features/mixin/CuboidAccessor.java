package traben.entity_model_features.mixin;

import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelPart.Cuboid.class)
public interface CuboidAccessor {
    @Accessor
    ModelPart.Quad[] getSides();

    @Mutable
    @Accessor
    void setSides(ModelPart.Quad[] sides);

    @Mutable
    @Accessor
    void setMinX(float minX);

    @Mutable
    @Accessor
    void setMinY(float minY);

    @Mutable
    @Accessor
    void setMinZ(float minZ);

    @Mutable
    @Accessor
    void setMaxX(float maxX);

    @Mutable
    @Accessor
    void setMaxY(float maxY);

    @Mutable
    @Accessor
    void setMaxZ(float maxZ);
}
