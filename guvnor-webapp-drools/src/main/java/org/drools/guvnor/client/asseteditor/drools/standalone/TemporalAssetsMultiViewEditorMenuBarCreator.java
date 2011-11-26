/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.asseteditor.drools.standalone;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

import org.drools.guvnor.client.asseteditor.MultiViewEditor;
import org.drools.guvnor.client.asseteditor.MultiViewEditorMenuBarCreator;
import org.drools.guvnor.client.messages.Constants;

/**
 * Creates a menu bar with 2 buttons: "Done" and "Cancel".
 */
public class TemporalAssetsMultiViewEditorMenuBarCreator implements MultiViewEditorMenuBarCreator {

    private Command doneCommand;
    private Command cancelCommand;
    private Constants constants = GWT.create(Constants.class);

    public TemporalAssetsMultiViewEditorMenuBarCreator(Command doneCommand, Command cancelCommand) {
        this.doneCommand = doneCommand;
        this.cancelCommand = cancelCommand;
    }

    /**
     * Creates a menu bar with 2 buttons: "Done" and "Cancel". Because this is 
     * meant for temporal rules, this toolbar doesn't check-in any asset. The
     * buttons just call doneCommand and cancelCommand respectively.
     * @param editor
     * @return
     */
    public MenuBar createMenuBar(final MultiViewEditor editor) {
        MenuBar toolbar = new MenuBar();

        toolbar.addItem(constants.Done(), doneCommand);
        toolbar.addItem(constants.Cancel(), cancelCommand);

        return toolbar;
    }

}
