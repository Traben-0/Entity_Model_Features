package traben.entity_model_features.mixin;


import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(BlockEntityRendererFactories.class)
public class MixinBlockEntityRendererFactories {

    @Unique
    private static final List<String> emf$renderers = new ArrayList<>();

    @Inject(method = "reload", at = @At(value = "RETURN"))
    private static void emf$clearMarker(final BlockEntityRendererFactory.Context args, final CallbackInfoReturnable<Map<BlockEntityType<?>, BlockEntityRenderer<?>>> cir) {
        if (EMF.testForForgeLoadingError()) return;
        EMFManager.getInstance().currentSpecifiedModelLoading = "";
        EMFManager.getInstance().currentBlockEntityTypeLoading = null;
        if (EMF.config().getConfig().logModelCreationData || EMF.config().getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE)
            EMFUtils.log("Identified block entity renderers: " + emf$renderers);
        emf$renderers.clear();
    }


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
        if (EMF.testForForgeLoadingError()) return;

        EMFManager.getInstance().currentBlockEntityTypeLoading = type;

        if (BlockEntityType.ENCHANTING_TABLE.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading = "enchanting_book";
        else if (BlockEntityType.LECTERN.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading = "lectern_book";
        else if (Registry.BLOCK_ENTITY_TYPE.getKey(type).isPresent()) {
            Identifier id = Registry.BLOCK_ENTITY_TYPE.getKey(type).get().getValue();
            if (id.getNamespace().equals("minecraft")) {
                EMFManager.getInstance().currentSpecifiedModelLoading = id.getPath();
            } else {
                EMFManager.getInstance().currentSpecifiedModelLoading = id.getNamespace() + ":" + id.getPath();
            }
        }

        emf$renderers.add(EMFManager.getInstance().currentSpecifiedModelLoading);
    }


}
