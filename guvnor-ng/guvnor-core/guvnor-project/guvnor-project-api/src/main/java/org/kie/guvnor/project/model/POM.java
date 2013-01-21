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

package org.kie.guvnor.project.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.guvnor.m2repo.model.GAV;

import java.util.ArrayList;
import java.util.List;

@Portable
public class POM {

    private GAV gav;

    private List<Dependency> dependencies = new ArrayList<Dependency>();

    public POM() {
    }

    public POM(GAV gav) {
        super();
        this.gav = gav;
    }

    public GAV getGav() {
        return gav;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }
}
