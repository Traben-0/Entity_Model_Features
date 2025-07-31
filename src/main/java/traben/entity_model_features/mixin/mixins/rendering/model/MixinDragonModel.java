package traben.entity_model_features.mixin.mixins.rendering.model;


import org.spongepowered.asm.mixin.Mixin;


//#if MC >=12102
@Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
public abstract class MixinDragonModel {}
//#else
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import traben.entity_model_features.EMFManager;
//$$ import net.minecraft.client.renderer.entity.EnderDragonRenderer;
//$$ @Mixin(EnderDragonRenderer.DragonModel.class)
//$$ public abstract class MixinDragonModel {
//$$     @Inject(method = "renderToBuffer",
//$$             at = @At(value = "INVOKE",
            //#if MC >= 12100
            //$$ target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
            //#else
            //$$ target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
            //#endif
//$$                     shift = At.Shift.BEFORE))
//$$     private void emf$allowMultiPartRender(final CallbackInfo ci) {
//$$         EMFManager.getInstance().entityRenderCount++;
//$$     }
//$$     @Inject(method = "renderToBuffer",
//$$             at = @At(value = "INVOKE",
            //#if MC >= 12100
            //$$ target = "Lnet/minecraft/client/renderer/entity/EnderDragonRenderer$DragonModel;renderSide(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFLnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;I)V",
            //#else
            //$$ target = "Lnet/minecraft/client/renderer/entity/EnderDragonRenderer$DragonModel;renderSide(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFLnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;F)V",
            //#endif
//$$             shift = At.Shift.BEFORE, ordinal = 0))
//$$     private void emf$allowMultiPartRender2(final CallbackInfo ci) {
//$$         EMFManager.getInstance().entityRenderCount++;
//$$     }
//$$ }
//#endif
