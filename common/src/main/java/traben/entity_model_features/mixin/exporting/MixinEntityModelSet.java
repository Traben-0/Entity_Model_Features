package traben.entity_model_features.mixin.exporting;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.utils.IEMFUnmodifiedLayerRootGetter;

import java.util.Map;

@Mixin(value = EntityModelSet.class, priority = 1001)
public class MixinEntityModelSet implements IEMFUnmodifiedLayerRootGetter {
    #if MC > MC_21
    @Unique
    private Map<ModelLayerLocation, LayerDefinition> emf$unmodifiedRoots = ImmutableMap.of();

    @Inject(method = "onResourceManagerReload",
            at = @At(value = "TAIL"))
    private void emf$unModifiedRoots(final CallbackInfo ci) {
        EMF.tempDisableModelModifications = true;
        this.emf$unmodifiedRoots = ImmutableMap.copyOf(LayerDefinitions.createRoots());
        EMF.tempDisableModelModifications = false;
    }

    @Override
    public Map<ModelLayerLocation, LayerDefinition> emf$getUnmodifiedRoots() {
        return emf$unmodifiedRoots;
    }

    #else
    @Shadow
    public Map<ModelLayerLocation, LayerDefinition> roots;

    @Override
    public Map<ModelLayerLocation, LayerDefinition> emf$getUnmodifiedRoots() {
        return roots;
    }
    #endif
}