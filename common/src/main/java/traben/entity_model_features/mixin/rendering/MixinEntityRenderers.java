package traben.entity_model_features.mixin.rendering;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;

@Mixin(EntityRenderers.class)
public class MixinEntityRenderers {
    #if MC > MC_21
    @Inject(method = "method_32174", at = @At(value = "HEAD"))
    private static void emf$locateTransient(final ImmutableMap.Builder<?,?> builder, final EntityRendererProvider.Context context, final EntityType<?> entityType, final EntityRendererProvider<?> entityRendererProvider, final CallbackInfo ci) {
        if(entityType.equals(EntityType.CREAKING_TRANSIENT))
            EMFManager.getInstance().currentSpecifiedModelLoading = "creaking_transient";
    }
    @Inject(method = "method_32174", at = @At(value = "TAIL"))
    private static void emf$reset(final ImmutableMap.Builder<?,?> builder, final EntityRendererProvider.Context context, final EntityType<?> entityType, final EntityRendererProvider<?> entityRendererProvider, final CallbackInfo ci) {
        EMFManager.getInstance().currentSpecifiedModelLoading = "";
    }
    #endif
}
