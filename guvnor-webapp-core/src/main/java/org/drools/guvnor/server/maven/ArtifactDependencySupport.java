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
import java.util.ArrayList;
import java.util.Collection;

import org.drools.guvnor.client.rpc.MavenArtifact;
import org.drools.guvnor.server.maven.parser.MavenDependencyTreeParser;

import static java.util.Collections.*;

public final class ArtifactDependencySupport {

    private ArtifactDependencySupport() {
    }

    private static Collection<MavenArtifact> dependencyTree = null;
    private static Collection<MavenArtifact> dependencyList = null;

    public static synchronized Collection<MavenArtifact> getDependencyTree() {
        if (dependencyTree == null) {
            buildRuntimeDependencyTree();
        }
        return dependencyTree;
    }

    public static synchronized Collection<MavenArtifact> getDependencyList() {
        if (dependencyList == null) {
            buildRuntimeDependencyTree();
        }

        return dependencyList;
    }

    private static synchronized void buildRuntimeDependencyTree() {
        final MavenDependencyTreeParser parser = new MavenDependencyTreeParser();
        final MavenDependencyTreeParser.Pair<Collection<MavenArtifact>, Collection<MavenArtifact>> pair = parser.buildDependencyTreeAndList(getStreamToParse());
        dependencyTree = new ArrayList<MavenArtifact>(pair.getV1());
        dependencyList = new ArrayList<MavenArtifact>(pair.getV2());
    }

    private static InputStream getStreamToParse() {
        return ArtifactDependencySupport.class.getClassLoader().getResourceAsStream("org/drools/guvnor/server/maven/dependency.tree");
    }

}
