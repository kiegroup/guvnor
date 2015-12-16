/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Cacheable;
import org.uberfire.security.authz.RuntimeContentResource;

public interface Repository extends RuntimeContentResource, Cacheable {

    /**
     * Most of the time, this can not be used as an unique ID.
     * If the Repository has branches each branch has the same alias.
     * @return short name for the repository
     */
    String getAlias();

    String getScheme();

    Map<String, Object> getEnvironment();

    void addEnvironmentParameter( final String key,
                                  final Object value );

    boolean isValid();

    String getUri();

    List<PublicURI> getPublicURIs();

    Path getRoot();

    Path getBranchRoot( String branch );

    void setRoot( final Path root );

    /**
     * Returns "read-only" view of all branches available in this repository.
     * @return
     */
    Collection<String> getBranches();

    /**
     * Returns current branch that is configured for this repository.
     * It will always provide branch name even if there was no explicit
     * branch selected/created - which in that case is always 'master'
     * @return
     */
    String getCurrentBranch();

}
