package traben.entity_model_features.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import traben.entity_model_features.EMFException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EMFResourceCaching {
    public static final Map<String, Boolean> RESOURCE_EXISTENCE_CACHE = new ConcurrentHashMap<>();
    private static boolean isPopulated = false;

    public static void clearCache() {
        RESOURCE_EXISTENCE_CACHE.clear();
        isPopulated = false;
    }

    private static void populateCacheIfNeeded(ResourceManager resources) {
        if (isPopulated) return;
        scanPrefix(resources, "emf/cem");
        scanPrefix(resources, "optifine/cem");
        isPopulated = true;
    }

    private static void scanPrefix(ResourceManager resources, String prefix) {
        try {
            String pathPrefix = prefix.contains(":") ? prefix.split(":", 2)[1] : prefix;
            var found = resources.listResources(pathPrefix, p -> true);
            for (ResourceLocation loc : found.keySet()) {
                RESOURCE_EXISTENCE_CACHE.put(loc.toString(), Boolean.TRUE);
            }
        } catch (Exception e) {
            EMFException.recordException(e);
        }
    }

    public static boolean resourceExists(ResourceManager resources, ResourceLocation loc) {
        String key = loc.toString();
        Boolean cached = RESOURCE_EXISTENCE_CACHE.get(key);
        if (cached != null) return cached;

        // By the time this method is first called after a reload starts we should be ready to populate the cache with the cem folders
        populateCacheIfNeeded(resources);

        boolean exists = resources.getResource(loc).isPresent();
        RESOURCE_EXISTENCE_CACHE.put(key, exists);
        return exists;
    }
}
