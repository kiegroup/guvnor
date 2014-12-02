package org.guvnor.common.services.project.model;

import org.drools.workbench.models.datamodel.imports.Imports;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ProjectImports {

    private Imports imports = new Imports();

    private String version = "1.0";

    public Imports getImports() {
        return imports;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        ProjectImports that = ( ProjectImports ) o;

        if ( imports != null ? !imports.equals( that.imports ) : that.imports != null ) {
            return false;
        }
        if ( version != null ? !version.equals( that.version ) : that.version != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = imports != null ? imports.hashCode() : 0;
        result = 31 * result + ( version != null ? version.hashCode() : 0 );
        return result;
    }
}
