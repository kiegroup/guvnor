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

package org.guvnor.ala.ui.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.guvnor.ala.services.api.backend.RuntimeProvisioningServiceBackend;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.guvnor.ala.ui.ProvisioningManagementBackendTestCommons.mockProviderTypeListSPI;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProviderTypeServiceImplTest {

    private static final int PROVIDER_TYPES_COUNT = 5;

    @Mock
    private RuntimeProvisioningServiceBackend runtimeProvisioningService;

    @Mock
    private ProviderTypeServiceImpl service;

    private List<org.guvnor.ala.runtime.providers.ProviderType> providerTypesSpi;

    private List<ProviderType> providerTypesToEnable;

    @Before
    public void setUp() {
        providerTypesSpi = mockProviderTypeListSPI(PROVIDER_TYPES_COUNT);

        when(runtimeProvisioningService.getProviderTypes(anyInt(),
                                                         anyInt(),
                                                         anyString(),
                                                         anyBoolean())).thenReturn(providerTypesSpi);

        service = new ProviderTypeServiceImpl(runtimeProvisioningService);
    }

    @Test
    public void testGetAvailableProviders() {

        Collection<ProviderType> result = service.getAvailableProviderTypes();
        assertEquals(PROVIDER_TYPES_COUNT,
                     result.size());

        int i = 0;
        for (ProviderType providerType : result) {
            assertNotNull(providerType.getKey());
            assertEquals(providerTypesSpi.get(i).getProviderTypeName(),
                         providerType.getKey().getId());
            assertEquals(providerTypesSpi.get(i).getVersion(),
                         providerType.getKey().getVersion());
            i++;
        }
    }

    @Test
    public void testGetProviderTypeExisting() {
        //pick an arbitrary existing provider type
        org.guvnor.ala.runtime.providers.ProviderType providerTypeSpi = providerTypesSpi.get(2);

        ProviderTypeKey providerTypeKey = new ProviderTypeKey(providerTypeSpi.getProviderTypeName(),
                                                              providerTypeSpi.getVersion());
        ProviderType providerType = service.getProviderType(providerTypeKey);
        assertNotNull(providerType);

        assertEquals(providerTypeSpi.getProviderTypeName(),
                     providerType.getKey().getId());
        assertEquals(providerTypeSpi.getVersion(),
                     providerType.getKey().getVersion());
    }

    @Test
    public void testGetProviderTypeNotExisting() {
        //invent a non existing provider type key
        ProviderTypeKey providerTypeKey = new ProviderTypeKey("NonExisting",
                                                              "NonExisting");
        ProviderType providerType = service.getProviderType(providerTypeKey);
        assertNull(providerType);
    }

    @Test
    public void testGetEnabledProviderTypes() {
        Collection<ProviderType> enabledProviderTypes = service.getEnabledProviderTypes();
        //no enabled types in the first invocation
        assertTrue(enabledProviderTypes.isEmpty());

        //enable some providers
        prepareProvidersToEnable();

        service.enableProviderTypes(providerTypesToEnable);

        Collection<ProviderType> result = service.getEnabledProviderTypes();
        assertEquals(providerTypesToEnable,
                     result);
    }

    @Test
    public void testEnableProviderTypes() {
        //enable some providers
        prepareProvidersToEnable();
        service.enableProviderTypes(providerTypesToEnable);

        Collection<ProviderType> result = service.getEnabledProviderTypes();
        assertEquals(providerTypesToEnable,
                     result);
    }

    @Test
    public void testDisableProviderTypes() {
        //enable some providers
        prepareProvidersToEnable();
        service.enableProviderTypes(providerTypesToEnable);

        //pick a provider type to disable and disable it
        ProviderType providerTypeToDisable = providerTypesToEnable.get(0);
        ProviderType providerTypeNotDisabled = providerTypesToEnable.get(1);
        service.disableProviderType(providerTypeToDisable);

        Collection<ProviderType> result = service.getEnabledProviderTypes();
        //the disabled provider type can't be present in the results.
        assertFalse(result.contains(providerTypeToDisable));
        //but the non disabled must be there.
        assertTrue(result.contains(providerTypeNotDisabled));
    }

    @Test
    public void testGetProviderTypesStatus() {
        Map<ProviderType, ProviderTypeStatus> initialStatusMap = service.getProviderTypesStatus();
        assertEquals(PROVIDER_TYPES_COUNT,
                     initialStatusMap.size());
        //no provider is enabled
        initialStatusMap.values().forEach(status -> assertEquals(status,
                                                                 ProviderTypeStatus.DISABLED));

        //enable some providers
        prepareProvidersToEnable();
        service.enableProviderTypes(providerTypesToEnable);

        Map<ProviderType, ProviderTypeStatus> currentStatusMap = service.getProviderTypesStatus();

        //only the enabled providers can have ENABLED status
        assertEquals(PROVIDER_TYPES_COUNT,
                     currentStatusMap.size());

        providerTypesToEnable.forEach(enabledProvider -> assertEquals(ProviderTypeStatus.ENABLED,
                                                                      currentStatusMap.get(providerTypesToEnable.get(0))));

        //all the remaining must be disabled.
        long expectedDisabled = PROVIDER_TYPES_COUNT - providerTypesToEnable.size();
        long currentDisabled = currentStatusMap.values().stream()
                .filter(status -> status == ProviderTypeStatus.DISABLED).count();
        assertEquals(expectedDisabled,
                     currentDisabled);
    }

    private void prepareProvidersToEnable() {
        //enable some providers
        Collection<ProviderType> providerTypes = service.getAvailableProviderTypes();
        Iterator<ProviderType> it = providerTypes.iterator();
        providerTypesToEnable = new ArrayList<>();
        providerTypesToEnable.add(it.next());
        providerTypesToEnable.add(it.next());
    }
}
