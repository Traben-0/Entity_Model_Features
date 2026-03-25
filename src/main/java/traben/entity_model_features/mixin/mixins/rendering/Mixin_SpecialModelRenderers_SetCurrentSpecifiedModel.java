package traben.entity_model_features.mixin.mixins.rendering;

//#if MC >= 12104
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

//#if MC >= 26.1
//$$ import net.minecraft.client.renderer.item.SpecialModelWrapper;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import com.llamalad7.mixinextras.sugar.Local;
//$$
//$$ @Mixin(net.minecraft.client.renderer.block.LoadedBlockModels.class)
//#else
@Mixin(SpecialModelRenderers.class)
//#endif
public class Mixin_SpecialModelRenderers_SetCurrentSpecifiedModel {

    @Unique
    private static final List<String> emf$renderers = new ArrayList<>();

    @Inject(method =
            //#if MC >= 26.1
            //$$ "bake"
            //#else
            "createBlockRenderers"
            //#endif
            , at = @At(value = "RETURN"))
    private static void emf$clearMarker(CallbackInfoReturnable<Map<Block, SpecialModelRenderer<?>>> cir) {
        if (EMF.testForForgeLoadingError()) return;
        EMFManager.getInstance().currentSpecifiedModelLoading = "";
        EMFManager.getInstance().currentBlockEntityTypeLoading = null;
        if (EMF.config().getConfig().logModelCreationData || EMF.config().getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE)
            EMFUtils.log("Identified SPECIAL block entity renderers: " + emf$renderers);
        emf$renderers.clear();
    }

    //#if MC >= 26.1
    //$$ @Inject(method = "lambda$bake$1", at = @At(value = "HEAD"))
    //$$ private static void setEmf$Model(CallbackInfo ci, @Local(argsOnly = true) BlockState blockState, @Local(argsOnly = true) net.minecraft.client.renderer.block.model.BlockModel.Unbaked unbakedModel) {
    //$$     if (unbakedModel instanceof SpecialModelWrapper<?>) {
    //$$         setModel(blockState);
    //$$     }
    //$$ }
    //#else
    @ModifyArg(method = "createBlockRenderers", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"))
    private static BiConsumer<Block, Object> setEmf$Model(BiConsumer<? super Block, Object> action) {
        return (Block type, Object idk) -> {
            setModel(type.defaultBlockState());

            // og code
            action.accept(type, idk);
        };
    }
    //#endif

    @Unique
    private static void setModel(BlockState state) {
        //mark which variant is currently specified for use by otherwise identical block entity renderers
        if (EMF.testForForgeLoadingError()) return;

        // TODO DONT FORGET TO REPLICATE CHANGES IN REGULAR RENDERERS
        if (state.is(Blocks.ENCHANTING_TABLE))
            EMFManager.getInstance().currentSpecifiedModelLoading = "enchanting_book";
        else if (state.is(Blocks.LECTERN))
            EMFManager.getInstance().currentSpecifiedModelLoading = "lectern_book";
        else if (state.is(Blocks.CHEST))
            EMFManager.getInstance().currentSpecifiedModelLoading = "chest";
        else if (state.is(Blocks.ENDER_CHEST))
            EMFManager.getInstance().currentSpecifiedModelLoading = "ender_chest";
        else if (state.is(Blocks.TRAPPED_CHEST))
            EMFManager.getInstance().currentSpecifiedModelLoading = "trapped_chest";
        else {
            try {
                ResourceLocation id = BuiltInRegistries.BLOCK.wrapAsHolder(state.getBlock()).unwrapKey().get().location();

                if (id.getNamespace().equals("minecraft")) {
                    EMFManager.getInstance().currentSpecifiedModelLoading = id.getPath();
                } else {
                    EMFManager.getInstance().currentSpecifiedModelLoading = id.getNamespace() + ":" + id.getPath();
                }
            } catch (Exception e) {
                EMFManager.getInstance().currentSpecifiedModelLoading = state.toString();
            }
        }
        emf$renderers.add(EMFManager.getInstance().currentSpecifiedModelLoading);
        if (EMF.config().getConfig().logModelCreationData)
            EMFUtils.log("Seeing SPECIAL block entity renderer init for: " + EMFManager.getInstance().currentSpecifiedModelLoading);
    }

}
//#else
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Mixin(CancelTarget.class)
//$$ public class Mixin_SpecialModelRenderers_SetCurrentSpecifiedModel { }
//#endif
