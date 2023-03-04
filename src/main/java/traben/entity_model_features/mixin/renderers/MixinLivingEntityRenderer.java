package traben.entity_model_features.mixin.renderers;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.mixin.accessor.CatCollarFeatureRendererAccessor;
import traben.entity_model_features.mixin.accessor.FoxHeldItemFeatureRendererAccessor;
import traben.entity_model_features.mixin.accessor.entity.feature.HorseArmorFeatureRendererAccessor;
import traben.entity_model_features.mixin.accessor.entity.feature.LlamaDecorFeatureRendererAccessor;
import traben.entity_model_features.mixin.accessor.entity.feature.SaddleFeatureRendererAccessor;
import traben.entity_model_features.mixin.accessor.entity.feature.SlimeOverlayFeatureRendererAccessor;
import traben.entity_model_features.mixin.accessor.entity.model.PlayerEntityModelAccessor;
import traben.entity_model_features.models.EMFArmorableModel;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericCustomEntityModel;
import traben.entity_model_features.models.features.EMFCustomArmorFeatureRenderer;
import traben.entity_model_features.models.features.EMFoxHeldItemFeatureRenderer;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.biped.EMFCustomPlayerEntityModel;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.quadraped.EMFCustomCatEntityModel;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.quadraped.EMFCustomHorseEntityModel;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.quadraped.EMFCustomLlamaEntityModel;

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

            if (!EMFData.getInstance().alreadyCalculatedForRenderer.get(hashCode())) {

                modifyableAbleJemName = (
                        livingEntity instanceof PlayerEntity ||
                                livingEntity instanceof PufferfishEntity ||
                                livingEntity instanceof TropicalFishEntity
                );


                EMFData emfData = EMFData.getInstance();
                emfData.alreadyCalculatedForRenderer.put(hashCode(), true);
                if(!(this.model instanceof EMFCustomEntityModel<?>))
                    emf$originalModel = this.model;

                emf$newModel = EMFData.getInstance().getModelVariant(livingEntity, getTypeName(livingEntity), this.model);
                if (emf$newModel != null) {
                    if (emf$newModel instanceof EMFArmorableModel armored) {
                        for (FeatureRenderer<?, ?> feature :
                                features) {
                            if (feature instanceof ArmorFeatureRenderer<?, ?, ?>) {
                                EMFGenericCustomEntityModel<?> inner = armored.getArmourModel(true);
                                EMFGenericCustomEntityModel<?> outer = armored.getArmourModel(false);
                                if (inner != null && outer != null) {
                                    features.remove(feature);
                                    features.add(new EMFCustomArmorFeatureRenderer<T, M>(this, inner, outer));
                                }
                                break;
                            }
                        }
                    }if (livingEntity instanceof FoxEntity) {
                        for (FeatureRenderer<?, ?> feature :
                                features) {
                            if (feature instanceof FoxHeldItemFeatureRenderer foxFeature) {

                                EMFoxHeldItemFeatureRenderer newFoxHeld =
                                        new EMFoxHeldItemFeatureRenderer(
                                                (FeatureRendererContext<FoxEntity, FoxEntityModel<FoxEntity>>) this,
                                                ((FoxHeldItemFeatureRendererAccessor)foxFeature).getHeldItemRenderer());

                                features.remove(feature);
                                features.add((FeatureRenderer<T, M>) newFoxHeld);

                                break;
                            }
                        }
                    }
                    if (livingEntity instanceof Saddleable) {
                        for (FeatureRenderer<?, ?> feature :
                                features) {
                            if (feature instanceof SaddleFeatureRenderer saddle) {
                                ((SaddleFeatureRendererAccessor<T, M>) saddle).setModel(emf$newModel);

                            }
                            break;
                        }
                    }
                    if (livingEntity instanceof AbstractHorseEntity && emf$newModel instanceof EMFCustomHorseEntityModel) {
                        for (FeatureRenderer<?, ?> feature :
                                features) {
                            if (feature instanceof HorseArmorFeatureRenderer armr) {
                                M model = emfData.getModelVariant(null, "horse_armor", getModel());
                                if (model != null) {
                                    ((HorseArmorFeatureRendererAccessor) armr).setModel((HorseEntityModel<HorseEntity>) model);

                                }
                                break;
                            }
                        }
                    }
                    if (livingEntity instanceof LlamaEntity && emf$newModel instanceof EMFCustomLlamaEntityModel) {
                        for (FeatureRenderer<?, ?> feature :
                                features) {
                            if (feature instanceof LlamaDecorFeatureRenderer decor) {
                                M llama_decor = emfData.getModelVariant(null, "llama_decor", getModel());
                                if (llama_decor != null) {
                                    ((LlamaDecorFeatureRendererAccessor) decor).setModel((LlamaEntityModel<LlamaEntity>) llama_decor);

                                }
                                break;
                            }
                        }
                    }
                    if (livingEntity instanceof CatEntity && emf$newModel instanceof EMFCustomCatEntityModel) {
                        for (FeatureRenderer<?, ?> feature :
                                features) {
                            if (feature instanceof CatCollarFeatureRenderer decor) {
                                M catCollar = emfData.getModelVariant(null, "cat_collar", getModel());
                                if (catCollar != null) {
                                    ((CatCollarFeatureRendererAccessor) decor).setModel((CatEntityModel<CatEntity>) catCollar);

                                }
                                break;
                            }
                        }
                    }

                    if (livingEntity instanceof SlimeEntity && emf$newModel instanceof EMFCustomEntityModel) {
                        for (FeatureRenderer<?, ?> feature :
                                features) {
                            if (feature instanceof SlimeOverlayFeatureRenderer over) {
                                EntityModel<T> slime_outer = emfData.getModelVariant(null, "slime_outer", getModel());
                                if (slime_outer != null) {
                                    ((SlimeOverlayFeatureRendererAccessor) over).setModel(slime_outer);
                                }
                                break;
                            }
                        }
                    }


                    this.model = emf$newModel;
                    if (emf$newModel instanceof EMFCustomPlayerEntityModel && MinecraftClient.getInstance().player != null && livingEntity.getUuid().equals(MinecraftClient.getInstance().player.getUuid())) {
                        EMFData.getInstance().clientPlayerModel = (EMFCustomPlayerEntityModel<?>) emf$newModel;
                        if (emf$originalModel instanceof PlayerEntityModel)
                            EMFData.getInstance().clientPlayerVanillaModel = (EntityModel<PlayerEntity>) emf$originalModel;
                    }
                }
                //}
                // }
            } else if (emf$newModel != null) {
                //if(livingEntity instanceof PufferfishEntity && new Random().nextInt(100)==1) System.out.println("puffer="+getTypeName(livingEntity));
                emf$newModel = EMFData.getInstance().getModelVariant(livingEntity, getTypeName(livingEntity), emf$originalModel);
                if (emf$newModel instanceof EMFCustomPlayerEntityModel && MinecraftClient.getInstance().player != null && livingEntity.getUuid().equals(MinecraftClient.getInstance().player.getUuid())) {
                    EMFData.getInstance().clientPlayerModel = (EMFCustomPlayerEntityModel<?>) emf$newModel;
                }
                ((EMFCustomEntityModel<?>) emf$newModel).getThisEMFModel().currentVertexProvider = vertexConsumerProvider;
                //if (((EMFCustomModel<?>) emf$newModel).doesThisModelNeedToBeReset()) {
                this.model = emf$newModel;
                //}
            }

    }

    private String entityTypeBaseName = null;
    private boolean modifyableAbleJemName = false;
    private String getTypeName(Entity entity){
        if(entityTypeBaseName == null){
            entityTypeBaseName = entity.getType().getName().getString().toLowerCase().replace("\s", "_");
        }

        if(!modifyableAbleJemName) return entityTypeBaseName;

        String forReturn ;
        if (emf$originalModel instanceof PlayerEntityModel plyr && ((PlayerEntityModelAccessor) plyr).isThinArms()) {
                forReturn = entityTypeBaseName + "_slim";
        } else if (entity instanceof PufferfishEntity puffer) {
                forReturn = "puffer_fish_" + switch(puffer.getPuffState()){
                    case 0-> "small";
                    case 1-> "medium";
                    default -> "big";
                };
        } else if (entity instanceof TropicalFishEntity fish) {
                forReturn = entityTypeBaseName+ (fish.getVariant().getSize() == TropicalFishEntity.Size.LARGE ? "_b" : "_a");
        }else{
                forReturn=entityTypeBaseName;
        }
        return forReturn;


    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void emf$ReturnModel(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {

        if(emf$newModel != null && ((EMFCustomEntityModel<?>)emf$newModel).doesThisModelNeedToBeReset()){
            this.model =  emf$originalModel;
        }
    }
    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void emf$ReturnModel2(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {

        if(EMFData.getInstance().getConfig().patchFeatures && emf$newModel != null && ((EMFCustomEntityModel<?>)emf$newModel).doesThisModelNeedToBeReset()){
            this.model =  emf$originalModel;
        }
    }

    private M emf$originalModel = null;
    //must be instance of EMFCustomModel<T>
    private M emf$newModel = null;


}
