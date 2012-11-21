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

package org.drools.guvnor.server.maven.parser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import org.drools.guvnor.client.rpc.MavenArtifact;

import static com.google.common.base.Preconditions.*;

public class MavenDependencyTreeParser {

    public MavenDependencyTreeParser() {
    }

    public Pair<Collection<MavenArtifact>, Collection<MavenArtifact>> buildDependencyTreeAndList(final String input) {
        checkNotNull(input);
        return buildDependencyTreeAndList(new ByteArrayInputStream(input.getBytes()));
    }

    public Pair<Collection<MavenArtifact>, Collection<MavenArtifact>> buildDependencyTreeAndList(final InputStream input) {
        checkNotNull(input);
        try {
            return build(new BufferedReader(new InputStreamReader(input)));
        } catch (IOException e) {
            throw new RuntimeException("Can't read input stream.", e);
        } catch (Exception e) {
            throw new RuntimeException("Can't parse stream.", e);
        }
    }

    private Pair<Collection<MavenArtifact>, Collection<MavenArtifact>> build(final BufferedReader buffer) throws Exception {
        final Collection<MavenArtifact> dependencyTree = new ArrayList<MavenArtifact>();
        final Collection<MavenArtifact> dependencyList = new ArrayList<MavenArtifact>();

        final Stack<Pair<Integer, MavenArtifact>> stack = new Stack<Pair<Integer, MavenArtifact>>() {{
            push(new Pair<Integer, MavenArtifact>(Integer.MIN_VALUE, new MavenArtifact()));
        }};

        String strLine = null;
        while ((strLine = buffer.readLine()) != null) {
            if (Character.isLetterOrDigit(strLine.charAt(0))) {
                //should discard root element
                continue;
            }
            for (int index = 0; index < strLine.length(); index++) {
                if (Character.isLetterOrDigit(strLine.charAt(index))) {
                    final String artifactDefinition = extractArtifactDefinition(strLine.substring(index));
                    final MavenArtifact newArtifact = new MavenArtifact(artifactDefinition);
                    if (!newArtifact.isNecessaryOnRuntime()) {
                        break;
                    }
                    dependencyList.add(newArtifact);
                    if (index == stack.peek().getV1()) {
                        //same level
                        stack.pop();
                        stack.peek().getV2().addChild(newArtifact);
                        stack.push(new Pair<Integer, MavenArtifact>(index, newArtifact));
                    } else if (index > stack.peek().getV1()) {
                        //active is child
                        stack.peek().getV2().addChild(newArtifact);
                        stack.push(new Pair<Integer, MavenArtifact>(index, newArtifact));
                    } else {
                        //back stack level until actual is child or same level
                        while (index < stack.peek().getV1()) {
                            stack.pop();
                        }
                        stack.pop();
                        stack.peek().getV2().addChild(newArtifact);
                        stack.push(new Pair<Integer, MavenArtifact>(index, newArtifact));
                    }
                    break;
                }
            }
        }

        while (stack.size() != 1) {
            stack.pop();
        }

        for (MavenArtifact artifact : stack.peek().getV2().getChild()) {
            dependencyTree.add(artifact);
        }

        return new Pair<Collection<MavenArtifact>, Collection<MavenArtifact>>(dependencyTree, dependencyList);
    }

    private String extractArtifactDefinition(final String content) {
        for (int index = 0; index < content.length(); index++) {
            if (Character.isSpaceChar(content.charAt(index))) {
                return content.substring(0, index);
            }
        }
        return content;
    }

    public static class Pair<T1, T2> {

        private final T1 v1;
        private final T2 v2;

        Pair(T1 v1, T2 v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        public T1 getV1() {
            return v1;
        }

        public T2 getV2() {
            return v2;
        }
    }

}
