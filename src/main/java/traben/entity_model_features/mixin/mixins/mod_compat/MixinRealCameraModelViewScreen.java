package traben.entity_model_features.mixin.mixins.mod_compat;


import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.mod_compat.RealCameraCompat;

import java.util.List;

@Pseudo
@Mixin(targets = "com.xtracr.realcamera.gui.ModelViewScreen", remap = false)
public class MixinRealCameraModelViewScreen {
    // This method is for drawing on screen,
    // It does NOT render the entity in the world
//#if MC == 12001 && !NEOFORGE
//#if MC >= 12101 && MC <= 12104 && !FORGE
//#if MC == 2601 && !FORGE
//$$    @Inject(method = "captureRotatedEntity", at = @At("HEAD"))
//$$    private void Emf$OnCaptureRotatedEntityHead (com.xtracr.realcamera.gui.ModelAnalyser analyser, com.xtracr.realcamera.config.BindTarget target, LivingEntity entity, CallbackInfoReturnable<List<com.xtracr.realcamera.renderer.state.BuiltModelRecord>> cir){
//$$        RealCameraCompat.isComputeCameraRendering = true;
//$$    }
//$$
//$$    @Inject(method = "captureRotatedEntity", at = @At("RETURN"))
//$$    private void Emf$OnCaptureRotatedEntityReturn (com.xtracr.realcamera.gui.ModelAnalyser analyser, com.xtracr.realcamera.config.BindTarget target, LivingEntity entity, CallbackInfoReturnable<List<com.xtracr.realcamera.renderer.state.BuiltModelRecord>> cir){
//$$        RealCameraCompat.isComputeCameraRendering = false;
//$$    }
//$$
//#endif
//#endif
//#endif
}

