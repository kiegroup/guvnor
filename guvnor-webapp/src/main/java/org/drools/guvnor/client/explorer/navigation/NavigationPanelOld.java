/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import org.drools.guvnor.client.common.StackItemHeaderViewImpl;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.util.Util;

/**
 * Navigation panel for the west area.
 */
public class NavigationPanelOld extends Composite {

    private StackLayoutPanel layout = new StackLayoutPanel( Unit.EM );
    private final ClientFactory clientFactory;

    public NavigationPanelOld( ClientFactory clientFactory ) {
        this.clientFactory = clientFactory;
        initWidget( layout );

        addCategoriesPanel();

        addKnowledgeBasesPanel();

        addQAPanel();

        addDeploymentPanel();

        addAdminPanel();
    }

    private void addAdminPanel() {
        if ( UserCapabilities.INSTANCE.hasCapability( Capability.SHOW_ADMIN ) ) {
            addItem( new AdministrationTree() );
        }
    }

    private void addDeploymentPanel() {
        if ( UserCapabilities.INSTANCE.hasCapability(
                Capability.SHOW_DEPLOYMENT,
                Capability.SHOW_DEPLOYMENT_NEW ) ) {

            addItem( new DeploymentTree() );
        }
    }

    private void addQAPanel() {
        if ( UserCapabilities.INSTANCE.hasCapability( Capability.SHOW_QA ) ) {
            addItem( new QATree() );
        }
    }

    private void addKnowledgeBasesPanel() {
//        if ( UserCapabilities.INSTANCE.hasCapability( Capability.SHOW_KNOWLEDGE_BASES_VIEW ) ) {
//            final KnowledgeModulesTreeViewImpl knowledgeModulesTreeItem = new KnowledgeModulesTreeViewImpl( clientFactory );
//
//            addItem( knowledgeModulesTreeItem );
//
//            //lazy loaded to easy startup wait time.
//            DeferredCommand.addCommand( new Command() {
//                public void execute() {
//                    knowledgeModulesTreeItem.loadPackageList();
//                }
//            } );
//        }
    }

    private void addCategoriesPanel() {
//        addItem(new BrowseTree());
    }

    private void addItem( NavigationItem tree ) {
        layout.add( tree.createContent().asWidget(),
                getHeaderHTML( tree.getImage(), tree.getName() ),
                2 );
    }

    public StackItemHeaderViewImpl getHeaderHTML( ImageResource imageResource, String name ) {
        return Util.getHeaderHTML( imageResource,
                name );
    }
}
