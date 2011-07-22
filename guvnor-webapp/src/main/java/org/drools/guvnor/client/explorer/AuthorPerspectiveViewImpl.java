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

package org.drools.guvnor.client.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilder;
import org.drools.guvnor.client.explorer.navigation.NavigationPanelFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationPanelView;
import org.drools.guvnor.client.explorer.navigation.admin.AdminTreeBuilder;
import org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeBuilder;
import org.drools.guvnor.client.explorer.navigation.deployment.DeploymentTreeBuilder;
import org.drools.guvnor.client.explorer.navigation.modules.KnowledgeModulesTreeBuilder;
import org.drools.guvnor.client.explorer.navigation.qa.QATreeBuilder;
import org.drools.guvnor.client.messages.Constants;

import java.util.ArrayList;
import java.util.Collection;

public class AuthorPerspectiveViewImpl extends Composite implements AuthorPerspectiveView {

    private Constants constants = GWT.create( Constants.class );
    private final ClientFactory clientFactory;

    interface AuthorPerspectiveViewImplBinder
            extends
            UiBinder<Widget, AuthorPerspectiveViewImpl> {
    }

    private static AuthorPerspectiveViewImplBinder uiBinder = GWT.create( AuthorPerspectiveViewImplBinder.class );

    private Presenter presenter;

    @UiField(provided = true)
    Widget navigationPanel;

    public AuthorPerspectiveViewImpl(ClientFactory clientFactory, NavigationPanelFactory navigationPanelFactory) {
        this.clientFactory = clientFactory;
        NavigationPanelView view = navigationPanelFactory.createNavigationPanel( getBuilders() ).getView();
        navigationPanel = view.asWidget();
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    private Collection<NavigationItemBuilder> getBuilders() {

        Collection<NavigationItemBuilder> navigationItemBuilders = new ArrayList<NavigationItemBuilder>();

        navigationItemBuilders.add( new BrowseTreeBuilder( clientFactory ) );

        navigationItemBuilders.add( new KnowledgeModulesTreeBuilder( clientFactory ) );

        navigationItemBuilders.add( new QATreeBuilder( clientFactory ) );

        navigationItemBuilders.add( new DeploymentTreeBuilder( clientFactory ) );

        navigationItemBuilders.add( new AdminTreeBuilder( clientFactory ) );

        return navigationItemBuilders;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public String getName() {
        return constants.AuthorPerspective();
    }
}
