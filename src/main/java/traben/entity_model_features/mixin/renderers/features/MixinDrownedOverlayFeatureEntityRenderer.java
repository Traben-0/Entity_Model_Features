package traben.entity_model_features.mixin.renderers.features;


import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.DrownedEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.EMFCustomModel;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.vanilla_model_children.EMFCustomPlayerModel;


@Mixin(DrownedOverlayFeatureRenderer.class)
public abstract class MixinDrownedOverlayFeatureEntityRenderer<T extends DrownedEntity> extends FeatureRenderer<T, DrownedEntityModel<T>> {


    @Shadow @Final private DrownedEntityModel<T> model;

    public MixinDrownedOverlayFeatureEntityRenderer(FeatureRendererContext<T, DrownedEntityModel<T>> context) {
        super(context);
    }

    @ModifyArgs(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/DrownedEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/DrownedOverlayFeatureRenderer;render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V")
    )
    private void emf$mixinDrownedLayer(Args args) {
        //4 is sub arm
        //5 is sleeve
        if(emf$originalModel == null) {
            emf$originalModel = this.model;
            if (this.getContextModel() instanceof EMFCustomModel) {
                String entityTypeName = "drowned_outer";
                EMFCustomModel<T> emfModel =  EMFData.getInstance().getModelVariant(null,entityTypeName, this.model);
                if(emfModel != null){
                    emf$emfModel = emfModel;

                }
            }
        }
        if(emf$emfModel != null){// && args.get(0) instanceof EMFCustomModel){
            //return actual vanilla arm
            args.set(1, emf$emfModel);
        }

    }

    private  DrownedEntityModel<T> emf$originalModel;
    private  EMFCustomModel<T> emf$emfModel;


}
