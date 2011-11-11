package org.drools.guvnor.client.asseteditor.drools;

import org.drools.guvnor.client.asseteditor.MultiViewEditor;
import org.drools.guvnor.client.asseteditor.MultiViewEditorMenuBarCreator;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * Default implementation for Oryx that includes "Save All Changes" button
 */
public class DefaultOryxMultiViewEditorMenuBarCreator implements MultiViewEditorMenuBarCreator {
    private Constants constants = GWT.create(Constants.class);

    public MenuBar createMenuBar(final MultiViewEditor editor) {
        MenuBar toolbar = new MenuBar();

        toolbar.addItem(constants.SaveAllChanges(),
                new Command() {

                    public void execute() {
                        editor.checkin(false);
                    }
                });
        return toolbar;
    }
}
