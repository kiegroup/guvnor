/*
 * Copyright 2015 JBoss Inc
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

package org.guvnor.asset.management.client.editors.repository.wizard.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.asset.management.client.editors.repository.wizard.CreateRepositoryWizardModel;
import org.guvnor.asset.management.client.editors.repository.wizard.WizardTestUtils;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.OrganizationalUnitServiceCallerMock;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.RepositoryServiceCallerMock;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.commons.data.Pair;

import static org.guvnor.asset.management.client.editors.repository.wizard.WizardTestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class RepositoryInfoPageTest {

    @GwtMock
    RepositoryInfoPageView view;

    OrganizationalUnitService organizationalUnitService = mock( OrganizationalUnitService.class );

    RepositoryService repositoryService = mock( RepositoryService.class );

    List<OrganizationalUnit> organizationalUnits = buildOrganiztionalUnits();

    List<Pair<String, String>> organizationalUnitsInfo = buildOrganiztionalUnitsInfo( organizationalUnits );

    /**
     * Tests that organizational units information is properly loaded when the page is initialized.
     */
    @Test
    public void testPageLoad() {

        RepositoryInfoPage infoPage = new RepositoryInfoPageExtended( view,
                new OrganizationalUnitServiceCallerMock( organizationalUnitService ),
                new RepositoryServiceCallerMock( repositoryService ),
                true,
                new WizardTestUtils.WizardPageStatusChangeEventMock() );

        CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();
        infoPage.setModel( model );

        when( organizationalUnitService.getOrganizationalUnits() ).thenReturn( organizationalUnits );

        infoPage.prepareView();

        verify( view, times( 1 ) ).init( infoPage );
        verify( view ).initOrganizationalUnits( eq( organizationalUnitsInfo ) );

        assertPageComplete( false, infoPage );
    }

    /**
     * Tests that the page reacts properly when the selected organizational unit changes.
     */
    public void testOrganizationalUnitChange() {

        RepositoryInfoPage infoPage = new RepositoryInfoPageExtended( view,
                new OrganizationalUnitServiceCallerMock( organizationalUnitService ),
                new RepositoryServiceCallerMock( repositoryService ),
                true,
                new WizardTestUtils.WizardPageStatusChangeEventMock() );

        CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();
        infoPage.setModel( model );

        when( organizationalUnitService.getOrganizationalUnits() ).thenReturn( organizationalUnits );
        when( view.getOrganizationalUnitName() ).thenReturn( "OrganizationalUnit1" );

        infoPage.prepareView();
        infoPage.onOUChange();

        verify( view, times( 1 ) ).getOrganizationalUnitName();

        assertEquals( organizationalUnits.get( 0 ), model.getOrganizationalUnit() );

        assertPageComplete( false, infoPage );
    }

    /**
     * Tests that the page reacts properly when a valid repository name is entered.
     */
    @Test
    public void testValidRepositoryNameChange( ) {

        RepositoryInfoPage infoPage = new RepositoryInfoPageExtended( view,
                new OrganizationalUnitServiceCallerMock( organizationalUnitService ),
                new RepositoryServiceCallerMock( repositoryService ),
                true,
                new WizardTestUtils.WizardPageStatusChangeEventMock() );

        CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();
        infoPage.setModel( model );

        when( repositoryService.validateRepositoryName( "ValidRepo" ) ).thenReturn( true );
        when( repositoryService.validateRepositoryName( "InvalidRepo" ) ).thenReturn( false );


        when( view.getName() ).thenReturn( "ValidRepo" );
        infoPage.onNameChange();

        verify( view, times( 2 ) ).getName();
        verify( view, times( 1 ) ).clearNameErrorMessage();


        assertEquals( "ValidRepo", model.getRepositoryName() );

        assertPageComplete( false, infoPage );
    }

    /**
     * Tests that the page reacts properly when an invalid repository name is typed.
     */
    @Test
    public void testInvalidRepositoryNameChange( ) {

        RepositoryInfoPage infoPage = new RepositoryInfoPageExtended( view,
                new OrganizationalUnitServiceCallerMock( organizationalUnitService ),
                new RepositoryServiceCallerMock( repositoryService ),
                true,
                new WizardTestUtils.WizardPageStatusChangeEventMock() );

        CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();
        infoPage.setModel( model );

        when( repositoryService.validateRepositoryName( "ValidRepo" ) ).thenReturn( true );
        when( repositoryService.validateRepositoryName( "InvalidRepo" ) ).thenReturn( false );


        when( view.getName() ).thenReturn( "InvalidRepo" );
        infoPage.onNameChange();

        verify( view, times( 2 ) ).getName();
        verify( view, times( 1 ) ).setNameErrorMessage( anyString() );

        assertEquals( "InvalidRepo", model.getRepositoryName() );

        assertPageComplete( false, infoPage );
    }

    /**
     * Tests that the page reacts properly when the managed repository option is checked.
     */
    @Test
    public void testManagedRepositorySelected() {
        testManagedRepositoryChange( true );
    }

    /**
     * Tests that the page reacts properly when the managed repository option is un checked.
     */
    @Test
    public void testUnManagedRepositorySelected() {
        testManagedRepositoryChange( false );
    }

    public void testManagedRepositoryChange( boolean isManaged ) {

        RepositoryInfoPage infoPage = new RepositoryInfoPageExtended( view,
                new OrganizationalUnitServiceCallerMock( organizationalUnitService ),
                new RepositoryServiceCallerMock( repositoryService ),
                true,
                new WizardTestUtils.WizardPageStatusChangeEventMock() );

        CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();
        infoPage.setModel( model );

        when( view.isManagedRepository() ).thenReturn( isManaged );
        infoPage.onManagedRepositoryChange();

        verify( view, times( isManaged ? 4 : 3 ) ).isManagedRepository();

        assertEquals( isManaged, model.isManged() );

        assertPageComplete( false, infoPage );
    }

    /**
     * Test a sequence of steps that will successfully complete the page.
     */
    @Test
    public void testPageCompleted() {

        RepositoryInfoPage infoPage = new RepositoryInfoPageExtended( view,
                new OrganizationalUnitServiceCallerMock( organizationalUnitService ),
                new RepositoryServiceCallerMock( repositoryService ),
                true,
                new WizardTestUtils.WizardPageStatusChangeEventMock() );

        CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();
        infoPage.setModel( model );

        when( organizationalUnitService.getOrganizationalUnits() ).thenReturn( organizationalUnits );
        when( repositoryService.validateRepositoryName( "ValidRepo" ) ).thenReturn( true );
        when( view.getOrganizationalUnitName() ).thenReturn( "OrganizationalUnit1" );
        when( view.getName() ).thenReturn( "ValidRepo" );

        infoPage.prepareView();
        infoPage.onNameChange();
        infoPage.onOUChange();

        assertEquals( organizationalUnits.get( 0 ), model.getOrganizationalUnit() );
        assertEquals( "ValidRepo", model.getRepositoryName() );

        assertPageComplete( true, infoPage );
    }

    public static List<OrganizationalUnit> buildOrganiztionalUnits() {
        List<OrganizationalUnit> organizationalUnits = new ArrayList<OrganizationalUnit>(  );

        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl( "OrganizationalUnit1", "user1", "group1");
        organizationalUnits.add( organizationalUnit );

        organizationalUnit = new OrganizationalUnitImpl( "OrganizationalUnit2", "user2", "group2");
        organizationalUnits.add( organizationalUnit );
        return organizationalUnits;
    }

    public static List<Pair<String, String>> buildOrganiztionalUnitsInfo( Collection<OrganizationalUnit> organizationalUnits ) {
        List<Pair<String, String>> organizationalUnitsInfo = new ArrayList<Pair<String, String>>(  );
        for ( OrganizationalUnit organizationalUnit : organizationalUnits ) {
            organizationalUnitsInfo.add( new Pair( organizationalUnit.getName(), organizationalUnit.getName() ) );
        }
        return organizationalUnitsInfo;
    }


    public static class RepositoryInfoPageExtended extends RepositoryInfoPage {

        private boolean ouMandatory = false;

        public RepositoryInfoPageExtended( RepositoryInfoPageView view,
                Caller<OrganizationalUnitService> organizationalUnitService,
                Caller<RepositoryService> repositoryService,
                boolean ouMandatory,
                WizardPageStatusChangeEventMock event ) {

            super( view, organizationalUnitService, repositoryService );
            this.ouMandatory = ouMandatory;
            super.wizardPageStatusChangeEvent = event;
            //emulates the invocation of the @PostConstruct method.
            super.init();
        }

        @Override
        protected boolean isOUMandatory() {
            return ouMandatory;
        }

    }

}
