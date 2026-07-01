package traben.entity_model_features.mixin.mixins.mod_compat;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.mod_compat.RealCameraCompat;

import java.util.List;

@Pseudo
@Mixin(targets = "com.xtracr.realcamera.gui.ModelAnalyser", remap = false)
public class MixinRealCameraModelAnalyser {
//#if MC == 12001 && !NEOFORGE || MC >= 12101 && MC <= 12104 && !FORGE || MC == 2601 && !FORGE
//$$
//$$    // This method is for drawing on screen,
//$$    // It does NOT render the entity in the world
//$$    @Inject(method = "captureModel", at = @At("HEAD"))
//$$    private void onCaptureModelHead(Minecraft client, Entity entity, float partialTicks, PoseStack poseStack, com.xtracr.realcamera.config.BindTarget target, CallbackInfoReturnable<List<com.xtracr.realcamera.renderer.state.BuiltModelRecord>> ci) {
//$$        RealCameraCompat.isComputeCameraRendering = true;
//$$    }
//$$
//$$    @Inject(method = "captureModel", at = @At("RETURN"))
//$$    private void onCaptureModelReturn(Minecraft client, Entity entity, float partialTicks, PoseStack poseStack, com.xtracr.realcamera.config.BindTarget target, CallbackInfoReturnable<List<com.xtracr.realcamera.renderer.state.BuiltModelRecord>> ci) {
//$$        RealCameraCompat.isComputeCameraRendering = false;
//$$    }
//$$
//#endif

}

