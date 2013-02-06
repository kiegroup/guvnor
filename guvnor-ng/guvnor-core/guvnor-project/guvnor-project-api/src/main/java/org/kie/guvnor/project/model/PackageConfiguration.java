package org.kie.guvnor.project.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.guvnor.services.config.model.imports.Imports;

@Portable
public class PackageConfiguration {

    private Imports imports = new Imports();

    public Imports getImports() {
        return imports;
    }
}
