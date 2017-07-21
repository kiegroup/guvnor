/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.client.wizard.project;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.ala.ui.client.util.PopupHelper;
import org.guvnor.ala.ui.client.widget.FormStatus;
import org.guvnor.ala.ui.client.wizard.project.artifact.ArtifactSelectorPresenter;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.SERVICE_CALLER_EXCEPTION_MESSAGE;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.prepareServiceCallerError;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.GAVConfigurationParamsPresenter_LoadGAVErrorMessage;
import static org.guvnor.ala.ui.client.util.UIUtil.EMPTY_STRING;
import static org.guvnor.ala.ui.client.wizard.project.GAVConfigurationParamsPresenter.ARTIFACT_ID;
import static org.guvnor.ala.ui.client.wizard.project.GAVConfigurationParamsPresenter.GROUP_ID;
import static org.guvnor.ala.ui.client.wizard.project.GAVConfigurationParamsPresenter.VERSION;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GAVConfigurationParamsPresenterTest {

    private static final String GROUP_ID_VALUE = "GROUP_ID_VALUE";

    private static final String ARTIFACT_ID_VALUE = "ARTIFACT_ID_VALUE";

    private static final String VERSION_VALUE = "VERSION_VALUE";

    private static final String TITLE = "TITLE";

    private static final String JAR_PATH = "JAR_PATH";

    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    @Mock
    private GAVConfigurationParamsPresenter.View view;

    @Mock
    private ArtifactSelectorPresenter artifactSelector;

    @Mock
    private ArtifactSelectorPresenter.View artifactSelectorView;

    @Mock
    private TranslationService translationService;

    @Mock
    private PopupHelper popupHelper;

    private CallerMock<M2RepoService> m2RepoServiceCaller;

    @Mock
    private M2RepoService m2RepoService;

    @Mock
    private EventSourceMock<GAVConfigurationChangeEvent> gavConfigurationChangeEvent;

    private GAVConfigurationParamsPresenter presenter;

    @Before
    public void setUp() {
        when(artifactSelector.getView()).thenReturn(artifactSelectorView);

        m2RepoServiceCaller = spy(new CallerMock<>(m2RepoService));

        presenter = spy(new GAVConfigurationParamsPresenter(view,
                                                            artifactSelector,
                                                            translationService,
                                                            popupHelper,
                                                            m2RepoServiceCaller,
                                                            gavConfigurationChangeEvent));
        presenter.init();
        verify(view,
               times(1)).init(presenter);
        verify(artifactSelector,
               times(1)).setArtifactSelectHandler(any());
        verify(view,
               times(1)).setArtifactSelectorPresenter(artifactSelectorView);
    }

    @Test
    public void testGetView() {
        assertEquals(view,
                     presenter.getView());
    }

    @Test
    public void testBuildParams() {
        when(view.getGroupId()).thenReturn(GROUP_ID_VALUE);
        when(view.getArtifactId()).thenReturn(ARTIFACT_ID_VALUE);
        when(view.getVersion()).thenReturn(VERSION_VALUE);

        Map<String, String> result = presenter.buildParams();

        Map<String, String> expectedParams = new HashMap<>();
        expectedParams.put(GROUP_ID,
                           GROUP_ID_VALUE);
        expectedParams.put(ARTIFACT_ID,
                           ARTIFACT_ID_VALUE);
        expectedParams.put(VERSION,
                           VERSION_VALUE);

        assertEquals(expectedParams,
                     result);
    }

    @Test
    public void testInitialize() {
        presenter.initialise();
        verify(artifactSelector,
               times(1)).clear();
    }

    @Test
    public void testPrepareView() {
        presenter.prepareView();
        verify(artifactSelector,
               times(1)).refresh();
    }

    @Test
    public void testClear() {
        prepareClearView();
        presenter.clear();
        verify(view,
               times(1)).clear();
        verify(gavConfigurationChangeEvent,
               times(1)).fire(new GAVConfigurationChangeEvent());
    }

    @Test
    public void testIsComplete() {
        prepareClearView();

        presenter.clear();
        //nothing was completed, so the wizard is not completed
        presenter.isComplete(Assert::assertFalse);

        //emulate the groupId was completed
        when(view.getGroupId()).thenReturn(GROUP_ID_VALUE);
        presenter.isComplete(Assert::assertFalse);

        //emulate the groupId and artifactId was completed
        when(view.getGroupId()).thenReturn(GROUP_ID_VALUE);
        when(view.getArtifactId()).thenReturn(ARTIFACT_ID_VALUE);
        presenter.isComplete(Assert::assertFalse);

        //emulate the groupId, artifactId and versioni was completed
        when(view.getGroupId()).thenReturn(GROUP_ID_VALUE);
        when(view.getArtifactId()).thenReturn(ARTIFACT_ID_VALUE);
        when(view.getVersion()).thenReturn(VERSION_VALUE);
        //the wizard is completed only when all elements are completed.
        presenter.isComplete(Assert::assertTrue);
    }

    @Test
    public void testGetWizardTitle() {
        when(view.getWizardTitle()).thenReturn(TITLE);
        assertEquals(TITLE,
                     presenter.getWizardTitle());
    }

    @Test
    public void testOnArtifactSelectedSuccessful() {
        GAV gav = new GAV(GROUP_ID_VALUE,
                          ARTIFACT_ID_VALUE,
                          VERSION_VALUE);
        when(m2RepoService.loadGAVFromJar(JAR_PATH)).thenReturn(gav);
        //emulate the returned value was properly loaded into the view.
        when(view.getGroupId()).thenReturn(GROUP_ID_VALUE);
        when(view.getArtifactId()).thenReturn(ARTIFACT_ID_VALUE);
        when(view.getVersion()).thenReturn(VERSION_VALUE);

        presenter.onArtifactSelected(JAR_PATH);

        verify(m2RepoService,
               times(1)).loadGAVFromJar(JAR_PATH);
        verify(view,
               times(1)).setGroupId(GROUP_ID_VALUE);
        verify(view,
               times(1)).setArtifactId(ARTIFACT_ID_VALUE);
        verify(view,
               times(1)).setVersion(VERSION_VALUE);

        verify(gavConfigurationChangeEvent,
               times(1)).fire(new GAVConfigurationChangeEvent(gav));
        verify(presenter,
               times(1)).fireChangeHandlers();
    }

    @Test
    public void testOnArtifactSelectedFailed() {
        prepareClearView();
        when(translationService.format(GAVConfigurationParamsPresenter_LoadGAVErrorMessage,
                                       SERVICE_CALLER_EXCEPTION_MESSAGE)).thenReturn(ERROR_MESSAGE);

        prepareServiceCallerError(m2RepoService,
                                  m2RepoServiceCaller);

        presenter.onArtifactSelected(JAR_PATH);
        verify(popupHelper,
               times(1)).showErrorPopup(ERROR_MESSAGE);
        verify(view,
               times(1)).setGroupId(EMPTY_STRING);
        verify(view,
               times(1)).setArtifactId(EMPTY_STRING);
        verify(view,
               times(1)).setVersion(EMPTY_STRING);
        verify(presenter,
               times(1)).fireChangeHandlers();
        verify(gavConfigurationChangeEvent,
               times(1)).fire(new GAVConfigurationChangeEvent());
    }

    @Test
    public void testOnGroupIdChangedValid() {
        when(view.getGroupId()).thenReturn(GROUP_ID_VALUE);
        presenter.onGroupIdChange();
        verify(view,
               times(1)).setGroupIdStatus(FormStatus.VALID);
        verify(presenter,
               times(1)).onContentChange();
    }

    @Test
    public void testOnGroupIdChangedInValid() {
        when(view.getGroupId()).thenReturn(EMPTY_STRING);
        presenter.onGroupIdChange();
        verify(view,
               times(1)).setGroupIdStatus(FormStatus.ERROR);
        verify(presenter,
               times(1)).onContentChange();
    }

    @Test
    public void testOnArtifactIdChangedValid() {
        when(view.getArtifactId()).thenReturn(ARTIFACT_ID_VALUE);
        presenter.onArtifactIdChange();
        verify(view,
               times(1)).setArtifactIdStatus(FormStatus.VALID);
        verify(presenter,
               times(1)).onContentChange();
    }

    @Test
    public void testOnArtifactIdChangedInValid() {
        when(view.getArtifactId()).thenReturn(EMPTY_STRING);
        presenter.onArtifactIdChange();
        verify(view,
               times(1)).setArtifactIdStatus(FormStatus.ERROR);
        verify(presenter,
               times(1)).onContentChange();
    }

    @Test
    public void testOnVersionChangedValid() {
        when(view.getVersion()).thenReturn(VERSION_VALUE);
        presenter.onVersionChange();
        verify(view,
               times(1)).setVersionStatus(FormStatus.VALID);
        verify(presenter,
               times(1)).onContentChange();
    }

    @Test
    public void testOnVersionChangedInValid() {
        when(view.getVersion()).thenReturn(EMPTY_STRING);
        presenter.onVersionChange();
        verify(view,
               times(1)).setVersionStatus(FormStatus.ERROR);
        verify(presenter,
               times(1)).onContentChange();
    }

    private void prepareClearView() {
        when(view.getGroupId()).thenReturn(EMPTY_STRING);
        when(view.getArtifactId()).thenReturn(EMPTY_STRING);
        when(view.getVersion()).thenReturn(EMPTY_STRING);
    }
}
