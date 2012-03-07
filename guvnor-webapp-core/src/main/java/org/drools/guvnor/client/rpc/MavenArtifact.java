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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.rpc.IsSerializable;

import static org.drools.guvnor.client.util.Preconditions.*;

public class MavenArtifact implements Serializable, IsSerializable {

    private static final String MAVEN_TEST_SCOPE = "test";
    private static final String MAVEN_PROVIDED_SCOPE = "provided";

    private String group;
    private String artifact;
    private String complement;
    private String version;
    private String type;
    private String scope;
    private Collection<MavenArtifact> child;
    private boolean necessaryOnRuntime;

    public MavenArtifact() {
        this.child = new ArrayList<MavenArtifact>();
    }

    public MavenArtifact(final String value) throws IllegalArgumentException {
        checkNotEmpty("value", value);
        final String[] values = value.split(":");
        if (values.length < 5 || values.length > 6) {
            throw new IllegalArgumentException("Invalid string format");
        }
        if (values.length == 5) {
            this.group = values[0];
            this.artifact = values[1];
            this.type = values[2];
            this.complement = null;
            this.version = values[3];
            this.scope = values[4];
        } else {
            this.group = values[0];
            this.artifact = values[1];
            this.type = values[2];
            this.complement = values[3];
            this.version = values[4];
            this.scope = values[5];
        }
        this.necessaryOnRuntime = checkNecessaryOnRuntime();
        this.child = new ArrayList<MavenArtifact>();
    }

    public MavenArtifact(final MavenArtifact source) {
        checkNotNull("source", source);
        this.group = source.group;
        this.artifact = source.artifact;
        this.complement = source.complement;
        this.version = source.version;
        this.type = source.type;
        this.scope = source.scope;
        this.necessaryOnRuntime = source.necessaryOnRuntime;
        if (source.child == null || source.child.size() == 0) {
            this.child = new ArrayList<MavenArtifact>();
        } else {
            this.child = new ArrayList<MavenArtifact>(source.child);
        }
    }

    private boolean checkNecessaryOnRuntime() {
        if (scope.equalsIgnoreCase(MAVEN_TEST_SCOPE) || scope.equalsIgnoreCase(MAVEN_PROVIDED_SCOPE)) {
            return false;
        }
        return true;
    }

    public String toValue() {
        if (complement == null) {
            return group + ":" + artifact + ":" + type + ":" + version + ":" + scope;
        }

        return group + ":" + artifact + ":" + type + ":" + complement + ":" + version + ":" + scope;
    }

    public String toLabel() {
        return group + ":" + toFileName();
    }

    public String toURL(final String repository) {
        checkNotNull("repository", repository);
        final StringBuilder sb = new StringBuilder(repository);

        if (!repository.endsWith("/")) {
            sb.append("/");
        }

        return sb.append(group.replace(".", "/"))
                .append('/').append(artifact)
                .append('/').append(version)
                .append('/').append(toFileName()).toString();
    }

    public String toFileName() {
        if (complement != null) {
            return artifact + "-" + version + "-" + complement + "." + getFileExtension();
        }

        return artifact + "-" + version + "." + getFileExtension();
    }

    public boolean isNecessaryOnRuntime() {
        return necessaryOnRuntime;
    }

    public synchronized void addChild(final MavenArtifact newArtifact) {
        checkNotNull("newArtifact", newArtifact);
        child.add(newArtifact);
    }

    public Collection<MavenArtifact> getChild() {
        return child;
    }

    public boolean hasChild() {
        return child.size() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MavenArtifact artifact1 = (MavenArtifact) o;

        if (!artifact.equals(artifact1.artifact)) {
            return false;
        }
        if (complement != null ? !complement.equals(artifact1.complement) : artifact1.complement != null) {
            return false;
        }
        if (!group.equals(artifact1.group)) {
            return false;
        }
        if (!scope.equals(artifact1.scope)) {
            return false;
        }
        if (!type.equals(artifact1.type)) {
            return false;
        }
        if (!version.equals(artifact1.version)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = group.hashCode();
        result = 31 * result + artifact.hashCode();
        result = 31 * result + (complement != null ? complement.hashCode() : 0);
        result = 31 * result + version.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + scope.hashCode();
        return result;
    }

    private String getFileExtension() {
        if (type.equalsIgnoreCase("ear")) {
            return "ear";
        } else if (type.equalsIgnoreCase("pom")) {
            return "pom";
        } else if (type.equalsIgnoreCase("war")) {
            return "war";
        }

        return "jar";
    }
}
