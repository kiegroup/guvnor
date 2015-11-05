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

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.asset.management.client.editors.repository.structure.release.ReleaseScreenPopupPresenter;
import org.guvnor.asset.management.client.editors.repository.structure.release.ReleaseScreenPopupViewImpl;
import org.guvnor.asset.management.client.i18n.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class ReleaseRepositoryPresenterTest {

    @GwtMock
    private ReleaseScreenPopupViewImpl releasePopupView;
    
    private ReleaseScreenPopupPresenter presenter;
    
    @Mock
    private TextBox versionText;
    
    @Mock
    private ControlGroup versionTextGroup;
    
    @Mock
    private HelpInline versionTextHelpInline;
    
    @Mock
    private Command callbackCommand;
    
    @Mock
    private TextBox sourceBranchText;
    
    @Mock
    private ControlGroup sourceBranchTextGroup;
    
    @Mock
    private HelpInline sourceBranchTextHelpInline;
    
    @Mock
    private CheckBox deployToRuntimeCheck;
    
    @Test
    public void testNotReleasingDueVersion() throws Exception {
        
        presenter = new ReleaseScreenPopupPresenter(releasePopupView, callbackCommand);
        when( releasePopupView.getVersionText()).thenReturn(versionText);
        when( releasePopupView.getVersionText().getText() ).thenReturn( "" );
        
        when( releasePopupView.getVersionTextGroup()).thenReturn(versionTextGroup);
        when( releasePopupView.getVersionTextHelpInline()).thenReturn(versionTextHelpInline);
        
        
        
        presenter.getOkCommand().execute();
        
        verify( releasePopupView.getVersionTextGroup(),  times( 1 )).setType(ControlGroupType.ERROR);
        verify( releasePopupView.getVersionTextHelpInline(), times( 1 ) ).setText(Constants.INSTANCE.FieldMandatory0( "Version" ) );
        
        verify(callbackCommand, times(0)).execute();
        verify(releasePopupView, times(0)).hide();
        
    }
    
    @Test
    public void testNotReleasingDueSnapshotVersion() throws Exception {
        
        presenter = new ReleaseScreenPopupPresenter(releasePopupView, callbackCommand);
        when( releasePopupView.getVersionText()).thenReturn(versionText);
        when( releasePopupView.getVersionText().getText() ).thenReturn( "XXX-SNAPSHOT" );
        
        when( releasePopupView.getVersionTextGroup()).thenReturn(versionTextGroup);
        when( releasePopupView.getVersionTextHelpInline()).thenReturn(versionTextHelpInline);
        
        presenter.getOkCommand().execute();
        
        verify( releasePopupView.getVersionTextGroup(),  times( 1 )).setType(ControlGroupType.ERROR);
        verify( releasePopupView.getVersionTextHelpInline(), times( 1 ) ).setText(Constants.INSTANCE.SnapshotNotAvailableForRelease( "-SNAPSHOT" ) );
        
        verify(callbackCommand, times(0)).execute();
        verify(releasePopupView, times(0)).hide();
        
    }
    
    @Test
    public void testNotReleasingDueWrongBranchName() throws Exception {
        
        presenter = new ReleaseScreenPopupPresenter(releasePopupView, callbackCommand);
        when( releasePopupView.getVersionText()).thenReturn(versionText);
        when( releasePopupView.getVersionText().getText() ).thenReturn( "1.0" );
        
        when( releasePopupView.getVersionTextGroup()).thenReturn(versionTextGroup);
        when( releasePopupView.getVersionTextHelpInline()).thenReturn(versionTextHelpInline);
        
        when( releasePopupView.getSourceBranchText()).thenReturn(sourceBranchText);
        when( releasePopupView.getSourceBranchText().getText() ).thenReturn( "dev-1.0" );
        
        when( releasePopupView.getSourceBranchTextGroup()).thenReturn(sourceBranchTextGroup);
        when( releasePopupView.getSourceBranchTextHelpInline()).thenReturn(sourceBranchTextHelpInline);
        
        
        presenter.getOkCommand().execute();
        
        verify( releasePopupView.getSourceBranchTextGroup(),  times( 1 )).setType(ControlGroupType.ERROR);
        verify( releasePopupView.getSourceBranchTextHelpInline(), times( 1 ) ).setText( Constants.INSTANCE.ReleaseCanOnlyBeDoneFromAReleaseBranch() );
        
        verify(callbackCommand, times(0)).execute();
        verify(releasePopupView, times(0)).hide();
        
    }
    
    @Test
    public void testSuccess() throws Exception {
        
        presenter = new ReleaseScreenPopupPresenter(releasePopupView, callbackCommand);
        when( releasePopupView.getVersionText()).thenReturn(versionText);
        when( releasePopupView.getVersionText().getText() ).thenReturn( "1.0" );
        
        when( releasePopupView.getVersionTextGroup()).thenReturn(versionTextGroup);
        when( releasePopupView.getVersionTextHelpInline()).thenReturn(versionTextHelpInline);
        
        when( releasePopupView.getSourceBranchText()).thenReturn(sourceBranchText);
        when( releasePopupView.getSourceBranchText().getText() ).thenReturn( "release-1.0" );
        
        when( releasePopupView.getSourceBranchTextGroup()).thenReturn(sourceBranchTextGroup);
        when( releasePopupView.getSourceBranchTextHelpInline()).thenReturn(sourceBranchTextHelpInline);
        
        when( releasePopupView.getDeployToRuntimeCheck()).thenReturn(deployToRuntimeCheck);
        when( releasePopupView.getDeployToRuntimeCheck().getValue()).thenReturn(false);
        
        
        presenter.getOkCommand().execute();
        
        verify(callbackCommand, times(1)).execute();
        verify(releasePopupView, times(1)).hide();
        
    }
    
}
