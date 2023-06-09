package traben.entity_model_features.mixin;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


@Mixin(SimpleResourceReload.class)
public abstract class MixinResourceReload2 {

    @Inject(method = "start", at = @At("HEAD"))
    private static void emf$reload(ResourceManager manager, List<ResourceReloader> reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, boolean profiled, CallbackInfoReturnable<ResourceReload> cir) {
        if(EMFConfig.getConfig().reloadMode == EMFConfig.ModelDataRefreshMode.TEST) {
            EMFManager.resetInstance();
        }
    }
}


