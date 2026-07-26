package traben.entity_model_features.mixin.mixins.mod_compat;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import traben.entity_model_features.mod_compat.RealCameraCompat;

@Pseudo
@Mixin(targets = "com.xtracr.realcamera.RealCameraCore", remap = false)
public class MixinRealCameraCore {
    // This method is used to get the vertex catcher
    // It does NOT render the entity in the world
//#if MC == 1.20.1 && !NEOFORGE || MC >= 1.21.1 && MC <= 1.21.4 && !FORGE || MC == 26.1 && !FORGE
//$$    @Inject(method = "computeCamera", at = @At("HEAD"))
//$$    private static void EFM$onComputeCameraHead(Minecraft client, float partialTicks, CallbackInfo ci) {
//$$        RealCameraCompat.isComputeCameraRendering = true;
//$$    }
//$$
//$$    @Inject(method = "computeCamera", at = @At("RETURN"))
//$$    private static void EFM$onComputeCameraReturn(Minecraft client, float partialTicks, CallbackInfo ci) {
//$$        RealCameraCompat.isComputeCameraRendering = false;
//$$    }
//#endif
}

