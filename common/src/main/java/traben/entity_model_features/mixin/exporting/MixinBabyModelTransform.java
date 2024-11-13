package traben.entity_model_features.mixin.exporting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;

#if MC > MC_21
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.geom.builders.MeshDefinition;

@Mixin(value = BabyModelTransform.class, priority = 1001)
public class MixinBabyModelTransform {

    @Inject(method = "apply",
            at = @At(value = "HEAD"),cancellable = true)
    private void emf$cancel(final MeshDefinition meshDefinition, final CallbackInfoReturnable<MeshDefinition> cir) {
        if (EMF.tempDisableModelModifications){
            cir.setReturnValue(meshDefinition);
        }
    }
}
#else
import net.minecraft.client.model.geom.EntityModelSet;

@Mixin(value = EntityModelSet.class, priority = 1001)
public class MixinBabyModelTransform {}
#endif
