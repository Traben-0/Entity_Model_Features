package traben.entity_model_features.mixin.accessor;

import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SlimeOverlayFeatureRenderer.class)
public interface SlimeOverlayFeatureRendererAccessor<T extends LivingEntity>  {
    @Mutable
    @Accessor
    void setModel(EntityModel<T> model);
}
