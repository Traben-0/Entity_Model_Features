package traben.entity_model_features.mixin.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFEntity;
#if MC > MC_21
import net.minecraft.client.renderer.entity.state.EntityRenderState;
#endif

@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {

    #if MC > MC_21_2

    @Inject(method = "setupAndRender",
            at = @At(value = "HEAD"))
    private static <T extends BlockEntity> void emf$grabEntity2(final BlockEntityRenderer<T> blockEntityRenderer, final T blockEntity, final float f, final PoseStack poseStack, final MultiBufferSource multiBufferSource, final CallbackInfo ci) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntity) blockEntity, new EntityRenderState());
    }
    #else

    @Inject(method = "tryRender",
            at = @At(value = "HEAD"))
    private static void emf$grabEntity2(BlockEntity blockEntity, Runnable runnable, CallbackInfo ci) {
        EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntity) blockEntity
                #if MC > MC_21 , new EntityRenderState() #endif);
    }
    #endif
}
