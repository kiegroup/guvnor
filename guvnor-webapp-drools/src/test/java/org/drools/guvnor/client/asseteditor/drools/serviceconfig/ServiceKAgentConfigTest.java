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

import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceKAgentConfigTest {

    @Test
    public void testGeneralState() {
        final ServiceKAgentConfig kagent = new ServiceKAgentConfig("kagent1", null, null, null);

        assertNotNull(kagent);
        assertEquals("kagent1", kagent.getName());
        assertNull(kagent.getNewInstance());
        assertNull(kagent.getUseKBaseClassloader());
        assertNotNull(kagent.getResources());
        assertEquals(0, kagent.getResources().size());

        assertEquals(kagent, new ServiceKAgentConfig(kagent.getName()));
        assertEquals(kagent.hashCode(), new ServiceKAgentConfig(kagent.getName()).hashCode());

        assertEquals(kagent, kagent);
        assertEquals(kagent, new ServiceKAgentConfig(kagent));
        assertEquals(kagent.hashCode(), new ServiceKAgentConfig(kagent).hashCode());

        final ServiceKAgentConfig kagentEmptySet = new ServiceKAgentConfig("kagent2", false, true, new ArrayList<AssetReference>());

        assertNotNull(kagentEmptySet);
        assertEquals("kagent2", kagentEmptySet.getName());
        assertEquals(false, kagentEmptySet.getNewInstance());
        assertEquals(true, kagentEmptySet.getUseKBaseClassloader());
        assertNotNull(kagentEmptySet.getResources());
        assertEquals(0, kagentEmptySet.getResources().size());

        assertFalse(kagentEmptySet.equals(new ServiceKAgentConfig(kagentEmptySet.getName())));
        assertFalse(kagentEmptySet.hashCode() == new ServiceKAgentConfig(kagentEmptySet.getName()).hashCode());

        assertEquals(kagentEmptySet, kagentEmptySet);
        assertEquals(kagentEmptySet, new ServiceKAgentConfig(kagentEmptySet));
        assertEquals(kagentEmptySet.hashCode(), new ServiceKAgentConfig(kagentEmptySet).hashCode());

        final Collection<AssetReference> resources = new ArrayList<AssetReference>() {{
            add(new AssetReference("a", "b", "c", "d", "e"));
            add(new AssetReference("a", "b", "c", "d", "e"));
            add(new AssetReference("a2", "b2", "c2", "d2", "e2"));
            add(new AssetReference("a3", "b3", "c3", "d3", "e3"));
        }};

        final ServiceKAgentConfig kagentFull = new ServiceKAgentConfig("kagent3", false, true, resources);

        assertNotNull(kagentFull);
        assertEquals("kagent3", kagentFull.getName());
        assertEquals(false, kagentFull.getNewInstance());
        assertEquals(true, kagentFull.getUseKBaseClassloader());
        assertNotNull(kagentFull.getResources());
        assertEquals(3, kagentFull.getResources().size());

        assertFalse(kagentFull.equals(new ServiceKAgentConfig(kagentFull.getName())));
        assertFalse(kagentFull.hashCode() == new ServiceKAgentConfig(kagentFull.getName()).hashCode());

        assertEquals(kagentFull, kagentFull);
        assertEquals(kagentFull, new ServiceKAgentConfig(kagentFull));
        assertEquals(kagentFull.hashCode(), new ServiceKAgentConfig(kagentFull).hashCode());

        assertFalse(kagentFull.equals(kagentEmptySet));
        assertFalse(kagentFull.equals(kagent));
        assertFalse(kagent.equals(kagentEmptySet));
    }

    @Test
    public void testEquals() {
        final Collection<AssetReference> resources1 = new ArrayList<AssetReference>() {{
            add(new AssetReference("a", "b", "c", "d", "e"));
            add(new AssetReference("a", "b", "c", "d", "e"));
            add(new AssetReference("a2", "b2", "c2", "d2", "e2"));
            add(new AssetReference("a3", "b3", "c3", "d3", "e3"));
        }};

        final Collection<AssetReference> resources2 = new ArrayList<AssetReference>() {{
            add(new AssetReference("a", "b", "c", "d", "e"));
            add(new AssetReference("a2", "b2", "c2", "d2", "e2"));
            add(new AssetReference("a3", "b3", "c3", "d3", "e3"));
        }};

        final Collection<AssetReference> resources3 = new ArrayList<AssetReference>() {{
            add(new AssetReference("a", "b", "c", "d", "e"));
            add(new AssetReference("a3", "b3", "c3", "d3", "e3"));
        }};

        final ServiceKAgentConfig kagent1a = new ServiceKAgentConfig("kagent1", false, true, resources1);
        final ServiceKAgentConfig kagent1b = new ServiceKAgentConfig("kagent1", false, true, resources2);
        final ServiceKAgentConfig kagent1c = new ServiceKAgentConfig("kagent1", false, true, resources3);

        assertNotNull(kagent1a);
        assertNotNull(kagent1b);
        assertNotNull(kagent1c);

        assertTrue(kagent1a.equals(kagent1b));
        assertFalse(kagent1b.equals(kagent1c));
        assertFalse(kagent1c.equals(kagent1a));
        assertFalse(kagent1c.equals(null));
        assertFalse(kagent1c.equals("??"));

        final ServiceKAgentConfig kagent1btf = new ServiceKAgentConfig("kagent1", true, false, resources2);
        final ServiceKAgentConfig kagent1ctf = new ServiceKAgentConfig("kagent1", true, false, resources3);

        assertFalse(kagent1b.equals(kagent1btf));
        assertFalse(kagent1c.equals(kagent1ctf));

        kagent1btf.setNewInstance(false);
        kagent1btf.setUseKBaseClassloader(true);
        kagent1ctf.setNewInstance(false);
        kagent1ctf.setUseKBaseClassloader(true);

        assertTrue(kagent1b.equals(kagent1btf));
        assertTrue(kagent1c.equals(kagent1ctf));

        final ServiceKAgentConfig kagent1bff = new ServiceKAgentConfig("kagent1", false, false, resources2);
        final ServiceKAgentConfig kagent1cff = new ServiceKAgentConfig("kagent1", false, false, resources3);

        assertFalse(kagent1b.equals(kagent1bff));
        assertFalse(kagent1c.equals(kagent1cff));

        kagent1btf.setNewInstance(false);
        kagent1btf.setUseKBaseClassloader(true);
        kagent1ctf.setNewInstance(false);
        kagent1ctf.setUseKBaseClassloader(true);

        assertTrue(kagent1b.equals(kagent1btf));
        assertTrue(kagent1c.equals(kagent1ctf));

        final ServiceKAgentConfig kagent1btt = new ServiceKAgentConfig("kagent1", true, true, resources2);
        final ServiceKAgentConfig kagent1ctt = new ServiceKAgentConfig("kagent1", true, true, resources3);

        assertFalse(kagent1b.equals(kagent1btt));
        assertFalse(kagent1c.equals(kagent1ctt));

        kagent1btf.setNewInstance(false);
        kagent1btf.setUseKBaseClassloader(true);
        kagent1ctf.setNewInstance(false);
        kagent1ctf.setUseKBaseClassloader(true);

        assertTrue(kagent1b.equals(kagent1btf));
        assertTrue(kagent1c.equals(kagent1ctf));
    }

    @Test
    public void testAddRemoveResources() {
        final ServiceKAgentConfig kagent = new ServiceKAgentConfig("kagent1", null, null, null);

        kagent.addResource(new AssetReference("a", "b", "c", "d", "e"));
        kagent.addResource(new AssetReference("a", "b", "c", "d", "e"));

        assertEquals(1, kagent.getResources().size());

        kagent.removeResource(new AssetReference("a", "b", "c", "d", "e"));
        kagent.removeResource(new AssetReference("a", "b", "c", "d", "e"));
        kagent.removeResource(null);

        assertEquals(0, kagent.getResources().size());
    }

    @Test
    public void tesAddResources() {
        final ServiceKAgentConfig kagent = new ServiceKAgentConfig("kagent1", null, null, null);

        assertEquals(0, kagent.getResources().size());

        kagent.addResource(new AssetReference("za", "zb", "zc", "zd", "ze"));

        assertEquals(1, kagent.getResources().size());

        final Collection<AssetReference> resources = new ArrayList<AssetReference>() {{
            add(new AssetReference("a", "b", "c", "d", "e"));
            add(new AssetReference("a", "b", "c", "d", "e"));
            add(new AssetReference("a2", "b2", "c2", "d2", "e2"));
            add(new AssetReference("a3", "b3", "c3", "d3", "e3"));
        }};

        kagent.addResources(null);
        kagent.addResources(new ArrayList<AssetReference>());
        kagent.addResources(resources);

        assertEquals(4, kagent.getResources().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        new ServiceKAgentConfig((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyName() {
        new ServiceKAgentConfig("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnCopy() {
        new ServiceKAgentConfig((ServiceKAgentConfig) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnAddResource() {
        final ServiceKAgentConfig kagent = new ServiceKAgentConfig("kagent1", null, null, null);

        kagent.addResource(null);
    }

}
