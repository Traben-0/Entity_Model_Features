package traben.entity_model_features.mixin.accessor.entity;

import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HorseArmorFeatureRenderer.class)
public interface HorseArmorFeatureRendererAccessor {
    @Mutable
    @Accessor
    void setModel(HorseEntityModel<HorseEntity> model);
}
