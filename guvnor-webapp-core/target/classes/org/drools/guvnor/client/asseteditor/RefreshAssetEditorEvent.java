package org.drools.guvnor.client.asseteditor;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RefreshAssetEditorEvent extends GwtEvent<RefreshAssetEditorEvent.Handler> {

    public interface Handler extends EventHandler {

        void onRefreshAsset( RefreshAssetEditorEvent refreshAssetEditorEvent );
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    private final String assetUUID;
    private final String moduleName;

    public RefreshAssetEditorEvent(String moduleName,  String uuid ) {
        this.moduleName = moduleName;
        this.assetUUID = uuid;
    }

    public String getAssetUUID() {
        return assetUUID;
    }

    public String getModuleName() {
        return moduleName;
    }
    
    @Override
    public Type<RefreshAssetEditorEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( RefreshAssetEditorEvent.Handler handler ) {
        handler.onRefreshAsset( this );
    }
}
