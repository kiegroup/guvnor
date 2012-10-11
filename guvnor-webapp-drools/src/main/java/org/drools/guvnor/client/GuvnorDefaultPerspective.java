package org.drools.guvnor.client;


import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GuvnorDefaultPerspective {

    @Perspective(identifier = "org.drools.guvnor.home", isDefault = true)
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition definition = new PerspectiveDefinitionImpl();
        definition.setName("home");

        final PanelDefinition east = new PanelDefinitionImpl();
        east.addPart(new PartDefinitionImpl(new DefaultPlaceRequest("navigationPanel")));
        definition.getRoot().setChild(Position.WEST, east);

        definition.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("search")));
        definition.setTransient( true );

        return definition;
    }
}
