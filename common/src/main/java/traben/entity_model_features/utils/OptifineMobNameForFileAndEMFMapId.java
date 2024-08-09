package traben.entity_model_features.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

public class OptifineMobNameForFileAndEMFMapId implements Comparable<OptifineMobNameForFileAndEMFMapId> {

    String namespace = "minecraft";

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    public void setMapId(final String mapId) {
        this.mapId = mapId;
    }

    private String mapId;
    private String secondaryFileName = null;
    private String secondaryNamespace = null;

    public OptifineMobNameForFileAndEMFMapId(String both) {
        this(both, null);
    }

    private OptifineMobNameForFileAndEMFMapId(String both, String mapId) {
        this.fileName = both;
        this.mapId = mapId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptifineMobNameForFileAndEMFMapId that = (OptifineMobNameForFileAndEMFMapId) o;
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

    public @Nullable OptifineMobNameForFileAndEMFMapId getSecondaryModel() {
        if (secondaryFileName != null) {
            var second = new OptifineMobNameForFileAndEMFMapId(secondaryFileName, getMapId());
            second.namespace = secondaryNamespace == null ? namespace : secondaryNamespace;
            return second;
        }
        return null;
    }

    public void pushCurrentToSecondaryAndAssertNewPrimary(String namespace, String fileName) {
        this.secondaryFileName = this.fileName;
        this.secondaryNamespace = this.namespace;
        this.fileName = fileName;
        this.namespace = namespace;
//        this.mapId = mapId;
    }

    public void setSecondaryFileName(String namespace, String fileName) {
        this.secondaryFileName = fileName;
        this.secondaryNamespace = namespace;
    }

    public void setMapIdAndSecondaryFileName(String both) {
        setMapIdAndSecondaryFileName(both, both);
    }

    public void setMapIdAndSecondaryFileName(String mapId, String fileName) {
        this.mapId = mapId;
        setSecondaryFileName(namespace, fileName);
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
        }else if (fileName.endsWith("_inner_armor")){
            //push the armor override jem name to the main filename
            secondaryFileName = "inner_armor";

        }else if (fileName.endsWith("_outer_armor")){
            //push the armor override jem name to the main filename
            secondaryFileName = "outer_armor";

        }
    }



    private void assertNamespaceAndCreateDeprecatedModdedFileName(final String namespace, final String fileName) {
        this.namespace = namespace;
        this.fileName = fileName;
        //recreate old modded directory method for back compatibility
        this.secondaryNamespace = "minecraft";
        this.secondaryFileName = "modded/" + namespace + "/" + fileName;
    }

    public String getMapId() {
        String namespace = getNamespace().equals("minecraft") ? "" : getNamespace() + ":";
        return namespace + (mapId == null ? fileName : mapId);
    }


    @Override
    public int compareTo(@NotNull final OptifineMobNameForFileAndEMFMapId o) {
        return this.getfileName().compareTo(o.getfileName());
    }

    public String getDisplayFileName() {
        if (fileName.startsWith("modded/")) return fileName + ".jem";
        return "assets/" + namespace + "/optifine/cem/" + fileName + ".jem";
    }
}
