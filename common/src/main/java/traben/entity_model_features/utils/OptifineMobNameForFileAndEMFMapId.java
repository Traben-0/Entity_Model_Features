package traben.entity_model_features.utils;

public class OptifineMobNameForFileAndEMFMapId {
    private String fileName;
    private String mapId;


    public OptifineMobNameForFileAndEMFMapId(String both) {
        this.fileName = both;
        this.mapId = null;
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
