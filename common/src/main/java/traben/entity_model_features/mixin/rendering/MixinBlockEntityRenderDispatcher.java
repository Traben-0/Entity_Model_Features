package traben.entity_model_features.mixin.rendering;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Nameable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.models.animation.EMFAnimationHelper;

import java.util.UUID;

@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {


    @Inject(method = "renderEntity",
            at = @At(value = "HEAD"))
    private <E extends BlockEntity> void emf$grabEntity(E entity, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int light, int overlay, CallbackInfoReturnable<Boolean> cir) {
        EMFAnimationHelper.setCurrentBlockEntity(entity, emf$getUuid(entity));
   }

    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
            at = @At(value = "HEAD"))
    private <E extends BlockEntity> void emf$grabEntity1(E blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        EMFAnimationHelper.setCurrentBlockEntity(blockEntity, emf$getUuid(blockEntity));
    }

    @Inject(method = "render(Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
            at = @At(value = "HEAD"))
    private static  <T extends BlockEntity> void emf$grabEntity2(BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        EMFAnimationHelper.setCurrentBlockEntity(blockEntity, emf$getUuid(blockEntity));
    }

    @Unique
    private static UUID emf$getUuid(BlockEntity entity){
        String seed = entity.getType().toString() + entity.getPos().toString() + entity.getCachedState().getBlock().toString();
        if (entity instanceof Nameable nameable && nameable.hasCustomName()) {
            //noinspection ConstantConditions
            seed += nameable.getCustomName().getString();
        }
        return UUID.nameUUIDFromBytes(seed.getBytes());
    }
}
