package traben.entity_model_features.mixin.accessor;

import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.entity.mob.DrownedEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DrownedOverlayFeatureRenderer.class)
public interface DrownedOverlayFeatureRendererAccessor<T extends DrownedEntity> {
    @Mutable
    @Accessor
    void setModel(DrownedEntityModel<T> model);
}
