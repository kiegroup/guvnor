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
import static org.junit.Assert.fail;

import io.fabric8.openshift.client.OpenShiftConfig;
import org.guvnor.ala.openshift.access.OpenShiftRuntimeId;
import org.guvnor.ala.openshift.config.impl.OpenShiftProviderConfigImpl;
import org.junit.Test;

public class OpenShiftAccessTest {

    @Test
    public void testGoodRuntimeId() {
        final String expectedPrj = "myProject";
        final String expectedSvc = "myService";
        final String expectedApp = "myApplication";
        OpenShiftRuntimeId oldId = new OpenShiftRuntimeId(expectedPrj, expectedSvc, expectedApp);
        OpenShiftRuntimeId newId = OpenShiftRuntimeId.fromString(oldId.toString());
        assertEquals(expectedPrj, newId.project());
        assertEquals(expectedSvc, newId.service());
        assertEquals(expectedApp, newId.application());
        assertEquals(oldId, newId);
    }

    @Test
    public void testBadRuntimeId() {
        final String nullStr = null;
        final String emptyStr = "";
        final String blankStr = " ";
        final String validStr = "valid";
        try {
            new OpenShiftRuntimeId(nullStr, validStr, validStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(emptyStr, validStr, validStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(blankStr, validStr, validStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(validStr, nullStr, validStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(validStr, emptyStr, validStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(validStr, blankStr, validStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(validStr, validStr, nullStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(validStr, validStr, emptyStr);
            fail();
        } catch (IllegalArgumentException iae) {}
        try {
            new OpenShiftRuntimeId(validStr, validStr, blankStr);
            fail();
        } catch (IllegalArgumentException iae) {}
    }

    @Test
    public void testProviderConfig() {
        OpenShiftProviderConfigImpl providerConfig = new OpenShiftProviderConfigImpl().clear();
        providerConfig.setKubernetesMaster("https://localhost:8443");
        providerConfig.setKubernetesOapiVersion("v2");
        OpenShiftConfig clientConfig = OpenShiftAccessInterfaceImpl.buildOpenShiftConfig(providerConfig);
        assertEquals("https://localhost:8443/", clientConfig.getMasterUrl());
        assertEquals("https://localhost:8443/oapi/v2/", clientConfig.getOpenShiftUrl());
    }

}
