package traben.entity_model_features.mixin.rendering.model;


import net.minecraft.client.model.WolfModel;
#if MC > MC_21
import net.minecraft.client.renderer.entity.state.WolfRenderState;
#endif
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.utils.IEMFWolfCollarHolder;

@Mixin(WolfModel.class)
public class MixinWolfEntityModel<T extends Wolf> implements #if MC > MC_21 IEMFWolfCollarHolder #else IEMFWolfCollarHolder<T> #endif {


    @Unique
    #if MC > MC_21 WolfModel #else WolfModel<T> #endif emf$collarModel = null;

#if MC > MC_21
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/WolfRenderState;)V", at = @At(value = "HEAD"))
    private void smf$setAngles(final WolfRenderState wolfRenderState, final CallbackInfo ci) {
        if (emf$hasCollarModel()) emf$collarModel.setupAnim(wolfRenderState);
    }
#else
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/animal/Wolf;FFFFF)V", at = @At(value = "HEAD"))
    private void smf$setAngles(T wolfEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (emf$hasCollarModel()) emf$collarModel.setupAnim(wolfEntity, f, g, h, i, j);
    }

    @Inject(method = "prepareMobModel(Lnet/minecraft/world/entity/animal/Wolf;FFF)V", at = @At(value = "HEAD"))
    private void smf$animateModel(T wolfEntity, float f, float g, float h, CallbackInfo ci) {
        if (emf$hasCollarModel()) emf$collarModel.prepareMobModel(wolfEntity, f, g, h);
    }
#endif

    @Override
    public #if MC > MC_21 WolfModel #else WolfModel<T> #endif emf$getCollarModel() {
        return emf$collarModel;
    }

    @Override
    public void emf$setCollarModel(#if MC > MC_21 WolfModel #else WolfModel<T> #endif model) {
        emf$collarModel = model;
    }


}
