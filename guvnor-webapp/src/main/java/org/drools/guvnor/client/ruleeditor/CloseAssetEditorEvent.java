package org.drools.guvnor.client.ruleeditor;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class CloseAssetEditorEvent extends GwtEvent<CloseAssetEditorEvent.Handler> {

    public interface Handler extends EventHandler {

        void onCloseAssetEditor( CloseAssetEditorEvent closeAssetEditorEvent );
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    private final String uuid;

    public CloseAssetEditorEvent( String uuid ) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public Type<CloseAssetEditorEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( CloseAssetEditorEvent.Handler handler ) {
        handler.onCloseAssetEditor( this );
    }
}
