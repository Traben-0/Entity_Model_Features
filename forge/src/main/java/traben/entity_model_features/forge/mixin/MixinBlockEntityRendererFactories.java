package traben.entity_model_features.forge.mixin;


import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_model_features.utils.EMFManager;

import java.util.function.BiConsumer;

@Mixin(BlockEntityRendererFactories.class)
public class MixinBlockEntityRendererFactories {


    @ModifyArg(
            method = "reload",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"),
            index = 0
    )
    private static BiConsumer<BlockEntityType<?>, BlockEntityRendererFactory<?>> setEmf$Model(BiConsumer<BlockEntityType<?>, BlockEntityRendererFactory<?>> action) {
        //forge 1.19 requires this method of injecting into the lambda
        return (type,factory)->{
            setEmf$Model(type);
            action.accept(type,factory);
        };
    }


    @Unique
    private static void setEmf$Model(BlockEntityType type) {
        //mark which variant is currently specified for use by otherwise identical block entity renderers
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
