/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.organizationalunit.manager.client.editor;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.organizationalunit.manager.client.editor.popups.AddOrganizationalUnitPopup;
import org.guvnor.organizationalunit.manager.client.editor.popups.EditOrganizationalUnitPopup;
import org.guvnor.structure.events.AfterCreateOrganizationalUnitEvent;
import org.guvnor.structure.events.AfterDeleteOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class OrganizationalUnitManagerPresenterTest {

    @GwtMock
    private AddOrganizationalUnitPopup addOrganizationalUnitPopup;

    @GwtMock
    private EditOrganizationalUnitPopup editOrganizationalUnitPopup;

    @Mock
    private EventSourceMock<AfterCreateOrganizationalUnitEvent> createOUEvent;

    @Mock
    private EventSourceMock<AfterDeleteOrganizationalUnitEvent> deleteOUEvent;

    private OrganizationalUnitManagerView view = mock( OrganizationalUnitManagerView.class );

    private OrganizationalUnitService mockOUService = mock( OrganizationalUnitService.class );
    private Caller<OrganizationalUnitService> organizationalUnitService = new CallerMock<OrganizationalUnitService>( mockOUService );

    private RepositoryService mockRepositoryService = mock( RepositoryService.class );
    private Caller<RepositoryService> repositoryService = new CallerMock<RepositoryService>( mockRepositoryService );

    private OrganizationalUnitManagerPresenter presenter;

    private OrganizationalUnit mockOU = mock( OrganizationalUnit.class );

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        presenter = new OrganizationalUnitManagerPresenterImpl( view,
                                                                organizationalUnitService,
                                                                repositoryService,
                                                                addOrganizationalUnitPopup,
                                                                editOrganizationalUnitPopup,
                                                                createOUEvent,
                                                                deleteOUEvent );

        when( mockOU.getName() ).thenReturn( "mock" );
        when( mockOU.getOwner() ).thenReturn( "mock" );
        when( mockOU.getDefaultGroupId() ).thenReturn( "mock" );

        when( mockOUService.getOrganizationalUnits() ).thenReturn( new ArrayList<OrganizationalUnit>() );

        when( mockOUService.createOrganizationalUnit( any( String.class ),
                                                      any( String.class ),
                                                      any( String.class ),
                                                      any( Collection.class ) ) ).thenReturn( mockOU );

        presenter.loadOrganizationalUnits();
    }

    @Test
    public void testCreateOUEvent() {
        presenter.createNewOrganizationalUnit( mockOU.getName(),
                                               mockOU.getOwner(),
                                               mockOU.getDefaultGroupId() );

        verify( createOUEvent,
                times( 1 ) ).fire( any( AfterCreateOrganizationalUnitEvent.class ) );
    }

    @Test
    public void testDeleteOUEvent() {
        presenter.deleteOrganizationalUnit( mockOU );

        verify( deleteOUEvent,
                times( 1 ) ).fire( any( AfterDeleteOrganizationalUnitEvent.class ) );
    }

}
