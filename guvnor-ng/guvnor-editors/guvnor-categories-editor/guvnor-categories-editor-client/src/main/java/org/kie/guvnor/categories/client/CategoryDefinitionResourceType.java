package org.kie.guvnor.categories.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.file.ResourceType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoryDefinitionResourceType implements ResourceType {

    @Override
    public String getShortName() {
        return "catogories xml";
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
        return "categories";
    }

    @Override
    public String getSuffix() {
        return "xml";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().equals( getPrefix() + "." + getSuffix() );
    }
}
