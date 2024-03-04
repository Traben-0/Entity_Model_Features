package traben.entity_model_features.mixin.rendering;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFEntity;

@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {


    @Inject(method = "runReported",
            at = @At(value = "HEAD"))
    private static void emf$grabEntity2(BlockEntity blockEntity, Runnable runnable, CallbackInfo ci) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntity) blockEntity);
    }

}
