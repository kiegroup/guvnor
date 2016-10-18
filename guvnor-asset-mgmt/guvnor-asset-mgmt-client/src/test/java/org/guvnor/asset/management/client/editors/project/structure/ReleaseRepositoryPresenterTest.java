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
package org.guvnor.asset.management.client.editors.project.structure;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.asset.management.client.editors.repository.structure.ReleaseCommand;
import org.guvnor.asset.management.client.editors.repository.structure.ReleaseInfo;
import org.guvnor.asset.management.client.editors.repository.structure.release.ReleaseScreenPopupPresenter;
import org.guvnor.asset.management.client.editors.repository.structure.release.ReleaseScreenPopupView;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ReleaseRepositoryPresenterTest {

    private static final String USER_IDENTIFIER = "salaboy";

    @Mock
    private ReleaseScreenPopupView releasePopupView;

    @Mock
    private User identity;

    @Mock
    private ReleaseCommand command;

    @Captor
    private ArgumentCaptor<ReleaseInfo> releaseInfoCaptor;

    // Tested class
    private ReleaseScreenPopupPresenter presenter;

    @Before
    public void setup() {
        when( identity.getIdentifier() ).thenReturn( USER_IDENTIFIER );
        presenter = new ReleaseScreenPopupPresenter( releasePopupView, identity );
    }

    @Test
    public void testNotReleasingDueVersion() throws Exception {

        presenter.configure( "", "dev-1.0", "", "", command );
        when( releasePopupView.getVersion() ).thenReturn( "" );
        when( releasePopupView.getSourceBranch() ).thenReturn( "dev-1.0" );

        presenter.onSubmit();

        verify( releasePopupView, times( 1 ) ).showErrorVersionEmpty();

        verify( command, times( 0 ) ).execute( any( ReleaseInfo.class ) );
        verify( releasePopupView, times( 0 ) ).hide();

    }

    @Test
    public void testNotReleasingDueSnapshotVersion() throws Exception {

        presenter.configure( "", "dev-1.0", "XXX-SNAPSHOT", "XXX-SNAPSHOT", command );

        when( releasePopupView.getVersion() ).thenReturn( "XXX-SNAPSHOT" );
        when( releasePopupView.getSourceBranch() ).thenReturn( "dev-1.0" );

        presenter.onSubmit();

        verify( releasePopupView, times( 1 ) ).showErrorVersionSnapshot();

        verify( command, times( 0 ) ).execute( any( ReleaseInfo.class ) );
        verify( releasePopupView, times( 0 ) ).hide();

    }

    @Test
    public void testNotReleasingDueWrongBranchName() throws Exception {

        presenter.configure( "", "dev-1.0", "1.0", "1.0", command );

        when( releasePopupView.getVersion() ).thenReturn( "1.0" );
        when( releasePopupView.getSourceBranch() ).thenReturn( "dev-1.0" );

        presenter.onSubmit();

        verify( releasePopupView, times( 1 ) ).showErrorSourceBranchNotRelease();
        verify( command, times( 0 ) ).execute( any( ReleaseInfo.class ) );
        verify( releasePopupView, times( 0 ) ).hide();

    }

    @Test
    public void testNotReleasingDueUserNamePasswordServerUrl() throws Exception {

        presenter.configure( "", "dev-1.0", "1.0", "1.0", command );

        when( releasePopupView.getVersion() ).thenReturn( "1.0" );
        when( releasePopupView.getSourceBranch() ).thenReturn( "release-1.0" );
        when( releasePopupView.isDeployToRuntime() ).thenReturn( true );

        presenter.onSubmit();

        verify( releasePopupView, times( 1 ) ).showErrorUserNameEmpty();
        verify( releasePopupView, times( 1 ) ).showErrorPasswordEmpty();
        verify( releasePopupView, times( 1 ) ).showErrorServerUrlEmpty();
        verify( command, times( 0 ) ).execute( any( ReleaseInfo.class ) );
        verify( releasePopupView, times( 0 ) ).hide();

    }

    @Test
    public void testSuccess() throws Exception {

        presenter.configure( "", "release-1.0", "1.0", "1.0", command );
        when( releasePopupView.getVersion() ).thenReturn( "1.1" );
        when( releasePopupView.getSourceBranch() ).thenReturn( "release-1.0" );
        when( releasePopupView.isDeployToRuntime() ).thenReturn( false );
        when( releasePopupView.getUserName() ).thenReturn( "username" );
        when( releasePopupView.getPassword() ).thenReturn( "password" );
        when( releasePopupView.getServerURL() ).thenReturn( "serverUrl" );

        presenter.onSubmit();

        verify( command, times( 1 ) ).execute( releaseInfoCaptor.capture() );
        ReleaseInfo releaseInfo = releaseInfoCaptor.getValue();
        assertEquals( "1.1", releaseInfo.getVersion() );
        assertFalse( releaseInfo.isDeployToRuntime() );
        assertEquals( "username", releaseInfo.getUsername() );
        assertEquals( "password", releaseInfo.getPassword() );
        assertEquals( "serverUrl", releaseInfo.getServerUrl() );
        verify( releasePopupView, times( 1 ) ).hide();

    }

    @Test
    public void testUserNamePasswordServerUrlStateSynchronizedWithDeployToRuntime() {
        presenter.onDeployToRuntimeStateChanged( true );
        verify( releasePopupView ).setUserNameEnabled( true );
        verify( releasePopupView ).setPasswordEnabled( true );
        verify( releasePopupView ).setServerURLEnabled( true );
        presenter.onDeployToRuntimeStateChanged( false );
        verify( releasePopupView ).setUserNameEnabled( false );
        verify( releasePopupView ).setPasswordEnabled( false );
        verify( releasePopupView ).setServerURLEnabled( false );
    }

    @Test
    public void testCommandNotExecutedWhenDialogCancelled() {
        presenter.show();
        presenter.onCancel();

        verify( command, times( 0 ) ).execute( any( ReleaseInfo.class ) );
        verify( releasePopupView, times( 1 ) ).hide();
    }

    @Test
    public void testPreFilledData() {
        presenter.configure( "alias", "branch", "1", "1-SNAPSHOT", command );

        verify( releasePopupView ).setRepository( "alias" );
        verify( releasePopupView ).setSourceBranch( "branch" );
        verify( releasePopupView ).setVersion( "1" );
        verify( releasePopupView ).showCurrentVersionHelpText( "1-SNAPSHOT" );
        verify( releasePopupView ).setDeployToRuntime( false );
        verify( releasePopupView ).setUserName( USER_IDENTIFIER );
        verify( releasePopupView ).setServerURL( anyString() );
    }

    @Test
    public void testErrorStatusCleanedAfterCorrection() {
        when( releasePopupView.getSourceBranch() ).thenReturn( "master" );
        when( releasePopupView.getVersion() ).thenReturn( "-SNAPSHOT" );

        presenter.configure( "", "", "", "", command );
        verify( releasePopupView, times( 1 ) ).clearWidgetsState();

        // first attempt with wrong branch and version
        presenter.show();
        presenter.onSubmit();
        verify( releasePopupView, times( 2 ) ).clearWidgetsState();
        verify( releasePopupView, times( 1 ) ).showErrorSourceBranchNotRelease();
        verify( releasePopupView, times( 1 ) ).showErrorVersionSnapshot();

        when( releasePopupView.getSourceBranch() ).thenReturn( "release" );

        // second attempt with correct branch (version still snapshot)
        presenter.onSubmit();
        verify( releasePopupView, times( 3 ) ).clearWidgetsState();
        verify( releasePopupView, times( 1 ) ).showErrorSourceBranchNotRelease();
        verify( releasePopupView, times( 2 ) ).showErrorVersionSnapshot();

        verify( command, times( 0 ) ).execute( any( ReleaseInfo.class ) );
        verify( releasePopupView, times( 0 ) ).hide();
    }
}
