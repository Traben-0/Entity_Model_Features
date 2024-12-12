package traben.entity_model_features.mixin.rendering;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.tags.EntityTypeTags;
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
        #if MC == MC_21_2
        if(entityType.equals(EntityType.CREAKING_TRANSIENT)) {
            EMFManager.getInstance().currentSpecifiedModelLoading = "creaking_transient";
        }else
        #endif
        if(entityType.equals(EntityType.SPECTRAL_ARROW)) {
            EMFManager.getInstance().currentSpecifiedModelLoading = "spectral_arrow";
        }else if(entityType.equals(EntityType.BREEZE_WIND_CHARGE)) {
            EMFManager.getInstance().currentSpecifiedModelLoading = "breeze_wind_charge";
        }else if (entityType.is(EntityTypeTags.BOAT)) {
            //entity.minecraft.dark_oak_boat
            EMFManager.getInstance().currentSpecifiedModelLoading = "emf$boat$" //key to not override
                    + entityType.getDescriptionId()
                        .replaceAll("entity.minecraft.","")
                        .replaceAll("(_boat|_raft|_chest_boat)$","");
        }
    }
    @Inject(method = "method_32174", at = @At(value = "TAIL"))
    private static void emf$reset(final ImmutableMap.Builder<?,?> builder, final EntityRendererProvider.Context context, final EntityType<?> entityType, final EntityRendererProvider<?> entityRendererProvider, final CallbackInfo ci) {
        EMFManager.getInstance().currentSpecifiedModelLoading = "";
    }
    #endif
}
