package traben.entity_model_features.utils;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.Map;

public interface IEMFUnmodifiedLayerRootGetter {
    Map<ModelLayerLocation, LayerDefinition> emf$getUnmodifiedRoots();
}
