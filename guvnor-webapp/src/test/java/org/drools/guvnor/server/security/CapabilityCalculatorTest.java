/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server.security;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.drools.guvnor.client.security.Capabilities;
import org.junit.Test;

public class CapabilityCalculatorTest {

    @Test
    public void testAdmin() {
        CapabilityCalculator loader = new CapabilityCalculator();
        List<RoleBasedPermission> perms = new ArrayList<RoleBasedPermission>();
        perms.add(new RoleBasedPermission("s", RoleType.ADMIN.getName(), null, null  ));

        Map<String,String> hm = new HashMap<String,String>();
        Capabilities caps = loader.calcCapabilities(perms, hm);
        assertEquals(7, caps.list.size());
        assertSame(hm, caps.prefs);
    }

    @Test
    public void testCapabilitiesCalculate() {
        Map<String,String> hm = new HashMap<String,String>();
        CapabilityCalculator loader = new CapabilityCalculator();
        List<RoleBasedPermission> perms = new ArrayList<RoleBasedPermission>();
        perms.add(new RoleBasedPermission("", RoleType.PACKAGE_DEVELOPER.getName(), null, null));
        perms.add(new RoleBasedPermission("", RoleType.ANALYST.getName(), null, null));
        Capabilities caps = loader.calcCapabilities(perms, hm);
        assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));
        assertSame(hm, caps.prefs);

        perms = new ArrayList<RoleBasedPermission>();
        perms.add(new RoleBasedPermission("", RoleType.PACKAGE_ADMIN.getName(), null, null));
        caps = loader.calcCapabilities(perms, hm);
        assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));
        assertSame(hm, caps.prefs);

        perms = new ArrayList<RoleBasedPermission>();
        perms.add(new RoleBasedPermission("", RoleType.PACKAGE_READONLY.getName(), null, null));
        caps = loader.calcCapabilities(perms, hm);
        assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));
        assertEquals(1, caps.list.size());
        assertSame(hm, caps.prefs);

        perms = new ArrayList<RoleBasedPermission>();
        perms.add(new RoleBasedPermission("", RoleType.PACKAGE_READONLY.getName(), null, null));
        perms.add(new RoleBasedPermission("", RoleType.PACKAGE_READONLY.getName(), null, null));
        perms.add(new RoleBasedPermission("", RoleType.ANALYST_READ.getName(), null, null));
        perms.add(new RoleBasedPermission("", RoleType.PACKAGE_DEVELOPER.getName(), null, null));
        caps = loader.calcCapabilities(perms, hm);
        assertSame(hm, caps.prefs);
        assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));
        assertTrue(caps.list.contains(Capabilities.SHOW_CREATE_NEW_ASSET));
        assertFalse(caps.list.contains(Capabilities.SHOW_CREATE_NEW_PACKAGE));
        assertTrue(caps.list.contains(Capabilities.SHOW_QA));
        assertEquals(3, caps.list.size());

        perms = new ArrayList<RoleBasedPermission>();
        perms.add(new RoleBasedPermission("", RoleType.PACKAGE_READONLY.getName(), null, null));
        perms.add(new RoleBasedPermission("", RoleType.PACKAGE_READONLY.getName(), null, null));
        perms.add(new RoleBasedPermission("", RoleType.ANALYST.getName(), null, null));
        perms.add(new RoleBasedPermission("", RoleType.PACKAGE_ADMIN.getName(), null, null));
        caps = loader.calcCapabilities(perms, hm);
        assertSame(hm, caps.prefs);
        assertTrue(caps.list.contains(Capabilities.SHOW_PACKAGE_VIEW));
        assertTrue(caps.list.contains(Capabilities.SHOW_CREATE_NEW_ASSET));
        assertTrue(caps.list.contains(Capabilities.SHOW_CREATE_NEW_PACKAGE));
        assertTrue(caps.list.contains(Capabilities.SHOW_DEPLOYMENT));
        assertTrue(caps.list.contains(Capabilities.SHOW_DEPLOYMENT_NEW));
        assertTrue(caps.list.contains(Capabilities.SHOW_QA));

        assertEquals(6, caps.list.size());

        perms = new ArrayList<RoleBasedPermission>();
        perms.add(new RoleBasedPermission("", RoleType.PACKAGE_READONLY.getName(), null, null));
        perms.add(new RoleBasedPermission("", RoleType.PACKAGE_READONLY.getName(), null, null));
        perms.add(new RoleBasedPermission("", RoleType.ADMIN.getName(), null, null));
        perms.add(new RoleBasedPermission("", RoleType.PACKAGE_ADMIN.getName(), null, null));
        caps = loader.calcCapabilities(perms, hm);
        assertSame(hm, caps.prefs);
        assertEquals(7, caps.list.size());

    }

}
