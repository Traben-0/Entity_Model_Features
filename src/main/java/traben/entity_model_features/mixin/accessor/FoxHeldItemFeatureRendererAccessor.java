package traben.entity_model_features.mixin.accessor;

import net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FoxHeldItemFeatureRenderer.class)
public interface FoxHeldItemFeatureRendererAccessor {
    @Accessor
    HeldItemRenderer getHeldItemRenderer();
}
