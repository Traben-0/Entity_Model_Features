package traben.entity_model_features.mixin.mixins.rendering;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {

    //#if MC >=12104
    @Inject(method = "setupAndRender",
            at = @At(value = "HEAD"))
    private static <T extends BlockEntity> void emf$grabEntity2(CallbackInfo ci, @Local(argsOnly = true) T blockEntity) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState)
                ETFEntityRenderState.forEntity((ETFEntity) blockEntity));
    }
    //#else
    //$$
    //$$ @Inject(method = "tryRender",
    //$$         at = @At(value = "HEAD"))
    //$$ private static void emf$grabEntity2(BlockEntity blockEntity, Runnable runnable, CallbackInfo ci) {
    //$$     EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState)
    //$$                ETFEntityRenderState.forEntity((ETFEntity) blockEntity));
    //$$ }
    //#endif
}
