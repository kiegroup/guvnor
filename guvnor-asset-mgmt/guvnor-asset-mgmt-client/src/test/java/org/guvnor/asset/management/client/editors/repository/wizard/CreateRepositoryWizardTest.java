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

package org.guvnor.asset.management.client.editors.repository.wizard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.asset.management.client.editors.repository.wizard.pages.RepositoryInfoPage;
import org.guvnor.asset.management.client.editors.repository.wizard.pages.RepositoryInfoPageTest;
import org.guvnor.asset.management.client.editors.repository.wizard.pages.RepositoryInfoPageView;
import org.guvnor.asset.management.client.editors.repository.wizard.pages.RepositoryStructurePage;
import org.guvnor.asset.management.client.editors.repository.wizard.pages.RepositoryStructurePageTest;
import org.guvnor.asset.management.client.editors.repository.wizard.pages.RepositoryStructurePageView;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

import static org.guvnor.asset.management.security.AssetsMgmtFeatures.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class CreateRepositoryWizardTest {

    private static final String REPOSITORY_NAME = "RepositoryName";

    private static final String ORGANIZATIONAL_UNIT = "OrganizationalUnit1";

    private static final String PROJECT_NAME = "ProjectName";

    private static final String PROJECT_DESCRIPTION = "Project description";

    private static final String GROUP_ID = "GroupId";

    private static final String ARTIFACT_ID = "ArtifactId";

    private static final String VERSION = "Version";

    RepositoryInfoPageTest.RepositoryInfoPageExtended infoPage;

    RepositoryStructurePageTest.RepositoryStructurePageExtended structurePage;

    @GwtMock
    RepositoryInfoPageView infoPageView;

    @GwtMock
    RepositoryStructurePageView structurePageView;

    @GwtMock
    WizardView view;

    CreateRepositoryWizard createRepositoryWizard;

    CreateRepositoryWizardModel model;

    Repository expectedRepository = mock( Repository.class );

    OrganizationalUnitService organizationalUnitService = mock( OrganizationalUnitService.class );

    RepositoryService repositoryService = mock( RepositoryService.class );

    RepositoryStructureService repositoryStructureService = mock( RepositoryStructureService.class );

    ProjectRepositoryResolver repositoryResolverService = mock( ProjectRepositoryResolver.class );

    AssetManagementService assetManagementService = mock( AssetManagementService.class );

    List<OrganizationalUnit> organizationalUnits = RepositoryInfoPageTest.buildOrganiztionalUnits();

    SessionInfo sessionInfo = mock( SessionInfo.class );

    ConflictingRepositoriesPopup conflictingRepositoriesPopup = mock( ConflictingRepositoriesPopup.class );

    KieWorkbenchACL kieACL = mock( KieWorkbenchACL.class );

    User user = mock( User.class );

    WizardTestUtils.NotificationEventMock notificationEvent;

    @Before
    public void init() {

        //mock user roles
        Set<Role> userRoles = new HashSet<Role>();
        userRoles.add( new RoleImpl( "mock-role" ) );

        when( sessionInfo.getIdentity() ).thenReturn( user );
        when( user.getIdentifier() ).thenReturn( "mock-user" );
        when( user.getRoles() ).thenReturn( userRoles );

        //mock the configure repository feature granted roles.
        Set<String> grantedRoles = new HashSet<String>();
        grantedRoles.add( "mock-role" );

        when( kieACL.getGrantedRoles( CONFIGURE_REPOSITORY ) ).thenReturn( grantedRoles );

        WizardTestUtils.WizardPageStatusChangeEventMock event = new WizardTestUtils.WizardPageStatusChangeEventMock();

        when( organizationalUnitService.getOrganizationalUnits() ).thenReturn( organizationalUnits );

        //All tests starts by the initialization of the wizard pages and the wizard itself
        infoPage = new RepositoryInfoPageTest.RepositoryInfoPageExtended( infoPageView,
                                                                          new CallerMock<OrganizationalUnitService>( organizationalUnitService ),
                                                                          new CallerMock<RepositoryService>( repositoryService ),
                                                                          true,
                                                                          event );

        structurePage = new RepositoryStructurePageTest.RepositoryStructurePageExtended( structurePageView,
                                                                                         new CallerMock<RepositoryStructureService>( repositoryStructureService ),
                                                                                         event );

        model = new CreateRepositoryWizardModel();

        notificationEvent = new WizardTestUtils.NotificationEventMock();

        createRepositoryWizard = new CreateRepositoryWizardExtended( infoPage,
                                                                     structurePage,
                                                                     model,
                                                                     new CallerMock<RepositoryService>( repositoryService ),
                                                                     new CallerMock<RepositoryStructureService>( repositoryStructureService ),
                                                                     new CallerMock<ProjectRepositoryResolver>( repositoryResolverService ),
                                                                     new CallerMock<AssetManagementService>( assetManagementService ),
                                                                     notificationEvent,
                                                                     kieACL,
                                                                     sessionInfo,
                                                                     conflictingRepositoriesPopup,
                                                                     view,
                                                                     event );
    }

    /**
     * Emulates a sequence of valid steps for the creation of an unmanaged repository.
     */
    @Test
    public void testUnmanagedRepositoryCompletedTest() {
        setupNameAndOrgUnitMocks( true,
                                  ORGANIZATIONAL_UNIT );
        when( infoPageView.isManagedRepository() ).thenReturn( false );

        createRepositoryWizard.start();

        infoPage.onNameChange();
        infoPage.onOUChange();
        infoPage.onManagedRepositoryChange();

        Map<String, Object> env = new HashMap<String, Object>();
        env.put( EnvironmentParameters.MANAGED,
                 false );

        when( repositoryService.createRepository( organizationalUnits.get( 0 ),
                                                  "git",
                                                  REPOSITORY_NAME,
                                                  env ) ).thenReturn( expectedRepository );

        createRepositoryWizard.complete();

        verify( repositoryResolverService,
                never() ).getRepositoriesResolvingArtifact( any( GAV.class ) );

        verify( repositoryService,
                times( 1 ) ).createRepository( eq( organizationalUnits.get( 0 ) ),
                                               eq( "git" ),
                                               eq( REPOSITORY_NAME ),
                                               eq( env ) );

        //the model should have the UI loaded values.
        assertEquals( REPOSITORY_NAME,
                      model.getRepositoryName() );
        assertEquals( organizationalUnits.get( 0 ),
                      model.getOrganizationalUnit() );
        assertEquals( false,
                      model.isManged() );

        WizardTestUtils.assertWizardComplete( true,
                                              createRepositoryWizard );
    }

    @Test
    public void testManagedRepositoryMultiCompletedNonClashingGAVTest() {
        //user completes the first page
        setupNameAndOrgUnitMocks( true,
                                  ORGANIZATIONAL_UNIT );
        when( infoPageView.isManagedRepository() ).thenReturn( true );

        createRepositoryWizard.start();

        infoPage.onNameChange();
        infoPage.onOUChange();
        infoPage.onManagedRepositoryChange();

        //and goes to the second the second page
        when( structurePageView.getProjectName() ).thenReturn( PROJECT_NAME );
        when( structurePageView.getProjectDescription() ).thenReturn( PROJECT_DESCRIPTION );
        when( structurePageView.getGroupId() ).thenReturn( GROUP_ID );
        when( structurePageView.getArtifactId() ).thenReturn( ARTIFACT_ID );
        when( structurePageView.getVersion() ).thenReturn( VERSION );
        when( structurePageView.isConfigureRepository() ).thenReturn( true );
        when( structurePageView.isMultiModule() ).thenReturn( true );

        //validations are true
        when( repositoryStructureService.isValidProjectName( PROJECT_NAME ) ).thenReturn( true );
        when( repositoryStructureService.isValidGroupId( GROUP_ID ) ).thenReturn( true );
        when( repositoryStructureService.isValidArtifactId( ARTIFACT_ID ) ).thenReturn( true );
        when( repositoryStructureService.isValidVersion( VERSION ) ).thenReturn( true );

        createRepositoryWizard.pageSelected( 1 );

        structurePage.onProjectNameChange();
        structurePage.onProjectDescriptionChange();
        structurePage.onGroupIdChange();
        structurePage.onArtifactIdChange();
        structurePage.onVersionChange();
        structurePage.onConfigureRepositoryChange();
        structurePage.onMultiModuleChange();

        Map<String, Object> env = new HashMap<String, Object>();
        env.put( EnvironmentParameters.MANAGED,
                 true );

        when( repositoryService.createRepository( organizationalUnits.get( 0 ),
                                                  "git",
                                                  REPOSITORY_NAME,
                                                  env ) ).thenReturn( expectedRepository );
        when( expectedRepository.getAlias() ).thenReturn( REPOSITORY_NAME );

        createRepositoryWizard.complete();

        //the repository should be created.
        verify( repositoryService,
                times( 1 ) ).createRepository( eq( organizationalUnits.get( 0 ) ),
                                               eq( "git" ),
                                               eq( REPOSITORY_NAME ),
                                               eq( env ) );

        //when repository was created the next wizard actions is to initialize the structure

        //mock the pom created internally by the wizard to emulate the cascaded initializations.
        Path pathToPom = mock( Path.class );
        POM pom = new POM();
        pom.setName( PROJECT_NAME );
        pom.setDescription( PROJECT_DESCRIPTION );
        pom.getGav().setGroupId( GROUP_ID );
        pom.getGav().setArtifactId( ARTIFACT_ID );
        pom.getGav().setVersion( VERSION );
        final String baseUrl = "";

        when( repositoryStructureService.initRepositoryStructure( eq( pom ),
                                                                  eq( baseUrl ),
                                                                  eq( expectedRepository ),
                                                                  eq( true ),
                                                                  eq( DeploymentMode.VALIDATED ) ) ).thenReturn( pathToPom );

        //the repository should be initialized.
        verify( repositoryStructureService,
                times( 1 ) ).initRepositoryStructure( eq( pom ),
                                                      eq( baseUrl ),
                                                      eq( expectedRepository ),
                                                      eq( true ),
                                                      eq( DeploymentMode.VALIDATED ) );

        //finally the assets management configuration process should be launched
        verify( assetManagementService,
                times( 1 ) ).configureRepository( eq( REPOSITORY_NAME ),
                                                  eq( "master" ),
                                                  eq( "dev" ),
                                                  eq( "release" ),
                                                  eq( VERSION ) );

        //the model should have the UI loaded values.
        assertEquals( REPOSITORY_NAME,
                      model.getRepositoryName() );
        assertEquals( organizationalUnits.get( 0 ),
                      model.getOrganizationalUnit() );
        assertEquals( true,
                      model.isManged() );
        assertEquals( true,
                      model.isMultiModule() );
        assertEquals( true,
                      model.isConfigureRepository() );
        assertEquals( PROJECT_NAME,
                      model.getProjectName() );
        assertEquals( PROJECT_DESCRIPTION,
                      model.getProjectDescription() );
        assertEquals( GROUP_ID,
                      model.getGroupId() );
        assertEquals( ARTIFACT_ID,
                      model.getArtifactId() );
        assertEquals( VERSION,
                      model.getVersion() );

        WizardTestUtils.assertWizardComplete( true,
                                              createRepositoryWizard );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testManagedRepositoryMultiCompletedClashingGAVTest() {
        //user completes the first page
        setupNameAndOrgUnitMocks( true,
                                  ORGANIZATIONAL_UNIT );
        when( infoPageView.isManagedRepository() ).thenReturn( true );

        createRepositoryWizard.start();

        infoPage.onNameChange();
        infoPage.onOUChange();
        infoPage.onManagedRepositoryChange();

        //and goes to the second the second page
        when( structurePageView.getProjectName() ).thenReturn( PROJECT_NAME );
        when( structurePageView.getProjectDescription() ).thenReturn( PROJECT_DESCRIPTION );
        when( structurePageView.getGroupId() ).thenReturn( GROUP_ID );
        when( structurePageView.getArtifactId() ).thenReturn( ARTIFACT_ID );
        when( structurePageView.getVersion() ).thenReturn( VERSION );
        when( structurePageView.isConfigureRepository() ).thenReturn( true );
        when( structurePageView.isMultiModule() ).thenReturn( true );

        //validations are true
        when( repositoryStructureService.isValidProjectName( PROJECT_NAME ) ).thenReturn( true );
        when( repositoryStructureService.isValidGroupId( GROUP_ID ) ).thenReturn( true );
        when( repositoryStructureService.isValidArtifactId( ARTIFACT_ID ) ).thenReturn( true );
        when( repositoryStructureService.isValidVersion( VERSION ) ).thenReturn( true );

        createRepositoryWizard.pageSelected( 1 );

        structurePage.onProjectNameChange();
        structurePage.onProjectDescriptionChange();
        structurePage.onGroupIdChange();
        structurePage.onArtifactIdChange();
        structurePage.onVersionChange();
        structurePage.onConfigureRepositoryChange();
        structurePage.onMultiModuleChange();

        Map<String, Object> env = new HashMap<String, Object>();
        env.put( EnvironmentParameters.MANAGED,
                 true );

        when( repositoryService.createRepository( organizationalUnits.get( 0 ),
                                                  "git",
                                                  REPOSITORY_NAME,
                                                  env ) ).thenReturn( expectedRepository );
        when( expectedRepository.getAlias() ).thenReturn( REPOSITORY_NAME );

        //mock the pom created internally by the wizard to emulate the cascaded initializations.
        POM pom = new POM();
        pom.setName( PROJECT_NAME );
        pom.setDescription( PROJECT_DESCRIPTION );
        pom.getGav().setGroupId( GROUP_ID );
        pom.getGav().setArtifactId( ARTIFACT_ID );
        pom.getGav().setVersion( VERSION );
        final String baseUrl = "";

        final GAVAlreadyExistsException gae = new GAVAlreadyExistsException( pom.getGav(),
                                                                             new HashSet<MavenRepositoryMetadata>() {{
                                                                                 add( new MavenRepositoryMetadata( "local-id",
                                                                                                                   "local-url",
                                                                                                                   MavenRepositorySource.LOCAL ) );
                                                                             }} );
        doThrow( gae ).when( repositoryStructureService ).initRepositoryStructure( eq( pom ),
                                                                                   eq( baseUrl ),
                                                                                   eq( expectedRepository ),
                                                                                   eq( true ),
                                                                                   eq( DeploymentMode.VALIDATED ) );

        createRepositoryWizard.complete();

        //the repository should be created.
        verify( repositoryService,
                times( 1 ) ).createRepository( eq( organizationalUnits.get( 0 ) ),
                                               eq( "git" ),
                                               eq( REPOSITORY_NAME ),
                                               eq( env ) );

        verify( conflictingRepositoriesPopup,
                times( 1 ) ).setContent( eq( pom.getGav() ),
                                         any( Set.class ),
                                         any( Command.class ) );
        verify( conflictingRepositoriesPopup,
                times( 1 ) ).show();

        //when repository was created the next wizard actions is to initialize the structure

        //the repository should be initialized.
        verify( repositoryStructureService,
                times( 1 ) ).initRepositoryStructure( eq( pom ),
                                                      eq( baseUrl ),
                                                      eq( expectedRepository ),
                                                      eq( true ),
                                                      eq( DeploymentMode.VALIDATED ) );

        //the assets management configuration process should NOT be launched
        verify( assetManagementService,
                never() ).configureRepository( eq( REPOSITORY_NAME ),
                                               eq( "master" ),
                                               eq( "dev" ),
                                               eq( "release" ),
                                               eq( VERSION ) );

        WizardTestUtils.assertWizardComplete( true,
                                              createRepositoryWizard );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testManagedRepositoryMultiCompletedClashingGAVForcedTest() {
        //user completes the first page
        setupNameAndOrgUnitMocks( true,
                                  ORGANIZATIONAL_UNIT );
        when( infoPageView.isManagedRepository() ).thenReturn( true );

        createRepositoryWizard.start();

        infoPage.onNameChange();
        infoPage.onOUChange();
        infoPage.onManagedRepositoryChange();

        //and goes to the second the second page
        when( structurePageView.getProjectName() ).thenReturn( PROJECT_NAME );
        when( structurePageView.getProjectDescription() ).thenReturn( PROJECT_DESCRIPTION );
        when( structurePageView.getGroupId() ).thenReturn( GROUP_ID );
        when( structurePageView.getArtifactId() ).thenReturn( ARTIFACT_ID );
        when( structurePageView.getVersion() ).thenReturn( VERSION );
        when( structurePageView.isConfigureRepository() ).thenReturn( true );
        when( structurePageView.isMultiModule() ).thenReturn( true );

        //validations are true
        when( repositoryStructureService.isValidProjectName( PROJECT_NAME ) ).thenReturn( true );
        when( repositoryStructureService.isValidGroupId( GROUP_ID ) ).thenReturn( true );
        when( repositoryStructureService.isValidArtifactId( ARTIFACT_ID ) ).thenReturn( true );
        when( repositoryStructureService.isValidVersion( VERSION ) ).thenReturn( true );

        createRepositoryWizard.pageSelected( 1 );

        structurePage.onProjectNameChange();
        structurePage.onProjectDescriptionChange();
        structurePage.onGroupIdChange();
        structurePage.onArtifactIdChange();
        structurePage.onVersionChange();
        structurePage.onConfigureRepositoryChange();
        structurePage.onMultiModuleChange();

        Map<String, Object> env = new HashMap<String, Object>();
        env.put( EnvironmentParameters.MANAGED,
                 true );

        when( repositoryService.createRepository( organizationalUnits.get( 0 ),
                                                  "git",
                                                  REPOSITORY_NAME,
                                                  env ) ).thenReturn( expectedRepository );
        when( expectedRepository.getAlias() ).thenReturn( REPOSITORY_NAME );

        //mock the pom created internally by the wizard to emulate the cascaded initializations.
        POM pom = new POM();
        pom.setName( PROJECT_NAME );
        pom.setDescription( PROJECT_DESCRIPTION );
        pom.getGav().setGroupId( GROUP_ID );
        pom.getGav().setArtifactId( ARTIFACT_ID );
        pom.getGav().setVersion( VERSION );
        final String baseUrl = "";

        final GAVAlreadyExistsException gae = new GAVAlreadyExistsException( pom.getGav(),
                                                                             new HashSet<MavenRepositoryMetadata>() {{
                                                                                 add( new MavenRepositoryMetadata( "local-id",
                                                                                                                   "local-url",
                                                                                                                   MavenRepositorySource.LOCAL ) );
                                                                             }} );
        doThrow( gae ).when( repositoryStructureService ).initRepositoryStructure( eq( pom ),
                                                                                   eq( baseUrl ),
                                                                                   eq( expectedRepository ),
                                                                                   eq( true ),
                                                                                   eq( DeploymentMode.VALIDATED ) );

        createRepositoryWizard.complete();

        //the repository should be created.
        verify( repositoryService,
                times( 1 ) ).createRepository( eq( organizationalUnits.get( 0 ) ),
                                               eq( "git" ),
                                               eq( REPOSITORY_NAME ),
                                               eq( env ) );

        //when repository was created the next wizard actions is to initialize the structure

        //the repository should be initialized.
        verify( repositoryStructureService,
                times( 1 ) ).initRepositoryStructure( eq( pom ),
                                                      eq( baseUrl ),
                                                      eq( expectedRepository ),
                                                      eq( true ),
                                                      eq( DeploymentMode.VALIDATED ) );

        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass( Command.class );

        verify( conflictingRepositoriesPopup,
                times( 1 ) ).setContent( eq( pom.getGav() ),
                                         any( Set.class ),
                                         commandArgumentCaptor.capture() );
        verify( conflictingRepositoriesPopup,
                times( 1 ) ).show();

        //the assets management configuration process should NOT be launched
        verify( assetManagementService,
                never() ).configureRepository( eq( REPOSITORY_NAME ),
                                               eq( "master" ),
                                               eq( "dev" ),
                                               eq( "release" ),
                                               eq( VERSION ) );

        //Emulate User electing to force save
        assertNotNull( commandArgumentCaptor.getValue() );
        commandArgumentCaptor.getValue().execute();

        verify( repositoryStructureService,
                times( 1 ) ).initRepositoryStructure( eq( pom ),
                                                      eq( baseUrl ),
                                                      eq( expectedRepository ),
                                                      eq( true ),
                                                      eq( DeploymentMode.FORCED ) );

        verify( assetManagementService,
                times( 1 ) ).configureRepository( eq( REPOSITORY_NAME ),
                                                  eq( "master" ),
                                                  eq( "dev" ),
                                                  eq( "release" ),
                                                  eq( VERSION ) );

        WizardTestUtils.assertWizardComplete( true,
                                              createRepositoryWizard );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testManagedRepositoryMultiCompletedClashingGAVBeforeRepositoryInitializationTest() {
        //user completes the first page
        setupNameAndOrgUnitMocks( true,
                                  ORGANIZATIONAL_UNIT );
        when( infoPageView.isManagedRepository() ).thenReturn( true );

        createRepositoryWizard.start();

        infoPage.onNameChange();
        infoPage.onOUChange();
        infoPage.onManagedRepositoryChange();

        //and goes to the second the second page
        when( structurePageView.getProjectName() ).thenReturn( PROJECT_NAME );
        when( structurePageView.getProjectDescription() ).thenReturn( PROJECT_DESCRIPTION );
        when( structurePageView.getGroupId() ).thenReturn( GROUP_ID );
        when( structurePageView.getArtifactId() ).thenReturn( ARTIFACT_ID );
        when( structurePageView.getVersion() ).thenReturn( VERSION );
        when( structurePageView.isConfigureRepository() ).thenReturn( true );
        when( structurePageView.isMultiModule() ).thenReturn( true );

        //validations are true
        when( repositoryStructureService.isValidProjectName( PROJECT_NAME ) ).thenReturn( true );
        when( repositoryStructureService.isValidGroupId( GROUP_ID ) ).thenReturn( true );
        when( repositoryStructureService.isValidArtifactId( ARTIFACT_ID ) ).thenReturn( true );
        when( repositoryStructureService.isValidVersion( VERSION ) ).thenReturn( true );

        createRepositoryWizard.pageSelected( 1 );

        structurePage.onProjectNameChange();
        structurePage.onProjectDescriptionChange();
        structurePage.onGroupIdChange();
        structurePage.onArtifactIdChange();
        structurePage.onVersionChange();
        structurePage.onConfigureRepositoryChange();
        structurePage.onMultiModuleChange();

        Map<String, Object> env = new HashMap<String, Object>();
        env.put( EnvironmentParameters.MANAGED,
                 true );

        when( repositoryService.createRepository( organizationalUnits.get( 0 ),
                                                  "git",
                                                  REPOSITORY_NAME,
                                                  env ) ).thenReturn( expectedRepository );
        when( expectedRepository.getAlias() ).thenReturn( REPOSITORY_NAME );

        //mock the pom created internally by the wizard to emulate the cascaded initializations.
        POM pom = new POM();
        pom.setName( PROJECT_NAME );
        pom.setDescription( PROJECT_DESCRIPTION );
        pom.getGav().setGroupId( GROUP_ID );
        pom.getGav().setArtifactId( ARTIFACT_ID );
        pom.getGav().setVersion( VERSION );
        final String baseUrl = "";

        when( repositoryResolverService.getRepositoriesResolvingArtifact( eq( pom.getGav() ) ) ).thenReturn( new HashSet<MavenRepositoryMetadata>() {{
            add( new MavenRepositoryMetadata( "local-id",
                                              "local-url",
                                              MavenRepositorySource.LOCAL ) );
        }} );

        createRepositoryWizard.complete();

        //the repository should NOT be created.
        verify( repositoryService,
                never() ).createRepository( eq( organizationalUnits.get( 0 ) ),
                                            eq( "git" ),
                                            eq( REPOSITORY_NAME ),
                                            eq( env ) );
        verify( repositoryStructureService,
                never() ).initRepositoryStructure( eq( pom ),
                                                   eq( baseUrl ),
                                                   eq( expectedRepository ),
                                                   eq( true ),
                                                   eq( DeploymentMode.VALIDATED ) );

        //the assets management configuration process should NOT be launched
        verify( assetManagementService,
                never() ).configureRepository( eq( REPOSITORY_NAME ),
                                               eq( "master" ),
                                               eq( "dev" ),
                                               eq( "release" ),
                                               eq( VERSION ) );

        //the Conflicting GAV popup should be shown
        verify( conflictingRepositoriesPopup,
                times( 1 ) ).setContent( eq( pom.getGav() ),
                                         any( Set.class ),
                                         any( Command.class ) );
        verify( conflictingRepositoriesPopup,
                times( 1 ) ).show();

        WizardTestUtils.assertWizardComplete( true,
                                              createRepositoryWizard );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testManagedRepositoryMultiCompletedClashingGAVForcedBeforeRepositoryInitializationTest() {
        //user completes the first page
        setupNameAndOrgUnitMocks( true,
                                  ORGANIZATIONAL_UNIT );
        when( infoPageView.isManagedRepository() ).thenReturn( true );

        createRepositoryWizard.start();

        infoPage.onNameChange();
        infoPage.onOUChange();
        infoPage.onManagedRepositoryChange();

        //and goes to the second the second page
        when( structurePageView.getProjectName() ).thenReturn( PROJECT_NAME );
        when( structurePageView.getProjectDescription() ).thenReturn( PROJECT_DESCRIPTION );
        when( structurePageView.getGroupId() ).thenReturn( GROUP_ID );
        when( structurePageView.getArtifactId() ).thenReturn( ARTIFACT_ID );
        when( structurePageView.getVersion() ).thenReturn( VERSION );
        when( structurePageView.isConfigureRepository() ).thenReturn( true );
        when( structurePageView.isMultiModule() ).thenReturn( true );

        //validations are true
        when( repositoryStructureService.isValidProjectName( PROJECT_NAME ) ).thenReturn( true );
        when( repositoryStructureService.isValidGroupId( GROUP_ID ) ).thenReturn( true );
        when( repositoryStructureService.isValidArtifactId( ARTIFACT_ID ) ).thenReturn( true );
        when( repositoryStructureService.isValidVersion( VERSION ) ).thenReturn( true );

        createRepositoryWizard.pageSelected( 1 );

        structurePage.onProjectNameChange();
        structurePage.onProjectDescriptionChange();
        structurePage.onGroupIdChange();
        structurePage.onArtifactIdChange();
        structurePage.onVersionChange();
        structurePage.onConfigureRepositoryChange();
        structurePage.onMultiModuleChange();

        Map<String, Object> env = new HashMap<String, Object>();
        env.put( EnvironmentParameters.MANAGED,
                 true );

        when( repositoryService.createRepository( organizationalUnits.get( 0 ),
                                                  "git",
                                                  REPOSITORY_NAME,
                                                  env ) ).thenReturn( expectedRepository );
        when( expectedRepository.getAlias() ).thenReturn( REPOSITORY_NAME );

        //mock the pom created internally by the wizard to emulate the cascaded initializations.
        POM pom = new POM();
        pom.setName( PROJECT_NAME );
        pom.setDescription( PROJECT_DESCRIPTION );
        pom.getGav().setGroupId( GROUP_ID );
        pom.getGav().setArtifactId( ARTIFACT_ID );
        pom.getGav().setVersion( VERSION );
        final String baseUrl = "";

        when( repositoryResolverService.getRepositoriesResolvingArtifact( eq( pom.getGav() ) ) ).thenReturn( new HashSet<MavenRepositoryMetadata>() {{
            add( new MavenRepositoryMetadata( "local-id",
                                              "local-url",
                                              MavenRepositorySource.LOCAL ) );
        }} );

        createRepositoryWizard.complete();

        //the repository should NOT be created.
        verify( repositoryService,
                never() ).createRepository( eq( organizationalUnits.get( 0 ) ),
                                            eq( "git" ),
                                            eq( REPOSITORY_NAME ),
                                            eq( env ) );
        verify( repositoryStructureService,
                never() ).initRepositoryStructure( eq( pom ),
                                                   eq( baseUrl ),
                                                   eq( expectedRepository ),
                                                   eq( true ),
                                                   eq( DeploymentMode.VALIDATED ) );

        //the assets management configuration process should NOT be launched
        verify( assetManagementService,
                never() ).configureRepository( eq( REPOSITORY_NAME ),
                                               eq( "master" ),
                                               eq( "dev" ),
                                               eq( "release" ),
                                               eq( VERSION ) );

        //the Conflicting GAV popup should be shown
        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass( Command.class );
        verify( conflictingRepositoriesPopup,
                times( 1 ) ).setContent( eq( pom.getGav() ),
                                         any( Set.class ),
                                         commandArgumentCaptor.capture() );
        verify( conflictingRepositoriesPopup,
                times( 1 ) ).show();

        //Emulate User electing to force save
        assertNotNull( commandArgumentCaptor.getValue() );
        commandArgumentCaptor.getValue().execute();

        //the repository should be created.
        verify( repositoryService,
                times( 1 ) ).createRepository( eq( organizationalUnits.get( 0 ) ),
                                               eq( "git" ),
                                               eq( REPOSITORY_NAME ),
                                               eq( env ) );
        verify( repositoryStructureService,
                times( 1 ) ).initRepositoryStructure( eq( pom ),
                                                      eq( baseUrl ),
                                                      eq( expectedRepository ),
                                                      eq( true ),
                                                      eq( DeploymentMode.FORCED ) );

        //the assets management configuration process should be launched
        verify( assetManagementService,
                times( 1 ) ).configureRepository( eq( REPOSITORY_NAME ),
                                                  eq( "master" ),
                                                  eq( "dev" ),
                                                  eq( "release" ),
                                                  eq( VERSION ) );

        WizardTestUtils.assertWizardComplete( true,
                                              createRepositoryWizard );
    }

    @Test
    public void testCompletionStatusSuccess() {
        setupNameAndOrgUnitMocks( true,
                                  ORGANIZATIONAL_UNIT );

        createRepositoryWizard.start();

        infoPage.onNameChange();
        infoPage.onOUChange();

        verify( view,
                times( 2 ) ).setCompletionStatus( false );
        verify( view,
                times( 2 ) ).setPageCompletionState( 0,
                                                     false );

        verify( view,
                times( 1 ) ).setCompletionStatus( true );
        verify( view,
                times( 1 ) ).setPageCompletionState( 0,
                                                     true );
    }

    @Test
    public void testCompletionInvalidName() {
        setupNameAndOrgUnitMocks( false,
                                  ORGANIZATIONAL_UNIT );

        createRepositoryWizard.start();

        infoPage.onNameChange();
        infoPage.onOUChange();

        verify( view,
                times( 2 ) ).setCompletionStatus( false );
        verify( view,
                times( 2 ) ).setPageCompletionState( 0,
                                                     false );
    }

    @Test
    public void testCompletionNoOrganizationalUnit() {
        setupNameAndOrgUnitMocks( true,
                                  null );

        createRepositoryWizard.start();

        infoPage.onNameChange();
        infoPage.onOUChange();

        verify( view,
                times( 2 ) ).setCompletionStatus( false );
        verify( view,
                times( 2 ) ).setPageCompletionState( 0,
                                                     false );
    }

    @Test
    public void testCompletionInvalidNameAndNoOrganizationalUnit() {
        setupNameAndOrgUnitMocks( false,
                                  null );

        createRepositoryWizard.start();

        infoPage.onNameChange();
        infoPage.onOUChange();

        verify( view,
                times( 1 ) ).setCompletionStatus( false );
        verify( view,
                times( 1 ) ).setPageCompletionState( 0,
                                                     false );
    }

    private void setupNameAndOrgUnitMocks( boolean isRepoNameValid,
                                           String orgUnit ) {
        when( infoPageView.getName() ).thenReturn( REPOSITORY_NAME );
        when( infoPageView.getOrganizationalUnitName() ).thenReturn( orgUnit );

        when( repositoryService.validateRepositoryName( REPOSITORY_NAME ) ).thenReturn( isRepoNameValid );
        when( repositoryService.normalizeRepositoryName( REPOSITORY_NAME ) ).thenReturn( REPOSITORY_NAME );
    }

    public static class CreateRepositoryWizardExtended
            extends CreateRepositoryWizard {

        public CreateRepositoryWizardExtended( final RepositoryInfoPage infoPage,
                                               final RepositoryStructurePage structurePage,
                                               final CreateRepositoryWizardModel model,
                                               final Caller<RepositoryService> repositoryService,
                                               final Caller<RepositoryStructureService> repositoryStructureService,
                                               final Caller<ProjectRepositoryResolver> repositoryResolverService,
                                               final Caller<AssetManagementService> assetManagementService,
                                               final Event<NotificationEvent> notification,
                                               final KieWorkbenchACL kieACL,
                                               final SessionInfo sessionInfo,
                                               final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                                               final WizardView view,
                                               final WizardTestUtils.WizardPageStatusChangeEventMock event ) {
            super( infoPage,
                   structurePage,
                   model,
                   repositoryService,
                   repositoryStructureService,
                   repositoryResolverService,
                   assetManagementService,
                   notification,
                   kieACL,
                   sessionInfo,
                   conflictingRepositoriesPopup );
            super.view = view;
            //emulates the invocation of the @PostConstruct method.
            setupPages();

            event.addEventHandler( new WizardTestUtils.WizardPageStatusChangeHandler() {
                @Override
                public void handleEvent( WizardPageStatusChangeEvent event ) {
                    onStatusChange( event );
                }
            } );
        }

    }
}
