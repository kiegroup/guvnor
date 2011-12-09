package org.drools.guvnor.client.asseteditor.drools;

import org.drools.guvnor.client.asseteditor.MultiViewEditor;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

public class OryxMultiViewEditorMenuBarCreator extends DefaultOryxMultiViewEditorMenuBarCreator {
    private Constants constants = GWT.create(Constants.class);
    
    private Command closeCommand;
    private final Command beforeSaveCommand;
    private final Command afterSaveCommand;

    /**
     * Constructor that takes 1 command as parameters for "Close" Button. 
     * @param closeCommand
     */
    public OryxMultiViewEditorMenuBarCreator(Command closeCommand, Command beforeSaveCommand, Command afterSaveCommand) {
        this.closeCommand = closeCommand;
        this.beforeSaveCommand = beforeSaveCommand;
        this.afterSaveCommand = afterSaveCommand;
    }

    @Override
    public MenuBar createMenuBar(final MultiViewEditor editor, EventBus eventBus) {
        MenuBar toolbar = super.createMenuBar(editor, eventBus);
        toolbar.addItem(constants.Close(),this.closeCommand);
        return toolbar;
    }

    @Override
    protected Command getAfterSaveCommand() {
        return this.afterSaveCommand;
    }

    @Override
    protected Command getBeforeSaveCommand() {
        return this.beforeSaveCommand;
    }
    
    

}
