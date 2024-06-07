package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFEntity;

@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {


    @Inject(method = "tryRender",
            at = @At(value = "HEAD"))
    private static void emf$grabEntity2(BlockEntity blockEntity, Runnable runnable, CallbackInfo ci) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntity) blockEntity);
    }

}
