/*
 * Copyright 2015 JBoss Inc
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

import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Model;
import org.guvnor.common.services.project.model.Dependency;

class DependencyUpdater {

    private Model model;

    DependencyUpdater(Model model) {
        this.model = model;
    }

    void updateDependencies(List<Dependency> dependencies) {
        removeAllThatDoNotExist(dependencies);
        addTheOnesThatDoNotExist(dependencies);
        updateTheRest(dependencies);
    }

    private void updateTheRest(List<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            for (org.apache.maven.model.Dependency modelDep : model.getDependencies()) {
                if (hasSameID(dependency, modelDep)) {
                    updateDependency(dependency, modelDep);
                }
            }
        }
    }

    private void addTheOnesThatDoNotExist(List<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            if (!depsContains(model.getDependencies(), dependency)) {
                model.addDependency(fromClientModelToPom(dependency));
            }
        }
    }

    private void removeAllThatDoNotExist(List<Dependency> dependencies) {
        Iterator<org.apache.maven.model.Dependency> iterator = model.getDependencies().iterator();
        while (iterator.hasNext()) {
            org.apache.maven.model.Dependency dependency = iterator.next();
            if (!depsContains(dependencies, dependency)) {
                iterator.remove();
            }
        }
    }

    private org.apache.maven.model.Dependency fromClientModelToPom(org.guvnor.common.services.project.model.Dependency from) {
        org.apache.maven.model.Dependency dependency = updateDependency(from, new org.apache.maven.model.Dependency());

        return dependency;
    }

    private org.apache.maven.model.Dependency updateDependency(org.guvnor.common.services.project.model.Dependency from, org.apache.maven.model.Dependency dependency) {

        dependency.setArtifactId(from.getArtifactId());
        dependency.setGroupId(from.getGroupId());
        dependency.setVersion(from.getVersion());
        dependency.setScope(from.getScope());
        return dependency;
    }

    private boolean depsContains(List<org.guvnor.common.services.project.model.Dependency> dependencies, org.apache.maven.model.Dependency dependency) {
        for (org.guvnor.common.services.project.model.Dependency modelDep : dependencies) {
            if (hasSameID(modelDep, dependency)) {
                return true;
            }
        }
        return false;
    }

    private boolean depsContains(List<org.apache.maven.model.Dependency> dependencies, org.guvnor.common.services.project.model.Dependency dependency) {
        for (org.apache.maven.model.Dependency modelDep : dependencies) {
            if (hasSameID(dependency, modelDep)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSameID(org.guvnor.common.services.project.model.Dependency dependency, org.apache.maven.model.Dependency modelDep) {
        return dependency.getArtifactId().equals(modelDep.getArtifactId()) && dependency.getGroupId().equals(modelDep.getGroupId());
    }

}
