/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.access.impl;

import static org.junit.Assert.assertEquals;

import org.guvnor.ala.openshift.access.OpenShiftRuntimeId;
import org.guvnor.ala.openshift.config.impl.OpenShiftProviderConfigImpl;
import org.junit.Test;

import io.fabric8.openshift.client.OpenShiftConfig;

public class OpenShiftAccessTest {

    @Test
    public void testRuntimeId() {
        final String expectedNs = "myNamespace";
        final String expectedSvc = "myService";
        final String expectedApp = "myApplication";
        OpenShiftRuntimeId oldId = new OpenShiftRuntimeId(expectedNs, expectedSvc, expectedApp);
        OpenShiftRuntimeId newId = OpenShiftRuntimeId.fromString(oldId.toString());
        assertEquals(expectedNs, newId.namespace());
        assertEquals(expectedSvc, newId.service());
        assertEquals(expectedApp, newId.application());
        assertEquals(oldId, newId);
    }

    @Test
    public void testProviderConfig() {
        OpenShiftProviderConfigImpl providerConfig = new OpenShiftProviderConfigImpl().clear();
        providerConfig.setKubernetesMaster("https://localhost:8443");
        providerConfig.setKubernetesOapiVersion("v2");
        OpenShiftConfig clientConfig = OpenShiftAccessInterfaceImpl.buildClientConfig(providerConfig);
        assertEquals("https://localhost:8443/", clientConfig.getMasterUrl());
        assertEquals("https://localhost:8443/oapi/v2/", clientConfig.getOpenShiftUrl());
    }

}
