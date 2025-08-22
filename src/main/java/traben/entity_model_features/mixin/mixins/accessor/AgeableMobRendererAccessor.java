package traben.entity_model_features.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.Mixin;
//#if MC >= 12102
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AgeableMobRenderer.class)
public interface AgeableMobRendererAccessor {
    @Accessor
    <S extends LivingEntityRenderState, M extends EntityModel<? super S>> M getBabyModel();
}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface AgeableMobRendererAccessor { }
//#endif