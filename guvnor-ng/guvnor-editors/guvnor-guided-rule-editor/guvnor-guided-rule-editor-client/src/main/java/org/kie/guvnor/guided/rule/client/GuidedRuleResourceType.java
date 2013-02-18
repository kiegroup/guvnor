package org.kie.guvnor.guided.rule.client;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.file.ResourceType;

@ApplicationScoped
public class GuidedRuleResourceType implements ResourceType {

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
        return "gre.drl";
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().endsWith( "." + getSuffix() );
    }
}
