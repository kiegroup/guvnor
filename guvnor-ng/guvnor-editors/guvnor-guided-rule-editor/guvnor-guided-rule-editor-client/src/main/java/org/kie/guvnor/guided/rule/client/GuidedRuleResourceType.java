package org.kie.guvnor.guided.rule.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.file.ResourceType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GuidedRuleResourceType implements ResourceType {

    @Override
    public String getShortName() {
        return "guided rule";
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
        return "gre.drl";
    }

    @Override
    public int getPriority() {
        return 102;
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().endsWith( "." + getSuffix() );
    }
}
