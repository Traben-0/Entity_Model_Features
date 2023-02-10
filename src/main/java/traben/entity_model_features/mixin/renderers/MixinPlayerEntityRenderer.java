package traben.entity_model_features.mixin.renderers;


import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.EMFGenericEntityEntityModel;
import traben.entity_model_features.models.EMFModelPart;


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
//        EMFCustomPlayerModel<?> client = EMFData.getInstance().clientPlayerModel;
//
//        if(client != null && !EMFData.getInstance().getConfig().useCustomPlayerHandInFPS && client.getThisEMFModel().vanillaModel instanceof PlayerEntityModel<?> player){
//            //return actual vanilla arm
//            args.set(4, player.leftArm);
//            args.set(5, player.leftSleeve);
//        }

        if(!EMFData.getInstance().checkedHand && EMFData.getInstance().clientPlayerModel != null){
            EMFData.getInstance().checkedHand = true;
            handModel = EMFData.getInstance().createEMFModelOnly("player_hand",  EMFData.getInstance().clientPlayerVanillaModel);

            checkForArms();
        }
        if(handModel != null){// && EMFData.getInstance().clientGetter != null){
//            AnimationGetters getter = handModel.animationGetters;
//            handModel.animateModel((PlayerEntity) getter.entity,
//                    ((PlayerEntity) getter.entity).limbAngle,
//                    ((PlayerEntity) getter.entity).limbDistance,
//                    MinecraftClient.getInstance().getTickDelta());
//            handModel.setAngles((PlayerEntity) getter.entity,
//                    ((PlayerEntity) getter.entity).limbAngle,
//                    ((PlayerEntity) getter.entity).limbDistance,
//                    getter.entity.age+ MinecraftClient.getInstance().getTickDelta(),
//                    MathHelper.lerp(MinecraftClient.getInstance().getTickDelta(),((PlayerEntity) getter.entity).prevHeadYaw ,((PlayerEntity) getter.entity).headYaw),
//                    MathHelper.lerp(MinecraftClient.getInstance().getTickDelta(),((PlayerEntity) getter.entity).prevPitch ,((PlayerEntity) getter.entity).getPitch()));
            if(leftArm != null) args.set(4, leftArm);
            if(leftSleeve != null) args.set(5, leftSleeve);
        }

    }

    private void checkForArms(){
        if(handModel!= null) {
            rightArm = handModel.childrenMap.get("right_arm");
            rightSleeve = handModel.childrenMap.get("right_sleeve");
            leftArm = handModel.childrenMap.get("left_arm");
            leftSleeve = handModel.childrenMap.get("left_sleeve");
//            if(EMFData.getInstance().clientGetter != null)
//                handModel.animationGetters = EMFData.getInstance().clientGetter;
        }
    }

    EMFModelPart leftArm = null;
    EMFModelPart leftSleeve = null;
    EMFModelPart rightArm = null;
    EMFModelPart rightSleeve = null;
    EMFGenericEntityEntityModel<PlayerEntity> handModel = null;

    @ModifyArgs(
            method = "renderRightArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;)V")
    )
    private void emf$mixinRightArm(Args args) {
        //4 is sub arm
        //5 is sleeve
//        EMFCustomPlayerModel<?> client = EMFData.getInstance().clientPlayerModel;
//        if(client != null && !EMFData.getInstance().getConfig().useCustomPlayerHandInFPS && client.getThisEMFModel().vanillaModel instanceof PlayerEntityModel<?> player){
//            //return actual vanilla arm
//            args.set(4, player.rightArm);
//            args.set(5, player.rightSleeve);
//        }
        if(!EMFData.getInstance().checkedHand && EMFData.getInstance().clientPlayerModel != null){
            EMFData.getInstance().checkedHand = true;
            handModel = EMFData.getInstance().createEMFModelOnly("player_hand",  EMFData.getInstance().clientPlayerVanillaModel);

            checkForArms();
        }
        if(handModel != null){// && EMFData.getInstance().clientGetter != null){
//            AnimationGetters getter = handModel.animationGetters;
//            handModel.animateModel((PlayerEntity) getter.entity,
//                    ((PlayerEntity) getter.entity).limbAngle,
//                    ((PlayerEntity) getter.entity).limbDistance,
//                    MinecraftClient.getInstance().getTickDelta());
//            handModel.setAngles((PlayerEntity) getter.entity,
//                    ((PlayerEntity) getter.entity).limbAngle,
//                    ((PlayerEntity) getter.entity).limbDistance,
//                    getter.entity.age+ MinecraftClient.getInstance().getTickDelta(),
//                    MathHelper.lerp(MinecraftClient.getInstance().getTickDelta(),((PlayerEntity) getter.entity).prevHeadYaw ,((PlayerEntity) getter.entity).headYaw),
//                    MathHelper.lerp(MinecraftClient.getInstance().getTickDelta(),((PlayerEntity) getter.entity).prevPitch ,((PlayerEntity) getter.entity).getPitch()));
            if(rightArm != null) args.set(4, rightArm);
            if(rightSleeve != null) args.set(5, rightSleeve);
        }
    }
}
