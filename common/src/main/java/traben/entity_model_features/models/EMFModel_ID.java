package traben.entity_model_features.models;

import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.utils.EMFUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EMFModel_ID implements Comparable<EMFModel_ID> {

    public String namespace = "minecraft";
    private String fileName;
    private String mapId;

    public void setRootTransformer(final BiConsumer<ModelPart, Boolean> rootTransformer) {
        this.rootTransformer = rootTransformer;
    }

    private BiConsumer<ModelPart, Boolean> rootTransformer = null;

    public void transformFinalRootIfRequired(ModelPart root, boolean printing) {
        if (rootTransformer != null) {
            rootTransformer.accept(root, printing);
            if (printing) EMFUtils.log("Transformed resulting model for " + getDisplayFileName());
        }
    }

//    private String secondaryFileName = null;
//    private String secondaryNamespace = null;

    private final List<FallbackModel> fallBackModels;

    private record FallbackModel(String namespace, String fileName, BiConsumer<ModelPart, Boolean> rootTransformer){}

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

    public void setFileName(final String fileName) {
        if (fileName.contains(":")) {
            var split = fileName.split(":");
            if (split.length == 2) {
                this.namespace = split[0];
                this.fileName = split[1];
            } else {
                this.fileName = fileName;
            }
        } else {
            this.fileName = fileName;
        }
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

    public void setBoth(String both) {
        this.fileName = both;
        this.mapId = null;
    }

    public boolean areBothSame() {
        return mapId == null || fileName.equals(mapId);
    }

    public void setBoth(String fileName, String mapId) {
        this.fileName = fileName;
        this.mapId = mapId;
    }

    public String getfileName() {
        return this.fileName;
    }

    public boolean hasFallbackModels() {
        return !fallBackModels.isEmpty();
    }

    public @Nullable EMFModel_ID getNextFallbackModel() {
        if (hasFallbackModels()) {
            var next = fallBackModels.get(0);

            var second = new EMFModel_ID(next.fileName, getMapId(), fallBackModels.subList(1, fallBackModels.size()));
            second.namespace = next.namespace == null ? namespace : next.namespace;
            second.setRootTransformer(next.rootTransformer);
            return second;
        }
        return null;
    }

    public void addFallbackModel(String namespace, String fileName, BiConsumer<ModelPart, Boolean> rootTransformer) {
        fallBackModels.add(new FallbackModel(namespace, fileName, rootTransformer));
    }

    public void addFallbackModel( String fileName, BiConsumer<ModelPart, Boolean> rootTransformer) {
        addFallbackModel(namespace, fileName, rootTransformer);
    }

    public void addFallbackModel(String namespace, String fileName) {
        addFallbackModel(namespace, fileName, rootTransformer);
    }

    public void addFallbackModel(String fileName) {
        addFallbackModel(namespace, fileName);
    }

    public void setMapIdAndAddFallbackModel(String both) {
        setMapIdAndAddFallbackModel(both, both);
    }

    public void setMapIdAndAddFallbackModel(String mapId, String fileName) {
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
    }

    public String getNamespace() {
        return namespace;
    }

    public void finishAndPrepSecondaries() {

        //validate namespaces which may have been injected earlier by block entity factories
        if (getfileName().contains(":")) {
            var split = fileName.split(":");
            if (split.length == 2) {
                assertNamespaceAndCreateDeprecatedModdedFileName(split[0], split[1]);
            }
        } else if (!"minecraft".equals(namespace)) {
            //create old deprecated modded directory as secondary
            assertNamespaceAndCreateDeprecatedModdedFileName(namespace, fileName);
        } else if (fileName.endsWith("_baby_inner_armor")) {
            addFallbackModel("baby_inner_armor");
        } else if (fileName.endsWith("_baby_outer_armor")) {
            addFallbackModel("baby_outer_armor");
        } else if (fileName.endsWith("_inner_armor")) {
            addFallbackModel("inner_armor");
        } else if (fileName.endsWith("_outer_armor")) {
            addFallbackModel("outer_armor");
        }
    }


    private void assertNamespaceAndCreateDeprecatedModdedFileName(final String namespace, final String fileName) {
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
