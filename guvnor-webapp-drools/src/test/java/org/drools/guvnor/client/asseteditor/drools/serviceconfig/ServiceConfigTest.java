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

import static java.util.Collections.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig.Protocol.*;
import static org.junit.Assert.*;

public class ServiceConfigTest {

    @Test
    public void testConstructorWebService() {
        final ServiceConfig basicConfig = new ServiceConfig("70", "web_service", null, null, null);

        assertNotNull(basicConfig);
        assertEquals(WEB_SERVICE, basicConfig.getProtocol());
        assertEquals(70, basicConfig.getPollingFrequency());
        assertEquals(emptyList(), basicConfig.getModels());
        assertEquals(emptyList(), basicConfig.getResources());
        assertEquals(emptyList(), basicConfig.getExcludedArtifacts());

        //ws is also an option
        assertEquals(WEB_SERVICE, new ServiceConfig("70", "ws", null, null, null).getProtocol());

        assertEquals(basicConfig, new ServiceConfig(basicConfig));
        assertEquals(basicConfig.hashCode(), new ServiceConfig(basicConfig).hashCode());
    }

    @Test
    public void testConstructorRest() {
        final ServiceConfig basicConfig = new ServiceConfig("70", "rest", null, null, null);

        assertNotNull(basicConfig);
        assertEquals(REST, basicConfig.getProtocol());
        assertEquals(70, basicConfig.getPollingFrequency());
        assertEquals(emptyList(), basicConfig.getModels());
        assertEquals(emptyList(), basicConfig.getResources());
        assertEquals(emptyList(), basicConfig.getExcludedArtifacts());

        //default is always rest
        assertEquals(REST, new ServiceConfig("70", "some", null, null, null).getProtocol());

        assertEquals(basicConfig, new ServiceConfig(basicConfig));
        assertEquals(basicConfig.hashCode(), new ServiceConfig(basicConfig).hashCode());
    }

    @Test
    public void testConstructorDefaultValues() {
        final ServiceConfig basicConfig = new ServiceConfig();

        assertNotNull(basicConfig);
        assertEquals(REST, basicConfig.getProtocol());
        assertEquals(60, basicConfig.getPollingFrequency());
        assertEquals(emptyList(), basicConfig.getModels());
        assertEquals(emptyList(), basicConfig.getResources());
        assertEquals(emptyList(), basicConfig.getExcludedArtifacts());

        assertEquals(basicConfig, new ServiceConfig(basicConfig));
        assertEquals(basicConfig.hashCode(), new ServiceConfig(basicConfig).hashCode());
    }

    @Test
    public void testAdvancedSetup() {

        final Collection<ServiceConfig.AssetReference> resources = new ArrayList<ServiceConfig.AssetReference>() {{
            add(new ServiceConfig.AssetReference("myPkg", "a", "drl", "http://localhost/c/source", "uuid1"));
            add(new ServiceConfig.AssetReference("myPkg", "aa", "drl", "http://localhost/cc/source", "uuid2"));
            add(new ServiceConfig.AssetReference("myPkg", "ab", "change_set", "http://localhost/cd/source", "uuid3"));
        }};

        final Collection<ServiceConfig.AssetReference> models = new ArrayList<ServiceConfig.AssetReference>() {{
            add(new ServiceConfig.AssetReference("myPkg", "a.jar", "model", "http://localhost/a.jar", "uudi44"));
        }};

        final ServiceConfig basicConfig = new ServiceConfig("70", "rest", resources, models, null);

        assertNotNull(basicConfig);
        assertEquals(REST, basicConfig.getProtocol());
        assertEquals(70, basicConfig.getPollingFrequency());
        assertEquals(models, basicConfig.getModels());
        assertEquals(resources, basicConfig.getResources());
        assertEquals(emptyList(), basicConfig.getExcludedArtifacts());

        final ServiceConfig rebuild = new ServiceConfig(basicConfig);
        assertEquals(basicConfig, rebuild);
        assertEquals(basicConfig.hashCode(), rebuild.hashCode());

        final Collection<MavenArtifact> exclucedArtifacts = new ArrayList<MavenArtifact>() {{
            add(new MavenArtifact("org.drools:knowledge-api:jar:5.4.0-SNAPSHOT:compile"));
            add(new MavenArtifact("org.apache.camel:camel-core:test-jar:tests:2.4.0:test"));
        }};

        rebuild.setExcludedArtifacts(exclucedArtifacts);

        assertFalse(basicConfig.equals(rebuild));
        assertFalse(basicConfig.hashCode() == rebuild.hashCode());

        //now consistency with excluded artifacts
        assertEquals(rebuild, new ServiceConfig(rebuild));
        assertEquals(rebuild.hashCode(), new ServiceConfig(rebuild).hashCode());
    }

    @Test(expected = IllegalStateException.class)
    public void testConstructorInvalidFrequency() {
        new ServiceConfig("70a", "web_service", null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullString() {
        new ServiceConfig(null);
    }

    @Test
    public void testAssetReferenceExplicitConstructor() {
        final ServiceConfig.AssetReference assetReference = new ServiceConfig.AssetReference("a", "b", "c", "d", "e");

        assertNotNull(assetReference);
        assertEquals("a", assetReference.getPkg());
        assertEquals("b", assetReference.getName());
        assertEquals("c", assetReference.getFormat());
        assertEquals("d", assetReference.getUrl());
        assertEquals("e", assetReference.getUuid());
    }

    @Test
    public void testAssetReferenceToValueConsistency() {
        final ServiceConfig.AssetReference assetReference = new ServiceConfig.AssetReference("a", "b", "c", "d", "e");

        assertEquals(assetReference, new ServiceConfig.AssetReference(assetReference));
        assertEquals(assetReference.hashCode(), new ServiceConfig.AssetReference(assetReference).hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceNullConstructor() {
        new ServiceConfig.AssetReference(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorNull1() {
        new ServiceConfig.AssetReference(null, "b", "c", "d", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorEmpty1() {
        new ServiceConfig.AssetReference("", "b", "c", "d", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorNull2() {
        new ServiceConfig.AssetReference("a", null, "c", "d", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorEmpty2() {
        new ServiceConfig.AssetReference("a", "", "c", "d", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorNull3() {
        new ServiceConfig.AssetReference("a", "b", null, "d", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorEmpty3() {
        new ServiceConfig.AssetReference("a", "b", "", "d", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorNull4() {
        new ServiceConfig.AssetReference("a", "b", "c", null, "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorEmpty4() {
        new ServiceConfig.AssetReference("a", "b", "c", "", "e");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorNull5() {
        new ServiceConfig.AssetReference("a", "b", "c", "d", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetReferenceExplicitConstructorEmpty5() {
        new ServiceConfig.AssetReference("a", "b", "c", "d", "");
    }

}
