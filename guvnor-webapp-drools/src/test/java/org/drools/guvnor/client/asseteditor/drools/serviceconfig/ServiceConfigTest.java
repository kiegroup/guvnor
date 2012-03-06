/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.guvnor.client.rpc.MavenArtifact;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceConfigTest {

    final Collection<MavenArtifact> excludedArtifacts = new ArrayList<MavenArtifact>() {{
        add(new MavenArtifact("org.drools:knowledge-api:jar:5.4.0-SNAPSHOT:compile"));
        add(new MavenArtifact("org.drools:knowledge-api:jar:5.4.0-SNAPSHOT:compile"));
        add(new MavenArtifact("org.apache.camel:camel-core:test-jar:tests:2.4.0:test"));
    }};

    final Collection<ServiceKBaseConfig> kbases = new ArrayList<ServiceKBaseConfig>() {{
        add(new ServiceKBaseConfig("kbase1"));
        add(new ServiceKBaseConfig("kbase1"));
        add(new ServiceKBaseConfig("kbase2"));
    }};

    @Test
    public void testNullConstructor() {
        final ServiceConfig serviceConfig = new ServiceConfig(null, null, null);

        assertNull(serviceConfig.getPollingFrequency());
        assertNotNull(serviceConfig.getKBases());
        assertNotNull(serviceConfig.getExcludedArtifacts());
        assertEquals(0, serviceConfig.getKBases().size());
        assertEquals(0, serviceConfig.getExcludedArtifacts().size());

        assertEquals(serviceConfig, new ServiceConfig(null, null, null));
        assertTrue(serviceConfig.hashCode() == new ServiceConfig(null, null, null).hashCode());

        assertEquals(serviceConfig, new ServiceConfig(serviceConfig));
        assertTrue(serviceConfig.hashCode() == new ServiceConfig(serviceConfig).hashCode());
    }

    @Test
    public void testConstructor() {
        final ServiceConfig serviceConfig = new ServiceConfig("70", excludedArtifacts, kbases);

        assertEquals(new Integer(70), serviceConfig.getPollingFrequency());
        assertNotNull(serviceConfig.getKBases());
        assertNotNull(serviceConfig.getExcludedArtifacts());
        assertEquals(2, serviceConfig.getKBases().size());
        assertEquals(2, serviceConfig.getExcludedArtifacts().size());

        assertFalse(serviceConfig.equals(new ServiceConfig(null, null, null)));
        assertFalse(serviceConfig.hashCode() == new ServiceConfig(null, null, null).hashCode());

        assertEquals(serviceConfig, new ServiceConfig(serviceConfig));
        assertTrue(serviceConfig.hashCode() == new ServiceConfig(serviceConfig).hashCode());
    }

    @Test
    public void testEquals() {
        ServiceConfig serviceConfig = new ServiceConfig("71", excludedArtifacts, kbases);

        assertFalse(serviceConfig.equals(new ServiceConfig("70", excludedArtifacts, kbases)));
        assertFalse(serviceConfig.hashCode() == new ServiceConfig("70", excludedArtifacts, kbases).hashCode());

        serviceConfig = new ServiceConfig("70", excludedArtifacts, kbases);
        serviceConfig.removeExcludedArtifact(new MavenArtifact("org.drools:knowledge-api:jar:5.4.0-SNAPSHOT:compile"));
        assertFalse(serviceConfig.equals(new ServiceConfig("70", excludedArtifacts, kbases)));
        assertFalse(serviceConfig.hashCode() == new ServiceConfig("70", excludedArtifacts, kbases).hashCode());

        serviceConfig = new ServiceConfig("70", excludedArtifacts, kbases);
        serviceConfig.removeKBase("kbase2");
        assertFalse(serviceConfig.equals(new ServiceConfig("70", excludedArtifacts, kbases)));
        assertFalse(serviceConfig.hashCode() == new ServiceConfig("70", excludedArtifacts, kbases).hashCode());

        serviceConfig = new ServiceConfig("70", excludedArtifacts, kbases);

        serviceConfig.setPollingFrequency(71);
        assertFalse(serviceConfig.equals(new ServiceConfig("70", excludedArtifacts, kbases)));
        assertFalse(serviceConfig.hashCode() == new ServiceConfig("70", excludedArtifacts, kbases).hashCode());

        assertEquals(serviceConfig, serviceConfig);
        assertFalse(serviceConfig.equals(null));
        assertFalse(serviceConfig.equals("??"));
    }

    @Test
    public void testAddRemoveExcludedArtifacts() {
        final ServiceConfig serviceConfig = new ServiceConfig(null, null, null);

        serviceConfig.addExcludedArtifact(new MavenArtifact("org.drools:knowledge-api:jar:5.4.0-SNAPSHOT:compile"));
        serviceConfig.addExcludedArtifact(new MavenArtifact("org.drools:knowledge-api:jar:5.4.0-SNAPSHOT:compile"));
        serviceConfig.addExcludedArtifact(null);
        assertEquals(1, serviceConfig.getExcludedArtifacts().size());

        serviceConfig.removeExcludedArtifact(new MavenArtifact("org.drools:knowledge-api:jar:5.4.0-SNAPSHOT:compile"));
        assertEquals(0, serviceConfig.getExcludedArtifacts().size());

        serviceConfig.setExcludedArtifacts(excludedArtifacts);
        serviceConfig.setExcludedArtifacts(null);
        assertEquals(2, serviceConfig.getExcludedArtifacts().size());

        serviceConfig.removeExcludedArtifact(new MavenArtifact("org.drools:knowledge-api:jar:5.4.0-SNAPSHOT:compile"));
        serviceConfig.removeExcludedArtifact(new MavenArtifact("org.drools:knowledge-aaaapi:jar:5.4.0-SNAPSHOT:compile"));
        serviceConfig.removeExcludedArtifact(null);
        assertEquals(1, serviceConfig.getExcludedArtifacts().size());

        serviceConfig.setExcludedArtifacts(new ArrayList<MavenArtifact>());
        assertEquals(0, serviceConfig.getExcludedArtifacts().size());

        serviceConfig.addExcludedArtifact(new MavenArtifact("org.drools:knowledge-aaaapi:jar:5.4.0-SNAPSHOT:compile"));
        serviceConfig.addExcludedArtifacts(excludedArtifacts);
        serviceConfig.addExcludedArtifacts(new ArrayList<MavenArtifact>());
        serviceConfig.addExcludedArtifacts(null);
        assertEquals(3, serviceConfig.getExcludedArtifacts().size());

        serviceConfig.removeExcludedArtifacts(excludedArtifacts);
        serviceConfig.removeExcludedArtifacts(new ArrayList<MavenArtifact>());
        serviceConfig.removeExcludedArtifacts(null);
        assertEquals(1, serviceConfig.getExcludedArtifacts().size());

    }

    @Test
    public void testAddRemoveKBases() {
        final ServiceConfig serviceConfig = new ServiceConfig(null, null, null);

        serviceConfig.addKBase(new ServiceKBaseConfig("kbase1"));
        serviceConfig.addKBase(null);
        assertEquals(1, serviceConfig.getKBases().size());

        assertEquals(serviceConfig.getKbase("kbase1"), new ServiceKBaseConfig("kbase1"));
        assertNull(serviceConfig.getKbase("kbase2"));
        assertNull(serviceConfig.getKbase(""));
        assertNull(serviceConfig.getKbase(null));

        serviceConfig.addKBase(new ServiceKBaseConfig("kbase2"));
        serviceConfig.addKBase(new ServiceKBaseConfig("kbase3"));
        assertEquals(3, serviceConfig.getKBases().size());

        serviceConfig.removeKBase("kbase3");
        serviceConfig.removeKBase("sss");
        serviceConfig.removeKBase("");
        serviceConfig.removeKBase(null);
        assertEquals(2, serviceConfig.getKBases().size());
    }

    @Test
    public void testNextKBaseName() {
        final ServiceConfig serviceConfig = new ServiceConfig(null, null, null);

        assertEquals("kbase1", serviceConfig.getNextKBaseName());
        assertEquals("kbase1", serviceConfig.getNextKBaseName());
        serviceConfig.addKBase(new ServiceKBaseConfig("kbase1"));
        serviceConfig.addKBase(new ServiceKBaseConfig("kbase3"));
        assertEquals("kbase2", serviceConfig.getNextKBaseName());
        serviceConfig.addKBase(new ServiceKBaseConfig("kbase2"));
        assertEquals("kbase4", serviceConfig.getNextKBaseName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnCopy() {
        new ServiceConfig(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testPollingFrequencyNotNumeric() {
        new ServiceConfig("70a", excludedArtifacts, kbases);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddKBaseAlreadyExists() {
        final ServiceConfig serviceConfig = new ServiceConfig(null, null, null);
        serviceConfig.addKBase(new ServiceKBaseConfig("kbase1"));
        serviceConfig.addKBase(new ServiceKBaseConfig("kbase1"));
    }

}
