package traben.entity_model_features.mixin.accessor.entity;

import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SaddleFeatureRenderer.class)
public interface SaddleFeatureRendererAccessor<T extends LivingEntity, M extends EntityModel<T>> {
    @Mutable
    @Accessor
    void setModel(M model);
}
