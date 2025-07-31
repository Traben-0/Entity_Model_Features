package traben.entity_model_features.mixin.mixins;

//#if MC >= 12102
import net.minecraft.client.Minecraft;
//#else
//$$ import net.minecraft.client.ResourceLoadStateTracker;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;


//#if MC >= 12102
@Mixin(Minecraft.class)
//#else
//$$ @Mixin(ResourceLoadStateTracker.class)
//#endif
public abstract class MixinResourceReloadEnd {


    //#if MC >= 12102
    @Inject(method = "onResourceLoadFinished", at = @At("HEAD"))
    //#else
    //$$ @Inject(method = "finishReload", at = @At("HEAD"))
    //#endif
    private void emf$reloadFinish(final CallbackInfo ci) {
        if (EMF.testForForgeLoadingError()) return;
        EMFManager.getInstance().modifyEBEIfRequired();
        EMFManager.getInstance().reloadEnd();
    }
}


