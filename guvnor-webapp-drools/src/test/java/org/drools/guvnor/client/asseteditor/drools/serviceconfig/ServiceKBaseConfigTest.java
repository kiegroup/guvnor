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

import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.EventProcessingOption.*;
import static org.junit.Assert.*;

public class ServiceKBaseConfigTest {

    final Collection<AssetReference> resources = new ArrayList<AssetReference>() {{
        add(new AssetReference("myPkg", "a", "drl", "http://localhost/c/source", "uuid1"));
        add(new AssetReference("myPkg", "a", "drl", "http://localhost/c/source", "uuid1"));
        add(new AssetReference("myPkg", "aa", "drl", "http://localhost/cc/source", "uuid2"));
        add(new AssetReference("myPkg", "ab", "changeset", "http://localhost/cd/source", "uuid3"));
    }};

    final Collection<AssetReference> models = new ArrayList<AssetReference>() {{
        add(new AssetReference("myPkg", "a.jar", "model", "http://localhost/a.jar", "uudi44"));
        add(new AssetReference("myPkg", "a.jar", "model", "http://localhost/a.jar", "uudi44"));
    }};

    final Collection<ServiceKSessionConfig> ksessions = new ArrayList<ServiceKSessionConfig>() {{
        add(new ServiceKSessionConfig("ksession1"));
        add(new ServiceKSessionConfig("ksession1"));
        add(new ServiceKSessionConfig("ksession2"));
    }};

    final Collection<ServiceKAgentConfig> kagents = new ArrayList<ServiceKAgentConfig>() {{
        add(new ServiceKAgentConfig("kagent1"));
        add(new ServiceKAgentConfig("kagent1"));
        add(new ServiceKAgentConfig("kagent2"));
    }};

    @Test
    public void testSimpleConstructors() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1", null, null, null, null, null, null, null, null, null, null);

        assertNotNull(kbase);
        assertEquals("kbase1", kbase.getName());

        assertNull(kbase.getMaxThreads());
        assertNull(kbase.getMbeans());
        assertNull(kbase.getEventProcessingMode());
        assertNull(kbase.getAssertBehavior());
        assertNull(kbase.getAssetsUser());
        assertNull(kbase.getAssetsPassword());
        assertFalse(kbase.hasConfig());

        assertNotNull(kbase.getResources());
        assertNotNull(kbase.getModels());

        assertEquals(0, kbase.getResources().size());
        assertEquals(0, kbase.getModels().size());

        assertNotNull(kbase.getKsessions());
        assertNotNull(kbase.getKagents());

        assertEquals(0, kbase.getKsessions().size());
        assertEquals(0, kbase.getKagents().size());

        assertEquals(kbase, new ServiceKBaseConfig(kbase.getName()));
        assertTrue(kbase.hashCode() == new ServiceKBaseConfig(kbase.getName()).hashCode());

        assertEquals(kbase, new ServiceKBaseConfig(kbase));
        assertTrue(kbase.hashCode() == new ServiceKBaseConfig(kbase).hashCode());

        final ServiceKBaseConfig kbaseEmptyCollections = new ServiceKBaseConfig("kbase1", null, null, null,
                null, null, null,
                new ArrayList<AssetReference>(),
                new ArrayList<AssetReference>(),
                new ArrayList<ServiceKSessionConfig>(),
                new ArrayList<ServiceKAgentConfig>());

        assertEquals(kbase, kbaseEmptyCollections);
        assertTrue(kbase.hashCode() == kbaseEmptyCollections.hashCode());
    }

    @Test
    public void testConstructorsWithContentLists() {

        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1", null, null,
                null, null, null, null,
                resources, models, ksessions, kagents);

        assertNotNull(kbase.getResources());
        assertNotNull(kbase.getModels());
        assertFalse(kbase.hasConfig());

        assertEquals(3, kbase.getResources().size());
        assertEquals(1, kbase.getModels().size());

        assertNotNull(kbase.getKsessions());
        assertNotNull(kbase.getKagents());

        assertEquals(2, kbase.getKsessions().size());
        assertEquals(2, kbase.getKagents().size());

        assertNull(kbase.getKsession("notExists"));
        assertNotNull(kbase.getKsession("ksession1"));
    }

    @Test
    public void testEquals() {
        ServiceKBaseConfig kbase1 = new ServiceKBaseConfig("kbase1");

        assertEquals(kbase1, kbase1);
        assertFalse(kbase1.equals("???"));
        assertFalse(kbase1.equals(null));
        assertFalse(kbase1.equals(new ServiceKBaseConfig("kbase2")));
        assertFalse(kbase1.hashCode() == new ServiceKBaseConfig("kbase2").hashCode());

        kbase1.setAssertBehavior(EQUALITY);
        assertTrue(kbase1.hasConfig());
        assertFalse(new ServiceKBaseConfig("kbase1").equals(kbase1));

        kbase1 = new ServiceKBaseConfig("kbase1");
        kbase1.setEventProcessingMode(CLOUD);
        assertTrue(kbase1.hasConfig());
        assertFalse(new ServiceKBaseConfig("kbase1").equals(kbase1));
        assertFalse(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());

        kbase1 = new ServiceKBaseConfig("kbase1");
        kbase1.setAssetsPassword("password");
        assertFalse(kbase1.hasConfig());
        assertFalse(new ServiceKBaseConfig("kbase1").equals(kbase1));
        assertFalse(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());

        kbase1 = new ServiceKBaseConfig("kbase1");
        kbase1.setAssetsUser("user");
        assertFalse(kbase1.hasConfig());
        assertFalse(new ServiceKBaseConfig("kbase1").equals(kbase1));
        assertFalse(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());

        kbase1 = new ServiceKBaseConfig("kbase1");
        kbase1.setMaxThreads(10);
        assertFalse(new ServiceKBaseConfig("kbase1").equals(kbase1));
        assertFalse(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());

        kbase1 = new ServiceKBaseConfig("kbase1");
        kbase1.setMbeans(true);
        assertFalse(new ServiceKBaseConfig("kbase1").equals(kbase1));
        assertFalse(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());
    }

    @Test
    public void testAddRemoveModels() {
        final ServiceKBaseConfig kbase1 = new ServiceKBaseConfig("kbase1");

        assertNotNull(kbase1.getResources());
        assertNotNull(kbase1.getModels());

        assertEquals(0, kbase1.getModels().size());

        kbase1.addModels(models);
        kbase1.addModels(null);
        kbase1.addModels(new ArrayList<AssetReference>());

        assertEquals(1, kbase1.getModels().size());

        assertFalse(kbase1.equals(new ServiceKBaseConfig("kbase1")));
        assertFalse(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());

        kbase1.removeModel(new AssetReference("myPkg", "a.jar", "model", "http://localhost/a.jar", "uudi44"));
        kbase1.removeModel(new AssetReference("aaamyPkg", "a.jar", "model", "http://localhost/a.jar", "uudi44"));
        kbase1.removeModel(null);

        assertTrue(kbase1.equals(new ServiceKBaseConfig("kbase1")));
        assertTrue(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());

        kbase1.addModel(new AssetReference("aaamyPkg", "a.jar", "model", "http://localhost/a.jar", "uudi44"));
        kbase1.addModel(null);

        assertEquals(1, kbase1.getModels().size());

        kbase1.setModels(null);
        assertEquals(0, kbase1.getModels().size());

        kbase1.setModels(models);
        assertEquals(1, kbase1.getModels().size());
    }

    @Test
    public void testAddRemoveResources() {
        final ServiceKBaseConfig kbase1 = new ServiceKBaseConfig("kbase1");

        assertNotNull(kbase1.getResources());

        assertEquals(0, kbase1.getResources().size());

        kbase1.addResources(resources);
        kbase1.addResources(null);
        kbase1.addResources(new ArrayList<AssetReference>());

        assertEquals(3, kbase1.getResources().size());

        assertFalse(kbase1.equals(new ServiceKBaseConfig("kbase1")));
        assertFalse(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());

        kbase1.removeResource(new AssetReference("myPkg", "a", "drl", "http://localhost/c/source", "uuid1"));
        kbase1.removeResource(new AssetReference("myPkg", "aa", "drl", "http://localhost/cc/source", "uuid2"));
        kbase1.removeResource(new AssetReference("myPkg", "ab", "changeset", "http://localhost/cd/source", "uuid3"));
        kbase1.removeResource(new AssetReference("aaaamyPkg", "ab", "changeset", "http://localhost/cd/source", "uuid3"));
        kbase1.removeResource(null);

        assertTrue(kbase1.equals(new ServiceKBaseConfig("kbase1")));
        assertTrue(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());

        kbase1.addResource(new AssetReference("aaaamyPkg", "ab", "changeset", "http://localhost/cd/source", "uuid3"));
        kbase1.addResource(null);

        assertEquals(1, kbase1.getResources().size());

        kbase1.setResources(null);
        assertEquals(0, kbase1.getResources().size());

        kbase1.setResources(resources);
        assertEquals(3, kbase1.getResources().size());
    }

    @Test
    public void testAddRemoveKAgents() {
        final ServiceKBaseConfig kbase1 = new ServiceKBaseConfig("kbase1");

        assertNotNull(kbase1.getKagents());

        assertEquals(0, kbase1.getKagents().size());

        kbase1.addKagent(new ServiceKAgentConfig("kagent1"));
        kbase1.addKagent(new ServiceKAgentConfig("kagent2"));
        kbase1.addKagent(new ServiceKAgentConfig("kagent3"));
        kbase1.addKagent(null);

        assertEquals(3, kbase1.getKagents().size());

        assertFalse(kbase1.equals(new ServiceKBaseConfig("kbase1")));
        assertFalse(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());

        kbase1.removeKagent("kagent1");
        kbase1.removeKagent("kagent2");
        kbase1.removeKagent("kagent3");
        kbase1.removeKagent("");
        kbase1.removeKagent(null);

        assertTrue(kbase1.equals(new ServiceKBaseConfig("kbase1")));
        assertTrue(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());
    }

    @Test
    public void testAddRemoveKSession() {
        final ServiceKBaseConfig kbase1 = new ServiceKBaseConfig("kbase1");

        assertNotNull(kbase1.getKsessions());

        assertEquals(0, kbase1.getKsessions().size());

        kbase1.addKsession(new ServiceKSessionConfig("ksession1"));
        kbase1.addKsession(new ServiceKSessionConfig("ksession2"));
        kbase1.addKsession(new ServiceKSessionConfig("ksession3"));
        kbase1.addKsession(null);

        assertEquals(3, kbase1.getKsessions().size());

        assertFalse(kbase1.equals(new ServiceKBaseConfig("kbase1")));
        assertFalse(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());

        kbase1.removeKsession("ksession1");
        kbase1.removeKsession("ksession2");
        kbase1.removeKsession("ksession3");
        kbase1.removeKsession("");
        kbase1.removeKsession(null);

        assertTrue(kbase1.equals(new ServiceKBaseConfig("kbase1")));
        assertTrue(kbase1.hashCode() == new ServiceKBaseConfig("kbase1").hashCode());
    }

    @Test
    public void testNextKSessionName() {
        final ServiceKBaseConfig kbase1 = new ServiceKBaseConfig("kbase1");

        assertEquals("ksession1", kbase1.getNextKSessionName());
        assertEquals("ksession1", kbase1.getNextKSessionName());
        kbase1.addKsession(new ServiceKSessionConfig("ksession1"));
        kbase1.addKsession(new ServiceKSessionConfig("ksession3"));
        assertEquals("ksession2", kbase1.getNextKSessionName());
        kbase1.addKsession(new ServiceKSessionConfig("ksession2"));
        assertEquals("ksession4", kbase1.getNextKSessionName());
    }

    @Test
    public void testNextKAgentName() {
        final ServiceKBaseConfig kbase1 = new ServiceKBaseConfig("kbase1");

        assertEquals("kagent1", kbase1.getNextKAgentName());
        assertEquals("kagent1", kbase1.getNextKAgentName());
        kbase1.addKagent(new ServiceKAgentConfig("kagent1"));
        kbase1.addKagent(new ServiceKAgentConfig("kagent3"));
        assertEquals("kagent2", kbase1.getNextKAgentName());
        kbase1.addKagent(new ServiceKAgentConfig("kagent2"));
        assertEquals("kagent4", kbase1.getNextKAgentName());
    }

    @Test
    public void testCloneWithDifferentName() {
        final ServiceKBaseConfig kbase1 = new ServiceKBaseConfig("kbase1");
        kbase1.setMbeans(true);
        kbase1.setModels(models);

        final ServiceKBaseConfig kbase2 = new ServiceKBaseConfig("kbase2", kbase1);

        assertTrue(kbase2.getMbeans());
        assertEquals(1, kbase2.getModels().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        new ServiceKBaseConfig((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyName() {
        new ServiceKBaseConfig("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnCopy() {
        new ServiceKBaseConfig((ServiceKBaseConfig) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnAssetsPassword() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.setAssetsPassword(null);
    }

    @Test
    public void testEmptyOnAssetsPassword() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.setAssetsPassword("");
    }

    @Test
    public void testNullOnAssetsPassword2() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.setAssetsPassword("sss");
        assertNotNull(kbase.getAssetsPassword());
        kbase.setAssetsPasswordToNull();
        assertNull(kbase.getAssetsPassword());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnAssetsUser() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.setAssetsUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyOnAssetsUser() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.setAssetsUser("");
    }

    @Test
    public void testEmptyOnAssetsUser2() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.setAssetsUser("sss");
        assertNotNull(kbase.getAssetsUser());
        kbase.setAssetsUserToNull();
        assertNull(kbase.getAssetsUser());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnEventProcessing() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.setEventProcessingMode(null);
    }

    @Test
    public void testNullOnEventProcessing2() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.setEventProcessingMode(CLOUD);
        assertNotNull(kbase.getEventProcessingMode());
        kbase.setEventProcessingModeToNull();
        assertNull(kbase.getEventProcessingMode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOnAssertBehavior() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.setAssertBehavior(null);
    }

    @Test
    public void testNullOnAssertBehavior2() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.setAssertBehavior(EQUALITY);
        assertNotNull(kbase.getAssertBehavior());
        kbase.setAssertBehaviorToNull();
        assertNull(kbase.getAssertBehavior());
    }

    @Test
    public void testNullOnMbeans() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.setMbeans(true);
        assertNotNull(kbase.getMbeans());
        kbase.setMbeansToNull();
        assertNull(kbase.getMbeans());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testKSessionAlreadyExists() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.addKsession(new ServiceKSessionConfig("ksession1"));
        kbase.addKsession(new ServiceKSessionConfig("ksession1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testKAgentAlreadyExists() {
        final ServiceKBaseConfig kbase = new ServiceKBaseConfig("kbase1");
        kbase.addKagent(new ServiceKAgentConfig("kagent1"));
        kbase.addKagent(new ServiceKAgentConfig("kagent1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCloneNull() {
        new ServiceKBaseConfig("kbase2", null);
    }

}
