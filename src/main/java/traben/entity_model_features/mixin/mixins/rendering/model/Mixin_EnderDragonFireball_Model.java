package traben.entity_model_features.mixin.mixins.rendering.model;



import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.DragonFireballRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFUtils;

import java.util.List;
import java.util.Map;

@Mixin(DragonFireballRenderer.class)
public abstract class Mixin_EnderDragonFireball_Model {


    @Shadow
    @Final
    private static RenderType RENDER_TYPE;
    @Unique
    private static final ModelLayerLocation emf$fireball =
            new ModelLayerLocation(EMFUtils.res("minecraft", "dragon"), "fireball");

    //#if MC >= 1.21.2
    @Unique private EntityModel<net.minecraft.client.renderer.entity.state.EntityRenderState> fireball = null;
    //#else
    //$$ @Unique private EntityModel<net.minecraft.world.entity.projectile.Fireball> fireball = null;
    //#endif


    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void emf$createModel(EntityRendererProvider.Context context, CallbackInfo ci) {
        if (EMF.testForForgeLoadingError()) return;

        var possibleModel = EMFManager.getInstance().injectIntoModelRootGetter(emf$fireball,
                new ModelPart(List.of(),
                        Map.of("fireball", new ModelPart(List.of(), Map.of()))));

        if (possibleModel instanceof EMFModelPartRoot) {
            //#if MC >= 1.21.2
            fireball = new EntityModel<>(possibleModel) {};
            //#else
            //$$ fireball = new EntityModel<>() {
                //#if MC >= 1.21
                //$$ @Override public void renderToBuffer(PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer, int i, int j, int k) {
                //$$     possibleModel.render(poseStack, vertexConsumer, i, j, k);
                //$$ }
                //#else
                //$$ @Override public void renderToBuffer(PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
                //$$     possibleModel.render(poseStack, vertexConsumer, i, j, 1, 1, 1, 1);
                //$$ }
                //#endif
            //$$
            //$$     @Override
            //$$     public void setupAnim(net.minecraft.world.entity.projectile.Fireball entity, float f, float g, float h, float i, float j) {
            //$$         possibleModel.resetPose();
            //$$     }
            //$$ };
            //#endif
        }
    }


    //#if MC >= 1.21.9
    private static final String RENDER_METHOD = "submit";
    //#else
    //$$ private static final String RENDER_METHOD = "render";
    //#endif

    @Inject(method = RENDER_METHOD, at = @At("HEAD"), cancellable = true)
    private void emf$renderModel(final CallbackInfo ci,
                               @Local(argsOnly = true) PoseStack poseStack,
                               //#if MC >= 1.21.9
                               @Local(argsOnly = true) net.minecraft.client.renderer.entity.state.EntityRenderState entityRenderState,
                               @Local(argsOnly = true) net.minecraft.client.renderer.SubmitNodeCollector submitNodeCollector
                               //#else
                               //$$ @Local(argsOnly = true) net.minecraft.client.renderer.MultiBufferSource multiBufferSource,
                               //$$ @Local(argsOnly = true) int light
                               //#endif
    ) {
        if (fireball != null) {
            //#if MC >= 1.21.9
            submitNodeCollector.submitModel(fireball, entityRenderState, poseStack,
                    RENDER_TYPE, entityRenderState.lightCoords, OverlayTexture.NO_OVERLAY,
                    -1, null, 0, null);
            //#else
            //$$ fireball.renderToBuffer(poseStack,
            //$$         multiBufferSource.getBuffer(RENDER_TYPE),
            //$$         light,
            //$$         OverlayTexture.NO_OVERLAY
                         //#if MC < 1.21
                         //$$ , 1, 1, 1, 1
                         //#endif
            //$$ );
            //#endif

            ci.cancel();
        }
    }

}



