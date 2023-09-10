package traben.entity_model_features.mixin.rendering;


import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.LecternBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.utils.EMFManager;

@Mixin(LecternBlockEntityRenderer.class)
public class MixinLecternBlockEntityRenderer {

    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/block/entity/BlockEntityRendererFactory$Context;getLayerModelPart(Lnet/minecraft/client/render/entity/model/EntityModelLayer;)Lnet/minecraft/client/model/ModelPart;"
            , shift = At.Shift.BEFORE))
    private void setEmf$Model(BlockEntityRendererFactory.Context ctx, CallbackInfo ci) {
        EMFManager.getInstance().currentSpecifiedModelLoading = "lectern_book";
    }

}
