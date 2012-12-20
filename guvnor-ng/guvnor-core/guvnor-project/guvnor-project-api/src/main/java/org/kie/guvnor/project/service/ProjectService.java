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

package org.kie.guvnor.project.service;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.guvnor.commons.data.workingset.WorkingSetSettings;
import org.kie.guvnor.project.model.GroupArtifactVersionModel;
import org.uberfire.backend.vfs.Path;

/**
 *
 */
@Remote
public interface ProjectService {

    Collection<Path> listProjectResources( final Path project );

    WorkingSetSettings loadWorkingSetConfig( final Path project );

    GroupArtifactVersionModel loadGav(Path path);

    /**
     * Given a Path to a resource resolve a Path for the containing Project's pom.xml file
     * @param resource
     * @return Path to the Project's pom.xml file or null if the resource was not in a Project
     */
    Path resolveProject( Path resource );
}
