package traben.entity_model_features.mixin.rendering;


import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFClient;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFWolfCollarHolder;

@Mixin(WolfCollarFeatureRenderer.class)
public abstract class MixinWolfCollarFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {

    @Unique
    private static final EntityModelLayer emf$collar_layer = new EntityModelLayer(new Identifier("minecraft", "wolf"), "collar");

    public MixinWolfCollarFeatureRenderer(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context) {
        super(context);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setEmf$Model(FeatureRendererContext<?, ?> featureRendererContext, CallbackInfo ci) {
        if (EMFClient.testForForgeLoadingError()) return;

        ModelPart collarModel = EMFManager.getInstance().injectIntoModelRootGetter(emf$collar_layer, WolfEntityModel.getTexturedModelData().createModel());

        //separate the collar model, if it has a custom jem model or the base wolf has a custom jem model
        if (collarModel instanceof EMFModelPartRoot || ((IEMFModel) featureRendererContext.getModel()).emf$isEMFModel()) {
            try {
                if (featureRendererContext.getModel() instanceof EMFWolfCollarHolder<?> holder) {
                    holder.emf$setCollarModel(new WolfEntityModel<>(collarModel));
                }
            } catch (Exception ignored) {
            }
        }
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/WolfEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/WolfCollarFeatureRenderer;renderModel(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFF)V"),
            index = 0
    )
    private EntityModel<?> emf$injectModel(EntityModel<?> par1) {
        if (getContextModel() instanceof EMFWolfCollarHolder<?> holder && holder.emf$hasCollarModel()) {
            holder.emf$getCollarModel().handSwingProgress = par1.handSwingProgress;
            holder.emf$getCollarModel().riding = par1.riding;
            holder.emf$getCollarModel().child = par1.child;
            return holder.emf$getCollarModel();
        }
        return par1;
    }


}
