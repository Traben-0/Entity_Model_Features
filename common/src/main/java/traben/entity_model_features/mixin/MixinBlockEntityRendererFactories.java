package traben.entity_model_features.mixin;


import com.google.common.collect.ImmutableMap;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFClient;
import traben.entity_model_features.utils.EMFManager;

@Mixin(BlockEntityRendererFactories.class)
public class MixinBlockEntityRendererFactories {

    @Inject(method = "method_32145", at = @At(value = "HEAD"))
    private static void setEmf$Model(ImmutableMap.Builder<?, ?> builder, BlockEntityRendererFactory.Context context, BlockEntityType<?> type, BlockEntityRendererFactory<?> factory, CallbackInfo ci) {
        //mark which variant is currently specified for use by otherwise identical block entity renderers
        if (EMFClient.testForForgeLoadingError()) return;

        if (BlockEntityType.CHEST.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading = "chest";
        else if (BlockEntityType.ENDER_CHEST.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading = "ender_chest";
        else if (BlockEntityType.TRAPPED_CHEST.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading = "trapped_chest";
        else if (BlockEntityType.SHULKER_BOX.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading = "shulker_box";
        else if (BlockEntityType.ENCHANTING_TABLE.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading = "enchanting_book";
        else if (BlockEntityType.LECTERN.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading = "lectern_book";

    }

}
