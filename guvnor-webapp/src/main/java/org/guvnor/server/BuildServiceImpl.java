/*
 * Copyright 2014 JBoss Inc
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

package org.guvnor.server;

import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

@Service
@ApplicationScoped
public class BuildServiceImpl
        implements BuildService {

    @Override
    public BuildResults build(final Project project) {
        return new BuildResults();
    }

    @Override
    public BuildResults buildAndDeploy(final Project project) {
        return new BuildResults();
    }

    @Override
    public BuildResults buildAndDeploy(final Project project, boolean suppressHandlers) {
        return new BuildResults();
    }

    @Override
    public boolean isBuilt(final Project project) {
        return true;
    }

    @Override
    public IncrementalBuildResults addPackageResource(final Path resource) {
        return new IncrementalBuildResults();
    }

    @Override
    public IncrementalBuildResults deletePackageResource(final Path resource) {
        return new IncrementalBuildResults();
    }

    @Override
    public IncrementalBuildResults updatePackageResource(final Path resource) {
        return new IncrementalBuildResults();
    }

    @Override
    public IncrementalBuildResults applyBatchResourceChanges(final Project project,
            final Map<Path, Collection<ResourceChange>> changes) {
        return new IncrementalBuildResults();
    }

}
