package org.drools.guvnor.client;


import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.shared.mvp.PlaceRequest;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GuvnorDefaultPerspective {

    @Perspective(identifier = "homePerspective", isDefault = true)
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition definition = new PerspectiveDefinition();
        definition.setName("home");

        final PanelDefinition east = new PanelDefinition();
        east.addPart(new PartDefinition(new PlaceRequest("navigationPanel")));
        definition.getRoot().setChild(Position.WEST, east);

        definition.getRoot().addPart(new PartDefinition(new PlaceRequest("search")));

        return definition;
    }
}
