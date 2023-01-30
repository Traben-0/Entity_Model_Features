package traben.entity_model_features.mixin.renderers;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.mixin.accessor.entity.LlamaDecorFeatureRendererAccessor;
import traben.entity_model_features.mixin.accessor.entity.SaddleFeatureRendererAccessor;
import traben.entity_model_features.mixin.accessor.entity.HorseArmorFeatureRendererAccessor;
import traben.entity_model_features.mixin.accessor.entity.SlimeOverlayFeatureRendererAccessor;
import traben.entity_model_features.models.EMFArmorableModel;
import traben.entity_model_features.models.EMFCustomModel;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.features.EMFArmorFeatureRenderer;
import traben.entity_model_features.models.vanilla_model_children.EMFCustomHorseModel;
import traben.entity_model_features.models.vanilla_model_children.EMFCustomLlamaModel;
import traben.entity_model_features.models.vanilla_model_children.EMFCustomPlayerModel;
import traben.entity_model_features.models.vanilla_model_children.EMFCustomSlimeModel;

import java.util.List;



@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }
    @Shadow
    public abstract M getModel();
    @Shadow
    protected M model;

    @Shadow @Final protected List<FeatureRenderer<T, M>> features;


    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void emf$InjectModel(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if(!EMFData.getInstance().alreadyCalculatedForRenderer.get(hashCode())) {

            EMFData emfData = EMFData.getInstance();
            emfData.alreadyCalculatedForRenderer.put(hashCode(), true);
            //int typeHash = this.hashCode(); // livingEntity.getType().hashCode();

            //if (!emfData.JEMPATH_CustomModel.containsKey(typeHash)) {
                entityTypeName = livingEntity.getType().getName().getString().toLowerCase().replace("\s", "_");

            //}
           // if (emfData.JEMPATH_CustomModel.containsKey(typeHash)) {
               // if (emfData.JEMPATH_CustomModel.get(typeHash) != null) {
                    emf$originalModel = this.model;
                    //EMF_EntityModel<T> emfSubmodel = emfData.createEMFModel(entityTypeName, typeHash, this.model);
                     emf$newModel = EMFData.getInstance().getModelVariant(livingEntity ,entityTypeName, this.model);
                    if(emf$newModel!= null) {
                        /*EMFCustomModel<T>*/
                        //emf$newModel = EMFData.getInstance().getCustomModelForRenderer(emfSubmodel, this.model);


                        if (emf$newModel instanceof EMFArmorableModel armored) {
                            for (FeatureRenderer<?, ?> feature :
                                    features) {
                                if (feature instanceof ArmorFeatureRenderer<?, ?, ?>) {
                                    EMF_EntityModel<?> inner = armored.getArmourModel(true);
                                    EMF_EntityModel<?> outer = armored.getArmourModel(false);
                                    if (inner != null && outer != null) {
                                        features.remove(feature);
                                        features.add(new EMFArmorFeatureRenderer<T, M>(this, inner, outer));
                                    }
                                    break;
                                }
                            }
                        }
                        if (livingEntity instanceof Saddleable) {
                            for (FeatureRenderer<?, ?> feature :
                                    features) {
                                if (feature instanceof SaddleFeatureRenderer saddle) {
                                    ((SaddleFeatureRendererAccessor<T, M>) saddle).setModel(emf$newModel);
                                    break;
                                }
                            }
                        }
                        if (livingEntity instanceof AbstractHorseEntity && emf$newModel instanceof EMFCustomHorseModel) {
                            for (FeatureRenderer<?, ?> feature :
                                    features) {
                                if (feature instanceof HorseArmorFeatureRenderer armr) {
                                    M model = emfData.getModelVariant(null,"horse_armor", getModel());
                                    if (model != null) {
                                        ((HorseArmorFeatureRendererAccessor) armr).setModel((HorseEntityModel<HorseEntity>) model);
                                        break;
                                    }
                                }
                            }
                        }
                        if (livingEntity instanceof LlamaEntity && emf$newModel instanceof EMFCustomLlamaModel) {
                            for (FeatureRenderer<?, ?> feature :
                                    features) {
                                if (feature instanceof LlamaDecorFeatureRenderer decor) {
                                    M llama_decor = emfData.getModelVariant(null,"llama_decor", getModel());
                                    if (llama_decor != null) {
                                        ((LlamaDecorFeatureRendererAccessor) decor).setModel((LlamaEntityModel<LlamaEntity>) llama_decor);
                                        break;
                                    }
                                }
                            }
                        }
                        if (livingEntity instanceof SlimeEntity && emf$newModel instanceof EMFCustomSlimeModel<?>) {
                            for (FeatureRenderer<?, ?> feature :
                                    features) {
                                if (feature instanceof SlimeOverlayFeatureRenderer over) {
                                    EntityModel<T> slime_outer = emfData.getModelVariant(null,"slime_outer", getModel());
                                    if (slime_outer != null) {
                                        ((SlimeOverlayFeatureRendererAccessor) over).setModel(slime_outer);
                                        break;
                                    }
                                }
                            }
                        }
                        this.model = emf$newModel;
                        if(emf$newModel instanceof EMFCustomPlayerModel && MinecraftClient.getInstance().player != null && livingEntity.getUuid().equals(MinecraftClient.getInstance().player.getUuid())){
                            EMFData.getInstance().clientPlayerModel = (EMFCustomPlayerModel<?>) emf$newModel;
                        }
                    }
                //}
           // }
        }else if (emf$newModel != null) {

            emf$newModel = EMFData.getInstance().getModelVariant(livingEntity, entityTypeName, emf$originalModel);
            if(emf$newModel instanceof EMFCustomPlayerModel && MinecraftClient.getInstance().player != null && livingEntity.getUuid().equals(MinecraftClient.getInstance().player.getUuid())){
                EMFData.getInstance().clientPlayerModel = (EMFCustomPlayerModel<?>) emf$newModel;
            }
            ((EMFCustomModel<?>) emf$newModel).getThisEMFModel().currentVertexProvider = vertexConsumerProvider;
            //if (((EMFCustomModel<?>) emf$newModel).doesThisModelNeedToBeReset()) {
                this.model = emf$newModel;
            //}
        }

    }

    String entityTypeName = null;


    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void emf$ReturnModel(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {

        if(emf$newModel != null && ((EMFCustomModel<?>)emf$newModel).doesThisModelNeedToBeReset()){
            this.model =  emf$originalModel;
        }
    }


    private M emf$originalModel = null;
    //must be instance of EMFCustomModel<T>
    private M emf$newModel = null;


}
