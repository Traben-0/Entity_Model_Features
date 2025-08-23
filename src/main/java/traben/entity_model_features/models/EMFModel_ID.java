package traben.entity_model_features.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFException;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class EMFModel_ID implements Comparable<EMFModel_ID> {

    public String namespace = "minecraft";
    private String fileName;
    private String mapId;

    private final List<FallbackModel> fallBackModels;

    private record FallbackModel(String namespace, String fileName){}

    public EMFModel_ID(String both) {
        this(both, null);
    }
    public EMFModel_ID(String both,String mapId) {
        this(both, mapId, new ArrayList<>());
    }

    private EMFModel_ID(String both, String mapId, List<FallbackModel> fallBackModels) {
        this.fileName = both;
        this.mapId = mapId;
        this.fallBackModels = fallBackModels;
    }

    public EMFModel_ID setFileName(final String fileName) throws EMFException {
        if (fileName.contains(":")) {
            var split = fileName.split(":");
            if (split.length == 2) {
                this.namespace = split[0];
                this.fileName = split[1];
            } else {
                ETFUtils2.logError("Invalid file name: " + fileName);
                throw new EMFException("Invalid model file name: " + fileName);
            }
        } else {
            this.fileName = fileName;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EMFModel_ID that = (EMFModel_ID) o;
        return fileName.equals(that.fileName) && Objects.equals(mapId, that.mapId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, mapId);
    }

    @Override
    public String toString() {
        return fileName;
    }

    public EMFModel_ID setBoth(String both) {
        this.fileName = both;
        this.mapId = null;
        return this;
    }

    public boolean areBothSame() {
        return mapId == null || fileName.equals(mapId);
    }

    public EMFModel_ID setBoth(String fileName, String mapId) {
        this.fileName = fileName;
        this.mapId = mapId;
        return this;
    }

    public String getfileName() {
        return this.fileName;
    }

    public boolean hasFallbackModels() {
        return !fallBackModels.isEmpty();
    }

    public void forEachFallback(Consumer<EMFModel_ID> action){
        var fallbackModel = this;
        while (fallbackModel.hasFallbackModels()) {
            fallbackModel = fallbackModel.getNextFallbackModel();
            if (fallbackModel == null) return;
            action.accept(fallbackModel);
        }
    }

    public @Nullable EMFModel_ID getNextFallbackModel() {
        if (hasFallbackModels()) {
            //noinspection SequencedCollectionMethodCanBeUsed
            var next = fallBackModels.get(0);

            var second = new EMFModel_ID(next.fileName, getMapId(), fallBackModels.subList(1, fallBackModels.size()));
            second.namespace = next.namespace == null ? namespace : next.namespace;
            return second;
        }
        return null;
    }

    public EMFModel_ID addFallbackModel(String namespace, String fileName) throws EMFException {
        if (fileName.contains(":")) {
            var split = fileName.split(":");
            if (split.length == 2) {
                namespace = split[0];
                fileName = split[1];
            } else {
                throw new EMFException("Invalid fallback model file name: " + fileName);
            }
        }
        fallBackModels.add(new FallbackModel(namespace, fileName));
        return this;
    }

//    public void propagateFallbacksWithoutPrefix(String prefix) {
//        var list = new ArrayList<>(fallBackModels);
//        if (fileName.startsWith(prefix)) {
//            var newFileName = fileName.substring(prefix.length());
//            fallBackModels.add(new FallbackModel(namespace, newFileName));
//        }
//        for (FallbackModel fallbackModel : list) {
//            if (fallbackModel.fileName.startsWith(prefix)) {
//                var newFileName = fallbackModel.fileName.substring(prefix.length());
//                fallBackModels.add(new FallbackModel(fallbackModel.namespace, newFileName));
//            }
//        }
//    }

    @SuppressWarnings("UnusedReturnValue")
    public EMFModel_ID pushNewMainModelAddingOldAsFallback(String fileName) throws EMFException {
        addFallbackModel(this.fileName);
        this.fileName = fileName;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public EMFModel_ID addFallbackModel(String fileName) throws EMFException {
        return addFallbackModel(namespace, fileName);
    }

    public EMFModel_ID setMapIdAndAddFallbackModel(String both) throws EMFException {
        return setMapIdAndAddFallbackModel(both, both);
    }

    public EMFModel_ID setMapIdAndAddFallbackModel(String mapId, String fileName) throws EMFException {
        this.mapId = mapId;
        if (fileName.contains(":")) {
            var split = fileName.split(":");
            if (split.length == 2) {
                addFallbackModel(split[0], split[1]);
            } else {
                addFallbackModel(fileName);
            }
        } else {
            addFallbackModel(fileName);
        }
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public void finishAndPrepAutomatedFallbacks() throws EMFException {

        //validate namespaces which may have been injected earlier by block entity factories
        if (getfileName().contains(":")) {
            var split = fileName.split(":");
            if (split.length == 2) {
                assertNamespaceAndCreateDeprecatedModdedFileName(split[0], split[1]);
            }
        }

        //create old deprecated modded directory as fallback
        if (!"minecraft".equals(namespace)) {
            assertNamespaceAndCreateDeprecatedModdedFileName(namespace, fileName);
        }

        if(fileName.endsWith("_armor")) {
            //armor fallbacks
            if (fileName.endsWith("_baby_inner_armor")) {
                addFallbackModel("baby_inner_armor");
            } else if (fileName.endsWith("_baby_outer_armor")) {
                addFallbackModel("baby_outer_armor");
            }

            //allow baby armor models to also fallback to the main ones
            if (fileName.endsWith("_inner_armor")) {
                addFallbackModel("inner_armor");
            } else if (fileName.endsWith("_outer_armor")) {
                addFallbackModel("outer_armor");
            }
        }
        //just to be safe
        fallBackModels.removeIf(next -> next.fileName.isBlank());
    }


    private void assertNamespaceAndCreateDeprecatedModdedFileName(final String namespace, final String fileName) throws EMFException {
        this.namespace = namespace;
        this.fileName = fileName;
        //recreate old modded directory method for back compatibility
        addFallbackModel("minecraft", "modded/" + namespace + "/" + fileName);
    }

    public String getMapId() {
        String namespace = getNamespace().equals("minecraft") ? "" : getNamespace() + ":";
        return namespace + (mapId == null ? fileName : mapId);
    }


    @Override
    public int compareTo(@NotNull final EMFModel_ID o) {
        return this.getfileName().compareTo(o.getfileName());
    }

    public String getDisplayFileName() {
        if (fileName.startsWith("modded/")) return fileName + ".jem";
        return "assets/" + namespace + "/optifine/cem/" + fileName + ".jem";
    }
}
