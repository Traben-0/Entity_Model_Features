package traben.entity_model_features.mixin.renderers;


import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.EMFGenericCustomEntityModel;


@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public MixinPlayerEntityRenderer(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }


    @ModifyArg(method = "renderLeftArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;)V"),
            index = 4)
    private ModelPart emf$mixinLeftArm1(ModelPart arm) {
        modelHands();
        if(handModel != null){
            if(leftArm != null) return leftArm;
            //if(leftSleeve != null) args.set(5, leftSleeve);
        }
        return arm;
    }
    @ModifyArg(method = "renderLeftArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;)V"),
            index = 5)
    private ModelPart emf$mixinLeftArm2(ModelPart arm) {
        modelHands();
        if(handModel != null){
            if(leftSleeve != null) return leftSleeve;
        }
        return arm;
    }

    private void modelHands(){
        if(!EMFData.getInstance().checkedHand && EMFData.getInstance().clientPlayerModel != null){
            EMFData.getInstance().checkedHand = true;
            handModel = EMFData.getInstance().createEMFModelOnly("player_hand",  EMFData.getInstance().clientPlayerVanillaModel);

            checkForArms();
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
    EMFGenericCustomEntityModel<PlayerEntity> handModel = null;

    @ModifyArg(method = "renderLeftArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;)V"),
            index = 4)
    private ModelPart emf$mixinRightArm1(ModelPart arm) {
        modelHands();
        if(handModel != null){
            if(rightArm != null) return rightArm;
        }
        return arm;
    }
    @ModifyArg(method = "renderLeftArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;)V"),
            index = 5)
    private ModelPart emf$mixinRightArm2(ModelPart arm) {
        modelHands();
        if(handModel != null){
            if(rightSleeve != null) return rightSleeve;
        }
        return arm;
    }
}
