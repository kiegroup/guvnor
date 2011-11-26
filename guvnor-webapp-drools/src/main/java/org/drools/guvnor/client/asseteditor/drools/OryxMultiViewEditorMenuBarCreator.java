package org.drools.guvnor.client.asseteditor.drools;

import org.drools.guvnor.client.asseteditor.MultiViewEditor;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

public class OryxMultiViewEditorMenuBarCreator extends DefaultOryxMultiViewEditorMenuBarCreator {
    private Command closeCommand;
    private Constants constants = GWT.create(Constants.class);

    /**
     * Constructor that takes 1 command as parameters for "Close" Button. 
     * @param closeCommand
     */
    public OryxMultiViewEditorMenuBarCreator(Command closeCommand) {
        this.closeCommand = closeCommand;
    }

    @Override
    public MenuBar createMenuBar(final MultiViewEditor editor) {
        MenuBar toolbar = super.createMenuBar(editor);
        toolbar.addItem(constants.Close(),this.closeCommand);
        return toolbar;
    }

}
