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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

import org.drools.guvnor.client.asseteditor.DefaultMultiViewEditorMenuBarCreator;
import org.drools.guvnor.client.asseteditor.MultiViewEditor;
import org.drools.guvnor.client.messages.Constants;

/**
 * Creates a default menu bar with an additional "Cancel" button.
 */
public class RealAssetsMultiViewEditorMenuBarCreator extends DefaultMultiViewEditorMenuBarCreator {

    private Command cancelCommand;
    private Constants constants = GWT.create(Constants.class);

    /**
     * Constructor that takes 1 command as parameters for "Cancel" Button. 
     * If you want to add a command for "Save and Close" button you cold use
     * {@link MultiViewEditor#setCloseCommand(com.google.gwt.user.client.Command)}
     * @param saveCommand
     * @param cancelCommand
     */
    public RealAssetsMultiViewEditorMenuBarCreator(Command cancelCommand) {
        this.cancelCommand = cancelCommand;
    }

    
    
    @Override
    public MenuBar createMenuBar(final MultiViewEditor editor, EventBus eventBus) {
        MenuBar toolbar = super.createMenuBar(editor, eventBus);
        toolbar.addItem(constants.Cancel(),this.cancelCommand);
        return toolbar;
    }

}
