package traben.entity_model_features.mixin;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resource.SimpleResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.Entity_model_featuresClient;


@Mixin(SimpleResourceReload.class)
public abstract class MixinSimpleResourceReload {

    private static boolean etf$falseAfterFirstRun = true;

    @Inject(method = "Lnet/minecraft/resource/SimpleResourceReload;getProgress()F", at = @At("RETURN"))
    private void etf$injected(CallbackInfoReturnable<Float> cir) {
        if (cir.getReturnValue() == 1.0) {
            if (etf$falseAfterFirstRun) {
                etf$falseAfterFirstRun = false;
                //do reset
                System.out.println("resetting emf");
                Entity_model_featuresClient.JEMPATH_CustomModel = new Int2ObjectOpenHashMap<>();
            }
        } else {
            etf$falseAfterFirstRun = true;
        }
    }
}


