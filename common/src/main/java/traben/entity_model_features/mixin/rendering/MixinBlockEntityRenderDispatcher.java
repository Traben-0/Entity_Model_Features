package traben.entity_model_features.mixin.rendering;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationHelper;

@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {


    @Inject(method = "runReported",
            at = @At(value = "HEAD"))
    private static <T extends BlockEntity> void emf$grabEntity2(BlockEntity blockEntity, Runnable runnable, CallbackInfo ci) {
        EMFAnimationHelper.setCurrentBlockEntity(blockEntity);
    }



//    @Inject(method = "renderEntity",
//            at = @At(value = "HEAD"))
//    private <E extends BlockEntity> void emf$grabEntity(E entity, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int light, int overlay, CallbackInfoReturnable<Boolean> cir) {
//        EMFAnimationHelper.setCurrentBlockEntity(entity);
//    }

//    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
//            at = @At(value = "HEAD"))
//    private <E extends BlockEntity> void emf$grabEntity1(E blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
//        EMFAnimationHelper.setCurrentBlockEntity(blockEntity);
//    }
}
