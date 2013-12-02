package org.guvnor.common.services.project.backend.server;

import javax.enterprise.context.Dependent;

import com.thoughtworks.xstream.XStream;
import org.drools.workbench.models.datamodel.imports.Import;
import org.guvnor.common.services.project.model.ProjectImports;

@Dependent
public class ProjectConfigurationContentHandler {

    public ProjectConfigurationContentHandler() {
        // Weld needs this for proxying.
    }

    public String toString( final ProjectImports configuration ) {
        if ( configuration == null ) {
            return "";
        }
        return createXStream().toXML( configuration );
    }

    public ProjectImports toModel( final String text ) {
        if ( text == null || text.isEmpty() ) {
            return new ProjectImports();
        }
        return (ProjectImports) createXStream().fromXML( text );
    }

    private XStream createXStream() {
        XStream xStream = new XStream();
        xStream.alias( "configuration", ProjectImports.class );
        xStream.alias( "import", Import.class );
        return xStream;
    }
}
