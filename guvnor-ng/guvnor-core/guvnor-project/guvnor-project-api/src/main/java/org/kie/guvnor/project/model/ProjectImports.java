package org.kie.guvnor.project.model;

import org.drools.guvnor.models.commons.shared.imports.Imports;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ProjectImports {

    private Imports imports = new Imports();

    private String version = "1.0";

    public Imports getImports() {
        return imports;
    }
}
