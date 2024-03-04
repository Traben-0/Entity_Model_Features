package traben.entity_model_features.utils;

import java.util.Objects;

public class OptifineMobNameForFileAndEMFMapId {

    private String fileName;
    private String mapId;

    public OptifineMobNameForFileAndEMFMapId(String both) {
        this.fileName = both;
        this.mapId = null;
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

    public String getMapId() {
        return mapId == null ? fileName : mapId;
    }


}
