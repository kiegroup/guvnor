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

package org.kie.guvnor.project.backend.server;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.commons.data.workingset.WorkingSetSettings;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.PathImpl;

import static java.util.Collections.*;

@Service
@ApplicationScoped
public class ProjectServiceImpl
        implements ProjectService {

    @Override
    public Collection<Path> listProjectResources( final Path project ) {
        //TODO {porcelli}
        return emptyList();
    }

    @Override
    public WorkingSetSettings loadWorkingSetConfig( final Path project ) {
        //TODO {porcelli}
        return new WorkingSetSettings();
    }

    @Override
    public Path resolveProject( final Path resource ) {
        //TODO {porcelli}
        return new PathImpl();
    }
}
