/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.guvnor.m2repo.backend.server.repositories;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.eclipse.aether.artifact.Artifact;
import org.guvnor.common.services.project.model.GAV;

public interface ArtifactRepository {

    String getName();

    Collection<File> listFiles(final List<String> wildcards);

    Collection<Artifact> listArtifacts(final List<String> wildcards);

    void deploy(String pom,
                Artifact... artifacts);

    void delete(final GAV gav);

    /**
     * Checks whether this Maven repository contains the specified artifact (GAV).
     * <p>
     * As opposed to ${code {@link #getArtifactFileFromRepository(GAV)}}, this method will not log any WARNings in case
     * the artifact is not present (the Aether exception is only logged as TRACE message).
     * @param gav artifact GAV, never null
     * @return true if the this Maven repo contains the specified artifact, otherwise false
     */
    boolean containsArtifact(final GAV gav);

    File getArtifactFileFromRepository(final GAV gav);
}
