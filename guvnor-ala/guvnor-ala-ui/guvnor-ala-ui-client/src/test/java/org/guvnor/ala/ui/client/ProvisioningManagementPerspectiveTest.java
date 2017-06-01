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

package org.guvnor.ala.ui.client;

import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.events.AddNewProviderEvent;
import org.guvnor.ala.ui.client.events.AddNewProviderTypeEvent;
import org.guvnor.ala.ui.client.events.AddNewRuntimeEvent;
import org.guvnor.ala.ui.client.wizard.EnableProviderTypeWizard;
import org.guvnor.ala.ui.client.wizard.NewDeployWizard;
import org.guvnor.ala.ui.client.wizard.NewProviderWizard;
import org.guvnor.ala.ui.model.PipelineKey;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.guvnor.ala.ui.service.ProviderTypeService;
import org.guvnor.ala.ui.service.RuntimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderKey;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderType;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderTypeKey;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProvisioningManagementPerspectiveTest {

    @Mock
    private ProviderTypeService providerTypeService;

    @Mock
    private RuntimeService runtimeService;

    @Mock
    private EnableProviderTypeWizard enableProviderTypeWizard;

    @Mock
    private NewProviderWizard newProviderWizard;

    @Mock
    private NewDeployWizard newDeployWizard;

    private ProvisioningManagementPerspective perspective;

    @Before
    public void setUp() {
        perspective = new ProvisioningManagementPerspective(new CallerMock<>(providerTypeService),
                                                            new CallerMock<>(runtimeService),
                                                            enableProviderTypeWizard,
                                                            newProviderWizard,
                                                            newDeployWizard);
    }

    @Test
    public void testAddNewProviderType() {
        @SuppressWarnings("unchecked")
        Map<ProviderType, ProviderTypeStatus> providerTypeStatusMap = mock(Map.class);
        when(providerTypeService.getProviderTypesStatus()).thenReturn(providerTypeStatusMap);

        perspective.onAddNewProviderType(mock(AddNewProviderTypeEvent.class));

        verify(providerTypeService,
               times(1)).getProviderTypesStatus();
        verify(enableProviderTypeWizard,
               times(1)).start(perspective.buildProviderStatusList(providerTypeStatusMap));
    }

    @Test
    public void testAddNewProvider() {
        ProviderType providerType = mockProviderType("");
        perspective.onAddNewProvider(new AddNewProviderEvent(providerType));

        verify(newProviderWizard,
               times(1)).start(providerType);
    }

    @Test
    public void testAddNewRuntime() {
        Provider provider = new Provider(mockProviderKey(mockProviderTypeKey(""),
                                                         ""),
                                         mock(ProviderConfiguration.class));
        @SuppressWarnings("unchecked")
        List<PipelineKey> pipelines = mock(List.class);
        when(runtimeService.getPipelines(provider.getKey().getProviderTypeKey())).thenReturn(pipelines);

        perspective.onAddNewRuntime(new AddNewRuntimeEvent(provider));

        verify(newDeployWizard,
               times(1)).start(provider,
                               pipelines);
    }
}
