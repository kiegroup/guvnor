package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class NewFactWidget implements IsWidget, NewFactWidgetView.Presenter {


    public NewFactWidget(FieldDataConstraintHelper helper, NewFactWidgetView view) {
        view.setPresenter(this);
        view.setFactName("Address");
    }

    @Override
    public Widget asWidget() {
        return null;  //TODO: -Rikkola-
    }
}
