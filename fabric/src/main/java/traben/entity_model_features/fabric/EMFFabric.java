package traben.entity_model_features.fabric;

import net.fabricmc.api.ClientModInitializer;
import traben.entity_model_features.fabriclike.EMFFabricLike;

public class EMFFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EMFFabricLike.init();
    }
}
