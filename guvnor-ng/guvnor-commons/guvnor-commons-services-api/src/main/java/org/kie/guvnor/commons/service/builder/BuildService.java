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

package org.kie.guvnor.commons.service.builder;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface BuildService {

    /**
     * Full build
     * @param pathToPom
     */
    void build( final Path pathToPom );

    /**
     * Add a resource to the build.
     * @param pathToPom
     * @param resource
     */
    void addResource( final Path pathToPom,
                      final Path resource );

    /**
     * Remove a resource from the build.
     * @param pathToPom
     * @param resource
     */
    void deleteResource( final Path pathToPom,
                         final Path resource );

    /**
     * Update an existing resource in the build.
     * @param pathToPom
     * @param resource
     */
    void updateResource( final Path pathToPom,
                         final Path resource );

}
