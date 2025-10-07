package traben.entity_model_features.mixin.mixins.rendering.arrows;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import org.spongepowered.asm.mixin.Final;

//#if MC >= 12109
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
//#elseif MC >= 12102
//$$ import net.minecraft.client.renderer.entity.state.PlayerRenderState;
//#endif
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(StuckInBodyLayer.class)
public abstract class MixinStuckArrowsFeatureRenderer
    //#if MC >= 12109
    <M extends PlayerModel> extends RenderLayer<AvatarRenderState, M>
    //#elseif MC >= 12102
    //$$ <M extends PlayerModel> extends RenderLayer<PlayerRenderState, M>
    //#else
    //$$ <T extends LivingEntity, M extends PlayerModel<T>> extends RenderLayer<T, M>
    //#endif
{

    //#if MC >= 12102
    @Shadow
    @Final
    private StuckInBodyLayer.PlacementStyle placementStyle;

    public MixinStuckArrowsFeatureRenderer() {super(null);}

    @Shadow
    private static float snapToFace(final float f) {
        return 0;
    }
        //#if MC >= 12109
        @Shadow protected abstract int numStuck(final AvatarRenderState playerRenderState);
        @Shadow protected abstract void submitStuckItem(final PoseStack par1, final SubmitNodeCollector par2, final int par3, final float par4, final float par5, final float par6, final int par7);
        //#else
        //$$ @Shadow protected abstract int numStuck(final PlayerRenderState playerRenderState);
        //$$ @Shadow protected abstract void renderStuckItem(final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final float f, final float g, final float h);
        //#endif

    //#else
    //$$ public MixinStuckArrowsFeatureRenderer(final RenderLayerParent<T, M> renderer) {
    //$$     super(renderer);
    //$$ }
    //$$ @Shadow protected abstract int numStuck(final T entity);
    //$$ @Shadow protected abstract void renderStuckItem(final PoseStack poseStack, final MultiBufferSource buffer, final int packedLight, final Entity entity, final float x, final float y, final float z, final float partialTick);
    //#endif


    //#if MC >= 12109
    private static final String RENDER_METHOD = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V";
    //#elseif MC >= 12102
    //$$ private static final String RENDER_METHOD = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V";
    //#else
    //$$ private static final String RENDER_METHOD = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V";
    //#endif


    @Inject(method = RENDER_METHOD, at = @At(value = "HEAD"), cancellable = true)
    private void emf$start(
            //#if MC >= 12109
            final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int packedLight, final AvatarRenderState playerRenderState, final float f2, final float g2, final CallbackInfo ci
            //#elseif MC >= 12102
            //$$ final PoseStack poseStack, final MultiBufferSource buffer, final int packedLight, final PlayerRenderState playerRenderState, final float f2, final float g2, final CallbackInfo ci
            //#else
            //$$ final PoseStack poseStack, final MultiBufferSource buffer, final int packedLight, final T livingEntity, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final CallbackInfo ci
            //#endif
    ) {
        EMFAnimationEntityContext.is_in_ground_override = true;
        if(((IEMFModel)this.getParentModel()).emf$isEMFModel()){
            ci.cancel();

            var root = ((IEMFModel) this.getParentModel()).emf$getEMFRootModel();

            int i = this.numStuck(
                    //#if MC >= 12102
                    playerRenderState
                    //#else
                    //$$ livingEntity
                    //#endif
                );
            RandomSource randomSource = RandomSource.create(
                    //#if MC >= 12102
                    playerRenderState.id
                    //#else
                    //$$ livingEntity.getId()
                    //#endif
                );
            if (i > 0) {
                for(int j = 0; j < i; ++j) {
                    Random partRand = new Random(j);
                    poseStack.pushPose();
                    Pair<ModelPart,Runnable> modelPart = emf$bestFromListMutable(new ArrayList<>(root.getAllVanillaPartsEMF()), partRand, poseStack, true);
                    if (modelPart == null) {
                        EMFAnimationEntityContext.is_in_ground_override = false;
                        poseStack.popPose();
                        return;
                    }

                    modelPart.getSecond().run();//transforms
//                    modelPart.translateAndRotate(poseStack);

                    float f = randomSource.nextFloat();
                    float g = randomSource.nextFloat();
                    float h = randomSource.nextFloat();

                    //#if MC >= 12102
                    if (this.placementStyle == StuckInBodyLayer.PlacementStyle.ON_SURFACE) {
                        int n = randomSource.nextInt(3);
                        switch (n) {
                            case 0 -> f = snapToFace(f);
                            case 1 -> g = snapToFace(g);
                            default -> h = snapToFace(h);
                        }
                    }
                    //#endif

                    if (!modelPart.getFirst().cubes.isEmpty()) {
                        ModelPart.Cube cube = modelPart.getFirst().getRandomCube(randomSource);
                        float k = Mth.lerp(f, cube.minX, cube.maxX) / 16.0F;
                        float l = Mth.lerp(g, cube.minY, cube.maxY) / 16.0F;
                        float m = Mth.lerp(h, cube.minZ, cube.maxZ) / 16.0F;
                        poseStack.translate(k, l, m);
                    }




                    f = -1.0F * (f * 2.0F - 1.0F);
                    g = -1.0F * (g * 2.0F - 1.0F);
                    h = -1.0F * (h * 2.0F - 1.0F);
                    //#if MC >= 12109
                    this.submitStuckItem(poseStack, submitNodeCollector, packedLight, f, g, h, playerRenderState.outlineColor);
                    //#elseif MC >= 12102
                    //$$ this.renderStuckItem(poseStack, buffer, packedLight, f, g, h);
                    //#else
                    //$$ this.renderStuckItem(poseStack, buffer, packedLight, livingEntity, f, g, h, partialTicks);
                    //#endif
                    poseStack.popPose();
                }
            }

        }
    }

    @Inject(method = RENDER_METHOD, at = @At(value = "RETURN"))
    private void emf$end(CallbackInfo ci) {
        EMFAnimationEntityContext.is_in_ground_override = false;
    }



    @Unique
    @Nullable
    private Pair<ModelPart,Runnable> emf$bestFromListMutable(List<ModelPart> partsMutable, Random randomSource, PoseStack poseStack, boolean firstIteration) {
        Collections.shuffle(partsMutable, randomSource);
        //try children instead
        for (ModelPart modelPart : partsMutable) {
            if (modelPart.visible) {
                if (!modelPart.cubes.isEmpty() && !modelPart.skipDraw) {
                    return Pair.of(modelPart, () -> modelPart.translateAndRotate(poseStack));
                }
                if (modelPart.children.isEmpty())continue;

                var child = emf$bestFromListMutable(new ArrayList<>(modelPart.children.values()), randomSource, poseStack, false);
                if (child != null){
                    var runnable = child.getSecond();
                    return Pair.of(child.getFirst(), () -> {
                        modelPart.translateAndRotate(poseStack);
                        runnable.run();
                    });
                }
            }
        }
        if (firstIteration && !partsMutable.isEmpty()) {
            //noinspection SequencedCollectionMethodCanBeUsed
            var part = partsMutable.get(0);
            return Pair.of(part, () -> part.translateAndRotate(poseStack));
        }
        return null;
    }
}
