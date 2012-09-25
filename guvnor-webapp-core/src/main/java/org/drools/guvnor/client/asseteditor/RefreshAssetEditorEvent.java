package org.drools.guvnor.client.asseteditor;

public class RefreshAssetEditorEvent {

    private final String assetUUID;
    private final String moduleName;

    public RefreshAssetEditorEvent(String moduleName, String uuid) {
        this.moduleName = moduleName;
        this.assetUUID = uuid;
    }

    public String getAssetUUID() {
        return assetUUID;
    }

    public String getModuleName() {
        return moduleName;
    }
}
