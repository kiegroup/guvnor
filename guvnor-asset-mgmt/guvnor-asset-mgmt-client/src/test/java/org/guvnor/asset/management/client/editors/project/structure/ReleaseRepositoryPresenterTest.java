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

import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.asset.management.client.editors.repository.structure.release.ReleaseScreenPopupPresenter;
import org.guvnor.asset.management.client.editors.repository.structure.release.ReleaseScreenPopupView;
import org.guvnor.asset.management.client.i18n.Constants;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ReleaseRepositoryPresenterTest {

    @GwtMock
    private ReleaseScreenPopupView releasePopupView;

    private ReleaseScreenPopupPresenter presenter;

    @Mock
    private Command callbackCommand;
    
    @Mock
    private User identity;
    
    @Before
    public void setup(){
        presenter = new ReleaseScreenPopupPresenter(releasePopupView);
        presenter.setIdentity(identity);
        when(identity.getIdentifier()).thenReturn("salaboy");
        
    }

    @Test
    public void testNotReleasingDueVersion() throws Exception {

        presenter.configure("", "dev-1.0", "", "", callbackCommand);
        when(releasePopupView.getVersion()).thenReturn("");
        when(releasePopupView.getSourceBranch()).thenReturn("dev-1.0");
        
        presenter.getOkCommand().execute();

        verify(releasePopupView, times(1)).setVersionStatus(ControlGroupType.ERROR);
        verify(releasePopupView, times(1)).setVersionHelpText(Constants.INSTANCE.FieldMandatory0("Version"));

        verify(callbackCommand, times(0)).execute();
        verify(releasePopupView, times(0)).hide();

    }

    @Test
    public void testNotReleasingDueSnapshotVersion() throws Exception {

        presenter.configure("", "dev-1.0", "XXX-SNAPSHOT", "XXX-SNAPSHOT", callbackCommand);
        
        when(releasePopupView.getVersion()).thenReturn("XXX-SNAPSHOT");
        when(releasePopupView.getSourceBranch()).thenReturn("dev-1.0");

        presenter.getOkCommand().execute();

        verify(releasePopupView, times(1)).setVersionStatus(ControlGroupType.ERROR);
        verify(releasePopupView, times(1)).setVersionHelpText(Constants.INSTANCE.SnapshotNotAvailableForRelease("-SNAPSHOT"));

        verify(callbackCommand, times(0)).execute();
        verify(releasePopupView, times(0)).hide();

    }

    @Test
    public void testNotReleasingDueWrongBranchName() throws Exception {

        presenter.configure("", "dev-1.0", "1.0", "1.0", callbackCommand);
        
        when(releasePopupView.getVersion()).thenReturn("1.0");
        when(releasePopupView.getSourceBranch()).thenReturn("dev-1.0");

        presenter.getOkCommand().execute();

        verify(releasePopupView, times(1)).setSourceBranchStatus(ControlGroupType.ERROR);
        verify(releasePopupView, times(1)).setSourceBranchHelpText(Constants.INSTANCE.ReleaseCanOnlyBeDoneFromAReleaseBranch());
        verify(callbackCommand, times(0)).execute();
        verify(releasePopupView, times(0)).hide();

    }

    @Test
    public void testSuccess() throws Exception {

        presenter.configure("", "release-1.0", "1.0", "1.0", callbackCommand);
        when(releasePopupView.getVersion()).thenReturn("1.0");
        when(releasePopupView.getSourceBranch()).thenReturn("release-1.0");
        when(releasePopupView.isDeployToRuntime()).thenReturn(false);

        presenter.getOkCommand().execute();

        verify(callbackCommand, times(1)).execute();
        verify(releasePopupView, times(1)).hide();

    }

}
