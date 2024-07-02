package traben.entity_model_features.mixin;


import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
#if MC < MC_20_2
import net.minecraft.core.registries.BuiltInRegistries;
#endif
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(BlockEntityRenderers.class)
public class MixinBlockEntityRendererFactories {

    @Unique
    private static final List<String> emf$renderers = new ArrayList<>();

    @Inject(method = "createEntityRenderers", at = @At(value = "RETURN"))
    private static void emf$clearMarker(final BlockEntityRendererProvider.Context args, final CallbackInfoReturnable<Map<BlockEntityType<?>, BlockEntityRenderer<?>>> cir) {
        if (EMF.testForForgeLoadingError()) return;
        EMFManager.getInstance().currentSpecifiedModelLoading = "";
        EMFManager.getInstance().currentBlockEntityTypeLoading = null;
        if (EMF.config().getConfig().logModelCreationData || EMF.config().getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE)
            EMFUtils.log("Identified block entity renderers: " + emf$renderers);
        emf$renderers.clear();
    }

    @Inject(method = "method_32145", at = @At(value = "HEAD"))
    private static void setEmf$Model(ImmutableMap.Builder<?, ?> builder, BlockEntityRendererProvider.Context context, BlockEntityType<?> type, BlockEntityRendererProvider<?> factory, CallbackInfo ci) {
        //mark which variant is currently specified for use by otherwise identical block entity renderers
        if (EMF.testForForgeLoadingError()) return;

        EMFManager.getInstance().currentBlockEntityTypeLoading = type;

        if (BlockEntityType.ENCHANTING_TABLE.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading = "enchanting_book";
        else if (BlockEntityType.LECTERN.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading = "lectern_book";
        #if MC >= MC_20_2
        else if (type.builtInRegistryHolder() != null && type.builtInRegistryHolder().unwrapKey().isPresent()) {
            ResourceLocation id = type.builtInRegistryHolder().unwrapKey().get().location();
        #else
        else if (BuiltInRegistries.BLOCK_ENTITY_TYPE.getResourceKey(type).isPresent()) {
            ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getResourceKey(type).get().location();
        #endif
            if (id.getNamespace().equals("minecraft")) {
                EMFManager.getInstance().currentSpecifiedModelLoading = id.getPath();
            } else {
                EMFManager.getInstance().currentSpecifiedModelLoading = id.getNamespace() + ":" + id.getPath();
            }
        }
        emf$renderers.add(EMFManager.getInstance().currentSpecifiedModelLoading);
    }
}
