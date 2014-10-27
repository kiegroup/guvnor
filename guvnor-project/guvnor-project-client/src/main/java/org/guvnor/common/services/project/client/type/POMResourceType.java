package org.guvnor.common.services.project.client.type;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.editor.type.POMResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class POMResourceType
        extends POMResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }
}
