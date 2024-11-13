package traben.entity_model_features.mixin.rendering.model;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

#if MC > MC_21
import net.minecraft.client.renderer.entity.state.ArmadilloRenderState;
import net.minecraft.client.model.ArmadilloModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import traben.entity_model_features.models.parts.EMFModelPartVanilla;

@Mixin(ArmadilloModel.class)
public abstract class MixinArmadilloModel extends EntityModel<ArmadilloRenderState> {

    @Shadow
    @Final
    private ModelPart body;

    protected MixinArmadilloModel(final ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(method =
            "setupAnim(Lnet/minecraft/client/renderer/entity/state/ArmadilloRenderState;)V"
            , at = @At(value = "TAIL"))
    private void emf$assertLayerFactory(final CallbackInfo ci) {
        if (body instanceof EMFModelPartVanilla emfPart){
            boolean set = !body.skipDraw;
            for (ModelPart sub : emfPart.getAllEMFCustomChildren()) {
                sub.visible = set;
            }
        }
    }

}
#else
@Mixin(value = LivingEntityRenderer.class, priority = 2000)
public abstract class MixinArmadilloModel{}
#endif