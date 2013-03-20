package org.kie.guvnor.testscenario.client.reporting;


import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.kie.guvnor.testscenario.model.Failure;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.Position;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@WorkbenchScreen(identifier = "org.kie.guvnor.TestResults")
public class TestRunnerReportingScreen
        implements TestRunnerReportingView.Presenter {

    private final TestRunnerReportingView view;

    @Inject
    public TestRunnerReportingScreen(TestRunnerReportingView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.SOUTH;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Reporting";
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onMessageSelected(Failure failure) {
        view.setExplanation(failure.getMessage());
    }
}
