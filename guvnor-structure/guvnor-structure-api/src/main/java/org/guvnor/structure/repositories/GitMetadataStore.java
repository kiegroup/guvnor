/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.repositories;

import java.util.Optional;

public interface GitMetadataStore {

    /**
     * Writes the meta information about a repository without an origin.
     * The repository and the origin must be in organizationalUnit/repositoryName format
     * @param name The name of the repository in organizationalUnit/repositoryName format
     */
    void write( String name );

    /**
     * Writes the meta information about a repository and its origin.
     * The repository and the origin must be in organizationalUnit/repositoryName format
     * @param name The name of the repository in organizationalUnit/repositoryName format
     * @param origin The name of the origin in organizationalUnit/repositoryName format
     */
    void write( String name,
                String origin );

    /**
     * Reads the git metadata from repository.
     * @param name the repository name in organizationalUnit/repositoryName format
     * @return
     */
    Optional<GitMetadata> read( String name );

    /**
     * Deletes that repository meta information and removes its reference from the origin and forks.
     * @param name The repository name in organizationalUnit/repositoryName format
     */
    void delete( String name );
}
