package traben.entity_model_features.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.utils.EMFManager;


@Mixin(MinecraftClient.class)
public abstract class MixinResourceReload {

//    private static boolean emf$falseAfterFirstRun = true;

    @Inject(method = "reloadResources(Z)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private void emf$injected(CallbackInfoReturnable<Float> cir) {
//        if (cir.getReturnValue() == 1.0) {
//            if (emf$falseAfterFirstRun) {
//                emf$falseAfterFirstRun = false;
//                //do reset
                System.out.println("resetting emf");
                //EMFData.reset();
                EMFManager.resetInstance();
//            }
//        } else {
//            emf$falseAfterFirstRun = true;
//        }
    }
}


