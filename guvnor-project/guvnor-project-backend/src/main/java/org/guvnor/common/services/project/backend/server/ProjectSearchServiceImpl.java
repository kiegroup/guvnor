/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectSearchService;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.bus.server.annotations.Service;

/**
 * TODO: Improve using indexes. Avoid to iterate thorough the entire repo>project hierarchy.
 */
@Service
@ApplicationScoped
public class ProjectSearchServiceImpl implements ProjectSearchService {

    private ProjectService projectService;

    @Inject
    public ProjectSearchServiceImpl(final ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public Collection<Project> searchByName(final String pattern,
                                            final int maxItems,
                                            final boolean caseSensitive) {
        final List<Project> result = new ArrayList<>();

        for (final Project project : projectService.getAllProjects()) {

            final String name = project.getName();
            if (caseSensitive ? name.contains(pattern) : name.toLowerCase().contains(pattern.toLowerCase())) {
                result.add(project);
            }

            if (maxItems == result.size()) {
                break;
            }
        }

        return result;
    }
}
