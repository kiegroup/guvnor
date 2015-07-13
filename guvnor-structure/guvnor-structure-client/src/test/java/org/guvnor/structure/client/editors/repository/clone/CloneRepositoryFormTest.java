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

package org.guvnor.structure.client.editors.repository.clone;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import org.guvnor.structure.client.editors.repository.RepositoryPreferences;
import org.guvnor.structure.client.editors.repository.clone.answer.OuServiceAnswer;
import org.guvnor.structure.client.editors.repository.clone.answer.RsCreateRepositoryAnswer;
import org.guvnor.structure.client.editors.repository.clone.answer.RsCreateRepositoryFailAnswer;
import org.guvnor.structure.client.editors.repository.clone.answer.RsNormalizedNameAnswer;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryAlreadyExistsException;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CloneRepositoryFormTest {

    private static final String ORG_UNIT_ONE = "OrganizationalUnitOne";
    private static final String ORG_UNIT_TWO = "OrganizationalUnitTwo";
    private static final String REPO_NAME = "GitRepositoryName";
    private static final String REPO_URL = "/home/user/git/url";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Mock
    private Message message;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private RepositoryPreferences repositoryPreferences;

    @Mock
    private CloneRepositoryView view;

    @Mock
    private Caller<RepositoryService> repoServiceCaller;

    @Mock
    private Caller<RepositoryService> repoFailServiceCaller;

    @Mock
    private Caller<OrganizationalUnitService> ouServiceCaller;

    @Mock
    private OrganizationalUnitService ouService;

    @Mock
    private RepositoryService repoService;

    @Mock
    private OrganizationalUnit ouUnit1;

    @Mock
    private OrganizationalUnit ouUnit2;

    @Mock
    private Repository repository;

    @Captor
    private ArgumentCaptor<Boolean> boolArgument;

    @Captor
    private ArgumentCaptor<Throwable> throwableArgument;

    private CloneRepositoryPresenter presenter;

    @Before
    public void initPresenter() {
        List<OrganizationalUnit> units = new ArrayList<OrganizationalUnit>();
        units.add( ouUnit1 );
        units.add( ouUnit2 );

        when( ouUnit1.getName() ).thenReturn( ORG_UNIT_ONE );
        when( ouUnit2.getName() ).thenReturn( ORG_UNIT_TWO );

        when( view.isNameEmpty() ).thenReturn( false );
        when( view.getName() ).thenReturn( REPO_NAME );
        when( view.getOrganizationalUnit( any( Integer.class ) ) ).thenReturn( ORG_UNIT_ONE );
        when( view.getUsername() ).thenReturn( USERNAME );
        when( view.getPassword() ).thenReturn( PASSWORD );

        when( ouServiceCaller.call( any( RemoteCallback.class ), any( ErrorCallback.class ) ) ).thenAnswer( new OuServiceAnswer( units, ouService ) );
        when( repoServiceCaller.call( any( RemoteCallback.class ) ) ).thenAnswer( new RsNormalizedNameAnswer( REPO_NAME, repoService ) );
        when( repoServiceCaller.call( any( RemoteCallback.class ), any( ErrorCallback.class ) ) ).thenAnswer( new RsCreateRepositoryAnswer( repository, repoService ) );
        when( repoFailServiceCaller.call( any( RemoteCallback.class ) ) ).thenAnswer( new RsNormalizedNameAnswer( REPO_NAME, repoService ) );

        when( repositoryPreferences.isOUMandatory() ).thenReturn( false );

        presenter = new CloneRepositoryPresenter( repositoryPreferences, view, repoServiceCaller, ouServiceCaller, placeManager );
        presenter.init();
    }

    /**
     * BZ 1003005 - Clone repository dialogue stays operational.
     * Test if clone repo form doesn't stay operational on valid data in form
     */
    @Test
    public void testComponentsStaysOperational() {
        when( view.isGitUrlEmpty() ).thenReturn( false );
        when( view.getGitUrl() ).thenReturn( REPO_URL );

        presenter.handleCloneClick();

        componentsLocked();
        verifyRepoCloned( true );
    }

    /**
     * Tests if clone repo form is non locked after invalid data filled in form
     */
    @Test
    public void testComponentsNonLockEmptyUrl() {
        when( view.isGitUrlEmpty() ).thenReturn( true );

        presenter.handleCloneClick();

        verify( view ).showUrlHelpMandatoryMessage();

        componentsNotAffected();
        verifyRepoCloned( false );
    }

    @Test
    public void testComponentsNonLockInvalidUrl() {
        when( view.isGitUrlEmpty() ).thenReturn( false );
        when( view.getGitUrl() ).thenReturn( "git repo" );

        presenter.handleCloneClick();

        verify( view ).showUrlHelpInvalidFormatMessage();

        componentsNotAffected();
        verifyRepoCloned( false );
    }

    @Test
    public void testComponentsNonLockOuMandatory() {
        when( view.isGitUrlEmpty() ).thenReturn( false );
        when( view.getGitUrl() ).thenReturn( REPO_URL );
        when( view.getOrganizationalUnit( any( Integer.class ) ) ).thenReturn( "non_existing" );
        when( repositoryPreferences.isOUMandatory() ).thenReturn( true );

        presenter.handleCloneClick();

        verify( view ).showOrganizationalUnitHelpMandatoryMessage();

        componentsNotAffected();
        verifyRepoCloned( false );
    }

    @Test
    public void testComponentsNonLockEmptyName() {
        when( view.isGitUrlEmpty() ).thenReturn( false );
        when( view.getGitUrl() ).thenReturn( REPO_URL );
        when( view.isNameEmpty() ).thenReturn( true );

        presenter.handleCloneClick();

        verify( view ).showNameHelpMandatoryMessage();

        componentsNotAffected();
        verifyRepoCloned( false );
    }

    @Test
    public void testAlreadyExistClone() {
        when( repoFailServiceCaller.call( any( RemoteCallback.class ), any( ErrorCallback.class ) ) ).thenAnswer(
                new RsCreateRepositoryFailAnswer( message, new RepositoryAlreadyExistsException(), repository, repoService ) );

        presenter = new CloneRepositoryPresenter( repositoryPreferences, view, repoFailServiceCaller, ouServiceCaller, placeManager );
        presenter.init();

        when( view.isGitUrlEmpty() ).thenReturn( false );
        when( view.getGitUrl() ).thenReturn( REPO_URL );

        presenter.handleCloneClick();
        verify( view ).errorRepositoryAlreadyExist();

        componentsLocked();
        componentsUnlocked();
        verifyRepoCloned( false );
    }

    @Test
    public void testFailClone() {
        when( view.isGitUrlEmpty() ).thenReturn( false );
        when( view.getGitUrl() ).thenReturn( REPO_URL );

        RuntimeException exc = new RuntimeException();
        when( repoFailServiceCaller.call( any( RemoteCallback.class ), any( ErrorCallback.class ) ) ).thenAnswer(
                new RsCreateRepositoryFailAnswer( message, exc, repository, repoService ) );

        presenter = new CloneRepositoryPresenter( repositoryPreferences, view, repoFailServiceCaller, ouServiceCaller, placeManager );
        presenter.init();

        presenter.handleCloneClick();
        verify( view ).errorCloneRepositoryFail( throwableArgument.capture() );
        assertEquals( exc, throwableArgument.getValue() );

        componentsLocked();
        componentsUnlocked();
        verifyRepoCloned( false );
    }

    /**
     * BZ 1006906 - Repository clone doesn't validate URL
     */
    @Test
    public void testGitUrlValidation() {
        when( view.isGitUrlEmpty() ).thenReturn( false );

        when( view.getGitUrl() ).thenReturn( "a b c" );
        presenter.handleCloneClick();

        when( view.getGitUrl() ).thenReturn( ":" );
        presenter.handleCloneClick();

        when( view.getGitUrl() ).thenReturn( "|" );
        presenter.handleCloneClick();

        verify( view, times( 3 ) ).showUrlHelpInvalidFormatMessage();

        componentsNotAffected();
        verifyRepoCloned( false );
    }

    /**
     * BZ 1006906 - Repository clone doesn't validate URL
     * <p/>
     * There are two variants of URIUtils class
     * At runtime is used javascript version, which consider correctly "abc" as invalid uri
     * In tests is used java version, which consider wrongly "abc" as valid uri
     */
    @Test
    @Ignore("See comments above")
    public void testGitUrlValidationSpecial() {
        when( view.isGitUrlEmpty() ).thenReturn( false );

        when( view.getGitUrl() ).thenReturn( "abc" );
        presenter.handleCloneClick();

        verify( view ).showUrlHelpInvalidFormatMessage();

        componentsNotAffected();
        verifyRepoCloned( false );
    }

    @Test
    public void testCloneNoGroup() {
        when( repositoryPreferences.isOUMandatory() ).thenReturn( true );
        when( view.isGitUrlEmpty() ).thenReturn( false );
        when( view.getGitUrl() ).thenReturn( REPO_URL );
        when( view.getName() ).thenReturn( REPO_NAME );
        when( view.getOrganizationalUnit( anyInt() ) ).thenReturn( "" );

        presenter = new CloneRepositoryPresenter( repositoryPreferences, view, repoServiceCaller, ouServiceCaller, placeManager );
        presenter.handleCloneClick();

        verify( view ).setOrganizationalUnitGroupType( ControlGroupType.ERROR );
        verify( view ).showOrganizationalUnitHelpMandatoryMessage();
        verifyRepoCloned( false );
    }

    @Test
    public void testCloneNoUrl() {
        when( repositoryPreferences.isOUMandatory() ).thenReturn( true );
        when( view.isGitUrlEmpty() ).thenReturn( true );
        when( view.getGitUrl() ).thenReturn( "" );
        when( view.getName() ).thenReturn( REPO_NAME );
        when( view.getOrganizationalUnit( anyInt() ) ).thenReturn( ORG_UNIT_ONE );

        presenter = new CloneRepositoryPresenter( repositoryPreferences, view, repoServiceCaller, ouServiceCaller, placeManager );
        presenter.handleCloneClick();

        verify( view ).setUrlGroupType( ControlGroupType.ERROR );
        verify( view ).showUrlHelpMandatoryMessage();
        verifyRepoCloned( false );
    }

    @Test
    public void testCancelButton() {
        presenter.handleCancelClick();
        verify( view ).hide();
    }

    private void componentsNotAffected() {
        verify( view, never() ).setCloneEnabled( anyBoolean() );

        verify( view, never() ).setGitUrlEnabled( anyBoolean() );

        verify( view, never() ).setNameEnabled( anyBoolean() );

        verify( view, never() ).setOrganizationalUnitEnabled( anyBoolean() );

        verify( view, never() ).setUsernameEnabled( anyBoolean() );

        verify( view, never() ).setPasswordEnabled( anyBoolean() );

        verify( view, never() ).showBusyPopupMessage();

        verify( view, never() ).closeBusyPopup();
    }

    private void componentsLocked() {
        verify( view ).showBusyPopupMessage();

        verify( view ).setCloneEnabled( false );

        verify( view ).setGitUrlEnabled( false );

        verify( view ).setNameEnabled( false );

        verify( view ).setOrganizationalUnitEnabled( false );

        verify( view ).setUsernameEnabled( false );

        verify( view ).setPasswordEnabled( false );
    }

    private void componentsUnlocked() {
        verify( view ).closeBusyPopup();

        verify( view ).setCloneEnabled( true );

        verify( view ).setGitUrlEnabled( true );

        verify( view ).setNameEnabled( true );

        verify( view ).setOrganizationalUnitEnabled( true );

        verify( view ).setUsernameEnabled( true );

        verify( view ).setPasswordEnabled( true );
    }

    private void verifyRepoCloned( boolean cloned ) {
        if ( cloned ) {
            verify( view ).alertRepositoryCloned();
            verify( view ).hide();
        } else {
            verify( view, never() ).alertRepositoryCloned();
            verify( view, never() ).hide();
        }
    }
}
