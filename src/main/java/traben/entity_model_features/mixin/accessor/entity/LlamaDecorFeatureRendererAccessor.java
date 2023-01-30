package traben.entity_model_features.mixin.accessor.entity;

import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.passive.LlamaEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LlamaDecorFeatureRenderer.class)
public interface LlamaDecorFeatureRendererAccessor {
    @Mutable
    @Accessor
    void setModel(LlamaEntityModel<LlamaEntity> model);
}
