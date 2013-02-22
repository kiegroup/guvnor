package org.kie.guvnor.globals.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.file.ResourceType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GlobalResourceType implements ResourceType {

    @Override
    public String getShortName() {
        return "global";
    }

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
        return "global.drl";
    }

    @Override
    public int getPriority() {
        return 101;
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().endsWith( "." + getSuffix() );
    }
}
