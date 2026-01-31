package traben.entity_model_features.mixin.mixins.rendering.model;

//#if MC >= 1.21.5

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.features.state.HoldsETFRenderState;

@Mixin(ChickenRenderer.class)
public abstract class Mixin_ChickenRenderer_WarmModel extends MobRenderer<Chicken, ChickenRenderState, ChickenModel> {

    @Unique
    private static final ModelLayerLocation emf$warm =
            new ModelLayerLocation(EMFUtils.res("minecraft", "warm_chicken"), "main");
    @Unique
    private static final ModelLayerLocation emf$warm_baby =
            new ModelLayerLocation(EMFUtils.res("minecraft", "warm_chicken_baby"), "main");

    @Unique
    private AdultAndBabyModelPair<ChickenModel> models = null;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void emf$createWarmModels(EntityRendererProvider.Context context, CallbackInfo ci) {
        if (EMF.testForForgeLoadingError()) return;

        models = new AdultAndBabyModelPair<>(
                new ChickenModel(EMFManager.getInstance().injectIntoModelRootGetter(emf$warm,
                        ChickenModel.createBodyLayer().bakeRoot())),
                new ChickenModel(EMFManager.getInstance().injectIntoModelRootGetter(emf$warm_baby,
                        ChickenModel.createBodyLayer()
                                .apply(ChickenModel.BABY_TRANSFORMER).bakeRoot()))
        );
    }


    //#if MC >= 1.21.9
    private static final String RENDER_METHOD = "submit(Lnet/minecraft/client/renderer/entity/state/ChickenRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V";
    //#else
    //$$ private static final String RENDER_METHOD = "render";
    //#endif

    @Inject(method = RENDER_METHOD, at = @At(value = "INVOKE", target =
            //#if MC >= 1.21.9
            "Lnet/minecraft/client/renderer/entity/MobRenderer;submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V"
            //#else
            //$$ "Lnet/minecraft/client/renderer/entity/MobRenderer;render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            //#endif
    ))
    private void emf$warmModel(final CallbackInfo ci, @Local(argsOnly = true) ChickenRenderState renderState) {
        var state = ((HoldsETFRenderState) renderState).etf$getState();
        if (state != null && state.entity() != null && state.entity() instanceof Chicken cluck
                && "minecraft:warm".equals(cluck.getVariant().getRegisteredName())) {
            this.model = models.getModel(cluck.isBaby());
        }
    }


    @SuppressWarnings("DataFlowIssue")
    public Mixin_ChickenRenderer_WarmModel() { super(null, null, 0); }
}
//#else
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Mixin(CancelTarget.class)
//$$ public class Mixin_ChickenRenderer_WarmModel { }
//#endif



