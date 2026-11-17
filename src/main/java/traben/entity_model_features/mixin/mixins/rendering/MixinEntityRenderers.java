package traben.entity_model_features.mixin.mixins.rendering;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.UEntityTypes;

@Mixin(EntityRenderers.class)
public class MixinEntityRenderers {


    //#if MC >= 1.21.9 && NEOFORGE || MC >= 26.1
    //$$ private static final String method = "lambda$createEntityRenderers$0";
    //#elseif MC>=12106 && NEOFORGE
    //$$ private static final String method = "lambda$createEntityRenderers$2";
    //#else
    private static final String method = "method_32174";
    //#endif

    //#if MC >=12102
    @Inject(method = method, at = @At(value = "HEAD"))
    private static void emf$locateTransient(final ImmutableMap.Builder<?,?> builder, final EntityRendererProvider.Context context, final EntityType<?> entityType, final EntityRendererProvider<?> entityRendererProvider, final CallbackInfo ci) {
        EMFManager.getInstance().currentSpecifiedModelLoading.set(UEntityTypes.getSpecifiedModel(entityType));
    }
    @Inject(method = method, at = @At(value = "TAIL"))
    private static void emf$reset(final ImmutableMap.Builder<?,?> builder, final EntityRendererProvider.Context context, final EntityType<?> entityType, final EntityRendererProvider<?> entityRendererProvider, final CallbackInfo ci) {
        EMFManager.getInstance().currentSpecifiedModelLoading.set("");
    }
    //#endif
}
