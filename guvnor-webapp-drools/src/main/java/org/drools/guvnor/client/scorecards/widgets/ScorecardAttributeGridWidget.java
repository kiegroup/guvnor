package org.drools.guvnor.client.scorecards.widgets;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.*;

public abstract class ScorecardAttributeGridWidget extends AbstractMergableGridWidget {

    public ScorecardAttributeGridWidget(ResourcesProvider resources, AbstractCellFactory cellFactory, AbstractCellValueFactory cellValueFactory, CellTableDropDownDataValueMapProvider dropDownManager, boolean isReadOnly, EventBus eventBus) {
        super(resources, cellFactory, cellValueFactory, dropDownManager, isReadOnly, eventBus);
    }


}
