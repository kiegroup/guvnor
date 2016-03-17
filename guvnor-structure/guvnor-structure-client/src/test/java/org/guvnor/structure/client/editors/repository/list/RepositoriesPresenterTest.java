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

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.client.editors.context.GuvnorStructureContext;
import org.guvnor.structure.client.editors.context.GuvnorStructureContextChangeHandler;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RepositoriesPresenterTest {

    @Mock
    private RepositoriesPresenter presenter;

    @Mock
    private RepositoriesView view;

    @Mock
    private RepositoryService repositoryService;

    private GuvnorStructureContext guvnorStructureContext;

    private Collection<Repository> repositories;

    private Repository r1 = new GitRepository( "r1" );
    private Repository r2 = new GitRepository( "r2" );
    private Repository r3 = new GitRepository( "r3" );
    private Repository r4 = new GitRepository( "r4" );

    private GuvnorStructureContextChangeHandler.HandlerRegistration changeHandlerRegistration;

    @Before
    public void init() {
        repositories = new ArrayList<Repository>();

        repositories.add( r1 );
        repositories.add( r2 );
        repositories.add( r3 );


        this.guvnorStructureContext = spy( new GuvnorStructureContext() {
            @Override
            public void getRepositories( final Callback<Collection<Repository>> callback ) {
                callback.callback( repositories );
            }

            @Override
            public GuvnorStructureContextChangeHandler.HandlerRegistration addGuvnorStructureContextChangeHandler( final GuvnorStructureContextChangeHandler handler ) {
                return changeHandlerRegistration;
            }
        } );

        presenter = new RepositoriesPresenter( view,
                                               guvnorStructureContext,
                                               new CallerMock<RepositoryService>( repositoryService ) );

        when( view.addRepository( any( Repository.class ), anyString() ) ).thenReturn( new RepositoryItemPresenter( null, null ) );
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocationOnMock ) throws Throwable {
                throw new RuntimeException( "Should remove a valid repository item." );
            }
        } ).when( view ).removeIfExists( eq( (RepositoryItemPresenter) null ) );
    }

    @Test
    public void testSetHandlers() throws Exception {
        verify( guvnorStructureContext ).addGuvnorStructureContextChangeHandler( presenter );
    }

    @Test
    public void testRemoveHandlers() throws Exception {
        verify( guvnorStructureContext, never() ).removeHandler( changeHandlerRegistration );
        presenter.shutdown();
        verify( guvnorStructureContext ).removeHandler( changeHandlerRegistration );
    }

    @Test
    public void removeIfExistsTest() {
        when( view.confirmDeleteRepository( r1 ) ).thenReturn( true );
        when( view.confirmDeleteRepository( r3 ) ).thenReturn( false );

        presenter.onStartup();

        presenter.removeRepository( r1 );
        presenter.removeRepository( r3 );

        verify( repositoryService ).removeRepository( "r1" );
        verify( repositoryService, never() ).removeRepository( "r2" );
        verify( repositoryService, never() ).removeRepository( "r3" );
    }

    @Test
    public void addAndRemoveTest() {
        when( view.confirmDeleteRepository( r4 ) ).thenReturn( true );

        presenter.onStartup();

        presenter.onNewRepositoryAdded( r4 );
        presenter.removeRepository( r4 );
        presenter.onRepositoryDeleted( r4 );

        verify( repositoryService ).removeRepository( "r4" );
    }
}
