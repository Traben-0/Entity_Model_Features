package traben.entity_model_features.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.utils.EMFManager;

@Mixin(Minecraft.class)
public abstract class MixinResourceReloadStart {

    @Inject(method = "reloadResourcePacks(ZLnet/minecraft/client/Minecraft$GameLoadCookie;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private void emf$reloadStart(CallbackInfoReturnable<Float> cir) {
        if (EMF.testForForgeLoadingError()) return;
        EMFManager.resetInstance();
    }

}


