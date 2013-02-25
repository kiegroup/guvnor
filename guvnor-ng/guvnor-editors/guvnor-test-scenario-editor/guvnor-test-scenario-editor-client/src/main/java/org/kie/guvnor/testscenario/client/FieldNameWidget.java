package org.kie.guvnor.testscenario.client;

import org.kie.guvnor.datamodel.oracle.DataModelOracle;

public class FieldNameWidget implements FieldNameWidgetView.Presenter {

    private final FieldNameWidgetView view;


    public FieldNameWidget(String fieldName,
                           DataModelOracle dmo,
                           FieldNameWidgetView view) {
        this.view = view;
        this.view.setPresenter(this);
        this.view.setTitle("fieldName");
    }

    @Override
    public void onClick() {
        view.openNewFieldSelector();
    }
}
