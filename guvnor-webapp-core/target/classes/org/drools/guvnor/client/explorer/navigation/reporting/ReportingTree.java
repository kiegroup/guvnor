package org.drools.guvnor.client.explorer.navigation.reporting;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ReportingTree implements ReportingTreeView.Presenter, IsWidget {

    private ReportingTreeView view;
    private PlaceController placeController;

    public ReportingTree(ReportingTreeView view, PlaceController placeController) {
        this.view = view;
        this.view.setPresenter(this);
        this.placeController = placeController;
    }

    public Widget asWidget() {
        return view.asWidget();
    }

    public void onReportTemplatesSelected() {
        placeController.goTo(new ReportTemplatesPlace());
    }
}
