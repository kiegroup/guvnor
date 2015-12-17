/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.structure.client.editors.repository.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.repositories.PublicURI;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryInfo;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.RepositoryServiceEditor;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RepositoryEditorPresenterTest {

    private RepositoryEditorPresenter presenter;

    @Mock
    private RepositoryEditorPresenter.View view;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private RepositoryServiceEditor repositoryServiceEditor;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private Path root;

    private RepositoryInfo repositoryInfo;
    private List<VersionRecord> repositoryHistory;
    private PlaceRequest place = new DefaultPlaceRequest();

    private Caller<RepositoryService> repositoryServiceCaller;
    private Caller<RepositoryServiceEditor> repositoryServiceEditorCaller;

    @Before
    public void before() {
        repositoryServiceCaller = new CallerMock<RepositoryService>( repositoryService );
        repositoryServiceEditorCaller = new CallerMock<RepositoryServiceEditor>( repositoryServiceEditor );
        presenter = new RepositoryEditorPresenter( view,
                                                   repositoryServiceCaller,
                                                   repositoryServiceEditorCaller,
                                                   placeManager );

        repositoryInfo = new RepositoryInfo( "repository",
                                             "owner",
                                             root,
                                             new ArrayList<PublicURI>(),
                                             new ArrayList<VersionRecord>() );
        repositoryHistory = Collections.EMPTY_LIST;

        when( repositoryService.getRepositoryInfo( any( String.class ) ) ).thenReturn( repositoryInfo );
        when( repositoryService.getRepositoryHistory( any( String.class ),
                                                      any( Integer.class ) ) ).thenReturn( repositoryHistory );
        when( repositoryServiceEditor.revertHistory( any( String.class ),
                                                     eq( root ),
                                                     any( String.class ),
                                                     any( VersionRecord.class ) ) ).thenReturn( repositoryHistory );

        //Each test needs the Presenter to be initialised
        place.addParameter( "alias",
                            "repository" );
        presenter.onStartup( place );
    }

    @Test
    public void testOnStartup() {
        verify( repositoryService,
                times( 1 ) ).getRepositoryInfo( eq( "repository" ) );

        verify( view,
                times( 1 ) ).setRepositoryInfo( eq( repositoryInfo.getAlias() ),
                                                eq( repositoryInfo.getOwner() ),
                                                eq( repositoryInfo.getPublicURIs() ),
                                                eq( CoreConstants.INSTANCE.Empty() ),
                                                eq( repositoryInfo.getInitialVersionList() ) );
    }

    @Test
    public void testLoadMoreHistory() {
        presenter.onLoadMoreHistory( 0 );

        verify( repositoryService,
                times( 1 ) ).getRepositoryHistory( eq( "repository" ),
                                                   eq( 0 ) );

        verify( view,
                times( 1 ) ).addHistory( eq( repositoryHistory ) );
    }

    @Test
    public void testRevertNoCommitMessage() {
        final VersionRecord vr = mock( VersionRecord.class );
        presenter.onRevert( vr );

        verify( repositoryServiceEditor,
                times( 1 ) ).revertHistory( eq( "repository" ),
                                            eq( root ),
                                            isNull( String.class ),
                                            eq( vr ) );

        verify( view,
                times( 1 ) ).reloadHistory( eq( repositoryHistory ) );
    }

    @Test
    public void testRevertWithCommitMessage() {
        final VersionRecord vr = mock( VersionRecord.class );
        presenter.onRevert( vr,
                            "comment" );

        verify( repositoryServiceEditor,
                times( 1 ) ).revertHistory( eq( "repository" ),
                                            eq( root ),
                                            eq( "comment" ),
                                            eq( vr ) );

        verify( view,
                times( 1 ) ).reloadHistory( eq( repositoryHistory ) );
    }

    @Test
    public void testRepositoryRemovedEvent() {
        final RepositoryRemovedEvent event = mock( RepositoryRemovedEvent.class );
        final Repository repository = mock( Repository.class );
        when( repository.getAlias() ).thenReturn( "repository" );
        when( event.getRepository() ).thenReturn( repository );

        presenter.onRepositoryRemovedEvent( event );

        verify( placeManager,
                times( 1 ) ).closePlace( eq( place ) );
    }

}
