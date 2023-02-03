package traben.entity_model_features.mixin.accessor.entity.feature;

import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreeperChargeFeatureRenderer.class)
public interface CreeperChargeFeatureRendererAccessor {
    @Mutable
    @Accessor
    void setModel(CreeperEntityModel<CreeperEntity> model);
}
