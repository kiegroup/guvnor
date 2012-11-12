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

package org.drools.guvnor.client.rpc;

import org.junit.Test;

import static org.junit.Assert.*;

public class MavenArtifactTest {

    @Test
    public void testConstructFromStringWithoutComplement() {
        final String mavenArtifactValue = "org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile";
        final MavenArtifact artifact = new MavenArtifact(mavenArtifactValue);

        assertNotNull(artifact);
        assertNotNull(artifact.getChild());
        assertEquals(0, artifact.getChild().size());
        assertEquals(false, artifact.hasChild());
        assertEquals(true, artifact.isNecessaryOnRuntime());
        assertEquals("knowledge-api-5.5.0-SNAPSHOT.jar", artifact.toFileName());
        assertEquals("http://my/site/org/drools/knowledge-api/5.5.0-SNAPSHOT/knowledge-api-5.5.0-SNAPSHOT.jar",
                artifact.toURL("http://my/site/"));
        assertEquals("http://my/site/org/drools/knowledge-api/5.5.0-SNAPSHOT/knowledge-api-5.5.0-SNAPSHOT.jar",
                artifact.toURL("http://my/site"));
        assertEquals("org.kie:knowledge-api-5.5.0-SNAPSHOT.jar",
                artifact.toLabel());
        assertEquals(mavenArtifactValue,
                artifact.toValue());
    }

    @Test
    public void testConstructFromStringWithComplement() {
        final String mavenArtifactValue = "org.apache.camel:camel-core:test-jar:tests:2.4.0:test";
        final MavenArtifact artifact = new MavenArtifact(mavenArtifactValue);

        assertNotNull(artifact);
        assertNotNull(artifact.getChild());
        assertEquals(0, artifact.getChild().size());
        assertEquals(false, artifact.hasChild());
        assertEquals(false, artifact.isNecessaryOnRuntime());
        assertEquals("camel-core-2.4.0-tests.jar", artifact.toFileName());
        assertEquals("http://my/site/org/apache/camel/camel-core/2.4.0/camel-core-2.4.0-tests.jar",
                artifact.toURL("http://my/site/"));
        assertEquals("http://my/site/org/apache/camel/camel-core/2.4.0/camel-core-2.4.0-tests.jar",
                artifact.toURL("http://my/site"));
        assertEquals("org.apache.camel:camel-core-2.4.0-tests.jar",
                artifact.toLabel());
        assertEquals(mavenArtifactValue,
                artifact.toValue());
    }

    @Test
    public void testEquals() {
        final String mavenArtifactValue = "org.apache.camel:camel-core:test-jar:tests:2.4.0:test";
        final MavenArtifact artifact = new MavenArtifact(mavenArtifactValue);

        assertEquals(new MavenArtifact(mavenArtifactValue), artifact);
        assertEquals(new MavenArtifact(mavenArtifactValue).hashCode(), artifact.hashCode());

        final MavenArtifact parent = new MavenArtifact("org.springframework:spring:jar:2.5.6:compile");
        parent.addChild(new MavenArtifact("commons-logging:commons-logging:jar:1.1.1:compile"));

        assertEquals(new MavenArtifact("org.springframework:spring:jar:2.5.6:compile"), parent);
        assertEquals(new MavenArtifact("org.springframework:spring:jar:2.5.6:compile").hashCode(), parent.hashCode());

        assertEquals(new MavenArtifact(artifact), artifact);
        assertEquals(new MavenArtifact(artifact).hashCode(), artifact.hashCode());
    }

    @Test
    public void testNotEquals() {
        //different on complement
        assertFalse(new MavenArtifact("org.apache.camel:camel-core:test-jar:tests:2.4.0:test")
                .equals(new MavenArtifact("org.apache.camel:camel-core:test-jar:2.4.0:test")));

        //different on group
        assertFalse(new MavenArtifact("org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile")
                .equals(new MavenArtifact("org.jboss:knowledge-api:jar:5.5.0-SNAPSHOT:compile")));

        //different on version
        assertFalse(new MavenArtifact("org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile")
                .equals(new MavenArtifact("org.kie:knowledge-api:jar:5.4.0:compile")));

        //different on artifact
        assertFalse(new MavenArtifact("org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile")
                .equals(new MavenArtifact("org.kie:knowledge-core:jar:5.5.0-SNAPSHOT:compile")));

        //different on type
        assertFalse(new MavenArtifact("org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile")
                .equals(new MavenArtifact("org.kie:knowledge-api:war:5.5.0-SNAPSHOT:compile")));

        //different on scope
        assertFalse(new MavenArtifact("org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile")
                .equals(new MavenArtifact("org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:test")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructInvalidStringMissingGroup() {
        final String mavenArtifactValue = "knowledge-api:jar:5.5.0-SNAPSHOT:compile";
        new MavenArtifact(mavenArtifactValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructInvalidStringLotsOfUnncesaryStrings() {
        final String mavenArtifactValue = "org.kie:invalid:data:here:knowledge-api:jar:5.5.0-SNAPSHOT:compile";
        new MavenArtifact(mavenArtifactValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNull() {
        new MavenArtifact((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNull2() {
        new MavenArtifact((MavenArtifact) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmpty() {
        new MavenArtifact("");
    }
}