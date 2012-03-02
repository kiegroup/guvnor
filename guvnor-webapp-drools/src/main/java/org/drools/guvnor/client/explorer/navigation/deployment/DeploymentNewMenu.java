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

package org.drools.guvnor.client.explorer.navigation.deployment;

import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilderOld;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SnapshotView;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.guvnor.client.util.Util;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class DeploymentNewMenu {

    public static MenuBar getMenu(final NavigationItemBuilderOld manager) {

        MenuBar createNewMenu = new MenuBar( true );

        createNewMenu.addItem( Util.getHeader( DroolsGuvnorImages.INSTANCE.snapshotSmall(),
                               Constants.INSTANCE.NewDeploymentSnapshot() ).asString(),
                               true,
                               new Command() {
                                   public void execute() {
                                       SnapshotView.showNewSnapshot( new Command() {
                                           public void execute() {
                                               //we have to refresh the whole tree when a snapshot was added to previously empty package.
                                               manager.refreshTree();
                                           }
                                       } );
                                   }
                               } );

        createNewMenu.addItem( Util.getHeader( DroolsGuvnorImages.INSTANCE.refresh(),
                                               Constants.INSTANCE.RebuildAllSnapshotBinaries() ).asString(),
                               true,
                               new Command() {
                                   public void execute() {
                                       SnapshotView.rebuildBinaries();
                                   }
                               } );

        MenuBar rootMenuBar = new MenuBar( true );
        rootMenuBar.setAutoOpen( true );
        rootMenuBar.setAnimationEnabled( true );

        rootMenuBar.addItem( new MenuItem( Constants.INSTANCE.CreateNew(),
                                           createNewMenu ) );

        return rootMenuBar;
    }

}
