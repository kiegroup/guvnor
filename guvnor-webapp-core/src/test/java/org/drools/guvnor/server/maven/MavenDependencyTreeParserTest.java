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

package org.drools.guvnor.server.maven;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.drools.guvnor.client.rpc.MavenArtifact;
import org.drools.guvnor.server.maven.parser.MavenDependencyTreeParser;
import org.junit.Test;

import static org.junit.Assert.*;

public class MavenDependencyTreeParserTest {

    @Test(expected = NullPointerException.class)
    public void testStreamNullPointer() {
        final MavenDependencyTreeParser parser = new MavenDependencyTreeParser();
        parser.buildDependencyTreeAndList((InputStream) null);
    }

    @Test(expected = NullPointerException.class)
    public void testStringNullPointer() {
        final MavenDependencyTreeParser parser = new MavenDependencyTreeParser();
        parser.buildDependencyTreeAndList((String) null);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidInput() {
        final MavenDependencyTreeParser parser = new MavenDependencyTreeParser();

        final String input = "some adkk ajhfklasjh\nkjwhgssf\n   dsdfasdf\n";

        parser.buildDependencyTreeAndList(input);
    }

    @Test
    public void testEmptyInput() {
        final MavenDependencyTreeParser parser = new MavenDependencyTreeParser();

        final MavenDependencyTreeParser.Pair<Collection<MavenArtifact>, Collection<MavenArtifact>> result;
        result = parser.buildDependencyTreeAndList("");

        assertNotNull(result);
        assertNotNull(result.getV1());
        assertNotNull(result.getV2());
        assertEquals(0, result.getV1().size());
        assertEquals(0, result.getV2().size());
    }

    @Test
    public void testNoTreeJustRoot() {
        final MavenDependencyTreeParser parser = new MavenDependencyTreeParser();

        final MavenDependencyTreeParser.Pair<Collection<MavenArtifact>, Collection<MavenArtifact>> result;
        result = parser.buildDependencyTreeAndList("org.kie:drools-camel-server-example:war:5.5.0-SNAPSHOT\n");

        assertNotNull(result);
        assertNotNull(result.getV1());
        assertNotNull(result.getV2());
        assertEquals(0, result.getV1().size());
        assertEquals(0, result.getV2().size());
    }

    @Test
    public void testTreeOneChild() {
        final MavenDependencyTreeParser parser = new MavenDependencyTreeParser();

        final String input = "org.kie:drools-camel-server-example:war:5.5.0-SNAPSHOT\n" +
                "+- org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile";

        final MavenDependencyTreeParser.Pair<Collection<MavenArtifact>, Collection<MavenArtifact>> result;
        result = parser.buildDependencyTreeAndList(input);

        assertNotNull(result);
        assertNotNull(result.getV1());
        assertNotNull(result.getV2());
        assertEquals(1, result.getV1().size());
        assertEquals(1, result.getV2().size());

        result.getV1().iterator().next().equals(new MavenArtifact("org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile"));
        result.getV2().iterator().next().equals(new MavenArtifact("org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile"));
    }

    @Test
    public void testTreeTwoChild() {
        final MavenDependencyTreeParser parser = new MavenDependencyTreeParser();

        final String input = "org.kie:drools-camel-server-example:war:5.5.0-SNAPSHOT\n" +
                "+- org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile\n" +
                "+- org.kie:drools-core:jar:5.5.0-SNAPSHOT:compile\n";

        final MavenDependencyTreeParser.Pair<Collection<MavenArtifact>, Collection<MavenArtifact>> result;
        result = parser.buildDependencyTreeAndList(input);

        assertNotNull(result);
        assertNotNull(result.getV1());
        assertNotNull(result.getV2());
        assertEquals(2, result.getV1().size());
        assertEquals(2, result.getV2().size());
    }

    @Test
    public void testTreeTwoChildButSecondIsJustForTest() {
        final MavenDependencyTreeParser parser = new MavenDependencyTreeParser();

        final String input = "org.kie:drools-camel-server-example:war:5.5.0-SNAPSHOT\n" +
                "+- org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile\n" +
                "+- org.kie:drools-core:jar:5.5.0-SNAPSHOT:test\n";

        final MavenDependencyTreeParser.Pair<Collection<MavenArtifact>, Collection<MavenArtifact>> result;
        result = parser.buildDependencyTreeAndList(input);

        assertNotNull(result);
        assertNotNull(result.getV1());
        assertNotNull(result.getV2());
        assertEquals(1, result.getV1().size());
        assertEquals(1, result.getV2().size());
    }

    @Test
    public void testTreeTwoChildSecondHasComments() {
        final MavenDependencyTreeParser parser = new MavenDependencyTreeParser();

        final String input = "org.kie:drools-camel-server-example:war:5.5.0-SNAPSHOT\n" +
                "+- org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile (version managed from 3.1)\n" +
                "+- org.kie:drools-core:jar:5.5.0-SNAPSHOT:compile\n";

        final MavenDependencyTreeParser.Pair<Collection<MavenArtifact>, Collection<MavenArtifact>> result;
        result = parser.buildDependencyTreeAndList(input);

        assertNotNull(result);
        assertNotNull(result.getV1());
        assertNotNull(result.getV2());
        assertEquals(2, result.getV1().size());
        assertEquals(2, result.getV2().size());

        result.getV1().iterator().next().equals(new MavenArtifact("org.kie:knowledge-api:jar:5.5.0-SNAPSHOT:compile"));
    }

    @Test
    public void testTreeComplex() {
        final MavenDependencyTreeParser parser = new MavenDependencyTreeParser();

        final String input = "org.kie:drools-camel-server-example:war:5.5.0-SNAPSHOT\n" +
                "+- org.kie:drools-camel:jar:5.5.0-SNAPSHOT:compile\n" +
                "|  +- org.springframework:spring-aop:jar:2.5.6:compile\n" +
                "|  \\- org.apache.cxf:cxf-rt-frontend-jaxws:jar:2.4.4:compile\n" +
                "|     +- xml-resolver:xml-resolver:jar:1.2:compile\n" +
                "|     |  +- org.apache.cxf:cxf-tools-common:jar:2.4.4:compile\n" +
                "|     |  \\- org.apache.cxf:cxf-rt-databinding-jaxb:jar:2.4.4:compile\n" +
                "|     +- org.jboss:something:jar:2.4.4:compile\n" +
                "+- org.apache.camel:camel-core:jar:2.4.0:compile\n";

        final MavenDependencyTreeParser.Pair<Collection<MavenArtifact>, Collection<MavenArtifact>> result;
        result = parser.buildDependencyTreeAndList(input);

        assertNotNull(result);
        assertNotNull(result.getV1());
        assertNotNull(result.getV2());
        assertEquals(8, result.getV2().size());
        assertEquals(2, result.getV1().size());

        for (final MavenArtifact artifact : result.getV1()) {
            if (artifact.equals(new MavenArtifact("org.kie:drools-camel:jar:5.5.0-SNAPSHOT:compile"))) {
                validateDroolsCamel(artifact);
            } else if (artifact.equals(new MavenArtifact("org.apache.camel:camel-core:jar:2.4.0:compile"))) {
                assertFalse(artifact.hasChild());
            } else {
                fail();
            }
        }
    }

    private void validateDroolsCamel(final MavenArtifact artifact) {
        assertEquals(2, artifact.getChild().size());

        final Iterator<MavenArtifact> droolsCamelChildIterator =  artifact.getChild().iterator();
        final MavenArtifact shouldBeSpringAOP = droolsCamelChildIterator.next();

        assertEquals(new MavenArtifact("org.springframework:spring-aop:jar:2.5.6:compile"), shouldBeSpringAOP);
        assertEquals(0, shouldBeSpringAOP.getChild().size());

        final MavenArtifact shouldBeCxfRtFrontendJaxws = droolsCamelChildIterator.next();

        assertEquals(new MavenArtifact("org.apache.cxf:cxf-rt-frontend-jaxws:jar:2.4.4:compile"), shouldBeCxfRtFrontendJaxws);
        assertEquals(2, shouldBeCxfRtFrontendJaxws.getChild().size());

        final Iterator<MavenArtifact> cxfRtFrontendJaxwsChildIterator =  shouldBeCxfRtFrontendJaxws.getChild().iterator();
        final MavenArtifact shouldXmlResolver = cxfRtFrontendJaxwsChildIterator.next();
        assertEquals(new MavenArtifact("xml-resolver:xml-resolver:jar:1.2:compile"), shouldXmlResolver);
        assertEquals(2, shouldXmlResolver.getChild().size());

        final Iterator<MavenArtifact> xmlResolverChildIterator =  shouldXmlResolver.getChild().iterator();

        final MavenArtifact shouldCxfToolsCommon = xmlResolverChildIterator.next();
        assertEquals(new MavenArtifact("org.apache.cxf:cxf-tools-common:jar:2.4.4:compile"), shouldCxfToolsCommon);
        assertEquals(0, shouldCxfToolsCommon.getChild().size());

        final MavenArtifact shouldcxfRtDatabindingJaxb = xmlResolverChildIterator.next();
        assertEquals(new MavenArtifact("org.apache.cxf:cxf-rt-databinding-jaxb:jar:2.4.4:compile"), shouldcxfRtDatabindingJaxb);
        assertEquals(0, shouldcxfRtDatabindingJaxb.getChild().size());


        final MavenArtifact shouldJbossSomething = cxfRtFrontendJaxwsChildIterator.next();
        assertEquals(new MavenArtifact("org.jboss:something:jar:2.4.4:compile"), shouldJbossSomething);
        assertEquals(0, shouldJbossSomething.getChild().size());
    }

}
