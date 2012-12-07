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

package org.kie.guvnor.projecteditor.backend.server;

import org.kie.guvnor.projecteditor.model.GroupArtifactVersionModel;

public class GroupArtifactVersionModelContentHandler {

    // TODO: Finish this when the core is more stable

    public String toString(GroupArtifactVersionModel gavModel) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "<groupId>org.kie.guvnor</groupId>\n" +
                "<artifactId>guvnor-parent</artifactId>\n" +
                "<name>Guvnor - Multi-project</name>";  // TODO -Rikkola-
    }

    public GroupArtifactVersionModel toModel(String xml) {
        return new GroupArtifactVersionModel();  // TODO -Rikkola-
    }
}
