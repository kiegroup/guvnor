package org.kie.guvnor.projecteditor.client;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.file.ResourceType;

@ApplicationScoped
public class KModuleResourceType implements ResourceType {

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public String getPrefix() {
        return "kmodule";
    }

    @Override
    public String getSuffix() {
        return "xml";
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().equals( getPrefix() + "." + getSuffix() );
    }
}
