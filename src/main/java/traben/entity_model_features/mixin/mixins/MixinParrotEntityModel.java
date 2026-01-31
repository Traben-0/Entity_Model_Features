package traben.entity_model_features.mixin.mixins;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.utils.EMFEntity;

//#if MC >= 12102
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.client.Minecraft;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

@Mixin(ParrotOnShoulderLayer.class)
public class MixinParrotEntityModel {

    @Unique
    private static final ModelLayerLocation emf$parrot_shoulder =
            new ModelLayerLocation(EMFUtils.res("minecraft", "parrot"), "shoulder");

    @ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/EntityModelSet;bakeLayer(Lnet/minecraft/client/model/geom/ModelLayerLocation;)Lnet/minecraft/client/model/geom/ModelPart;"))
    private ModelPart emf$injectParrotShoulderLayer(ModelPart original) {
        if (EMF.testForForgeLoadingError()) return original;

        return EMFManager.getInstance().injectIntoModelRootGetter(emf$parrot_shoulder, ParrotModel.createBodyLayer().bakeRoot());
    }


    //#if MC >= 12109
    private static final String RENDER_METHOD = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V";
    //#else
    //$$ private static final String RENDER_METHOD = "renderOnShoulder";
    //#endif

    @Inject(method = RENDER_METHOD, at = @At("HEAD"))
    private void emf$parrot1(final CallbackInfo ci) {
        try{
            var entity = (EMFEntity) EntityType.PARROT.create(Minecraft.getInstance().level, EntitySpawnReason.COMMAND);
            if (entity != null)
                EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState) ETFEntityRenderState.forEntity(entity));
        }catch (Exception ignored){
        }
        EMFAnimationEntityContext.setCurrentEntityOnShoulder(true);
    }

    @Inject(method = RENDER_METHOD, at = @At("TAIL"))
    private void emf$parrot2(final CallbackInfo ci) {
        EMFAnimationEntityContext.setCurrentEntityOnShoulder(false);
    }
}

//#else
//$$ @Mixin(ParrotModel.class)
//$$ public class MixinParrotEntityModel {
//$$     @Inject(method = "renderOnShoulder", at = @At("HEAD"))
//$$     private void emf$parrot1(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float limbAngle, float limbDistance, float headYaw, float headPitch, int danceAngle, CallbackInfo ci) {
//$$         EMFAnimationEntityContext.setCurrentEntityOnShoulder(true);
//$$     }
//$$     @Inject(method = "renderOnShoulder", at = @At("TAIL"))
//$$     private void emf$parrot2(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float limbAngle, float limbDistance, float headYaw, float headPitch, int danceAngle, CallbackInfo ci) {
//$$         EMFAnimationEntityContext.setCurrentEntityOnShoulder(false);
//$$     }
//$$ }
//#endif



