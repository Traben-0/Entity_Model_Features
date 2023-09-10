package traben.entity_model_features.mixin.rendering;


import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.utils.EMFManager;

@Mixin(WolfEntityModel.class)
public class MixinWolfEntityModel<T extends WolfEntity> {




    @Inject(method = "setAngles(Lnet/minecraft/entity/passive/WolfEntity;FFFFF)V",
            at = @At(value = "HEAD")
    )
    private void smf$setAngles(T wolfEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
       if(EMFManager.wolfCollarModel != null && !equals(EMFManager.wolfCollarModel)){
            EMFManager.wolfCollarModel.setAngles(wolfEntity, f, g, h, i, j);
        }
    }
    @Inject(method = "animateModel(Lnet/minecraft/entity/passive/WolfEntity;FFF)V",
            at = @At(value = "HEAD")
    )
    private void smf$animateModel(T wolfEntity, float f, float g, float h, CallbackInfo ci) {
        if(EMFManager.wolfCollarModel != null && !equals(EMFManager.wolfCollarModel)){
            EMFManager.wolfCollarModel.animateModel(wolfEntity, f, g, h);
        }
    }

}
