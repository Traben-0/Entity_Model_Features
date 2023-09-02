package traben.entity_model_features.utils;

public class OptifineMobNameForFileAndEMFMapId {
    private String fileName;
    private String mapId;


    @Override
    public String toString() {
        return fileName;
    }

    public OptifineMobNameForFileAndEMFMapId(String both) {
        this.fileName = both;
        this.mapId = both;
    }

    public void setBoth(String both) {
        this.fileName = both;
        this.mapId = both;
    }

    public void setBoth(String fileName, String mapId) {
        this.fileName = fileName;
        this.mapId = mapId;
    }

    public String getfileName() {
        return this.fileName;
    }

    @SuppressWarnings("unused")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMapId() {
        return this.mapId;
    }

    @SuppressWarnings("unused")
    public void setMapId(String mapId) {
        this.mapId = mapId;
    }
}
