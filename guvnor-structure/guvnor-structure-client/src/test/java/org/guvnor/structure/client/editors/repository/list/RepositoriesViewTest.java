/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.client.editors.repository.list;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RepositoriesViewTest {

    @Mock
    private RepositoriesView view;

    @Before
    public void init() {
        doCallRealMethod().when( view ).removeIfExists( any( Repository.class ) );
    }

    @Test
    public void removeIfExistsTest() {
        view.repositoryToWidgetMap = new HashMap<Repository, Widget>();
        view.panel = GWT.create( FlowPanel.class );

        Repository r1 = new GitRepository( "r1" );
        Repository r2 = new GitRepository( "r2" );
        Repository r3 = new GitRepository( "r3" );

        final RepositoriesViewItem i1 = new RepositoriesViewItem( r1.getAlias(), null, null, null, null, null, null, null );
        final RepositoriesViewItem i2 = new RepositoriesViewItem( r1.getAlias(), null, null, null, null, null, null, null );

        view.repositoryToWidgetMap.put( r1, i1 );
        view.repositoryToWidgetMap.put( r2, i2 );

        view.removeIfExists( r1 );
        view.removeIfExists( r3 );

        verify( view.panel ).remove( i1 );
        verify( view.panel, times( 1 ) ).remove( any( RepositoriesViewItem.class ) );
    }
}
