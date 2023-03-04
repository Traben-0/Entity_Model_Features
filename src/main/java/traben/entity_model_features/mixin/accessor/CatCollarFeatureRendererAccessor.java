package traben.entity_model_features.mixin.accessor;

import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CatCollarFeatureRenderer.class)
public interface CatCollarFeatureRendererAccessor {
    @Mutable
    @Accessor
    void setModel(CatEntityModel<CatEntity> model);
}
