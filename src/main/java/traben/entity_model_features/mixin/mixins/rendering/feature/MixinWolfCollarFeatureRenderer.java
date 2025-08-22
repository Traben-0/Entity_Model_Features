package traben.entity_model_features.mixin.mixins.rendering.feature;


import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
//#if MC >= 12102
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
//#else
//$$ import net.minecraft.world.entity.animal.Wolf;
//#endif
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.mixin.mixins.accessor.AgeableMobRendererAccessor;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.IEMFWolfCollarHolder;

@Mixin(WolfCollarLayer.class)
public abstract class MixinWolfCollarFeatureRenderer extends RenderLayer<
//#if MC >= 12102
WolfRenderState, WolfModel
//#else
//$$ Wolf, WolfModel<Wolf>
//#endif
> {

    @Unique
    private static final ModelLayerLocation emf$collar_layer = new ModelLayerLocation(EMFUtils.res("minecraft", "wolf"), "collar");
    @Unique
    private static final ModelLayerLocation emf$collar_layer_baby = new ModelLayerLocation(EMFUtils.res("minecraft", "wolf_baby"), "collar");

    public MixinWolfCollarFeatureRenderer() { super(null); }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setEmf$Model(RenderLayerParent<?, ?> featureRendererContext, CallbackInfo ci) {
        if (EMF.testForForgeLoadingError()) return;

        ModelPart collarModel = EMFManager.getInstance().injectIntoModelRootGetter(emf$collar_layer,
                WolfModel
                        //#if MC >= 12006
                        .createMeshDefinition(CubeDeformation.NONE).getRoot().bake(64,32)
                        //#else
                        //$$ .createBodyLayer().bakeRoot()
                        //#endif
        );

        //separate the collar model, if it has a custom jem model or the base wolf has a custom jem model
        if (collarModel instanceof EMFModelPartRoot || ((IEMFModel) featureRendererContext.getModel()).emf$isEMFModel()) {
            try {
                if (featureRendererContext.getModel() instanceof
                    //#if MC >= 12102
                    IEMFWolfCollarHolder
                    //#else
                    //$$ IEMFWolfCollarHolder<?>
                    //#endif
                        holder) {
                    holder.emf$setCollarModel(new
                            //#if MC >= 12102
                            WolfModel
                            //#else
                            //$$ WolfModel<>
                            //#endif
                            (collarModel));
                }
            } catch (Exception ignored) {
            }
        }


        //#if MC >= 12102
        ModelPart collarModelBaby = EMFManager.getInstance().injectIntoModelRootGetter(emf$collar_layer_baby,
                LayerDefinition.create(WolfModel.createMeshDefinition(CubeDeformation.NONE), 64, 32).apply(WolfModel.BABY_TRANSFORMER).bakeRoot()
        );



        //separate the collar model, if it has a custom jem model or the base wolf has a custom jem model
        if (collarModelBaby instanceof EMFModelPartRoot
                || // base model is custom
                EMFManager.getInstance().injectIntoModelRootGetter(new ModelLayerLocation(EMFUtils.res("minecraft", "wolf_baby"), "main"),
                    LayerDefinition.create(WolfModel.createMeshDefinition(CubeDeformation.NONE), 64, 32).bakeRoot()
                    ) instanceof EMFModelPart) {
            try {
                // store in primary model
                if (featureRendererContext instanceof AgeableMobRendererAccessor ageModelsAccessor
                        && ageModelsAccessor.getBabyModel() instanceof
                        //#if MC >= 12102
                        IEMFWolfCollarHolder
                                //#else
                                //$$ IEMFWolfCollarHolder<?>
                                //#endif
                                holder) {
                    holder.emf$setCollarModel(new
                            //#if MC >= 12102
                            WolfModel
                            //#else
                            //$$ WolfModel<>
                            //#endif
                            (collarModelBaby));
                }
            } catch (Exception ignored) {}
        }

        //#endif
    }

    @Override
    public @NotNull
        //#if MC >= 12102
        WolfModel
        //#else
        //$$ WolfModel<Wolf>
        //#endif
    getParentModel() {
        var base = super.getParentModel(); // already either adult or baby model

        if (base instanceof
                //#if MC >= 12102
                IEMFWolfCollarHolder
                        //#else
                        //$$ IEMFWolfCollarHolder<?>
                        //#endif
                        holder
                && holder.emf$hasCollarModel()) {
            //noinspection unchecked
            var model = (
                    //#if MC >= 12102
                    WolfModel
                    //#else
                    //$$ WolfModel<Wolf>
                    //#endif
                    ) holder.emf$getCollarModel();

            //#if MC < 12102
            //$$ model.attackTime = base.attackTime;
            //$$ model.riding = base.riding;
            //$$ model.young = base.young;
            //#endif
            return model;
        }
        return base;
    }
}
