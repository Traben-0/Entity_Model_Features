package traben.entity_model_features.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.utils.EMFManager;


@Mixin(MinecraftClient.class)
public abstract class MixinResourceReload {

    @Inject(method = "reloadResources()Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private void emf$reloadStart(CallbackInfoReturnable<Float> cir) {
        if (EMF.testForForgeLoadingError()) return;
        EMFManager.resetInstance();
    }

    @Inject(method = "onFinishedLoading", at = @At("HEAD"))
    private void emf$reloadFinish(final CallbackInfo ci) {
        if (EMF.testForForgeLoadingError()) return;
        EMFManager.getInstance().modifyEBEIfRequired();
    }
}


