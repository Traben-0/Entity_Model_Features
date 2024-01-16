package traben.entity_model_features.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMFClient;
import traben.entity_model_features.utils.EMFManager;


@Mixin(MinecraftClient.class)
public abstract class MixinResourceReload {

    @Inject(method = "reloadResources(Z)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private void emf$reload(CallbackInfoReturnable<Float> cir) {
        if (EMFClient.testForForgeLoadingError()) return;
        EMFManager.resetInstance();
    }
}


