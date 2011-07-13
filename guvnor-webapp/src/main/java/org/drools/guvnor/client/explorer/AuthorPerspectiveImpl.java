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

import org.drools.guvnor.client.explorer.AuthorPerspectiveView.Presenter;
import org.drools.guvnor.client.explorer.navigation.NavigationPanelFactory;

public class AuthorPerspectiveImpl implements Presenter {

    private ClientFactory clientFactory;

    public AuthorPerspectiveImpl( ClientFactory clientFactory ) {
        this.clientFactory = clientFactory;

        NavigationPanelFactory navigationPanelFactory = new NavigationPanelFactory( clientFactory.getNavigationViewFactory() );

        AuthorPerspectiveView authorPerspectiveView = clientFactory.getAuthorPerspectiveView( navigationPanelFactory );
        authorPerspectiveView.setPresenter( this );

//        panel.setWidget( authorPerspectiveView );

    }

}
