package org.kie.guvnor.testscenario.client.service;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.kie.guvnor.testscenario.model.Failure;
import org.kie.guvnor.testscenario.model.TestResultMessage;
import org.uberfire.client.mvp.PlaceManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class TestRuntimeReportingService {

    @Inject
    private PlaceManager placeManager;

    private ListDataProvider<Failure> dataProvider = new ListDataProvider<Failure>();

    public TestRuntimeReportingService() {

    }

    public void addBuildMessages(final @Observes TestResultMessage message) {
        dataProvider.getList().addAll(message.getFailures());

        placeManager.goTo("org.kie.guvnor.TestResults");
    }

    public void addDataDisplay(HasData<Failure> display) {
        dataProvider.addDataDisplay(display);
    }
}
