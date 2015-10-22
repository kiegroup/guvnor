/*
 * Copyright 2015 JBoss Inc
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

package org.guvnor.structure.client.editors.repository.create;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.events.AfterCreateOrganizationalUnitEvent;
import org.guvnor.structure.events.AfterDeleteOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class CreateRepositoryFormTest {

    private RepositoryService mockRepositoryService = mock( RepositoryService.class );
    private Caller<RepositoryService> repositoryService = new CallerMock<RepositoryService>( mockRepositoryService );

    private OrganizationalUnitService mockOUService = mock( OrganizationalUnitService.class );
    private Caller<OrganizationalUnitService> ouService = new CallerMock<OrganizationalUnitService>( mockOUService );
    private PlaceManager placeManager = mock( PlaceManager.class );

    private CreateRepositoryForm presenter;

    @Before
    public void setup() {
        presenter = new CreateRepositoryForm( repositoryService,
                                              ouService,
                                              placeManager ) {
            @Override
            boolean isOUMandatory() {
                //Override as we cannot mock IOC.getBeanManager()
                return false;
            }
        };
        presenter.init();
    }

    @Test
    public void testCreateOUEvent() {
        final OrganizationalUnit ou = new OrganizationalUnitImpl( "ou1",
                                                                  "owner1",
                                                                  "ou" );

        final CreateRepositoryForm spy = spy( presenter );
        spy.onCreateOrganizationalUnit( new AfterCreateOrganizationalUnitEvent( ou ) );

        verify( spy,
                times( 1 ) ).addOrganizationalUnit( ou );
    }

    @Test
    public void testDeleteOUEvent() {
        final OrganizationalUnit ou = new OrganizationalUnitImpl( "ou1",
                                                                  "owner1",
                                                                  "ou" );

        final CreateRepositoryForm spy = spy( presenter );
        spy.onDeleteOrganizationalUnit( new AfterDeleteOrganizationalUnitEvent( ou ) );

        verify( spy,
                times( 1 ) ).deleteOrganizationalUnit( ou );
    }

}
