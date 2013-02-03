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

package org.kie.guvnor.factmodel.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.guvnor.services.config.model.imports.Imports;

import java.util.ArrayList;
import java.util.List;

@Portable
public class FactModelContent {

    private FactModels factModels;
    private List<FactMetaModel> superTypes = new ArrayList<FactMetaModel>();
    private Imports imports = new Imports();

    public FactModelContent() {
    }

    public FactModelContent(final FactModels factModels,
                            final List<FactMetaModel> superTypes,
                            final Imports imports) {
        this.factModels = factModels;
        superTypes.addAll(superTypes);
        this.imports = imports;
    }

    public FactModels getFactModels() {
        return factModels;
    }

    public List<FactMetaModel> getSuperTypes() {
        return superTypes;
    }

    public Imports getImports() {
        return imports;
    }

}
