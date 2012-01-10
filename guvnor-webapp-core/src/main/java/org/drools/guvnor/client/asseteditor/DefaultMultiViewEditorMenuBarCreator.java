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
package org.drools.guvnor.client.asseteditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

import org.drools.guvnor.client.messages.Constants;

/**
 * Default implementation of EditorMenuBarCreator that includes 2 button:
 * "Save All Changes" and "Save And Close All" 
 */
public class DefaultMultiViewEditorMenuBarCreator implements MultiViewEditorMenuBarCreator {

    private Constants constants = GWT.create(Constants.class);
    private EventBus eventBus;

    public MenuBar createMenuBar(final MultiViewEditor editor, EventBus eventBus) {
        this.eventBus = eventBus;
        
        MenuBar toolbar = new MenuBar();

        toolbar.addItem(constants.SaveAllChanges(),
                new Command() {

                    public void execute() {
                        editor.checkin(false);
                    }
                });
        toolbar.addItem(constants.SaveAndCloseAll(),
                new Command() {

                    public void execute() {
                        editor.checkin(true);
                    }
                });

        return toolbar;
    }
}
