package traben.entity_model_features.mixin.renderers;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.mixin.LlamaDecorFeatureRendererAccessor;
import traben.entity_model_features.mixin.SaddleFeatureRendererAccessor;
import traben.entity_model_features.mixin.accessor.HorseArmorFeatureRendererAccessor;
import traben.entity_model_features.models.EMFArmorableModel;
import traben.entity_model_features.models.EMFCustomModel;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.features.EMFArmorFeatureRenderer;
import traben.entity_model_features.models.vanilla_model_children.EMFCustomHorseModel;
import traben.entity_model_features.models.vanilla_model_children.EMFCustomLlamaModel;
import traben.entity_model_features.models.vanilla_model_children.EMFCustomPlayerModel;

import java.util.List;


@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public MixinPlayerEntityRenderer(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @ModifyArgs(
            method = "renderLeftArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;)V")
    )
    private void emf$mixinLeftArm(Args args) {
        //4 is sub arm
        //5 is sleeve
        EMFCustomPlayerModel<?> client = EMFData.getInstance().clientPlayerModel;
        if(client != null && !EMFData.getInstance().getConfig().useCustomPlayerHandInFPS && client.getThisEMFModel().vanillaModel instanceof PlayerEntityModel<?> player){
            //return actual vanilla arm
            args.set(4, player.leftArm);
            args.set(5, player.leftSleeve);
        }

    }

    @ModifyArgs(
            method = "renderRightArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;)V")
    )
    private void emf$mixinRightArm(Args args) {
        //4 is sub arm
        //5 is sleeve
        EMFCustomPlayerModel<?> client = EMFData.getInstance().clientPlayerModel;
        if(client != null && !EMFData.getInstance().getConfig().useCustomPlayerHandInFPS && client.getThisEMFModel().vanillaModel instanceof PlayerEntityModel<?> player){
            //return actual vanilla arm
            args.set(4, player.rightArm);
            args.set(5, player.rightSleeve);
        }

    }
}
