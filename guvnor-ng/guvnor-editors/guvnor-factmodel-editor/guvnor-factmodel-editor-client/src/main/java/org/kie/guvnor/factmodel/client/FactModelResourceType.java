package org.kie.guvnor.factmodel.client;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.file.ResourceType;

@ApplicationScoped
public class FactModelResourceType implements ResourceType {

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
        return "";
    }

    @Override
    public String getSuffix() {
        return "model.drl";
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().endsWith( "." + getSuffix() );
    }
}
