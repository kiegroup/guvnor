package org.kie.guvnor.globals.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.guvnor.services.config.model.imports.Imports;

/**
 * The model for Globals
 */
@Portable
public class GlobalsModel {

    private List<Global> globals = new ArrayList<Global>();
    private Imports imports = new Imports();

    public List<Global> getGlobals() {
        return globals;
    }

    public void setGlobals( List<Global> globals ) {
        this.globals = globals;
    }

    public Imports getImports() {
        return imports;
    }

    public void setImports( Imports imports ) {
        this.imports = imports;
    }

}
