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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.guvnor.client.rpc.MavenArtifact;
import org.drools.guvnor.server.maven.cache.GuvnorArtifactCacheSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.io.FileUtils.*;
import static org.drools.guvnor.server.maven.ArtifactDependencySupport.*;
import static org.drools.guvnor.server.maven.cache.GuvnorArtifactCacheSupport.*;
import static org.junit.Assert.*;

public class GuvnorArtifactCacheSupportTest {

    private static final String GUVNOR_TEMP_DIR = GuvnorArtifactCacheSupport.getGuvnorTempDir();

    @Before
    @After
    public void cleanUp() {
        cleanTempDir();
    }

    @Test
    public void testCacheWithRepeatedArtifacts() {

        final Collection<String> repositories = new ArrayList<String>() {{
            add(getURLtoLocalUserMavenRepo());
        }};

        final Collection<MavenArtifact> dependencies = new ArrayList<MavenArtifact>() {{
            add(new MavenArtifact("org.antlr:antlr-runtime:jar:3.3:compile"));
            add(new MavenArtifact("org.antlr:antlr-runtime:jar:3.3:compile"));
            add(new MavenArtifact("org.antlr:antlr-runtime:jar:3.3:compile"));
        }};

        buildCache(repositories, dependencies);

        final Iterator iterator = iterateFiles(new File(GUVNOR_TEMP_DIR), new String[]{"jar", "war", "ear"}, false);
        int count = 0;
        while (iterator.hasNext()) {
            count++;
            final File activeFile = (File) iterator.next();
            assertEquals(new MavenArtifact("org.antlr:antlr-runtime:jar:3.3:compile").toFileName(), activeFile.getName());
        }

        assertEquals(1, count);
    }

    @Test
    public void testCacheWithNonExistentArtifact() {
        final Collection<String> repositories = new ArrayList<String>() {{
            add(getURLtoLocalUserMavenRepo());
        }};

        final Collection<MavenArtifact> dependencies = new ArrayList<MavenArtifact>() {{
            add(new MavenArtifact("org.some:artifact-runtime:jar:0.1:compile"));
        }};

        buildCache(repositories, dependencies);

        final Iterator iterator = iterateFiles(new File(GUVNOR_TEMP_DIR), new String[]{"jar", "war", "ear"}, false);
        int count = 0;
        while (iterator.hasNext()) {
            count++;
        }

        assertEquals(0, count);
    }

    @Test
    public void testCacheMultipleFilesAndOneMissing() {
        final Collection<String> repositories = new ArrayList<String>() {{
            add(getURLtoLocalUserMavenRepo());
        }};

        final Collection<MavenArtifact> dependencies = new ArrayList<MavenArtifact>() {{
            add(new MavenArtifact("org.some:artifact-runtime:jar:0.1:compile"));
            add(new MavenArtifact("log4j:log4j:jar:1.2.16:compile"));
            add(new MavenArtifact("commons-io:commons-io:jar:1.4:compile"));
            add(new MavenArtifact("jdom:jdom:jar:1.0:compile"));
        }};

        buildCache(repositories, dependencies);

        final Iterator iterator = iterateFiles(new File(GUVNOR_TEMP_DIR), new String[]{"jar", "war", "ear"}, false);
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }

        assertEquals(3, count);

        //build againg, should keep the same items
        buildCache(repositories, dependencies);
        final Iterator iterator2 = iterateFiles(new File(GUVNOR_TEMP_DIR), new String[]{"jar", "war", "ear"}, false);
        int count2 = 0;
        while (iterator2.hasNext()) {
            iterator2.next();
            count2++;
        }

        assertEquals(3, count2);
    }

    @Test(expected = NullPointerException.class)
    public void testResolveDependenciesNPE() {
        resolveDependencies(null);
    }

    @Test
    public void testResolveDependencies() {
        final Collection<String> repositories = new ArrayList<String>() {{
            add(getURLtoLocalUserMavenRepo());
        }};

        final Collection<MavenArtifact> dependencies = new ArrayList<MavenArtifact>() {{
            add(new MavenArtifact("org.some:artifact-runtime:jar:0.1:compile"));
            add(new MavenArtifact("log4j:log4j:jar:1.2.16:compile"));
            add(new MavenArtifact("commons-io:commons-io:jar:1.4:compile"));
            add(new MavenArtifact("jdom:jdom:jar:1.0:compile"));
        }};

        buildCache(repositories, dependencies);

        final Collection<File> files = resolveDependencies(dependencies);
        assertEquals(3, files.size());
    }

    @Test
    public void testMavenRepoList() {
        assertTrue(getMavenRepositoryList().contains(getURLtoLocalUserMavenRepo()));
    }

    @Test
    public void testResolveArtifacts() {
        final Collection<MavenArtifact> dependencies = getDependencyList();
        final Collection<File> files = resolveArtifacts(null);
        assertEquals(dependencies.size(), files.size());
    }

    @Test
    public void testResolveArtifactsWithoutSome() {
        final Collection<MavenArtifact> dependenciesToBeRemoved = new ArrayList<MavenArtifact>();

        int count = 0;
        for (final MavenArtifact dependency : dependenciesToBeRemoved) {
            count++;
            dependenciesToBeRemoved.add(dependency);
            if (count > 3) {
                break;
            }
        }

        final Collection<File> files = resolveArtifacts(dependenciesToBeRemoved);
        assertEquals(getDependencyList().size() - dependenciesToBeRemoved.size(), files.size());
    }

}
