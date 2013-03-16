package org.kie.guvnor.testscenario.client.reporting;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TestRunnerReportingViewImpl
        implements TestRunnerReportingView {

    @Override
    public Widget asWidget() {
        return new Label("Screen here");
    }
}
