package traben.entity_model_features.mixin.accessor;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelPart.Cube.class)
public interface CuboidAccessor {


    @Mutable
    @Accessor
    void setPolygons(ModelPart.Polygon[] sides);

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
