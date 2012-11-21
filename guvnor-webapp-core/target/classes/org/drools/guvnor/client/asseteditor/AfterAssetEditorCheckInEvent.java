package org.drools.guvnor.client.asseteditor;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.drools.guvnor.client.widgets.VersionBrowser;

/**
 * Event fired after an editor performs a check-in of an asset.
 */
public class AfterAssetEditorCheckInEvent extends GwtEvent<AfterAssetEditorCheckInEvent.Handler> {

    public interface Handler extends EventHandler {

        void onRefreshAsset( AfterAssetEditorCheckInEvent afterAssetEditorCheckInEvent );
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    /**
     * The uuid of the asset
     */
    private final String uuid;
    
    /**
     * The editor that performed the check-in. The only known case where
     * this editor is null is for {@link VersionBrowser}. This is because
     * this class is not an instance of GuvnorEditor.
     */
    private final GuvnorEditor editor;

    public AfterAssetEditorCheckInEvent( String uuid, GuvnorEditor editor ) {
        this.uuid = uuid;
        this.editor = editor;
    }
    
    public String getUuid() {
        return uuid;
    }

    public GuvnorEditor getEditor() {
        return editor;
    }
    
    @Override
    public Type<AfterAssetEditorCheckInEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( AfterAssetEditorCheckInEvent.Handler handler ) {
        handler.onRefreshAsset( this );
    }
}
