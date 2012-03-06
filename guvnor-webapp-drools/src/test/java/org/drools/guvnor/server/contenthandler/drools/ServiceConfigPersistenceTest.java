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

package org.drools.guvnor.server.contenthandler.drools;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig;
import org.drools.guvnor.client.rpc.MavenArtifact;
import org.junit.Test;

import static junit.framework.Assert.*;

public class ServiceConfigPersistenceTest {

    private static final Collection<AssetReference> resources = new ArrayList<AssetReference>() {{
        add(new AssetReference("myPkg", "a", "drl", "http://localhost/c/source", "uuid1"));
        add(new AssetReference("myPkg", "aa", "drl", "http://localhost/cc/source", "uuid2"));
        add(new AssetReference("myPkg", "ab", "change_set", "http://localhost/cd/source", "uuid3"));
    }};

    private static final Collection<AssetReference> models = new ArrayList<AssetReference>() {{
        add(new AssetReference("myPkg", "a.jar", "model", "http://localhost/a.jar", "uudi44"));
    }};

    private static final Collection<MavenArtifact> exclucedArtifacts = new ArrayList<MavenArtifact>() {{
        add(new MavenArtifact("org.drools:knowledge-api:jar:5.4.0-SNAPSHOT:compile"));
        add(new MavenArtifact("org.apache.camel:camel-core:test-jar:tests:2.4.0:test"));
    }};

    private static final ServiceConfig REST_SERVICE_CONFIG = null;//new ServiceConfig("70", "rest", resources, models, null);
    private static final ServiceConfig WS_SERVICE_CONFIG = null;//new ServiceConfig("70", "ws", resources, models, exclucedArtifacts);

    @Test
    public void testEmpty() {
        final String result = ServiceConfigPersistence.getInstance().marshal(null);
        assertEquals("", result);

        final ServiceConfig config = ServiceConfigPersistence.getInstance().unmarshal("");
        assertNotNull(config);
        assertEquals(new ServiceConfig(), config);
    }

    @Test
    public void testAssets() {
        final String result = ServiceConfigPersistence.getInstance().marshal(REST_SERVICE_CONFIG);
        assertFalse(result.equals(""));

        final ServiceConfig config = ServiceConfigPersistence.getInstance().unmarshal(result);
        assertNotNull(config);
        assertEquals(REST_SERVICE_CONFIG, config);
    }

    @Test
    public void testComplete() {
        final String result = ServiceConfigPersistence.getInstance().marshal(WS_SERVICE_CONFIG);
        assertFalse(result.equals(""));

        final ServiceConfig config = ServiceConfigPersistence.getInstance().unmarshal(result);
        assertNotNull(config);
        assertEquals(WS_SERVICE_CONFIG, config);
    }

}
