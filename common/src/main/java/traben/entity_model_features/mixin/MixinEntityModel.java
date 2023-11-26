package traben.entity_model_features.mixin;


import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.utils.EMFManager;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

@Mixin(Model.class)
public class MixinEntityModel implements IEMFModel {
    @Unique
    private EMFModelPartRoot emf$thisEMFModelRoot = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$discoverEMFModel(Function<?, ?> layerFactory, CallbackInfo ci) {
        emf$thisEMFModelRoot = EMFManager.lastCreatedRootModelPart;
        EMFManager.lastCreatedRootModelPart = null;

    }

    @Unique
    private static int entity_model_features$printModelPart(ModelPart part, int index, boolean hidePrint) {
        if (part.visible) {
            for(int i = 0; i < ((ModelPartAccessor)part).getCuboids().size(); ++i) {
                ++index;
            }

            ModelPart child;
            for(Iterator<Map.Entry<String, ModelPart>> var6 = ((ModelPartAccessor)part).getChildren().entrySet().iterator(); var6.hasNext(); index = entity_model_features$printModelPart(child, index, hidePrint)) {
                Map.Entry<String, ModelPart> entry = var6.next();
                if (!hidePrint) {
                    PrintStream var10000 = System.out;
                    String var10001 = entry.getKey();
                    var10000.println(var10001 + ": " + index);
                }

                child = entry.getValue();
            }
        }

        return index;
    }

    @Override
    public boolean emf$isEMFModel() {
        return emf$thisEMFModelRoot != null;
    }

    @Override
    public EMFModelPartRoot emf$getEMFRootModel() {
        return emf$thisEMFModelRoot;
    }
}
