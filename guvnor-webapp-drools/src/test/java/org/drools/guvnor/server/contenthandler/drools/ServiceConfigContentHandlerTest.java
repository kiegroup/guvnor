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

import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig;
import org.drools.guvnor.client.rpc.MavenArtifact;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceConfigContentHandlerTest {

    final Collection<ServiceConfig.AssetReference> resources = new ArrayList<ServiceConfig.AssetReference>() {{
        add(new ServiceConfig.AssetReference("pkgRef|a|drl|http://localhost/c/source|uuid1"));
        add(new ServiceConfig.AssetReference("pkgRef|aa|drl|http://localhost/cc/source|uuid2"));
        add(new ServiceConfig.AssetReference("pkgRef|ab|change_set|http://localhost/cd/source|uuid3"));
    }};

    final Collection<ServiceConfig.AssetReference> models = new ArrayList<ServiceConfig.AssetReference>() {{
        add(new ServiceConfig.AssetReference("pkgRef|a.jar|model|http://localhost/a.jar|uuidx"));
    }};

    final Collection<MavenArtifact> exclucedArtifacts = new ArrayList<MavenArtifact>() {{
        add(new MavenArtifact("org.drools:knowledge-api:jar:5.4.0-SNAPSHOT:compile"));
        add(new MavenArtifact("org.apache.camel:camel-core:test-jar:tests:2.4.0:test"));
    }};

    @Test
    public void testValidContent() {
        final ServiceConfigContentHandler sch = new ServiceConfigContentHandler();

        final ServiceConfig basicRestConfig = new ServiceConfig("70", "rest", resources, models, exclucedArtifacts);
        final ServiceConfig basicWsConfig = new ServiceConfig("70", "ws", resources, models, exclucedArtifacts);

        final String validRestInput = sch.validate(basicRestConfig.toContent());
        assertNotNull(validRestInput);
        assertEquals(0, validRestInput.length());

        final String validWsInput = sch.validate(basicWsConfig.toContent());
        assertNotNull(validWsInput);
        assertEquals(0, validWsInput.length());

        final String emptyInput = sch.validate("");
        assertNotNull(emptyInput);
        assertEquals(0, emptyInput.length());
    }

    @Test
    public void testInvalidPollingFrequency() {
        final ServiceConfigContentHandler sch = new ServiceConfigContentHandler();

        final String content = "polling=70a\n" +
                "protocol=REST\n" +
                "resource=pkgRef|a|drl|http://localhost/c/source|uuidx\n" +
                "model=pkgRef|a.jar|model|http://localhost/a.jar|uuidy\n" +
                "excluded.artifact=org.apache.camel:camel-core:test-jar:tests:2.4.0:test\n";

        final String invalidResult = sch.validate(content);
        assertNotNull(invalidResult);
        assertTrue(invalidResult.length() != 0);
        assertEquals("Invalid polling format.", invalidResult);
    }

    @Test
    public void testInvalidProtocol() {
        final ServiceConfigContentHandler sch = new ServiceConfigContentHandler();

        final String content = "polling=70\n" +
                "protocol=some\n" +
                "resource=pkgRef|a|drl|http://localhost/c/source|uuidx\n" +
                "model=pkgRef|a.jar|model|http://localhost/a.jar|uuidy\n" +
                "excluded.artifact=org.apache.camel:camel-core:test-jar:tests:2.4.0:test\n";

        final String invalidResult = sch.validate(content);
        assertNotNull(invalidResult);
        assertTrue(invalidResult.length() != 0);
        assertEquals("Invalid protocol.", invalidResult);
    }

    @Test
    public void testInvalidResourceFormat() {
        final ServiceConfigContentHandler sch = new ServiceConfigContentHandler();

        final String content = "polling=70\n" +
                "protocol=REST\n" +
                "resource=a|http://localhost/c/source\n" +
                "model=pkgRef|a.jar|model|http://localhost/a.jar|uuidy\n" +
                "excluded.artifact=org.apache.camel:camel-core:test-jar:tests:2.4.0:test\n";

        final String invalidResult = sch.validate(content);
        assertNotNull(invalidResult);
        assertTrue(invalidResult.length() != 0);
        assertEquals("Invalid resource format.", invalidResult);
    }

    @Test
    public void testInvalidModelFormat() {
        final ServiceConfigContentHandler sch = new ServiceConfigContentHandler();

        final String content = "polling=70\n" +
                "protocol=REST\n" +
                "resource=pkgRef|a|drl|http://localhost/c/source|uuidx\n" +
                "model=a.jar\n" +
                "excluded.artifact=org.apache.camel:camel-core:test-jar:tests:2.4.0:test\n";

        final String invalidResult = sch.validate(content);
        assertNotNull(invalidResult);
        assertTrue(invalidResult.length() != 0);
        assertEquals("Invalid model format.", invalidResult);
    }

    @Test
    public void testInvalidExclArtifactFormat() {
        final ServiceConfigContentHandler sch = new ServiceConfigContentHandler();

        final String content = "polling=70\n" +
                "protocol=REST\n" +
                "resource=pkgRef|a|drl|http://localhost/c/source|uuidx\n" +
                "model=pkgRef|a.jar|model|http://localhost/a.jar|uuidy\n" +
                "excluded.artifact=camel-core:test-jar:2.4.0:test\n";

        final String invalidResult = sch.validate(content);
        assertNotNull(invalidResult);
        assertTrue(invalidResult.length() != 0);
        assertEquals("Invalid excluded artifact format.", invalidResult);

        final String content2 = "polling=70\n" +
                "protocol=REST\n" +
                "resource=pkgRef|a|drl|http://localhost/c/source|uuidx\n" +
                "model=pkgRef|a.jar|model|http://localhost/a.jar|uuidy\n" +
                "excluded.artifact=camel-core:test-jar:2.4.0:test:2:3:3:3:3:3:3:3\n";

        final String invalid2 = sch.validate(content2);
        assertNotNull(invalid2);
        assertTrue(invalid2.length() != 0);
        assertEquals("Invalid excluded artifact format.", invalid2);
    }

    @Test
    public void testCompleteInvalidFormat() {
        final ServiceConfigContentHandler sch = new ServiceConfigContentHandler();

        final String content = "psdfkjssaf hsdol\n\n    ";

        final String invalidResult = sch.validate(content);
        assertNotNull(invalidResult);
        assertTrue(invalidResult.length() != 0);
        assertEquals("Invalid data entry", invalidResult);
    }
}
