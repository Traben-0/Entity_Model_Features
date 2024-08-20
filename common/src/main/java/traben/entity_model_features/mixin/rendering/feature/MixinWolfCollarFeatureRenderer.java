package traben.entity_model_features.mixin.rendering.feature;


import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.world.entity.animal.Wolf;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.IEMFWolfCollarHolder;

@Mixin(WolfCollarLayer.class)
public abstract class MixinWolfCollarFeatureRenderer extends RenderLayer<Wolf, WolfModel<Wolf>> {

    @Unique
    private static final ModelLayerLocation emf$collar_layer = new ModelLayerLocation(EMFUtils.res("minecraft", "wolf"), "collar");

    @SuppressWarnings("unused")
    public MixinWolfCollarFeatureRenderer(RenderLayerParent<Wolf, WolfModel<Wolf>> context) {
        super(context);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setEmf$Model(RenderLayerParent<?, ?> featureRendererContext, CallbackInfo ci) {
        if (EMF.testForForgeLoadingError()) return;

        ModelPart collarModel = EMFManager.getInstance().injectIntoModelRootGetter(emf$collar_layer,
                WolfModel
                        #if MC >= MC_20_6
                        .createMeshDefinition(CubeDeformation.NONE).getRoot().bake(64,32)
                        #else
                        .createBodyLayer().bakeRoot()
                        #endif
        );

        //separate the collar model, if it has a custom jem model or the base wolf has a custom jem model
        if (collarModel instanceof EMFModelPartRoot || ((IEMFModel) featureRendererContext.getModel()).emf$isEMFModel()) {
            try {
                if (featureRendererContext.getModel() instanceof IEMFWolfCollarHolder<?> holder) {
                    holder.emf$setCollarModel(new WolfModel<>(collarModel));
                }
            } catch (Exception ignored) {
            }
        }
    }

//    @ModifyArg(
//            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/WolfEntity;FFFFFF)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/WolfEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"),
//            index = 0
//    )
//    private EntityModel<?> emf$injectModel(EntityModel<?> par1) {
//        if (getContextModel() instanceof EMFWolfCollarHolder<?> holder && holder.emf$hasCollarModel()) {
//            holder.emf$getCollarModel().handSwingProgress = par1.handSwingProgress;
//            holder.emf$getCollarModel().riding = par1.riding;
//            holder.emf$getCollarModel().child = par1.child;
//            return holder.emf$getCollarModel();
//        }
//        return par1;
//    }

    @Override
    public @NotNull WolfModel<Wolf> getParentModel() {
        var base = super.getParentModel();
        if (base instanceof IEMFWolfCollarHolder<?> holder && holder.emf$hasCollarModel()) {
            //noinspection unchecked
            var model = (WolfModel<Wolf>) holder.emf$getCollarModel();
            model.attackTime = base.attackTime;
            model.riding = base.riding;
            model.young = base.young;
            return model;
        }
        return base;
    }
}
