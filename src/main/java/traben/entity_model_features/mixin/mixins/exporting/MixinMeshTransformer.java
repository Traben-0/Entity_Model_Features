package traben.entity_model_features.mixin.mixins.exporting;


import org.spongepowered.asm.mixin.Mixin;
//#if MC >= 12102
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;

@Mixin(value = MeshTransformer.class, priority = 1001)
public interface MixinMeshTransformer {

    @Inject(method = "method_62140",
            at = @At(value = "HEAD"),cancellable = true)
    private static void emf$cancel(final float f, final float g, final MeshDefinition meshDefinition, final CallbackInfoReturnable<MeshDefinition> cir) {
        if (EMF.tempDisableModelModifications){
            cir.setReturnValue(meshDefinition);
        }
    }
}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public class MixinMeshTransformer {}
//#endif
