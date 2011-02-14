package org.drools.guvnor.client.qa;

public class ScenarioResultSummaryRow
    implements
    ScenarioResultSummaryView.Presenter {

    private final ScenarioResultSummaryView display;

    public ScenarioResultSummaryRow(ScenarioResultSummaryView display) {
        this.display = display;

        display.setPresenter( this );
    }
}
