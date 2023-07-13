package traben.entity_model_features.mixin.rendering;

import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SheepWoolEntityModel.class)
public class MixinSheepWoolEntityModel<T extends SheepEntity> {

//    @Inject(method = "setAngles(Lnet/minecraft/entity/passive/SheepEntity;FFFFF)V", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
//    private void emf$setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
//        //todo no texture support cause null vertex lol
//        EMFManager.getInstance().preRenderEMFActions(entity, null, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
//    }
}
