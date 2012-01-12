package org.drools.guvnor.client.asseteditor.drools;

import org.drools.guvnor.client.asseteditor.MultiViewEditor;
import org.drools.guvnor.client.asseteditor.MultiViewEditorMenuBarCreator;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import org.drools.guvnor.client.asseteditor.AfterAssetEditorCheckInEvent;

/**
 * Default implementation for Oryx that includes "Save All Changes" button
 */
public class DefaultOryxMultiViewEditorMenuBarCreator implements MultiViewEditorMenuBarCreator {
    private Constants constants = GWT.create(Constants.class);
    private EventBus eventBus;

    public MenuBar createMenuBar(final MultiViewEditor editor,final EventBus eventBus) {
        this.eventBus = eventBus;
        MenuBar toolbar = new MenuBar();

        toolbar.addItem(constants.SaveAllChanges(),
                new Command() {

                    public void execute() {
                        //before save command
                        Command cmd = getBeforeSaveCommand();
                        if (cmd != null){
                            cmd.execute();
                        }
                        
                        //persist the asset
                        editor.checkin(false);
                        
                        //after save command
                        eventBus.addHandler(
                            AfterAssetEditorCheckInEvent.TYPE,
                            new AfterAssetEditorCheckInEvent.Handler() {
                                public void onRefreshAsset(AfterAssetEditorCheckInEvent afterAssetEditorCheckInEvent) {
                                    if (editor == afterAssetEditorCheckInEvent.getEditor()) {
                                        Command cmd = getAfterSaveCommand();
                                        if (cmd != null){
                                            cmd.execute();
                                        }
                                    }
                                }
                        });
                        
                    }
                });
        return toolbar;
    }
    
    /**
     * Method returning the command to be executed before the asset is persisted.
     * This implementation returns null, meaning that no command needs to be
     * executed.
     * Subclasses of this class can overwrite  this method to add a custom command.
     * @return 
     */
    protected Command getBeforeSaveCommand(){
        return null;
    }
    
    /**
     * Method returning the command to be executed after the asset is persisted.
     * This implementation returns null, meaning that no command needs to be
     * executed.
     * Subclasses of this class can overwrite  this method to add a custom command.
     * @return 
     */
    protected Command getAfterSaveCommand(){
        return null;
    }
}
