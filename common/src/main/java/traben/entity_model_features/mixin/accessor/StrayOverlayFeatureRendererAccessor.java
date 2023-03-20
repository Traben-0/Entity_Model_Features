package traben.entity_model_features.mixin.accessor;

import net.minecraft.client.render.entity.feature.StrayOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StrayOverlayFeatureRenderer.class)
public interface StrayOverlayFeatureRendererAccessor  <T extends MobEntity & RangedAttackMob>{
    @Mutable
    @Accessor
    void  setModel(SkeletonEntityModel<T> model);
}
