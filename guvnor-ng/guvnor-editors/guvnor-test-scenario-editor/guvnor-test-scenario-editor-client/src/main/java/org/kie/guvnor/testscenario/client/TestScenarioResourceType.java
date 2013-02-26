package org.kie.guvnor.testscenario.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

public class TestScenarioResourceType implements ClientResourceType {

    @Override
    public String getShortName() {
        return "test scenario";
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
        return "scenario";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accept(Path path) {
        return path.getFileName().endsWith("." + getSuffix());
    }
}
