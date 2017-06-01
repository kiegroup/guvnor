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

package org.guvnor.ala.ui.client.wizard;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.events.RefreshRuntimeEvent;
import org.guvnor.ala.ui.client.wizard.pipeline.SelectPipelinePagePresenter;
import org.guvnor.ala.ui.client.wizard.source.SourceConfigurationPagePresenter;
import org.guvnor.ala.ui.model.PipelineKey;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.Source;
import org.guvnor.ala.ui.service.RuntimeService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.ERROR_MESSAGE;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.PIPELINE1_KEY;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.PIPELINE2_KEY;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.SUCCESS_MESSAGE;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.prepareServiceCallerError;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.NewDeployWizard_PipelineStartErrorMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.NewDeployWizard_PipelineStartSuccessMessage;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewDeployWizardTest
        extends WizardBaseTest {

    private static final String RUNTIME = "RUNTIME";

    @Mock
    private SelectPipelinePagePresenter selectPipelinePage;

    @Mock
    private SourceConfigurationPagePresenter sourceConfigPage;

    @Mock
    private RuntimeService runtimeService;

    private Caller<RuntimeService> runtimeServiceCaller;

    @Mock
    private EventSourceMock<RefreshRuntimeEvent> refreshRuntimeEvent;

    private NewDeployWizard wizard;

    @Mock
    private Provider provider;

    private Collection<PipelineKey> pipelines;

    @Mock
    private Source source;

    @Before
    public void setUp() {
        pipelines = new ArrayList<>();
        pipelines.add(PIPELINE1_KEY);
        pipelines.add(PIPELINE2_KEY);

        when(provider.getKey()).thenReturn(mock(ProviderKey.class));

        when(translationService.getTranslation(NewDeployWizard_PipelineStartSuccessMessage)).thenReturn(SUCCESS_MESSAGE);
        when(translationService.getTranslation(NewDeployWizard_PipelineStartErrorMessage)).thenReturn(ERROR_MESSAGE);

        runtimeServiceCaller = spy(new CallerMock<>(runtimeService));
        wizard = new NewDeployWizard(selectPipelinePage,
                                     sourceConfigPage,
                                     translationService,
                                     runtimeServiceCaller,
                                     notification,
                                     refreshRuntimeEvent) {
            {
                this.view = wizardView;
            }
        };
        wizard.init();
    }

    @Test
    public void testStartDeploymentSuccess() {
        //initialize and start the wizard.
        wizard.start(provider,
                     pipelines);
        verifyStart();

        //emulate the user completing the wizard.
        preCompleteWizard();

        //emulates the user pressing the finish button
        wizard.complete();

        verify(runtimeService,
               times(1)).createRuntime(provider.getKey(),
                                       RUNTIME,
                                       source,
                                       PIPELINE1_KEY);
        verify(notification,
               times(1)).fire(new NotificationEvent(SUCCESS_MESSAGE,
                                                    NotificationEvent.NotificationType.SUCCESS));

        verify(refreshRuntimeEvent,
               times(1)).fire(new RefreshRuntimeEvent(provider.getKey()));
    }

    @Test
    public void testStartDeploymentFailure() {
        //initialize and start the wizard.
        wizard.start(provider,
                     pipelines);
        verifyStart();

        //emulate the user completing the wizard.
        preCompleteWizard();

        prepareServiceCallerError(runtimeService,
                                  runtimeServiceCaller);

        //emulates the user pressing the finish button
        wizard.complete();

        verify(runtimeService,
               times(1)).createRuntime(provider.getKey(),
                                       RUNTIME,
                                       source,
                                       PIPELINE1_KEY);
        verify(notification,
               times(1)).fire(new NotificationEvent(ERROR_MESSAGE,
                                                    NotificationEvent.NotificationType.ERROR));
    }

    private void verifyStart() {
        verify(selectPipelinePage,
               times(1)).setup(pipelines);
        verify(sourceConfigPage,
               times(1)).setup();
        verify(selectPipelinePage,
               times(1)).clear();
        verify(sourceConfigPage,
               times(1)).setup();
    }

    private void preCompleteWizard() {
        when(selectPipelinePage.getPipeline()).thenReturn(PIPELINE1_KEY);
        when(sourceConfigPage.buildSource()).thenReturn(source);
        when(sourceConfigPage.getRuntime()).thenReturn(RUNTIME);

        preparePageCompletion(selectPipelinePage);
        preparePageCompletion(sourceConfigPage);
        wizard.isComplete(Assert::assertTrue);
    }
}
