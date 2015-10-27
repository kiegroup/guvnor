/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.common.services.project.backend.server;

import java.net.URL;
import java.util.List;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class POMServiceImplLoadTest
        extends CDITestBase {

    private POMService service;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        service = getReference( POMService.class );
    }

    @Test
    public void testLoad() throws Exception {
        final URL url = this.getClass().getResource( "/TestProject/pom.xml" );

        POM pom = service.load( Paths.convert( fs.getPath( url.toURI() ) ) );

        assertEquals( "org.test", pom.getGav().getGroupId() );
        assertEquals( "my-test", pom.getGav().getArtifactId() );
        assertEquals( "1.0", pom.getGav().getVersion() );

        assertEquals( 2, pom.getDependencies().size() );

        assertContainsDependency( "org.apache.commons", "commons-lang3", "compile",
                                  pom.getDependencies() );
        assertContainsDependency( "org.jboss.weld", "weld-core", "test",
                                  pom.getDependencies() );
    }

    private void assertContainsDependency( String groupID,
                                           String artifactID,
                                           String scope,
                                           List<Dependency> dependencies ) {
        boolean foundOne = false;
        for (Dependency dependency : dependencies) {
            if ( groupID.equals( dependency.getGroupId() )
                    && artifactID.equals( dependency.getArtifactId() )
                    &&
                    (
                            scope.equals( dependency.getScope() )
                                    || (scope.equals( "compile" ) && dependency.getScope() == null)
                    ) ) {
                foundOne = true;
            }
        }

        assertTrue( "Did not find dependency: " + groupID + ":" + artifactID + ":" + scope, foundOne );
    }
}