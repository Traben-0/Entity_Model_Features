package traben.entity_model_features.mixin.renderers;


import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.vanilla_model_children.EMFCustomPlayerModel;


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
